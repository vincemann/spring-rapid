package com.github.vincemann.springrapid.auth.controller;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.dto.user.abs.AbstractFindUserDto;
import com.github.vincemann.springrapid.core.controller.dto.DtoPostProcessor;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Order(value = Ordered.LOWEST_PRECEDENCE)
public class UserDtoPostProcessor implements DtoPostProcessor<AbstractFindUserDto, AbstractUser<?>> {

    @Override
    public boolean supports(Class<?> entityClazz, Class<?> dtoClass) {
        return AbstractFindUserDto.class.isAssignableFrom(dtoClass);
    }

    @Override
    public void postProcessDto(AbstractFindUserDto abstractUserDto, AbstractUser<?> entity, String... fieldsToMap) throws BadEntityException {
        abstractUserDto.initFlags();
    }

}
