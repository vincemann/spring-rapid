package com.github.vincemann.springlemon.auth.controller.dtoMapper;

import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.domain.dto.user.AbstractLemonUserDto;
import com.github.vincemann.springrapid.core.controller.dto.mapper.DtoPostProcessor;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.core.annotation.Order;

@Order(value = 999)
public class LemonDtoPostProcessor implements DtoPostProcessor<AbstractLemonUserDto, AbstractUser<?>> {

    @Override
    public boolean supports(Class<?> entityClazz, Class<?> dtoClass) {
        return AbstractLemonUserDto.class.isAssignableFrom(dtoClass);
    }

    @Override
    public void postProcessDto(AbstractLemonUserDto abstractLemonUserDto, AbstractUser<?> entity) throws BadEntityException {
        abstractLemonUserDto.initFlags();
    }

    @Override
    public void postProcessEntity(AbstractUser<?> entity, AbstractLemonUserDto abstractLemonUserDto) throws BadEntityException {
    }

}
