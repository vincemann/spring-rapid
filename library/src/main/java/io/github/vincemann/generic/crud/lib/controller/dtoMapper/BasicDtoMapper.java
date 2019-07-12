package io.github.vincemann.generic.crud.lib.controller.dtoMapper;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import org.modelmapper.ModelMapper;


public class BasicDtoMapper implements DtoMapper {

    @Override
    public <ServiceE extends IdentifiableEntity> ServiceE mapServiceEntityToDto(Object source, Class<ServiceE> destinationClass) {
        return modelMapper.map(source,destinationClass);
    }

    private ModelMapper modelMapper;

    public BasicDtoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public BasicDtoMapper() {
        this.modelMapper= new ModelMapper();
    }

    @Override
    public <Dto extends IdentifiableEntity> Dto mapDtoToServiceEntity(Object source, Class<Dto> destinationClass) throws EntityMappingException {
        return modelMapper.map(source,destinationClass);
    }


    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public ModelMapper getModelMapper() {
        return modelMapper;
    }
}
