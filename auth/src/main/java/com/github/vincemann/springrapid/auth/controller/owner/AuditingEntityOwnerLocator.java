package com.github.vincemann.springrapid.auth.controller.owner;

import com.github.vincemann.springrapid.auth.domain.AbstractUser;
import com.github.vincemann.springrapid.auth.domain.AuditingEntity;
import com.github.vincemann.springrapid.auth.service.UserService;


import com.github.vincemann.springrapid.core.controller.owner.OwnerLocator;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Slf4j
public class AuditingEntityOwnerLocator implements OwnerLocator<AuditingEntity> {

    private UserService userService;

    @Override
    public boolean supports(Class clazz) {
        return AuditingEntity.class.isAssignableFrom(clazz);
    }

    //@LogInteraction
    @Override
    public Optional<String> find(AuditingEntity entity) {
        try {
            if (entity.getCreatedById()==null){
                return Optional.empty();
            }
            Optional<AbstractUser> byId = userService.findById(entity.getCreatedById());
            return byId.map(AbstractUser::getEmail);
        } catch (BadEntityException e) {
            log.warn("Could not find Owner by createdById",e);
            return Optional.empty();
        }
    }

    @Autowired

    public void injectUserService(UserService userService) {
        this.userService = userService;
    }
}
