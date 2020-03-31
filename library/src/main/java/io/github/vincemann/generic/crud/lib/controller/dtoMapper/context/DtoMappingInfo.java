package io.github.vincemann.generic.crud.lib.controller.dtoMapper.context;

import com.google.common.base.Objects;
import lombok.*;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

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
        this.authorities =info.authorities;
    }

    public DtoMappingInfo(String endpoint, Direction direction) {
        this.endpoint = endpoint;
        this.direction = direction;
    }

}
