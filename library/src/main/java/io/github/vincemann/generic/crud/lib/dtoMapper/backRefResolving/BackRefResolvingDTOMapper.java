package io.github.vincemann.generic.crud.lib.dtoMapper.backRefResolving;

import io.github.vincemann.generic.crud.lib.dtoMapper.BasicDTOMapper;
import org.modelmapper.ModelMapper;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;

import java.io.Serializable;
import java.util.List;

public class BackRefResolvingDTOMapper<DTO extends IdentifiableEntity<Id>,ServiceE extends IdentifiableEntity<Id>,Id extends Serializable> extends BasicDTOMapper<DTO,ServiceE,Id> {

    public BackRefResolvingDTOMapper(Class<ServiceE> serviceEntityClass, List<BackRefResolvingConverter> backRefResolvingConverters) {
        super(serviceEntityClass, null);
        ModelMapper mapper = new ModelMapper();
        for(BackRefResolvingConverter backRefResolvingConverter: backRefResolvingConverters){
            mapper.addConverter(backRefResolvingConverter,backRefResolvingConverter.getDtoClass(),backRefResolvingConverter.getServiceEntityClass());
        }
        setModelMapper(mapper);
    }

}
