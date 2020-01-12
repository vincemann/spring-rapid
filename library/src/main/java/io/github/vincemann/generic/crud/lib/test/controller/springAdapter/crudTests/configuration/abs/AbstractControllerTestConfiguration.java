package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.configuration.abs;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.equalChecker.EqualChecker;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.Serializable;
import java.net.URI;
import java.util.Map;

@Getter
@AllArgsConstructor
@Builder
@Setter
public abstract class AbstractControllerTestConfiguration {
    private RequestMethod method;
    private URI uri;
    private MultiValueMap<String,String> headers;
    private HttpStatus expectedHttpStatus;
    private Map<String,String> queryParams;
}
