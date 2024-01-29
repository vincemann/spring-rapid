package com.github.vincemann.springrapid.core.controller.dto.mapper;

import com.github.vincemann.springrapid.core.util.Lists;
import lombok.*;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents information about current Request.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class DtoRequestInfo {
    private String endpoint;
    private Direction direction;
    private List<String> authorities = new ArrayList<>();
    private Principal principal;
    private List<String> urlParams = new ArrayList<>();


    @Builder
    public DtoRequestInfo(String endpoint, Direction direction, @Nullable List<String> authorities, Principal principal, List<String> urlParams) {
        this.endpoint = endpoint;
        this.direction = direction;
        if (authorities!=null)
            this.authorities = authorities;
        if (principal!=null)
            this.principal = principal;
        if (urlParams != null)
            this.urlParams = urlParams;

    }

    public DtoRequestInfo(DtoRequestInfo info){
        this.endpoint=info.getEndpoint();
        this.direction=info.getDirection();
        this.authorities = Lists.newArrayList(info.getAuthorities());
        this.urlParams = Lists.newArrayList(info.getUrlParams());
        this.principal = info.getPrincipal();
    }

    public DtoRequestInfo(String endpoint, Direction direction) {
        this.endpoint = endpoint;
        this.direction = direction;
    }

}
