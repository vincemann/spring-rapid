package com.github.vincemann.springrapid.acl.service;

import com.github.vincemann.aoplog.Severity;
import com.github.vincemann.aoplog.api.LogInteraction;
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

    public void inheritPermissions(IdentifiableEntity<?> targetObj,IdentifiableEntity<?> parent) throws AclNotFoundException;
    public void deleteAclOfEntity(IdentifiableEntity<?> entity, boolean deleteCascade);
    public void deleteAclOfEntity(Class<? extends IdentifiableEntity> clazz, Serializable id, boolean deleteCascade);
}
