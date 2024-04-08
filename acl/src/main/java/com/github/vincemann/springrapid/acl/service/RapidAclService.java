package com.github.vincemann.springrapid.acl.service;

import com.github.vincemann.springrapid.auth.IdAware;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.Permission;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public interface RapidAclService {

    void grantRolePermissionForEntity(String role, IdAware<?> entity, Permission... permissions);

    void revokeRolesPermissionForEntity(String role, IdAware<?> entity, boolean ignoreNotFound, Permission... permissions) throws NotFoundException;

    void grantAuthenticatedPermissionForEntity(IdAware<?> entity, Permission... permissions);

    void revokeAuthenticatedPermissionForEntity(IdAware<?> entity, boolean ignoreNotFound, Permission... permissions) throws NotFoundException;

    void grantUserPermissionForEntity(String user, IdAware<?> entity, Permission... permissions);

    public void deleteAclOfEntity(IdAware<?> entity, boolean deleteCascade);

    void revokeUsersPermissionForEntity(String user, IdAware<?> entity, boolean ignoreNotFound, Permission... permissions) throws NotFoundException;


    public void deleteAclOfEntity(Class<? extends IdAware> clazz, Serializable id, boolean deleteCascade);

    void inheritAces(IdAware<?> parent, List<AclCascadeInfo> infos) throws NotFoundException;

    void removeAces(IdAware<?> parent, List<AclCascadeInfo> infos) throws NotFoundException;

    void inheritAces(Collection<? extends IdAware<?>> parents, List<AclCascadeInfo> infos) throws NotFoundException;

    void updateEntriesInheriting(boolean value, IdAware<?> child, IdAware<?> parent) throws NotFoundException;

    void copyParentAces(IdAware<?> child, IdAware<?> parent, Predicate<AccessControlEntry> filter) throws NotFoundException;
    void copyParentAces(IdAware<?> child, IdAware<?> parent, AceFilterMapping... filterMappings) throws NotFoundException;

    int removeAces(IdAware<?> target, Predicate<AccessControlEntry> aceFilter) throws NotFoundException;
}
