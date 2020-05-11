package io.github.vincemann.springrapid.core.controller.dtoMapper;

import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import org.modelmapper.spi.ConditionalConverter;
import org.modelmapper.spi.MappingContext;

import java.io.Serializable;

public class IdentifiableEntityIdConverter implements ConditionalConverter<IdentifiableEntity, Serializable> {


    @Override
    public MatchResult match(Class<?> sourceType, Class<?> destinationType) {

        if (destinationType.isAssignableFrom(Serializable.class)) {
            if (sourceType.isAssignableFrom(IdentifiableEntity.class)) {
                return MatchResult.FULL;
            } else {
                return MatchResult.PARTIAL;
            }
        }
        return MatchResult.NONE;
    }

    @Override
    public Serializable convert(MappingContext<IdentifiableEntity, Serializable> context) {
        return context.getSource().getId();
    }
}
