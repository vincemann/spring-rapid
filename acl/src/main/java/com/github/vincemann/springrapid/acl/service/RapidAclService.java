package com.github.vincemann.springrapid.acl.service;

import com.github.vincemann.springrapid.acl.IdAware;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Permission;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public interface RapidAclService {

    void grantRolePermissionForEntity(String role, IdAware<?> entity, Permission... permissions);

    void revokeRolesPermissionForEntity(String role, IdAware<?> entity, boolean ignoreNotFound, Permission... permissions) throws AclNotFoundException, AceNotFoundException;

    void grantAuthenticatedPermissionForEntity(IdAware<?> entity, Permission... permissions);

    void revokeAuthenticatedPermissionForEntity(IdAware<?> entity, boolean ignoreNotFound, Permission... permissions) throws AclNotFoundException, AceNotFoundException;

    void grantUserPermissionForEntity(String user, IdAware<?> entity, Permission... permissions);

    public void deleteAclOfEntity(IdAware<?> entity, boolean deleteCascade);

    void revokeUsersPermissionForEntity(String user, IdAware<?> entity, boolean ignoreNotFound, Permission... permissions) throws AclNotFoundException, AceNotFoundException;


    public void deleteAclOfEntity(Class<? extends IdAware> clazz, Serializable id, boolean deleteCascade);

    void inheritAces(IdAware<?> parent, List<AclCascadeInfo> infos) throws AclNotFoundException;

    void removeAces(IdAware<?> parent, List<AclCascadeInfo> infos) throws AclNotFoundException;

    void inheritAces(Collection<? extends IdAware<?>> parents, List<AclCascadeInfo> infos) throws AclNotFoundException;

    void updateEntriesInheriting(boolean value, IdAware<?> child, IdAware<?> parent) throws AclNotFoundException;

    void copyParentAces(IdAware<?> child, IdAware<?> parent, Predicate<AccessControlEntry> filter) throws AclNotFoundException;
    void copyParentAces(IdAware<?> child, IdAware<?> parent, AceFilterMapping... filterMappings) throws AclNotFoundException;

    int removeAces(IdAware<?> target, Predicate<AccessControlEntry> aceFilter) throws AclNotFoundException;
}
