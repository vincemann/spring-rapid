package io.github.vincemann.generic.crud.lib.test.controller.crudTests.config;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.callback.PostUpdateControllerTestCallback;
import io.github.vincemann.generic.crud.lib.test.controller.crudTests.config.abs.ControllerTestConfiguration;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
public class UpdateControllerTestConfiguration<E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends ControllerTestConfiguration<Id> {
    private Boolean fullUpdate;
    private PostUpdateControllerTestCallback<E,Id> postUpdateCallback;

    @Builder(builderMethodName = "Builder")
    public UpdateControllerTestConfiguration(RequestMethod method, MultiValueMap<String, String> headers, HttpStatus expectedHttpStatus, Map<String, String> queryParams, Boolean fullUpdate, PostUpdateControllerTestCallback<E, Id> postUpdateCallback) {
        super(method, headers, expectedHttpStatus, queryParams);
        this.fullUpdate = fullUpdate;
        this.postUpdateCallback = postUpdateCallback;
    }
}
