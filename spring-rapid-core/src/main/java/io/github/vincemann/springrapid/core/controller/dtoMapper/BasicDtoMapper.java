package io.github.vincemann.springrapid.core.controller.dtoMapper;

import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
public class BasicDtoMapper implements DtoMapper {

    private ModelMapper modelMapper;

    @Override
    public boolean isDtoClassSupported(Class<?> clazz) {
        return true;
    }

    @Override
    public <Dto> Dto mapToDto(IdentifiableEntity<?> source, Class<Dto> destinationClass) throws DtoMappingException {
        return modelMapper.map(source,destinationClass);
    }

    @Autowired
    public void injectModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public <E extends IdentifiableEntity<?>> E mapToEntity(Object source, Class<E> destinationClass) throws DtoMappingException {
        return modelMapper.map(source,destinationClass);
    }
}
