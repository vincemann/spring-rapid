package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.configuration.abs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.Serializable;
import java.net.URI;
import java.util.Map;

@Getter
@AllArgsConstructor
@Setter
public abstract class AbstractControllerTestConfiguration<Id extends Serializable> {
    private RequestMethod method;
    private MultiValueMap<String,String> headers;
    private HttpStatus expectedHttpStatus;
    private Map<String,String> queryParams;
    @Nullable
    private Id id;
}
