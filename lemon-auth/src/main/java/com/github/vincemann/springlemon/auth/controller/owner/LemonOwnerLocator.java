package com.github.vincemann.springlemon.auth.controller.owner;

import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.domain.AuditingEntity;
import com.github.vincemann.springlemon.auth.service.UserService;

import com.github.vincemann.springrapid.core.controller.owner.OwnerLocator;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class LemonOwnerLocator implements OwnerLocator<AuditingEntity> {

    private UserService userService;

    public LemonOwnerLocator(UserService<?, ?, ?> userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class clazz) {
        return AuditingEntity.class.isAssignableFrom(clazz);
    }

    //@LogInteraction
    @Override
    public Optional<String> find(AuditingEntity entity) {
        try {
            Optional<AbstractUser> byId = userService.findById(entity.getCreatedById());
            return byId.map(AbstractUser::getEmail);
        } catch (BadEntityException e) {
            log.warn("Could not find Owner by createdById",e);
            return Optional.empty();
        }
    }
}
