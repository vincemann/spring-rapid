package com.github.vincemann.springrapid.acldemo.service.ext.acl;

import com.github.vincemann.springrapid.acl.service.ext.acl.AclExtension;
import com.github.vincemann.springrapid.acldemo.model.User;
import com.github.vincemann.springrapid.acldemo.model.abs.UserAwareEntity;
import com.github.vincemann.springrapid.core.proxy.GenericCrudServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.transaction.annotation.Transactional;

/**
 * Propagates Admin Permission of entity over contained user obj
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserGainsAdminPermissionOnContainedUserOnCreate
        extends AclExtension<CrudService<UserAwareEntity,Long>>
                implements GenericCrudServiceExtension<CrudService<UserAwareEntity,Long>, UserAwareEntity,Long> {

    @Transactional
    @Override
    public UserAwareEntity create(UserAwareEntity entity) throws BadEntityException {
        UserAwareEntity saved = getNext().create(entity);
        String user = saved.getAuthenticationName();
        User containedUser = saved.getUser();
        rapidAclService.savePermissionForUserOverEntity(user,containedUser, BasePermission.ADMINISTRATION);
        return saved;
    }
}
