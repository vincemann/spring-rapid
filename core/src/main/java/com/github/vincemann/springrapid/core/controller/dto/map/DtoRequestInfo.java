package com.github.vincemann.springrapid.core.controller.dto.map;

import com.github.vincemann.springrapid.core.util.Lists;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents information about current Request.
 */
public class DtoRequestInfo {
    private String endpoint;
    private Direction direction;
    private List<String> authorities = new ArrayList<>();
    private Principal principal;
    private HttpServletRequest request;


    public DtoRequestInfo(String endpoint, Direction direction, List<String> authorities, Principal principal, HttpServletRequest request) {
        this.endpoint = endpoint;
        this.direction = direction;
        this.request = request;
        if (authorities!=null)
            this.authorities = authorities;
        if (principal!=null)
            this.principal = principal;
    }

    public DtoRequestInfo(DtoRequestInfo info){
        this.endpoint=info.getEndpoint();
        this.direction=info.getDirection();
        this.authorities = Lists.newArrayList(info.getAuthorities());
        this.request = info.getRequest();
        this.principal = info.getPrincipal();
    }

    public DtoRequestInfo(String endpoint, Direction direction) {
        this.endpoint = endpoint;
        this.direction = direction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof DtoRequestInfo)) return false;

        DtoRequestInfo that = (DtoRequestInfo) o;

        return new EqualsBuilder().append(getEndpoint(), that.getEndpoint()).append(getDirection(), that.getDirection()).append(getAuthorities(), that.getAuthorities()).append(getPrincipal(), that.getPrincipal()).append(getRequest(), that.getRequest()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getEndpoint()).append(getDirection()).append(getAuthorities()).append(getPrincipal()).append(getRequest()).toHashCode();
    }

    @Override
    public String toString() {
        return "DtoRequestInfo{" +
                "endpoint='" + endpoint + '\'' +
                ", direction=" + direction +
                ", authorities=" + authorities +
                ", principal=" + principal +
                '}';
    }

    public String getEndpoint() {
        return endpoint;
    }

    public Direction getDirection() {
        return direction;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public Principal getPrincipal() {
        return principal;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public static final class Builder {
        private String endpoint;
        private Direction direction;
        private List<String> authorities;
        private Principal principal;
        private HttpServletRequest request;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder withEndpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public Builder withDirection(Direction direction) {
            this.direction = direction;
            return this;
        }

        public Builder withAuthorities(List<String> authorities) {
            this.authorities = authorities;
            return this;
        }

        public Builder withPrincipal(Principal principal) {
            this.principal = principal;
            return this;
        }

        public Builder withRequest(HttpServletRequest request) {
            this.request = request;
            return this;
        }

        public DtoRequestInfo build() {
            return new DtoRequestInfo(endpoint, direction, authorities, principal, request);
        }
    }
}
