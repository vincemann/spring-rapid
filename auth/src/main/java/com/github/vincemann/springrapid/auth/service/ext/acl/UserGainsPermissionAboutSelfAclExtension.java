package com.github.vincemann.springrapid.auth.service.ext.acl;

import com.github.vincemann.springrapid.acl.service.ext.acl.AclExtension;
import com.github.vincemann.springrapid.auth.model.AuthenticatingEntity;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.GenericCrudServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.security.acls.model.Permission;

import java.io.Serializable;

public class UserGainsPermissionAboutSelfAclExtension
        <E extends IdentifiableEntity<Id> & AuthenticatingEntity<Id>,Id extends Serializable>
        extends AclExtension<CrudService<E,Id>>
        implements GenericCrudServiceExtension<CrudService<E,Id>,E,Id>
{
    private Permission permission;

    public UserGainsPermissionAboutSelfAclExtension(Permission permission) {
        this.permission = permission;
    }

    @Override
    public E create(E entity) throws BadEntityException {
        E saved = getNext().create(entity);
        getRapidAclService().savePermissionForUserOverEntity(saved.getAuthenticationName(),saved, this.permission);
        return saved;
    }
}