package com.github.vincemann.springrapid.core.controller.dto.map;

import com.github.vincemann.springrapid.core.util.Lists;
import lombok.*;

import javax.servlet.http.HttpServletRequest;
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
    private Principal principal;
    private Map<String, String[]> urlParams = new HashMap<>();


    @Builder
    public DtoRequestInfo(String endpoint, Direction direction, List<String> authorities, Principal principal, Map<String, String[]> urlParams) {
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
        this.urlParams = new HashMap<>(info.getUrlParams());
        this.principal = info.getPrincipal();
    }

    public DtoRequestInfo(String endpoint, Direction direction) {
        this.endpoint = endpoint;
        this.direction = direction;
    }

}
