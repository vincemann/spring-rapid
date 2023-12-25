package com.github.vincemann.springrapid.acldemo.dto.user;

import com.github.vincemann.springrapid.acldemo.model.User;
import com.github.vincemann.springrapid.acldemo.model.abs.UserAwareEntity;
import com.github.vincemann.springrapid.acldemo.service.MyUserService;
import com.github.vincemann.springrapid.core.controller.dto.mapper.EntityPostProcessor;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.slicing.WebComponent;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@WebComponent
public class MapUserUuidDtoPostProcessor implements EntityPostProcessor<CreateUserDto, UserAwareEntity>, ApplicationContextAware {

    private MyUserService userService;

//    @Autowired
//    public MapUserUuidDtoPostProcessor(MyUserService userService) {
//        this.userService = userService;
//    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        if(service == null){
        this.userService = applicationContext.getBean(MyUserService.class);
        System.err.println("setting user service instance for post processor to: " + userService);
//        }
    }
//    @Lazy
//    @Autowired
//    public void setUserService(MyUserService userService) {
//        System.err.println("setting user service instance for post processor to: " + userService.getClass());
//        this.userService = userService;
//    }

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

        System.err.println("Calling full update on:" + userService);

        userService.fullUpdate(user);
    }
}
