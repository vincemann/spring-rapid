package com.github.vincemann.springrapid.core.controller.dtoMapper.context;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
/**
 * Represents the Context that contains the information when which dto class should be used for mapping.
 * Create with {@link DtoMappingContext} impl.
 */
@Slf4j
@Getter
@ToString
public class DtoMappingContext{
    private Map<DtoMappingInfo, Class<?>> mappingEntries = new HashMap<>();

    DtoMappingContext() {
    }

}
