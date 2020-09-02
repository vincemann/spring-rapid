package com.github.vincemann.springrapid.core.controller.dtoMapper.context;

import com.github.vincemann.springrapid.core.util.Lists;
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
    private DtoMappingInfo.Principal principal = DtoMappingInfo.Principal.ALL;

    public enum Principal{
        OWN,
        FOREIGN,
        ALL
    }

    @Builder
    public DtoMappingInfo(String endpoint, Direction direction, @Nullable List<String> authorities, DtoMappingInfo.Principal principal) {
        this.endpoint = endpoint;
        this.direction = direction;
        if (authorities!=null)
            this.authorities = authorities;
        if (principal!=null)
            this.principal = principal;
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
