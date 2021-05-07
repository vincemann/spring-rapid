package com.github.vincemann.springrapid.auth.service.extension;

import com.github.vincemann.springrapid.acl.model.AclParentAware;
import com.github.vincemann.springrapid.acl.service.extensions.acl.AbstractAclExtension;
import com.github.vincemann.springrapid.auth.domain.AuthenticatingEntity;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.GenericCrudServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;

import java.io.Serializable;

public class UserHasPermissionAboutSelfAclExtension
        <E extends IdentifiableEntity<Id> & AuthenticatingEntity<Id>,Id extends Serializable>
        extends AbstractAclExtension<CrudService<E,Id>>
        implements GenericCrudServiceExtension<CrudService<E,Id>,E,Id>
{
    private Permission permission;

    public UserHasPermissionAboutSelfAclExtension(Permission permission) {
        this.permission = permission;
    }

    @Override
    public E save(E entity) throws BadEntityException {
        E saved = getNext().save(entity);
        getAclPermissionService().savePermissionForUserOverEntity(saved.getAuthenticationString(),saved, this.permission);
        return saved;
    }
}
