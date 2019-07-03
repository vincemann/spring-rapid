package io.github.vincemann.generic.crud.lib.dtoMapper.backRefResolving;


import io.github.vincemann.generic.crud.lib.model.biDir.BiDirParentEntity;
import io.github.vincemann.generic.crud.lib.model.biDir.BiDirParentId;
import io.github.vincemann.generic.crud.lib.bidir.BiDirDTOChild;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.model.biDir.BiDirChild;
import io.github.vincemann.generic.crud.lib.model.biDir.BiDirParent;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.service.exception.UnknownParentTypeException;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;

import java.io.Serializable;
import java.util.Optional;

/**
 *
 * @param <DTO>             DTO Entity Type that is a {@link BiDirDTOChild} of {@link ParentServiceE}
 * @param <ServiceE>        Service Entity Type that should be the result of the mapping
 * @param <ParentServiceE>  Service Entity Type of the {@link BiDirParent} of {@link ServiceE}
 * @param <Id>              Id Type
 * @param <ParentId>        Parent Id Type
 */
public class BackRefResolvingConverter<DTO extends IdentifiableEntity<Id> & BiDirDTOChild<ParentId>, ServiceE extends IdentifiableEntity<Id> & BiDirChild, ParentServiceE extends IdentifiableEntity<ParentId> & BiDirParent, Id extends Serializable, ParentId extends Serializable> implements Converter<DTO, ServiceE> {
    private CrudService<ParentServiceE,ParentId> parentCrudService;
    private Class<? extends IdentifiableEntity> parentClass;
    private Class<DTO> dtoClass;
    private Class<ServiceE> serviceEntityClass;

    /**
     * Converts a {@link BiDirDTOChild} to its ServiceEntity of Type {@link ParentServiceE}.
     * The backRefence Id, annotated with {@link BiDirParentId} of BiDirDTOChild {@link ParentId} is used in order to find ParentServiceEntity by id from vincemann.github.generic.crud.lib.service {@param parentCrudService}
     * The retrieved ParentService entity can then be set as a Backreference for the mapping result Entity. See: {@link BiDirParentEntity}
     *
     * So a mapping from {@link BiDirParentId} to {@link BiDirParentEntity} happens.
     * A mapping from parent id to the corresponding parent ServiceEntity.
     *
     * @param parentCrudService
     * @param parentClass
     * @param dtoClass                  Child
     * @param serviceEntityClass        Child
     */
    public BackRefResolvingConverter(CrudService<ParentServiceE,ParentId> parentCrudService, Class<ParentServiceE> parentClass, Class<DTO> dtoClass, Class<ServiceE> serviceEntityClass) {
        this.parentCrudService = parentCrudService;
        this.parentClass = parentClass;
        this.dtoClass=dtoClass;
        this.serviceEntityClass=serviceEntityClass;
    }


    @Override
    public ServiceE convert(MappingContext<DTO, ServiceE> context) {
        ParentServiceE serviceParentEntity;
        try {
            ParentId parentId = context.getSource().findParentId(parentClass);
            if (parentId != null) {

                Optional<ParentServiceE> optionalParentServiceEntity = parentCrudService.findById(parentId);
                if (optionalParentServiceEntity.isPresent()) {
                    serviceParentEntity = optionalParentServiceEntity.get();
                } else {
                    throw new RuntimeException(new EntityNotFoundException("No Parent ServiceEntity of tpye "+parentClass.getSimpleName()+" found with provided parentId: " + parentId));
                }
                ServiceE mappedServiceEntity = new ModelMapper().map(context.getSource(), context.getDestinationType());
                mappedServiceEntity.findAndSetParent(serviceParentEntity);
                return mappedServiceEntity;
            } else {
                return new ModelMapper().map(context.getSource(), context.getDestinationType());
            }

        } catch (NoIdException | UnknownParentTypeException| IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Class<DTO> getDtoClass() {
        return dtoClass;
    }

    public Class<ServiceE> getServiceEntityClass() {
        return serviceEntityClass;
    }
}
