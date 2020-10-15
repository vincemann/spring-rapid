package com.github.vincemann.springrapid.core.controller.dto.mapper;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;


/**
 * Maps a Dto to its ServiceEntity and vice versa, by using {@link ModelMapper}
 */
@Setter
@Getter
@Transactional
@NoArgsConstructor
@Order(Ordered.LOWEST_PRECEDENCE)
public class BasicDtoMapper implements DtoMapper<IdentifiableEntity<?>,Object> {

    private ModelMapper modelMapper;

    @Override
    public boolean supports(Class<?> dtoClass) {
        return true;
    }

    @Override
    public <T extends IdentifiableEntity<?>> T mapToEntity(Object source, Class<T> destinationClass) throws BadEntityException {
        try {
            return modelMapper.map(source, destinationClass);
        }catch (MappingException e){
            throw new BadEntityException(e);
        }
    }

    @Override
    public <T> T mapToDto(IdentifiableEntity<?> source, Class<T> destinationClass) {
        return modelMapper.map(source, destinationClass);
    }

    @Autowired
    public void injectModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

}
