package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.abs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
public class ControllerTestConfiguration<Id extends Serializable> {
    //todo body into this?
    private RequestMethod method;
    private MultiValueMap<String,String> headers;
    private HttpStatus expectedHttpStatus;
    private Map<String,String> queryParams;
    @Nullable
    private Id id = null;

    @Builder
    public ControllerTestConfiguration(RequestMethod method, MultiValueMap<String, String> headers, HttpStatus expectedHttpStatus, Map<String, String> queryParams) {
        this.method = method;
        this.headers = headers;
        this.expectedHttpStatus = expectedHttpStatus;
        this.queryParams = queryParams;
    }
}
