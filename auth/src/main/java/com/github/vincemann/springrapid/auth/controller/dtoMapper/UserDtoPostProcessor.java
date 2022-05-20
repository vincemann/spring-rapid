package com.github.vincemann.springrapid.auth.controller.dtoMapper;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.dto.user.AbstractFindRapidUserDto;
import com.github.vincemann.springrapid.core.controller.dto.mapper.EntityDtoPostProcessor;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.core.annotation.Order;

@Order(value = 999)
public class UserDtoPostProcessor implements EntityDtoPostProcessor<AbstractFindRapidUserDto, AbstractUser<?>> {

    @Override
    public boolean supports(Class<?> entityClazz, Class<?> dtoClass) {
        return AbstractFindRapidUserDto.class.isAssignableFrom(dtoClass);
    }

    @Override
    public void postProcessDto(AbstractFindRapidUserDto abstractUserDto, AbstractUser<?> entity,String... fieldsToMap) throws BadEntityException {
        abstractUserDto.initFlags();
    }

}
