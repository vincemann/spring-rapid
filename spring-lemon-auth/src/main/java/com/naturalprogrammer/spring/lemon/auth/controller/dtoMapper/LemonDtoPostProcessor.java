package com.naturalprogrammer.spring.lemon.auth.controller.dtoMapper;

import com.naturalprogrammer.spring.lemon.auth.domain.AbstractUser;
import com.naturalprogrammer.spring.lemon.auth.domain.dto.user.AbstractLemonUserDto;
import io.github.vincemann.springrapid.core.controller.dtoMapper.DtoMapper;
import io.github.vincemann.springrapid.core.controller.dtoMapper.DtoPostProcessor;
import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.service.exception.BadEntityException;
import io.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;

@Order(value = 999)
public class LemonDtoPostProcessor implements DtoPostProcessor<AbstractLemonUserDto,AbstractUser<?>> {

    @Override
    public boolean supports(Class<?> entityClazz, Class<?> dtoClass) {
        return AbstractLemonUserDto.class.isAssignableFrom(dtoClass);
    }

    @Override
    public void postProcessDto(AbstractLemonUserDto abstractLemonUserDto, AbstractUser<?> entity) throws BadEntityException {
        abstractLemonUserDto.initialize();
    }

    @Override
    public void postProcessEntity(AbstractUser<?> entity, AbstractLemonUserDto abstractLemonUserDto) throws BadEntityException {
    }

}
