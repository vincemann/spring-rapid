package com.github.vincemann.springrapid.acl.service;

import com.github.vincemann.aoplog.Severity;
import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import org.springframework.security.acls.model.Permission;

import java.io.Serializable;
import java.util.List;

@LogInteraction(Severity.TRACE)
public interface AclPermissionService {

    public void savePermissionForRoleOverEntity(IdentifiableEntity<?> entity, String role, Permission permission);
    public void deletePermissionForRoleOverEntity(IdentifiableEntity<?> entity, String role, Permission permission) throws AclNotFoundException, AceNotFoundException;

    public void savePermissionForAuthenticatedOverEntity(IdentifiableEntity<?> entity, Permission permission);
    public void deletePermissionForAuthenticatedOverEntity(IdentifiableEntity<?> entity, Permission permission) throws AclNotFoundException, AceNotFoundException;

    public void savePermissionForUserOverEntity(String user, IdentifiableEntity<?> entity, Permission permission);
    public void deletePermissionForUserOverEntity(String user, IdentifiableEntity<?> entity, Permission permission) throws AclNotFoundException, AceNotFoundException;

    public void deleteAclOfEntity(IdentifiableEntity<?> entity, boolean deleteCascade);

    void deletePermissionForUserOverEntity(String user, IdentifiableEntity<?> entity, Permission permission, Boolean deleteCascade) throws AclNotFoundException, AceNotFoundException;

    public void deleteAclOfEntity(Class<? extends IdentifiableEntity> clazz, Serializable id, boolean deleteCascade);

    void inheritAces(IdentifiableEntity<?> parent, List<AclInheritanceInfo> infos) throws AclNotFoundException;

    void updateEntriesInheriting(boolean value, IdentifiableEntity<?> child, IdentifiableEntity<?> parent) throws AclNotFoundException;

    void inheritAces(IdentifiableEntity<?> child, IdentifiableEntity<?> parent, AceFilter aceFilter) throws AclNotFoundException;
}
