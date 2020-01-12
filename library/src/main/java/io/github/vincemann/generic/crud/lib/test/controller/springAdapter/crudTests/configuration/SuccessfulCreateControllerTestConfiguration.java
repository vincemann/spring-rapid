package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.configuration;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.configuration.abs.AbstractControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.equalChecker.EqualChecker;
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
@Setter
public class SuccessfulCreateControllerTestConfiguration<Id extends Serializable> extends AbstractControllerTestConfiguration {
    private EqualChecker<? extends IdentifiableEntity<Id>> createArg_createReturn_equalChecker;

    @Builder
    public SuccessfulCreateControllerTestConfiguration(RequestMethod method, URI uri, MultiValueMap<String, String> headers, HttpStatus expectedHttpStatus, Map<String, String> queryParams, EqualChecker<? extends IdentifiableEntity<Id>> createArg_createReturn_equalChecker) {
        super(method, uri, headers, expectedHttpStatus, queryParams);
        this.createArg_createReturn_equalChecker = createArg_createReturn_equalChecker;
    }
}
