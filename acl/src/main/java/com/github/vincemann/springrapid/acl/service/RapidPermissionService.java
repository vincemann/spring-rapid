package com.github.vincemann.springrapid.acl.service;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import com.github.vincemann.springrapid.core.security.Roles;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.vincemann.springrapid.acl.util.AclUtils.getSidString;

/**
 * API for managing acl data.
 */
@Service
@Transactional
@Slf4j
public class RapidPermissionService implements AclPermissionService {

    private MutableAclService aclService;
    private RapidSecurityContext<RapidAuthenticatedPrincipal> securityContext;
    private PermissionStringConverter permissionStringConverter;

    @Autowired
    public RapidPermissionService(MutableAclService aclService) {
        this.aclService = aclService;
    }

    @Override
    public void savePermissionForRoleOverEntity(IdentifiableEntity<?> entity, String role, Permission... permissions) {
        securityContext.runAsAdmin(() ->{
            final Sid sid = new GrantedAuthoritySid(role);
            addPermissionsForSid(entity, sid, permissions);
        });
    }

    @Override
    public void deletePermissionForRoleOverEntity(IdentifiableEntity<?> entity, String role, Permission... permissions) throws AclNotFoundException, AceNotFoundException {
        final Sid sid = new GrantedAuthoritySid(role);
        deletePermissionForSid(entity,sid,false, permissions);
    }


    @Override
    public void savePermissionForAuthenticatedOverEntity(IdentifiableEntity<?> entity, Permission... permissions) {
        String authenticatedName = findAuthenticatedName();
        savePermissionForUserOverEntity(authenticatedName,entity,permissions);
    }

    @Override
    public void deletePermissionForAuthenticatedOverEntity(IdentifiableEntity<?> entity, Permission... permissions) throws AclNotFoundException, AceNotFoundException {
        String authenticatedName = findAuthenticatedName();
        deletePermissionForUserOverEntity(authenticatedName,entity,permissions);
    }

    @Override
    public void savePermissionForUserOverEntity(String user, IdentifiableEntity<?> entity, Permission... permissions) {
        securityContext.runWithName(user,() ->{
            final Sid sid = new PrincipalSid(user);
            addPermissionsForSid(entity, sid,permissions);
        });
    }

    @Override
    public void deletePermissionForUserOverEntity(String user, IdentifiableEntity<?> entity, Permission... permissions) throws AclNotFoundException, AceNotFoundException{
        deletePermissionForUserOverEntity(user,entity,false,permissions);
    }

    @Override
    public void deletePermissionForUserOverEntity(String user, IdentifiableEntity<?> entity, Boolean deleteCascade, Permission... permissions) throws AclNotFoundException, AceNotFoundException {
        final Sid sid = new PrincipalSid(user);
        deletePermissionForSid(entity,sid,deleteCascade,permissions);
    }

    @Override
    public void deleteAclOfEntity(Class<? extends IdentifiableEntity> clazz, Serializable id, boolean deleteCascade) {
        ObjectIdentity oi = new ObjectIdentityImpl(clazz, id);
        aclService.deleteAcl(oi,deleteCascade);
    }

    @Override
    public void deleteAclOfEntity(IdentifiableEntity<?> entity, boolean deleteCascade) {
        ObjectIdentity oi = new ObjectIdentityImpl(entity.getClass(), entity.getId());
        //delete children ist nur richtig wenn ich wirklich one to n habe mit Delete Cascade!
        aclService.deleteAcl(oi,deleteCascade);
    }

    protected String findAuthenticatedName(){
        if (RapidSecurityContext.hasRole(Roles.ANON)){
            throw new AccessDeniedException("Non anon Authentication required");
        }
        String name = RapidSecurityContext.getName();
        //Nicht auslagern. MutableAclService macht das intern auch so -> use @MockUser(username="testUser") in tests
        if(name==null){
            throw new AccessDeniedException("Authentication required");
        }
        return name;
    }

    protected MutableAcl findAcl(ObjectIdentity oi) throws AclNotFoundException {
        try {
            return (MutableAcl) aclService.readAclById(oi);
        } catch (final NotFoundException nfe) {
            throw new AclNotFoundException("Acl not found for oi: " + oi);
        }
    }

    protected MutableAcl findOrCreateAcl(ObjectIdentity oi) {
        try {
            return (MutableAcl) aclService.readAclById(oi);
        } catch (final NotFoundException nfe) {
            log.debug("Acl not found for oi: " + oi+ ", creating new");
            return aclService.createAcl(oi);
        }
    }

//    @Override
//    public void inheritPermissionEntriesOfParent(IdentifiableEntity<?> targetObj, IdentifiableEntity<?> parent) throws AclNotFoundException {
//        ObjectIdentity childOi = new ObjectIdentityImpl(targetObj.getClass(), targetObj.getId());
//        ObjectIdentity parentOi = new ObjectIdentityImpl(parent.getClass(), parent.getId());
//
//        MutableAcl childAcl = findOrCreateAcl(childOi);
//        MutableAcl parentAcl = findAcl(parentOi);
//
////        childAcl.setEntriesInheriting(true);
//        childAcl.setParent(parentAcl);
//
//        logAclInformation(parentAcl, childAcl);
//
//        aclService.updateAcl(childAcl);
//    }

    @Override
    public void inheritAces(IdentifiableEntity<?> parent, List<AclInheritanceInfo> infos) throws AclNotFoundException {
        AclInheritanceInfo info = getParentInfo(parent,infos);
        if (!info.getSourceFilter().matches(parent)){
            return;
        }
        Collection<IdentifiableEntity<?>> children = getAclChildren(parent, info);
        for (IdentifiableEntity<?> child : children) {
            inheritAces(child,parent,info.getAceFilter());
            // recursion
            inheritAces(parent,infos);
        }
    }

    protected Collection<IdentifiableEntity<?>> getAclChildren(IdentifiableEntity<?> parent, AclInheritanceInfo info) {
        try {
            Collection<IdentifiableEntity<?>> children = (Collection<IdentifiableEntity<?>>) info.getTarget().invoke(parent);
            return info.getTargetFilter().filter(children);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateEntriesInheriting(boolean value, IdentifiableEntity<?> child, IdentifiableEntity<?> parent) throws AclNotFoundException {
        ObjectIdentity childOi = new ObjectIdentityImpl(child.getClass(), child.getId());
        ObjectIdentity parentOi = new ObjectIdentityImpl(parent.getClass(), parent.getId());

        MutableAcl childAcl = findOrCreateAcl(childOi);
        MutableAcl parentAcl = findAcl(parentOi);

        childAcl.setParent(parentAcl);
        childAcl.setEntriesInheriting(value);
        aclService.updateAcl(childAcl);
    }

    protected AclInheritanceInfo getParentInfo(IdentifiableEntity<?> parent, List<AclInheritanceInfo> infos){
        // first info is always parent
        Optional<AclInheritanceInfo> info = infos.stream()
                .filter(i -> i.matches(parent.getClass()))
                .findFirst();
        if (info.isEmpty())
            throw new IllegalArgumentException("Cannot find matching info from supplied info list");
        return info.get();
    }

    @Override
    public void inheritAces(IdentifiableEntity<?> child, IdentifiableEntity<?> parent, AceFilter aceFilter) throws AclNotFoundException {
        ObjectIdentity childOi = new ObjectIdentityImpl(child.getClass(), child.getId());
        ObjectIdentity parentOi = new ObjectIdentityImpl(parent.getClass(), parent.getId());

        MutableAcl childAcl = findOrCreateAcl(childOi);
        MutableAcl parentAcl = findAcl(parentOi);

        copyMatchingAces(parentAcl, childAcl, aceFilter);

        childAcl.setParent(parentAcl);
        logAclInformation(parentAcl, childAcl);
        aclService.updateAcl(childAcl);
    }

    /**
     * Copy all aces from parentAcl to childAcl that match aceFilter: {@link AceFilter#matches(AccessControlEntry)}.
     */
    protected void copyMatchingAces(MutableAcl parentAcl, MutableAcl childAcl, AceFilter aceFilter) {
        for (AccessControlEntry ace : parentAcl.getEntries()) {
           if (aceFilter.matches(ace))
               childAcl.insertAce(childAcl.getEntries().size(), ace.getPermission(), ace.getSid(), ace.isGranting());
        }
    }

    protected void logAclInformation(MutableAcl parentAcl, MutableAcl childAcl) {
        if (log.isDebugEnabled()){
            log.debug("Parent Acl: " + parentAcl);
            log.debug("Child Acl: " + childAcl);
        }
    }

    protected void addPermissionsForSid(IdentifiableEntity<?> targetObj, Sid sid, Permission... permissions) {
        if (log.isDebugEnabled())
            log.debug("sid: "+ sid +" will gain permissions: " + Arrays.stream(permissions).map(p -> permissionStringConverter.convert(p)).collect(Collectors.toSet()) +" over entity: " + targetObj);
        final ObjectIdentity oi = new ObjectIdentityImpl(targetObj.getClass(), targetObj.getId());

        MutableAcl acl = findOrCreateAcl(oi);
        if (log.isDebugEnabled())
            log.debug("acl of entity before adding: " + acl);

        for (Permission permission : permissions) {
            acl.insertAce(acl.getEntries().size(), permission, sid, true);
        }
        MutableAcl updated = aclService.updateAcl(acl);

        if (log.isDebugEnabled())
            log.debug("updated acl: " + updated);
    }

    protected void deletePermissionForSid(IdentifiableEntity<?> targetObj, Sid sid, boolean deleteCascade, Permission... permissions) throws AclNotFoundException, AceNotFoundException {
        if (log.isDebugEnabled())
            log.debug("sid: "+ sid +" will loose permission: " + Arrays.stream(permissions).map(p -> permissionStringConverter.convert(p)).collect(Collectors.toSet()) +" over entity: " + targetObj);
        final ObjectIdentity oi = new ObjectIdentityImpl(targetObj.getClass(), targetObj.getId());
        MutableAcl acl = findAcl(oi);
        if (log.isDebugEnabled())
            log.debug("acl of entity before removal" + acl);
        List<AccessControlEntry> aces = acl.getEntries();
        Set<Integer> aceIndicesToRemove = findMatchingAceIndices(aces, sid, permissions);

        if (aceIndicesToRemove.isEmpty()){
            throw new AceNotFoundException("Cant remove permission for sid: " + sid + " on target: " + oi + ", bc no matching ace found");
        }
                // todo have to do weird remove workaround again bc of hibernate set, see DirEntity
        // AccessControlEntry removed = aces.remove(aceIndexToRemove);
        Iterator<AccessControlEntry> aceIterator = aces.iterator();
        int index = 0;
        while (aceIterator.hasNext()){
            aceIterator.next();
            if (aceIndicesToRemove.contains(index)){
                aceIterator.remove();
                break;
            }
            index++;
        }

        // do weird stuff so update wont be ignored ...
        deleteAclOfEntity(targetObj,deleteCascade);
        MutableAcl updatedAcl = aclService.createAcl(oi);
        for (AccessControlEntry ace : aces) {
            updatedAcl.getEntries().add(ace);
        }
        MutableAcl updated = aclService.updateAcl(updatedAcl);
        if (log.isDebugEnabled())
            log.debug("updated acl: " + updated);
    }

    protected void deletePermissionForSid(){

    }

    // todo reduce overhead when updating to newer spring version
    protected Set<Integer> findMatchingAceIndices(List<AccessControlEntry> aces,Sid sid, Permission... permissions) throws AceNotFoundException {
        Set<Integer> positions = new HashSet<>();
        // https://github.com/spring-projects/spring-security/issues/5401
//        Set<AccessControlEntry> result = aces.stream()
//                .peek(x -> position[0]++)
//                .filter(accessControlEntry -> {
//                    return getSidString(accessControlEntry.getSid()).equals(getSidString(sid)) &&
//                            accessControlEntry.getPermission().equals(permission);
//                }).collect(Collectors.toSet());
        Set<AccessControlEntry> result = aces.stream()
                .filter(accessControlEntry -> {
                    boolean isMatching = getSidString(accessControlEntry.getSid()).equals(getSidString(sid)) &&
                            Arrays.stream(permissions).anyMatch(p -> p.equals(accessControlEntry.getPermission()));
                    if (isMatching) {
                        positions.add(positions.size() + 1); // Add the position to the Set
                    }
                    return isMatching;
                })
                .collect(Collectors.toSet());
//        if (result.isEmpty()){
//            return -1;
//        }else{
            if (log.isDebugEnabled())
                log.debug("Aces to remove: " + result);
//            return position[0];
        return positions;
    }


    @Autowired
    public void injectAclService(MutableAclService aclService) {
        this.aclService = aclService;
    }

    @Autowired
    public void injectSecurityContext(RapidSecurityContext<RapidAuthenticatedPrincipal> securityContext) {
        this.securityContext = securityContext;
    }

    @Autowired
    public void injectPermissionStringConverter(PermissionStringConverter permissionStringConverter) {
        this.permissionStringConverter = permissionStringConverter;
    }
}