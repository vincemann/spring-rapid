package io.github.vincemann.generic.crud.lib.controller.dtoMapper;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.exception.EntityMappingException;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;


/**
 * Maps a Dto to its ServiceEntity and vice versa, by using {@link ModelMapper}
 */
@Setter
@Getter
@AllArgsConstructor
public class BasicDtoMapper implements DtoMapper {

    private ModelMapper modelMapper;

    @Override
    public Class getDtoClass() {
        return IdentifiableEntity.class;
    }

    @Override
    public <Dto extends IdentifiableEntity> Dto mapEntityToDto(Object source, Class<Dto> destinationClass) {
        return modelMapper.map(source,destinationClass);
    }

    public BasicDtoMapper() {
        this.modelMapper= new ModelMapper();
    }

    @Override
    public <E extends IdentifiableEntity> E mapDtoToEntity(Object source, Class<E> destinationClass) throws EntityMappingException {
        return modelMapper.map(source,destinationClass);
    }
}
