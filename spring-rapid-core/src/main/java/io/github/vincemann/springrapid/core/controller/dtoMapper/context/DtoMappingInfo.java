package io.github.vincemann.springrapid.core.controller.dtoMapper.context;

import io.github.vincemann.springrapid.core.util.Lists;
import lombok.*;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Bundle of information that maps to dto class for
 * entity -> dto mapping.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class DtoMappingInfo {
    private String endpoint;
    private Direction direction;
    private List<String> authorities = new ArrayList<>();

    @Builder
    public DtoMappingInfo(String endpoint, Direction direction, @Nullable List<String> authorities) {
        this.endpoint = endpoint;
        this.direction = direction;
        this.authorities = authorities;
    }

    public DtoMappingInfo(DtoMappingInfo info){
        this.endpoint=info.endpoint;
        this.direction=info.direction;
        this.authorities = Lists.newArrayList(info.authorities);
    }

    public DtoMappingInfo(String endpoint, Direction direction) {
        this.endpoint = endpoint;
        this.direction = direction;
    }

}
