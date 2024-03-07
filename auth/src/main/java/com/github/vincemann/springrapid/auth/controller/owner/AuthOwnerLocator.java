package com.github.vincemann.springrapid.auth.controller.owner;

import com.github.vincemann.springrapid.core.Root;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.core.model.audit.AuditingEntity;
import com.github.vincemann.springrapid.auth.service.UserService;


import com.github.vincemann.springrapid.core.controller.owner.OwnerLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.Optional;

/**
 * Find owner (is always a user) of and entity.
 */
@Order(Ordered.LOWEST_PRECEDENCE)
public class AuthOwnerLocator implements OwnerLocator<AuditingEntity> {

    private UserService userService;

    @Override
    public boolean supports(Class clazz) {
        return AuditingEntity.class.isAssignableFrom(clazz);
    }

    @Override
    public Optional<String> find(AuditingEntity entity) {
        if (entity.getCreatedById() == null) {
            return Optional.empty();
        }
        Optional<? extends AbstractUser> byId = userService.findById(entity.getCreatedById());
        return byId.map(AbstractUser::getContactInformation);
    }

    @Autowired
    @Root
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
