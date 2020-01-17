package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.callback.PostFindTestCallback;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.abs.ControllerTestConfiguration;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.Serializable;
import java.util.Map;

@Getter
public class SuccessfulFindControllerTestConfiguration<E extends IdentifiableEntity<Id>, Id extends Serializable>
        extends ControllerTestConfiguration<Id> {

    private PostFindTestCallback<E,Id> postFindCallback;

    @Builder(builderMethodName = "Builder")
    public SuccessfulFindControllerTestConfiguration(RequestMethod method, MultiValueMap<String, String> headers, HttpStatus expectedHttpStatus, Map<String, String> queryParams, PostFindTestCallback<E, Id> postFindCallback) {
        super(method, headers, expectedHttpStatus, queryParams);
        this.postFindCallback = postFindCallback;
    }
}
