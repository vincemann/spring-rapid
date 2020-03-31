package io.github.vincemann.generic.crud.lib.controller.dtoMapper.context;

import lombok.*;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class DtoMappingInfo {
    private int endpoint;
    private Direction direction;
    private List<String> authorities = new ArrayList<>();

    @Builder
    public DtoMappingInfo(int endpoint, Direction direction, @Nullable List<String> authorities) {
        this.endpoint = endpoint;
        this.direction = direction;
        this.authorities = authorities;
    }

    public DtoMappingInfo(DtoMappingInfo info){
        this.endpoint=info.endpoint;
        this.direction=info.direction;
        this.authorities =info.authorities;
    }

    public DtoMappingInfo(int endpoint, Direction direction) {
        this.endpoint = endpoint;
        this.direction = direction;
    }
}
