package com.github.vincemann.springrapid.auth.controller.dtoMapper;

import com.github.vincemann.springrapid.auth.domain.AbstractUser;
import com.github.vincemann.springrapid.auth.domain.dto.user.AbstractFindRapidUserDto;
import com.github.vincemann.springrapid.core.controller.dto.mapper.DtoPostProcessor;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.core.annotation.Order;

@Order(value = 999)
public class UserDtoPostProcessor implements DtoPostProcessor<AbstractFindRapidUserDto, AbstractUser<?>> {

    @Override
    public boolean supports(Class<?> entityClazz, Class<?> dtoClass) {
        return AbstractFindRapidUserDto.class.isAssignableFrom(dtoClass);
    }

    @Override
    public void postProcessDto(AbstractFindRapidUserDto abstractUserDto, AbstractUser<?> entity) throws BadEntityException {
        abstractUserDto.initFlags();
    }

    @Override
    public void postProcessEntity(AbstractUser<?> entity, AbstractFindRapidUserDto abstractUserDto) throws BadEntityException {
    }

}
