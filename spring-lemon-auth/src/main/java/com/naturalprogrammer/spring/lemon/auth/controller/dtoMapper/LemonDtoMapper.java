package com.naturalprogrammer.spring.lemon.auth.controller.dtoMapper;

import com.naturalprogrammer.spring.lemon.auth.domain.AbstractUser;
import com.naturalprogrammer.spring.lemon.auth.domain.dto.user.AbstractLemonUserDto;
import io.github.vincemann.springrapid.core.controller.dtoMapper.DtoMapper;
import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.service.exception.BadEntityException;
import io.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;

@Order(value = 999)
public class LemonDtoMapper implements DtoMapper<AbstractUser<?>,AbstractLemonUserDto> {

    @Getter
    private DtoMapper childMapper;

    @Override
    public boolean supports(Class<?> dtoClass) {
        return AbstractLemonUserDto.class.isAssignableFrom(dtoClass);
    }

    @Autowired
    @Qualifier(value = "lemonChildMapper")
    public void injectChildMapper(DtoMapper childMapper) {
        this.childMapper = childMapper;
    }

    @Override
    public <T extends AbstractUser<?>> T mapToEntity(AbstractLemonUserDto source, Class<T> destinationClass) throws EntityNotFoundException, BadEntityException {
        return (T) childMapper.mapToEntity(source,destinationClass);
    }

    @Override
    public <T extends AbstractLemonUserDto> T mapToDto(AbstractUser<?> source, Class<T> destinationClass) {
        AbstractLemonUserDto dto = (AbstractLemonUserDto) childMapper.mapToDto(source, destinationClass);
        dto.initialize();
        return (T) dto;
    }


}
