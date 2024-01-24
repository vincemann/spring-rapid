package com.github.vincemann.springrapid.core.controller.dto.mapper.context;

import com.github.vincemann.springrapid.core.util.Lists;
import lombok.*;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private DtoRequestInfo.Principal principal = DtoRequestInfo.Principal.ALL;
    private List<String> urlParams = new ArrayList<>();

    public enum Principal{
        OWN,
        FOREIGN,
        ALL
    }

    @Builder
    public DtoRequestInfo(String endpoint, Direction direction, @Nullable List<String> authorities, DtoRequestInfo.Principal principal, List<String> urlParams) {
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
        this.endpoint=info.endpoint;
        this.direction=info.direction;
        this.authorities = Lists.newArrayList(info.authorities);
        this.urlParams = info.urlParams;
    }

    public DtoRequestInfo(String endpoint, Direction direction) {
        this.endpoint = endpoint;
        this.direction = direction;
    }

}
