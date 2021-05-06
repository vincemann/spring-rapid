package com.github.vincemann.springrapid.acl.service;

import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.LogException;
import com.github.vincemann.aoplog.api.LogInteraction;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
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

/**
 * API for managing acl data.
 */
@Service
@Transactional
//todo test
@Slf4j
//@LogException
public class RapidPermissionService implements AclPermissionService , AopLoggable {

    private MutableAclService aclService;
//    private RapidSecurityContext<RapidAuthenticatedPrincipal> securityContext;

    @Autowired
    public RapidPermissionService(MutableAclService aclService) {
        this.aclService = aclService;
    }

    @Override
    public void savePermissionForRoleOverEntity(IdentifiableEntity<?> entity, String role, Permission permission) {
//        securityContext.runAsAdmin(() ->
        final Sid sid = new GrantedAuthoritySid(role);
        addPermissionForSid(entity, permission, sid);
    }

    @Override
    public void deletePermissionForRoleOverEntity(IdentifiableEntity<?> entity, String role, Permission permission) {

    }

    @Override
    public void savePermissionForAuthenticatedOverEntity(IdentifiableEntity<?> entity, Permission permission) {
        String authenticatedName = findAuthenticatedName();
        savePermissionForUserOverEntity(authenticatedName,entity,permission);
    }

    @Override
    public void deletePermissionForAuthenticatedOverEntity(IdentifiableEntity<?> entity, Permission permission) {

    }

    @Override
    public void savePermissionForUserOverEntity(String user, IdentifiableEntity<?> entity, Permission permission) {
//        securityContext.runWithName(user,() ->
        final Sid sid = new PrincipalSid(user);
        addPermissionForSid(entity, permission, sid);
    }

    @Override
    public void deletePermissionForUserOverEntity(String user, IdentifiableEntity<?> entity, Permission permission) {
        final ObjectIdentity oi = new ObjectIdentityImpl(entity.getClass(), entity.getId());
        try {
            childAcl = (MutableAcl) aclService.readAclById(childOi);
        } catch (final NotFoundException nfe) {
            childAcl = aclService.createAcl(childOi);
        }
    }


    public String findAuthenticatedName(){
        String name = RapidSecurityContext.getName();
        //Nicht auslagern. MutableAclService macht das intern auch so -> use @MockUser(username="testUser") in tests
        if(name==null){
            throw new AccessDeniedException("Authentication required");
        }
        return name;
    }


    protected void deleteAcl(Serializable id, Class entityClass){
        log.debug("deleting acl for entity with id: " + id + " and class: " + entityClass);
        //delete acl as well
        ObjectIdentity oi = new ObjectIdentityImpl(entityClass, id);
        log.debug("ObjectIdentity getting deleted: " + oi);
        //todo delete children ist nur richtig wenn ich wirklich one to n habe mit Delete Cascade!
        getMutableAclService().deleteAcl(oi,deleteCascade);
        log.debug("Acl successfully deleted");
    }

    /**
     *
     * @param targetObj   inherits all permissions from parent
     * @param parent
     * @throws AclNotFoundException
     */
    @LogInteraction
    @Override
    public void inheritPermissions(IdentifiableEntity<?> targetObj,IdentifiableEntity<? extends Serializable> parent) throws AclNotFoundException {
        final ObjectIdentity childOi = new ObjectIdentityImpl(targetObj.getClass(), targetObj.getId());
        final ObjectIdentity parentOi = new ObjectIdentityImpl(parent.getClass(), parent.getId());
//        log.debug("Entity: " + targetObj + " will inherit permissions from: " + parent);
        MutableAcl childAcl = null;
        MutableAcl parentAcl =null;

        try {
            childAcl = (MutableAcl) aclService.readAclById(childOi);
        } catch (final NotFoundException nfe) {
            childAcl = aclService.createAcl(childOi);
        }
        try {
            parentAcl = (MutableAcl) aclService.readAclById(parentOi);
        } catch (final NotFoundException nfe) {
            throw new AclNotFoundException("Acl not found for parent: " + parentOi);
        }

        childAcl.setEntriesInheriting(true);
        childAcl.setParent(parentAcl);

        log.trace("Parent Acl: " + parentAcl);
        log.trace("Child Acl before Update: " + childAcl);
        MutableAcl updated = aclService.updateAcl(childAcl);
        log.trace("Updated Child Acl: " + updated);
    }


    protected void addPermissionForSid(IdentifiableEntity<?> targetObj, Permission permission, Sid sid) {
//        log.debug("sid: "+ sid +" will gain permission: " + PermissionUtils.toString(permission) +" over entity: " + targetObj);
        final ObjectIdentity oi = new ObjectIdentityImpl(targetObj.getClass(), targetObj.getId());

        MutableAcl acl = null;
        try {
            acl = (MutableAcl) aclService.readAclById(oi);
        } catch (final NotFoundException nfe) {
            acl = aclService.createAcl(oi);
        }
        log.trace("old acl of entity " + acl);
        acl.insertAce(acl.getEntries().size(), permission, sid, true);
        MutableAcl updated = aclService.updateAcl(acl);
        log.trace("updated acl: " + updated);
    }



    //    /**
//     *
//     * @param targetObj
//     * @param permission
//     * @param username      username of user, that gains permission over target object
//     */
//    @LogInteraction
//    public void addPermissionForUserOver(IdentifiableEntity<?> targetObj, Permission permission, String username) {
//        final Sid sid = new PrincipalSid(username);
//        addPermissionForSid(targetObj, permission, sid);
//    }

//    /**
//     *
//     * @param targetObj
//     * @param permission
//     * @param authority     the authority that gains the given permission over the target obj
//     */
//    @LogInteraction
//    public void addPermissionForAuthorityOver(IdentifiableEntity<?> targetObj, Permission permission, String authority) {
//        final Sid sid = new GrantedAuthoritySid(authority);
//        addPermissionForSid(targetObj, permission, sid);
//    }

}