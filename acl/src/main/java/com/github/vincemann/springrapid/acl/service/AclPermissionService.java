package com.github.vincemann.springrapid.acl.service;

import com.github.vincemann.aoplog.Severity;
import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import org.springframework.security.acls.model.Permission;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@LogInteraction(Severity.TRACE)
public interface AclPermissionService extends AopLoggable {

    void savePermissionForRoleOverEntity(IdentifiableEntity<?> entity, String role, Permission... permissions);

    void deletePermissionForRoleOverEntity(IdentifiableEntity<?> entity, String role, Permission... permissions) throws AclNotFoundException, AceNotFoundException;

    void savePermissionForAuthenticatedOverEntity(IdentifiableEntity<?> entity, Permission... permissions);

    void deletePermissionForAuthenticatedOverEntity(IdentifiableEntity<?> entity, Permission... permissions) throws AclNotFoundException, AceNotFoundException;

    void savePermissionForUserOverEntity(String user, IdentifiableEntity<?> entity, Permission... permissions);

    public void deleteAclOfEntity(IdentifiableEntity<?> entity, boolean deleteCascade);

    void deletePermissionForUserOverEntity(String user, IdentifiableEntity<?> entity, Permission... permissions) throws AclNotFoundException, AceNotFoundException;

    public void deleteAclOfEntity(Class<? extends IdentifiableEntity> clazz, Serializable id, boolean deleteCascade);

    void inheritAces(IdentifiableEntity<?> parent, List<AclInheritanceInfo> infos) throws AclNotFoundException;

    void inheritAces(Collection<? extends IdentifiableEntity<?>> parents, List<AclInheritanceInfo> infos) throws AclNotFoundException;

    void updateEntriesInheriting(boolean value, IdentifiableEntity<?> child, IdentifiableEntity<?> parent) throws AclNotFoundException;

    void copyParentAces(IdentifiableEntity<?> child, IdentifiableEntity<?> parent, AceFilter aceFilter) throws AclNotFoundException;
}
