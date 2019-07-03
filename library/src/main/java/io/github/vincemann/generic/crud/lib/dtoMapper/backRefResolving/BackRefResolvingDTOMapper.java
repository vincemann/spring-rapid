package io.github.vincemann.generic.crud.lib.dtoMapper.backRefResolving;

import io.github.vincemann.generic.crud.lib.dtoMapper.BasicDTOMapper;
import org.modelmapper.ModelMapper;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;

import java.io.Serializable;
import java.util.List;

/**
 * Maps an DTO Entity to a ServiceEntity, using a set of custom {@link BackRefResolvingConverter}s for mapping a DTO id,
 * to the corresponding ServiceEntity
 * @param <DTO>         DTO Entity Type that should be mapped (source)
 * @param <ServiceE>    ServiceEntity Type that should be the result of mapping (destination)
 * @param <Id>          Id Type
 */
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
