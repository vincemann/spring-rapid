package com.github.vincemann.springrapid.acl.service;

import com.github.vincemann.aoplog.Severity;
import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import org.springframework.security.acls.model.Permission;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface RapidAclService {

    void grantRolePermissionForEntity(String role, IdentifiableEntity<?> entity, Permission... permissions);

    void revokeRolesPermissionForEntity(String role, IdentifiableEntity<?> entity, Permission... permissions) throws AclNotFoundException, AceNotFoundException;

    void revokeRolesPermissionForEntityIfGranted(String role, IdentifiableEntity<?> entity, Permission... permissions) throws AclNotFoundException, AceNotFoundException;

    void grantAuthenticatedPermissionForEntity(IdentifiableEntity<?> entity, Permission... permissions);

    void revokeAuthenticatedPermissionForEntityIfGranted(IdentifiableEntity<?> entity, Permission... permissions) throws AclNotFoundException, AceNotFoundException;

    void grantUserPermissionForEntity(String user, IdentifiableEntity<?> entity, Permission... permissions);

    public void deleteAclOfEntity(IdentifiableEntity<?> entity, boolean deleteCascade);

    void revokeUsersPermissionForEntity(String user, IdentifiableEntity<?> entity, Permission... permissions) throws AclNotFoundException, AceNotFoundException;

    void revokeUsersPermissionForEntityIfGranted(String user, IdentifiableEntity<?> entity, Permission... permissions) throws AclNotFoundException, AceNotFoundException;

    public void deleteAclOfEntity(Class<? extends IdentifiableEntity> clazz, Serializable id, boolean deleteCascade);

    void inheritAces(IdentifiableEntity<?> parent, List<AclCascadeInfo> infos) throws AclNotFoundException;

    void removeAces(IdentifiableEntity<?> parent, List<AclCascadeInfo> infos) throws AclNotFoundException;

    void inheritAces(Collection<? extends IdentifiableEntity<?>> parents, List<AclCascadeInfo> infos) throws AclNotFoundException;

    void updateEntriesInheriting(boolean value, IdentifiableEntity<?> child, IdentifiableEntity<?> parent) throws AclNotFoundException;

    void copyParentAces(IdentifiableEntity<?> child, IdentifiableEntity<?> parent, AceFilter aceFilter) throws AclNotFoundException;

    int removeAces(IdentifiableEntity<?> target, AceFilter aceFilter) throws AclNotFoundException;
}
