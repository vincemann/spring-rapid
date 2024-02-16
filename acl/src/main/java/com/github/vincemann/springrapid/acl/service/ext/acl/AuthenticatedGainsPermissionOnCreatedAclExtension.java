package com.github.vincemann.springrapid.acl.service.ext.acl;

import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.CrudServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.security.acls.model.Permission;

@EqualsAndHashCode(callSuper = true)
public class AuthenticatedGainsPermissionOnCreatedAclExtension extends AclExtension<CrudService>
        implements CrudServiceExtension<CrudService> {

    private Permission[] permissions;

    public AuthenticatedGainsPermissionOnCreatedAclExtension(Permission... permissions) {
        this.permissions= permissions;
    }

    @LogInteraction
    @Override
    public IdentifiableEntity create(IdentifiableEntity entity) throws BadEntityException {
        IdentifiableEntity saved = getNext().create(entity);
        rapidAclService.savePermissionForAuthenticatedOverEntity(saved, permissions);
        return saved;
    }

}