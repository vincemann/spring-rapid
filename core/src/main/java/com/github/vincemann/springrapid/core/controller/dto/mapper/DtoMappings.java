package com.github.vincemann.springrapid.core.controller.dto.mapper;

import com.google.common.collect.Lists;
import lombok.*;

import java.util.*;
import java.util.function.Predicate;

@SuppressWarnings("ALL")
/**
 * Represents the Context that contains the information when which dto class should be used for mapping.
 * Create with {@link CrudDtoMappingContextBuilder}.
 */
//@Slf4j
//@ToString
public class DtoMappings {

    private List<Mapping> mappings;

    public List<Mapping> get() {
        return mappings;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("DtoMappings: { ");
        for (Mapping mapping : mappings) {
            Predicate<DtoRequestInfo> condition = mapping.getCondition();
            if (condition instanceof DescribablePredicate) {
                sb.append(((DescribablePredicate<?>) condition).getDescription());
            } else {
                sb.append(condition.toString());
            }
            sb.append("  ->  ").append(mapping.getDtoClass().getSimpleName()).append(", ");
        }
        sb.setLength(sb.length() - 2); // Remove the last comma and space
        sb.append(" }");
        return sb.toString();
    }
}
