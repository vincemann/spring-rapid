package com.github.vincemann.springrapid.acl.service;

import com.github.vincemann.springrapid.core.model.IdAwareEntity;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Permission;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public interface RapidAclService {

    void grantRolePermissionForEntity(String role, IdAwareEntity<?> entity, Permission... permissions);

    void revokeRolesPermissionForEntity(String role, IdAwareEntity<?> entity, boolean ignoreNotFound, Permission... permissions) throws AclNotFoundException, AceNotFoundException;

    void grantAuthenticatedPermissionForEntity(IdAwareEntity<?> entity, Permission... permissions);

    void revokeAuthenticatedPermissionForEntity(IdAwareEntity<?> entity, boolean ignoreNotFound, Permission... permissions) throws AclNotFoundException, AceNotFoundException;

    void grantUserPermissionForEntity(String user, IdAwareEntity<?> entity, Permission... permissions);

    public void deleteAclOfEntity(IdAwareEntity<?> entity, boolean deleteCascade);

    void revokeUsersPermissionForEntity(String user, IdAwareEntity<?> entity, boolean ignoreNotFound, Permission... permissions) throws AclNotFoundException, AceNotFoundException;


    public void deleteAclOfEntity(Class<? extends IdAwareEntity> clazz, Serializable id, boolean deleteCascade);

    void inheritAces(IdAwareEntity<?> parent, List<AclCascadeInfo> infos) throws AclNotFoundException;

    void removeAces(IdAwareEntity<?> parent, List<AclCascadeInfo> infos) throws AclNotFoundException;

    void inheritAces(Collection<? extends IdAwareEntity<?>> parents, List<AclCascadeInfo> infos) throws AclNotFoundException;

    void updateEntriesInheriting(boolean value, IdAwareEntity<?> child, IdAwareEntity<?> parent) throws AclNotFoundException;

    void copyParentAces(IdAwareEntity<?> child, IdAwareEntity<?> parent, Predicate<AccessControlEntry> filter) throws AclNotFoundException;
    void copyParentAces(IdAwareEntity<?> child, IdAwareEntity<?> parent, AceFilterMapping... filterMappings) throws AclNotFoundException;

    int removeAces(IdAwareEntity<?> target, Predicate<AccessControlEntry> aceFilter) throws AclNotFoundException;
}
