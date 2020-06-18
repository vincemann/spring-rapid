package com.github.vincemann.springlemon.auth.controller.owner;

import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.domain.LemonEntity;
import com.github.vincemann.springlemon.auth.service.LemonService;

import com.github.vincemann.springrapid.core.controller.owner.OwnerLocator;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class LemonOwnerLocator implements OwnerLocator<LemonEntity> {

    private LemonService lemonService;

    public LemonOwnerLocator(LemonService<?, ?, ?> lemonService) {
        this.lemonService = lemonService;
    }

    @Override
    public boolean supports(Class clazz) {
        return LemonEntity.class.isAssignableFrom(clazz);
    }

    //@LogInteraction
    @Override
    public Optional<String> find(LemonEntity entity) {
        try {
            Optional<AbstractUser> byId = lemonService.findById(entity.getCreatedById());
            return byId.map(AbstractUser::getEmail);
        } catch (BadEntityException e) {
            log.warn("Could not find Owner by createdById",e);
            return Optional.empty();
        }
    }
}
