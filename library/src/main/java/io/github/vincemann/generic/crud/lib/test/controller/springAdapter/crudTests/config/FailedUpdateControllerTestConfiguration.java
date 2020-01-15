package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config;

import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.abs.ControllerTestConfiguration;
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
public class FailedUpdateControllerTestConfiguration<Id extends Serializable> extends ControllerTestConfiguration<Id> {
    private Boolean fullUpdate;

    @Builder(builderMethodName = "Builder")
    public FailedUpdateControllerTestConfiguration(RequestMethod method, MultiValueMap<String, String> headers, HttpStatus expectedHttpStatus, Map<String, String> queryParams, Boolean fullUpdate) {
        super(method, headers, expectedHttpStatus, queryParams);
        this.fullUpdate = fullUpdate;
    }
}
