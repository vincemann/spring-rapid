package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.configuration.factory;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.ControllerIntegrationTestContext;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.configuration.SuccessfulCreateControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.configuration.factory.abs.AbstractControllerTestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.exception.InvalidConfigurationModificationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.Serializable;

public class SuccessfulCreateTestConfigurationFactory<E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends AbstractControllerTestConfigurationFactory<E, Id, SuccessfulCreateControllerTestConfiguration<Id>> {


    public SuccessfulCreateTestConfigurationFactory(ControllerIntegrationTestContext<E, Id> context) {
        super(context);
    }

    @Override
    public SuccessfulCreateControllerTestConfiguration<Id> createDefaultConfig() {
        return SuccessfulCreateControllerTestConfiguration.<Id>builder()
                .expectedHttpStatus(HttpStatus.OK)
                .method(RequestMethod.POST)
                .id(null)
                .createArg_createReturn_equalChecker(getContext().getDefaultDtoEqualChecker())
                .build();
    }

    @Override
    public SuccessfulCreateControllerTestConfiguration<Id> createMergedConfig(SuccessfulCreateControllerTestConfiguration<Id> modification) throws InvalidConfigurationModificationException {
        return null;
    }
}
