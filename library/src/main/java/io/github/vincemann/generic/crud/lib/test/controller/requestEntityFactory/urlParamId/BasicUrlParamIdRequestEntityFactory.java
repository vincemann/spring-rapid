package io.github.vincemann.generic.crud.lib.test.controller.requestEntityFactory.urlParamId;

import io.github.vincemann.generic.crud.lib.test.controller.crudTests.config.abs.ControllerTestConfiguration;
import org.springframework.http.RequestEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.Serializable;

public class BasicUrlParamIdRequestEntityFactory<Id extends Serializable>
        extends AbstractUrlParamIdRequestEntityFactory<Id, ControllerTestConfiguration<Id>> {
    private String methodName;

    public BasicUrlParamIdRequestEntityFactory(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public RequestEntity<?> create(ControllerTestConfiguration<Id> config, Object body, Id id) {
        UriComponentsBuilder uriBuilder = buildUri(methodName, config, id);
        return createRequestEntity(config,body,uriBuilder.build().toUri());
    }
}
