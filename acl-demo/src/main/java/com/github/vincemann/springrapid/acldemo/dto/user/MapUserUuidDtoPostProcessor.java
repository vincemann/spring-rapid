package com.github.vincemann.springrapid.acldemo.dto.user;

import com.github.vincemann.springrapid.acldemo.model.User;
import com.github.vincemann.springrapid.acldemo.model.abs.UserAwareEntity;
import com.github.vincemann.springrapid.acldemo.service.jpa.MyJpaUserService;
import com.github.vincemann.springrapid.core.controller.dto.EntityPostProcessor;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class MapUserUuidDtoPostProcessor implements EntityPostProcessor<CreateUserDto, UserAwareEntity> {

    private MyJpaUserService userService;


    // need lazy injection here, otherwise not wrapped with aop proxies somehow
    @Lazy
    @Autowired
    public void injectUserService(MyJpaUserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> entityClazz, Class<?> dtoClass) {
        return UserAwareEntity.class.isAssignableFrom(entityClazz) || CreateUserDto.class.isAssignableFrom(dtoClass);
    }

    @Transactional
    @Override
    public void postProcessEntity(UserAwareEntity entity, CreateUserDto createUserDto) throws BadEntityException, EntityNotFoundException {
        String uuid = createUserDto.getUuid();
        userService.findById(42L);
        Optional<User> byUuid = userService.findByUuid(uuid);
        if (byUuid.isEmpty()){
            throw new BadEntityException("Wrong uuid");
        }
        User user = byUuid.get();
        entity.setUser(user);
        user.setUuid(null);

        userService.fullUpdate(user);
    }
}
