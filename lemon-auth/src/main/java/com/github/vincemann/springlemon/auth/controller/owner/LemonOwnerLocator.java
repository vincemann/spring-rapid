package com.github.vincemann.springlemon.auth.controller.owner;

import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.domain.AuditingEntity;
import com.github.vincemann.springlemon.auth.service.UserService;

import com.github.vincemann.springrapid.acl.proxy.Unsecured;
import com.github.vincemann.springrapid.core.controller.owner.OwnerLocator;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Slf4j
public class LemonOwnerLocator implements OwnerLocator<AuditingEntity> {

    private UserService unsecuredUserService;

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
            Optional<AbstractUser> byId = unsecuredUserService.findById(entity.getCreatedById());
            return byId.map(AbstractUser::getEmail);
        } catch (BadEntityException e) {
            log.warn("Could not find Owner by createdById",e);
            return Optional.empty();
        }
    }

    @Autowired
    @Unsecured
    public void injectUnsecuredUserService(UserService userService) {
        this.unsecuredUserService = userService;
    }
}
