package com.naturalprogrammer.spring.lemon.auth.controller.dtoMapper;

import com.naturalprogrammer.spring.lemon.auth.domain.dto.user.AbstractLemonUserDto;
import io.github.vincemann.springrapid.core.controller.dtoMapper.BasicDtoMapper;
import io.github.vincemann.springrapid.core.controller.dtoMapper.DtoMappingException;
import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class LemonDtoMapper extends BasicDtoMapper {

    @Override
    public boolean isDtoClassSupported(Class<?> clazz) {
        return AbstractLemonUserDto.class.isAssignableFrom(clazz);
    }

    @Override
    public <T> T mapToDto(IdentifiableEntity<?> source, Class<T> destinationClass) throws DtoMappingException {
        AbstractLemonUserDto lemonUserDto = (AbstractLemonUserDto) super.mapToDto(source, destinationClass);
        lemonUserDto.initialize();
        return (T) lemonUserDto;
    }
}
