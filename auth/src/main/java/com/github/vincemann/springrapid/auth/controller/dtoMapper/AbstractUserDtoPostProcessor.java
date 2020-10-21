package com.github.vincemann.springrapid.auth.controller.dtoMapper;

import com.github.vincemann.springrapid.auth.domain.AbstractUser;
import com.github.vincemann.springrapid.auth.domain.dto.user.RapidAbstractUserDto;
import com.github.vincemann.springrapid.core.controller.dto.mapper.DtoPostProcessor;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.core.annotation.Order;

@Order(value = 999)
public class AbstractUserDtoPostProcessor implements DtoPostProcessor<RapidAbstractUserDto, AbstractUser<?>> {

    @Override
    public boolean supports(Class<?> entityClazz, Class<?> dtoClass) {
        return RapidAbstractUserDto.class.isAssignableFrom(dtoClass);
    }

    @Override
    public void postProcessDto(RapidAbstractUserDto abstractUserDto, AbstractUser<?> entity) throws BadEntityException {
        abstractUserDto.initFlags();
    }

    @Override
    public void postProcessEntity(AbstractUser<?> entity, RapidAbstractUserDto abstractUserDto) throws BadEntityException {
    }

}
