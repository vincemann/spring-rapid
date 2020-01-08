package io.github.vincemann.generic.crud.lib.controller.dtoMapper.finder;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMapper;

public interface DtoMapperFinder {
    public DtoMapper find(Class<?> dtoClass);
}
