package io.github.vincemann.generic.crud.lib.controller.dtoMapper.finder;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMapper;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;

import java.io.Serializable;

public interface DtoMapperFinder<Id extends Serializable> {
    public DtoMapper find(Class<? extends IdentifiableEntity<Id>> dtoClass);
}
