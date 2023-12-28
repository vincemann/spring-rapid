package com.github.vincemann.springrapid.auth.controller.owner;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.core.model.AuditingEntity;
import com.github.vincemann.springrapid.auth.service.UserService;


import com.github.vincemann.springrapid.core.controller.owner.OwnerLocator;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.io.Serializable;
import java.util.Optional;

/**
 * Find owner (is always a user) of and entity.
 */
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE)
public abstract class AbstractEntitysUserOwnerLocator<Id extends Serializable> implements OwnerLocator<AuditingEntity<Id>> {

    private UserService userService;

    @Override
    public boolean supports(Class clazz) {
        return AuditingEntity.class.isAssignableFrom(clazz);
    }

    //@LogInteraction
    @Override
    public Optional<String> find(AuditingEntity<Id> entity) {
        if (entity.getCreatedById() == null) {
            return Optional.empty();
        }
        Optional<? extends AbstractUser<Id>> byId = userService.findById(entity.getCreatedById());
        return byId.map(AbstractUser::getContactInformation);
    }

    @Autowired
    public void injectUserService(UserService userService) {
        this.userService = userService;
    }
}
