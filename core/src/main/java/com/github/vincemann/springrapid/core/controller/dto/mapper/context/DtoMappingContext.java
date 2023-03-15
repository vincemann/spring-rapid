package com.github.vincemann.springrapid.core.controller.dto.mapper.context;

import com.github.vincemann.springrapid.core.util.Lists;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@SuppressWarnings("ALL")
/**
 * Represents the Context that contains the information when which dto class should be used for mapping.
 * Create with {@link CrudDtoMappingContextBuilder}.
 */
//@Slf4j
@Getter
//@ToString
public class DtoMappingContext{
    private Map<DtoRequestInfo, Class<?>> mappingEntries = new HashMap<>();

    DtoMappingContext() {
    }


    public String toPrettyString() {
        //groups entries by endpoint
        StringBuilder sb = new StringBuilder("DtoMappingContext{mappings:");
        sb.append(System.lineSeparator());

        Map<String,List<Map.Entry<DtoRequestInfo, Class<?>>>> groupedByEndpoint = new HashMap<>();
        for (Map.Entry<DtoRequestInfo, Class<?>> entry : mappingEntries.entrySet()) {
            String endpoint = entry.getKey().getEndpoint();
            List<Map.Entry<DtoRequestInfo, Class<?>>> endpointGroup = groupedByEndpoint.get(endpoint);
            if (endpointGroup==null){
                groupedByEndpoint.put(endpoint, Lists.newArrayList(entry));
            }else {
                endpointGroup.add(entry);
            }
        }
        groupedByEndpoint.forEach((endpoint,entries) ->
                {
                    sb.append("ENDPOINT: ").append(endpoint).append(" :").append(System.lineSeparator());
                    for (Map.Entry<DtoRequestInfo, Class<?>> entry : entries) {
                                        sb.append("info: ").append(entry.getKey()).append("-> ").append(entry.getValue().getSimpleName()).append(System.lineSeparator());
                    }
                    sb.append(System.lineSeparator());
                }
        );
        sb.append("}");

        return sb.toString();
    }
}
