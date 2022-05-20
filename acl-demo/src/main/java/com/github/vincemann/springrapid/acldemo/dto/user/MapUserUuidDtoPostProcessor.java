package com.github.vincemann.springrapid.acldemo.dto.user;

import com.github.vincemann.springrapid.acldemo.model.User;
import com.github.vincemann.springrapid.acldemo.model.abs.UserAwareEntity;
import com.github.vincemann.springrapid.acldemo.service.MyUserService;
import com.github.vincemann.springrapid.core.controller.dto.mapper.DtoEntityPostProcessor;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.slicing.WebComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@WebComponent
public class MapUserUuidDtoPostProcessor implements DtoEntityPostProcessor<CreateUserDto, UserAwareEntity> {

    private MyUserService userService;

    @Autowired
    public MapUserUuidDtoPostProcessor(MyUserService userService) {
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
