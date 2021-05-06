package com.github.vincemann.springrapid.acldemo.service.extensions;

import com.github.vincemann.springrapid.acl.service.extensions.AbstractAclExtension;
import com.github.vincemann.springrapid.acldemo.model.abs.UserAwareEntity;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.GenericCrudServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.transaction.annotation.Transactional;

/**
 * Propagates Admin Permission of entity over contained user obj
 */
@ServiceComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AuthenticatedHasFullPermissionAboutSavedContainedUserAclExtension
        extends AbstractAclExtension<CrudService<UserAwareEntity,Long>>
                implements GenericCrudServiceExtension<CrudService<UserAwareEntity,Long>, UserAwareEntity,Long> {

    @Transactional
    @Override
    public UserAwareEntity save(UserAwareEntity entity) throws BadEntityException {
        UserAwareEntity saved = getNext().save(entity);
        savePermissionForAuthenticatedOverEntity(saved.getUser(), BasePermission.ADMINISTRATION);
        return saved;
    }
}
