package com.github.vincemann.springlemon.auth.controller.dtoMapper;

import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.domain.dto.user.AbstractUserDto;
import com.github.vincemann.springrapid.core.controller.dto.mapper.DtoPostProcessor;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.core.annotation.Order;

@Order(value = 999)
public class AbstractUserDtoPostProcessor implements DtoPostProcessor<AbstractUserDto, AbstractUser<?>> {

    @Override
    public boolean supports(Class<?> entityClazz, Class<?> dtoClass) {
        return AbstractUserDto.class.isAssignableFrom(dtoClass);
    }

    @Override
    public void postProcessDto(AbstractUserDto abstractUserDto, AbstractUser<?> entity) throws BadEntityException {
        abstractUserDto.initFlags();
    }

    @Override
    public void postProcessEntity(AbstractUser<?> entity, AbstractUserDto abstractUserDto) throws BadEntityException {
    }

}
