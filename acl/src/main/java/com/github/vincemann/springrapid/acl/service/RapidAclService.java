package com.github.vincemann.springrapid.acl.service;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Permission;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public interface RapidAclService {

    void grantRolePermissionForEntity(String role, IdentifiableEntity<?> entity, Permission... permissions);

    void revokeRolesPermissionForEntity(String role, IdentifiableEntity<?> entity, boolean ignoreNotFound, Permission... permissions) throws AclNotFoundException, AceNotFoundException;

    void grantAuthenticatedPermissionForEntity(IdentifiableEntity<?> entity, Permission... permissions);

    void revokeAuthenticatedPermissionForEntity(IdentifiableEntity<?> entity,boolean ignoreNotFound, Permission... permissions) throws AclNotFoundException, AceNotFoundException;

    void grantUserPermissionForEntity(String user, IdentifiableEntity<?> entity, Permission... permissions);

    public void deleteAclOfEntity(IdentifiableEntity<?> entity, boolean deleteCascade);

    void revokeUsersPermissionForEntity(String user, IdentifiableEntity<?> entity, boolean ignoreNotFound, Permission... permissions) throws AclNotFoundException, AceNotFoundException;


    public void deleteAclOfEntity(Class<? extends IdentifiableEntity> clazz, Serializable id, boolean deleteCascade);

    void inheritAces(IdentifiableEntity<?> parent, List<AclCascadeInfo> infos) throws AclNotFoundException;

    void removeAces(IdentifiableEntity<?> parent, List<AclCascadeInfo> infos) throws AclNotFoundException;

    void inheritAces(Collection<? extends IdentifiableEntity<?>> parents, List<AclCascadeInfo> infos) throws AclNotFoundException;

    void updateEntriesInheriting(boolean value, IdentifiableEntity<?> child, IdentifiableEntity<?> parent) throws AclNotFoundException;

    void copyParentAces(IdentifiableEntity<?> child, IdentifiableEntity<?> parent, Predicate<AccessControlEntry> filter) throws AclNotFoundException;
    void copyParentAces(IdentifiableEntity<?> child, IdentifiableEntity<?> parent, AceFilterMapping... filterMappings) throws AclNotFoundException;

    int removeAces(IdentifiableEntity<?> target, Predicate<AccessControlEntry> aceFilter) throws AclNotFoundException;
}
