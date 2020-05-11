package io.github.vincemann.springrapid.entityrelationship.controller.dtomapper;

import io.github.vincemann.springrapid.core.controller.dtoMapper.DtoMapper;
import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.service.exception.BadEntityException;
import io.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import io.github.vincemann.springrapid.entityrelationship.dto.biDir.BiDirChildDto;
import io.github.vincemann.springrapid.entityrelationship.dto.biDir.BiDirParentDto;
import io.github.vincemann.springrapid.entityrelationship.dto.uniDir.UniDirChildDto;
import io.github.vincemann.springrapid.entityrelationship.dto.uniDir.UniDirParentDto;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Maps an {@link IdentifiableEntity} to its Dto and vice versa using {@link org.modelmapper.ModelMapper} AND resolves
 * id fields, referencing parent/child entities.
 * The id resolving is done by the given {@link EntityIdResolver}s.
 *
 * @see EntityIdResolver
 */
@Order(1000)
@Transactional
public class IdResolvingDtoMapper implements DtoMapper<IdentifiableEntity<?>,Object> {

    private List<EntityIdResolver> entityIdResolvers;
    @Getter
    @Setter
    private ModelMapper modelMapper;

    public IdResolvingDtoMapper(List<EntityIdResolver> entityIdResolvers) {
        this.entityIdResolvers = entityIdResolvers;
        this.modelMapper=new ModelMapper();
    }

    @Override
    public boolean supports(Class<?> dtoClass) {
        return (BiDirChildDto.class.isAssignableFrom(dtoClass) ||
                BiDirParentDto.class.isAssignableFrom(dtoClass) ||
                UniDirParentDto.class.isAssignableFrom(dtoClass) ||
                UniDirChildDto.class.isAssignableFrom(dtoClass));
    }

    @Override
    public <T extends IdentifiableEntity<?>> T mapToEntity(Object dto, Class<T> destinationClass) throws EntityNotFoundException, BadEntityException {
        T mappingResult = modelMapper.map(dto, destinationClass);
        //yet unfinished
        EntityIdResolver entityIdResolver = findResolver(dto.getClass());
        entityIdResolver.resolveEntityIds(mappingResult, dto);
        //is now finished
        return mappingResult;
    }

    @Override
    public <Dto> Dto mapToDto(IdentifiableEntity<?> entity, Class<Dto> destinationClass) {
        Dto mappingResult = modelMapper.map(entity, destinationClass);
        //yet unfinished
        EntityIdResolver entityIdResolver = findResolver(destinationClass);
        entityIdResolver.resolveDtoIds(mappingResult, entity);
        //is now finished
        return mappingResult;
    }

    private EntityIdResolver findResolver(Class<?> dstClass){
        for (EntityIdResolver entityIdResolver : entityIdResolvers) {
            if (entityIdResolver.getDtoClass().isAssignableFrom(dstClass)) {
                return entityIdResolver;
            }
        }
        throw new IllegalArgumentException("No "+EntityIdResolver.class.getSimpleName() + " found for dstClass: " + dstClass.getSimpleName());
    }
}
