package com.github.vincemann.springrapid.acl.service;

import com.github.vincemann.aoplog.Severity;
import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import org.springframework.security.acls.model.Permission;

import java.io.Serializable;

@LogInteraction(Severity.TRACE)
public interface AclPermissionService {

    public void savePermissionForRoleOverEntity(IdentifiableEntity<?> entity, String role, Permission permission);
    public void deletePermissionForRoleOverEntity(IdentifiableEntity<?> entity, String role, Permission permission) throws AclNotFoundException, AceNotFoundException;

    public void savePermissionForAuthenticatedOverEntity(IdentifiableEntity<?> entity, Permission permission);
    public void deletePermissionForAuthenticatedOverEntity(IdentifiableEntity<?> entity, Permission permission) throws AclNotFoundException, AceNotFoundException;

    public void savePermissionForUserOverEntity(String user, IdentifiableEntity<?> entity, Permission permission);
    public void deletePermissionForUserOverEntity(String user, IdentifiableEntity<?> entity, Permission permission) throws AclNotFoundException, AceNotFoundException;

    public void deleteAclOfEntity(IdentifiableEntity<?> entity, boolean deleteCascade);

    @LogInteraction(Severity.TRACE)
    void deletePermissionForUserOverEntity(String user, IdentifiableEntity<?> entity, Permission permission, Boolean deleteCascade) throws AclNotFoundException, AceNotFoundException;

    public void deleteAclOfEntity(Class<? extends IdentifiableEntity> clazz, Serializable id, boolean deleteCascade);



    /**
     * see {@link this#inheritUserPermissionEntriesOfParent(IdentifiableEntity, IdentifiableEntity, String, Permission...)} but for all permissions.
     */
    public void inheritPermissionEntriesOfParent(IdentifiableEntity<?> targetObj, IdentifiableEntity<?> parent) throws AclNotFoundException;
    /**
     * targetObj will get all acl-entries that parent has, that match permission.
     * i.E. If some user has read and create permission for parent and permissions is [READ],
     * Then the entry [User.class,42] -> R will propagate to targetObj, but [User.class,42] -> C  wont.
     * So user will have read access for target obj as well but no create permission.
     */
    void inheritPermissionEntriesOfParent(IdentifiableEntity<?> targetObj, IdentifiableEntity<?> parent, Permission... permissions) throws AclNotFoundException;

    /**
     * see {@link this#inheritUserPermissionEntriesOfParent(IdentifiableEntity, IdentifiableEntity, String, Permission...)} but for all permissions.
     * @param targetObj
     * @param parent
     * @param user
     * @throws AclNotFoundException
     */
    void inheritUserEntriesOfParent(IdentifiableEntity<?> targetObj, IdentifiableEntity<?> parent, String user) throws AclNotFoundException;

    /**
     * targetObj will get only acl-entries, that regard user x and are matching permissions.
     * i.E. If user a has read and create permission for parent.
     * User b has read and create permission for parent.
     * and permissions is set to [READ] and user is set to user a
     * then the entry user b -> R will propagate to targetObj.
     * entry user a -> R, user a -> C and user b -> C wont propagate.
     */
    void inheritUserPermissionEntriesOfParent(IdentifiableEntity<?> targetObj, IdentifiableEntity<?> parent, String user, Permission... permissions) throws AclNotFoundException;
}
