package com.github.vincemann.springrapid.acl.service;

import com.github.vincemann.springrapid.acl.util.AclUtils;
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
    public void deletePermissionForRoleOverEntityIfPresent(IdentifiableEntity<?> entity, String role, Permission... permissions) throws AclNotFoundException, AceNotFoundException {
        final Sid sid = new GrantedAuthoritySid(role);
        deletePermissionForSid(entity,sid, true, permissions);
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
    public void deletePermissionForAuthenticatedOverEntityIfPresent(IdentifiableEntity<?> entity, Permission... permissions) throws AclNotFoundException, AceNotFoundException {
        String authenticatedName = findAuthenticatedName();
        deletePermissionForUserOverEntityIfPresent(authenticatedName,entity,permissions);
    }

    @Override
    public void savePermissionForUserOverEntity(String user, IdentifiableEntity<?> entity, Permission... permissions) {
        securityContext.runWithName(user,() ->{
            final Sid sid = new PrincipalSid(user);
            addPermissionsForSid(entity, sid,permissions);
        });
    }

    @Override
    public void deletePermissionForUserOverEntity(String user, IdentifiableEntity<?> entity, Permission... permissions) throws AclNotFoundException, AceNotFoundException {
        final Sid sid = new PrincipalSid(user);
        deletePermissionForSid(entity,sid,false, permissions);
    }

    @Override
    public void deletePermissionForUserOverEntityIfPresent(String user, IdentifiableEntity<?> entity, Permission... permissions) throws AclNotFoundException, AceNotFoundException {
        final Sid sid = new PrincipalSid(user);
        deletePermissionForSid(entity,sid,true,permissions);
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
            throw new AclNotFoundException("Acl not found for oi: " + AclUtils.objectIdentityToString(oi));
        }
    }

    protected MutableAcl findOrCreateAcl(ObjectIdentity oi) {
        try {
            return (MutableAcl) aclService.readAclById(oi);
        } catch (final NotFoundException nfe) {
            log.debug("Acl not found for oi: " + AclUtils.objectIdentityToString(oi)+ ", creating new");
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
        if (info == null)
            return;
        EntityFilter filter = info.getSourceFilter();
        if (filter != null){
            if (!filter.matches(parent)){
                return;
            }
        }
        Collection<IdentifiableEntity<?>> children = getAclChildren(parent, info);
        for (IdentifiableEntity<?> child : children) {
            copyParentAces(child,parent,info.getAceFilter());
            // recursion
            inheritAces(child,infos);
        }
    }

    @Override
    public void inheritAces(Collection<? extends IdentifiableEntity<?>>parents, List<AclInheritanceInfo> infos) throws AclNotFoundException {
        for (IdentifiableEntity<?> parent : parents) {
            inheritAces(parent,infos);
        }
    }

    protected Collection<IdentifiableEntity<?>> getAclChildren(IdentifiableEntity<?> parent, AclInheritanceInfo info) {
        try {
            Collection<IdentifiableEntity<?>> children = (Collection<IdentifiableEntity<?>>) info.getTarget().invoke(parent);
            EntityFilter filter = info.getTargetFilter();
            if (filter != null)
                return filter.apply(children);
            else
                return children;
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

        if (value){
            childAcl.setParent(parentAcl);
        }else {
            childAcl.setParent(null);
        }
        childAcl.setEntriesInheriting(value);
        aclService.updateAcl(childAcl);
    }

    protected AclInheritanceInfo getParentInfo(IdentifiableEntity<?> parent, List<AclInheritanceInfo> infos){
        // first info is always parent
        Optional<AclInheritanceInfo> info = infos.stream()
                .filter(i -> i.matches(parent.getClass()))
                .findFirst();
        return info.orElse(null);
//            throw new IllegalArgumentException("Cannot find matching info from supplied info list");
    }

    @Override
    public void copyParentAces(IdentifiableEntity<?> child, IdentifiableEntity<?> parent, AceFilter aceFilter) throws AclNotFoundException {
        ObjectIdentity childOi = new ObjectIdentityImpl(child.getClass(), child.getId());
        ObjectIdentity parentOi = new ObjectIdentityImpl(parent.getClass(), parent.getId());

        MutableAcl childAcl = findOrCreateAcl(childOi);
        MutableAcl parentAcl = findAcl(parentOi);

        if (log.isDebugEnabled()){
            log.debug("child acl of entity before copying: ");
            log.debug(AclUtils.aclToString(childAcl));
        }


        copyMatchingAces(parentAcl, childAcl, aceFilter);

//        childAcl.setParent(parentAcl);
        logAclInformation(parentAcl, childAcl);
        aclService.updateAcl(childAcl);
    }

    /**
     * Copy all aces from parentAcl to childAcl that match aceFilter: {@link AceFilter#matches(AccessControlEntry)}
     * and are not already present in childAcl.
     */
    protected void copyMatchingAces(MutableAcl parentAcl, MutableAcl childAcl, AceFilter aceFilter) {
        for (AccessControlEntry ace : parentAcl.getEntries()) {
           if (aceFilter.matches(ace)){
               if (!AclUtils.isAcePresent(ace,childAcl))
                    childAcl.insertAce(childAcl.getEntries().size(), ace.getPermission(), ace.getSid(), ace.isGranting());
           }
        }
    }

    protected void logAclInformation(MutableAcl parentAcl, MutableAcl childAcl) {
        if (log.isDebugEnabled()){

            log.debug("parent acl: ");
            log.debug(AclUtils.aclToString(parentAcl));
            log.debug("Child Acl: ");
            log.debug(AclUtils.aclToString(childAcl));
        }
    }

    protected void addPermissionsForSid(IdentifiableEntity<?> targetObj, Sid sid, Permission... permissions) {
        final ObjectIdentity oi = new ObjectIdentityImpl(targetObj.getClass(), targetObj.getId());
        if (log.isDebugEnabled())
            log.debug("sid: "+ AclUtils.sidToString(sid) +" will gain permissions: "
                    + AclUtils.permissionsToString(permissions)
                    +" over entity: " + AclUtils.objectIdentityToString(oi));

        MutableAcl acl = findOrCreateAcl(oi);
        if (log.isDebugEnabled()){
            log.debug("acl of entity before adding: ");
            log.debug(AclUtils.aclToString(acl));
        }

        for (Permission permission : permissions) {
            if (AclUtils.isAcePresent(permission,sid,acl))
                log.warn("ace is already present in acl: " + AclUtils.sidToString(sid) + " - " + AclUtils.permissionToString(permission) + ". Skipping");
            else
                acl.insertAce(acl.getEntries().size(), permission, sid, true);
        }
        MutableAcl updated = aclService.updateAcl(acl);

        if (log.isDebugEnabled()){
            log.debug("updated acl ");
            log.debug(AclUtils.aclToString(updated));
        }
    }

    protected void deletePermissionForSid(IdentifiableEntity<?> targetObj, Sid sid, boolean ignoreNotFound, Permission... permissions) throws AclNotFoundException, AceNotFoundException {
        final ObjectIdentity oi = new ObjectIdentityImpl(targetObj.getClass(), targetObj.getId());
        if (log.isDebugEnabled())
            log.debug("sid: "+ AclUtils.sidToString(sid) +" will loose permission: " + AclUtils.permissionsToString(permissions) +" over entity: " + AclUtils.objectIdentityToString(oi));
        MutableAcl acl = findAcl(oi);
        if (log.isDebugEnabled()){
            log.debug("child acl of entity before removal: ");
            log.debug(AclUtils.aclToString(acl));
        }
        // create new list here, so I dont create concurrent modification down below - there is no exception, but it does not seem safe
        List<AccessControlEntry> aces = new ArrayList<>(acl.getEntries());
//        Set<Integer> aceIndicesToRemove = findMatchingAceIndices(aces, sid, permissions);

//        if (aceIndicesToRemove.isEmpty()){
//            throw new AceNotFoundException("Cant remove permission for sid: " + sid + " on target: " + oi + ", bc no matching ace found");
//        }
                // todo have to do weird remove workaround again bc of hibernate set, see DirEntity
        // also I cannot use equals method in old release, see: https://github.com/vincemann/spring-rapid/issues/155
        // AccessControlEntry removed = aces.remove(aceIndexToRemove);

        // todo reduce overhead when updating to newer spring version
        Iterator<AccessControlEntry> aceIterator = aces.iterator();
        int removed = 0;
        int index = 0;
        while (aceIterator.hasNext()){
            AccessControlEntry ace = aceIterator.next();
            if(ace.getSid().equals(sid) &&
                    Arrays.stream(permissions).anyMatch(p -> p.equals(ace.getPermission()))){
//                aceIterator.remove();
                acl.deleteAce(index-removed);
                removed++;
            }
            index++;
        }
        if (removed != permissions.length && !ignoreNotFound)
            throw new AceNotFoundException("Cant remove permissions " + AclUtils.permissionsToString(permissions) + " for sid: " + AclUtils.sidToString(sid) + " on target: " + AclUtils.objectIdentityToString(oi) + ", bc not all matching aces found");

//        Iterator<AccessControlEntry> aceIterator = aces.iterator();
//        int index = 0;
//        while (aceIterator.hasNext()){
//            AccessControlEntry ace = aceIterator.next();
//            if (aceIndicesToRemove.contains(index)){
//                if (log.isDebugEnabled())
//                    log.debug("removing ace: " + ace);
//                aceIterator.remove();
//                break;
//            }
//            index++;
//        }

        // do weird stuff so update wont be ignored ...
//        deleteAclOfEntity(targetObj,deleteCascade);
//        MutableAcl updatedAcl = aclService.createAcl(oi);
//        for (AccessControlEntry ace : aces) {
//            updatedAcl.getEntries().add(ace);
//        }

        MutableAcl updated = aclService.updateAcl(acl);
        if (log.isDebugEnabled()){
            log.debug("updated acl ");
            log.debug(AclUtils.aclToString(updated));
        }
    }


//    protected Set<Integer> findMatchingAceIndices(List<AccessControlEntry> aces,Sid sid, Permission... permissions) throws AceNotFoundException {
//        Set<Integer> indices = new HashSet<>();
////        int[] index = {-1};
//        int index = 0;
//        // https://github.com/spring-projects/spring-security/issues/5401
////        Set<AccessControlEntry> result = aces.stream()
////                .peek(x -> index[0]++)
////                .filter(accessControlEntry -> {
////                    return getSidString(accessControlEntry.getSid()).equals(getSidString(sid)) &&
////                            accessControlEntry.getPermission().equals(permissions[0]);
////                }).collect(Collectors.toSet());
//        Iterator<AccessControlEntry> iterator = aces.iterator();
//        while (iterator.hasNext()){
//            AccessControlEntry ace = iterator.next();
//            if(getSidString(ace.getSid()).equals(getSidString(sid)) &&
//                    Arrays.stream(permissions).anyMatch(p -> p.equals(ace.getPermission()))){
//                        indices.add(index);
//            }
//            index += 1;
//        }
////        long matches = aces.stream()
////                .peek(x -> index[0]++)
////                .filter(accessControlEntry -> {
////                    boolean isMatching = getSidString(accessControlEntry.getSid()).equals(getSidString(sid)) &&
////                            Arrays.stream(permissions).anyMatch(p -> p.equals(accessControlEntry.getPermission()));
////                    if (isMatching) {
////                        indices.add(index[0]); // Add the position to the Set
////                    }
////                    return isMatching;
////                }).count();
////                .collect(Collectors.toSet());
////        if (result.isEmpty()){
////            return -1;
////        }else{
////            if (log.isDebugEnabled())
////                log.debug("Aces indices to remove: " + matches);
////            return position[0];
//        return indices;
//    }


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