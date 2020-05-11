package io.github.vincemann.springrapid.core.controller.dtoMapper;

import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import io.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import lombok.SneakyThrows;
import org.modelmapper.spi.ConditionalConverter;
import org.modelmapper.spi.MappingContext;

import java.io.Serializable;
import java.util.Optional;

public class IdIdentifiableEntityConverter implements ConditionalConverter<Serializable, IdentifiableEntity> {

    private CrudServiceLocator crudServiceLocator;

    public IdIdentifiableEntityConverter(CrudServiceLocator crudServiceLocator) {
        this.crudServiceLocator = crudServiceLocator;
    }

    @Override
    public MatchResult match(Class<?> sourceType, Class<?> destinationType) {
        if (destinationType.isAssignableFrom(IdentifiableEntity.class)) {
            if (sourceType.isAssignableFrom(Serializable.class)) {
                return MatchResult.FULL;
            } else {
                return MatchResult.PARTIAL;
            }
        }
        return MatchResult.NONE;
    }

    @SneakyThrows
    @Override
    public IdentifiableEntity convert(MappingContext<Serializable, IdentifiableEntity> context) {
        Optional<IdentifiableEntity> byId = crudServiceLocator.find(context.getDestinationType()).findById(context.getSource());
        if (byId.isEmpty()) {
            throw new EntityNotFoundException(context.getSource(), context.getDestinationType());
        }
        return byId.get();
    }
}
