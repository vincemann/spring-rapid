package com.github.vincemann.springrapid.acl.service;

import com.github.vincemann.aoplog.Severity;
import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.annotation.LogInteraction;
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
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * API for managing acl data.
 */
@Service
@Transactional
@Slf4j
public class RapidPermissionService implements AclPermissionService , AopLoggable {

    private MutableAclService aclService;
    private RapidSecurityContext<RapidAuthenticatedPrincipal> securityContext;
    private PermissionStringConverter permissionStringConverter;

    @Autowired
    public RapidPermissionService(MutableAclService aclService) {
        this.aclService = aclService;
    }

    @LogInteraction(Severity.TRACE)
    @Override
    public void savePermissionForRoleOverEntity(IdentifiableEntity<?> entity, String role, Permission permission) {
        securityContext.runAsAdmin(() ->{
            final Sid sid = new GrantedAuthoritySid(role);
            addPermissionForSid(entity, permission, sid);
        });
    }

    @LogInteraction(Severity.TRACE)
    @Override
    public void deletePermissionForRoleOverEntity(IdentifiableEntity<?> entity, String role, Permission permission) throws AclNotFoundException, AceNotFoundException {
        final Sid sid = new GrantedAuthoritySid(role);
        deletePermissionForSid(entity,permission,sid);
    }


    @LogInteraction(Severity.TRACE)
    @Override
    public void savePermissionForAuthenticatedOverEntity(IdentifiableEntity<?> entity, Permission permission) {
        String authenticatedName = findAuthenticatedName();
        savePermissionForUserOverEntity(authenticatedName,entity,permission);
    }

    @LogInteraction(Severity.TRACE)
    @Override
    public void deletePermissionForAuthenticatedOverEntity(IdentifiableEntity<?> entity, Permission permission) throws AclNotFoundException, AceNotFoundException {
        String authenticatedName = findAuthenticatedName();
        deletePermissionForUserOverEntity(authenticatedName,entity,permission);
    }

    @LogInteraction(Severity.TRACE)
    @Override
    public void savePermissionForUserOverEntity(String user, IdentifiableEntity<?> entity, Permission permission) {
        securityContext.runWithName(user,() ->{
            final Sid sid = new PrincipalSid(user);
            addPermissionForSid(entity, permission, sid);
        });
    }

    @LogInteraction(Severity.TRACE)
    @Override
    public void deletePermissionForUserOverEntity(String user, IdentifiableEntity<?> entity, Permission permission) throws AclNotFoundException, AceNotFoundException {
        final Sid sid = new PrincipalSid(user);
        deletePermissionForSid(entity,permission,sid);
    }

    @LogInteraction(Severity.TRACE)
    @Override
    public void deleteAclOfEntity(Class<? extends IdentifiableEntity> clazz, Serializable id, boolean deleteCascade) {
        ObjectIdentity oi = new ObjectIdentityImpl(clazz, id);
        aclService.deleteAcl(oi,deleteCascade);
    }

    @LogInteraction(Severity.TRACE)
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
    /**
     *
     * @param targetObj   inherits all permissions from parent
     * @param parent
     * @throws AclNotFoundException
     */
    @LogInteraction(Severity.TRACE)
    @Override
    public void inheritPermissions(IdentifiableEntity<?> targetObj,IdentifiableEntity<?> parent) throws AclNotFoundException {
        final ObjectIdentity childOi = new ObjectIdentityImpl(targetObj.getClass(), targetObj.getId());
        final ObjectIdentity parentOi = new ObjectIdentityImpl(parent.getClass(), parent.getId());
//        log.debug("Entity: " + targetObj + " will inherit permissions from: " + parent);
        MutableAcl childAcl = findOrCreateAcl(childOi);
        MutableAcl parentAcl  = findAcl(parentOi);

        childAcl.setEntriesInheriting(true);
        childAcl.setParent(parentAcl);

        log.trace("Parent Acl: " + parentAcl);
        log.trace("Child Acl before Update: " + childAcl);
        MutableAcl updated = aclService.updateAcl(childAcl);
        log.trace("Updated Child Acl: " + updated);
    }



    protected void addPermissionForSid(IdentifiableEntity<?> targetObj, Permission permission, Sid sid) {
        log.debug("sid: "+ sid +" will gain permission: " + permissionStringConverter.convert(permission) +" over entity: " + targetObj);
        final ObjectIdentity oi = new ObjectIdentityImpl(targetObj.getClass(), targetObj.getId());

        MutableAcl acl = findOrCreateAcl(oi);
        log.trace("acl of entity before adding" + acl);
        acl.insertAce(acl.getEntries().size(), permission, sid, true);
        MutableAcl updated = aclService.updateAcl(acl);
        log.trace("updated acl: " + updated);
    }

    protected void deletePermissionForSid(IdentifiableEntity<?> targetObj, Permission permission, Sid sid) throws AclNotFoundException, AceNotFoundException {
        log.debug("sid: "+ sid +" will loose permission: " + permissionStringConverter.convert(permission) +" over entity: " + targetObj);
        final ObjectIdentity oi = new ObjectIdentityImpl(targetObj.getClass(), targetObj.getId());
        MutableAcl acl = findAcl(oi);
        log.trace("acl of entity before removal" + acl);
        List<AccessControlEntry> aces = acl.getEntries();
        int aceIndexToRemove = findMatchingAceIndex(aces, permission, sid);

        if (aceIndexToRemove == -1){
            throw new AceNotFoundException("Cant remove permission for sid: " + sid + " on target: " + oi + ", bc no matching ace found");
        }

        // todo have to do weird remove workaround again bc of hibernate set, see DirEntity
        // AccessControlEntry removed = aces.remove(aceIndexToRemove);
        Iterator<AccessControlEntry> aceIterator = aces.iterator();
        int index = 0;
        while (aceIterator.hasNext()){
            aceIterator.next();
            if (index==aceIndexToRemove){
                aceIterator.remove();
                break;
            }
            index++;
        }

        // do weird stuff so update wont be ignored ...
        deleteAclOfEntity(targetObj,false);
        MutableAcl updatedAcl = aclService.createAcl(oi);
        for (AccessControlEntry ace : aces) {
            updatedAcl.getEntries().add(ace);
        }
        MutableAcl updated = aclService.updateAcl(updatedAcl);
        log.trace("updated acl: " + updated);
    }

    // todo reduce overhead when updating to newer spring version
    protected int findMatchingAceIndex(List<AccessControlEntry> aces, Permission permission, Sid sid) throws AceNotFoundException {
        int[] position = {-1};
        // https://github.com/spring-projects/spring-security/issues/5401
        Optional<AccessControlEntry> ace = aces.stream()
                .peek(x -> position[0]++)
                .filter(accessControlEntry -> {
                    return findSidString(accessControlEntry.getSid()).equals(findSidString(sid)) &&
                            accessControlEntry.getPermission().equals(permission);
                }).findFirst();
        if (ace.isEmpty()){
            return -1;
        }else{
            log.debug("Ace to remove: " + ace.get());
            return position[0];
        }
    }

    // https://github.com/spring-projects/spring-security/issues/5401
    protected String findSidString(Sid sid){
        if (sid instanceof GrantedAuthoritySid){
            return ((GrantedAuthoritySid) sid).getGrantedAuthority();
        }else if (sid instanceof PrincipalSid){
            return ((PrincipalSid) sid).getPrincipal();
        }else {
            throw new IllegalArgumentException("Unknown Sid type: " + sid);
        }
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