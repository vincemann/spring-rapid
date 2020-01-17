package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.factory;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.ControllerIntegrationTestContext;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.SuccessfulCreateControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.abs.ControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.factory.abs.AbstractControllerTestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.exception.InvalidConfigurationModificationException;
import io.github.vincemann.generic.crud.lib.util.NullAwareBeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.Serializable;

public class CreateTestConfigurationFactory<E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends AbstractControllerTestConfigurationFactory<E, Id, SuccessfulCreateControllerTestConfiguration<E,Id>,ControllerTestConfiguration<Id>> {


    public CreateTestConfigurationFactory(ControllerIntegrationTestContext<E, Id> context) {
        super(context);
    }

    @Override
    public SuccessfulCreateControllerTestConfiguration<E,Id> createSuccessfulDefaultConfig() {
        return SuccessfulCreateControllerTestConfiguration.<E,Id>Builder()
                .expectedHttpStatus(HttpStatus.OK)
                .method(RequestMethod.POST)
                .postCreateCallback((a,b)->{})
                .build();
    }

    @Override
    public SuccessfulCreateControllerTestConfiguration<E,Id> createSuccessfulMergedConfig(SuccessfulCreateControllerTestConfiguration<E,Id> modification) throws InvalidConfigurationModificationException {
        SuccessfulCreateControllerTestConfiguration<E,Id> config = createSuccessfulDefaultConfig();
        NullAwareBeanUtils.copyProperties(config,modification);
        return config;
    }


    @Override
    public ControllerTestConfiguration<Id> createFailedDefaultConfig() {
        return ControllerTestConfiguration.<Id>builder()
                .expectedHttpStatus(HttpStatus.BAD_REQUEST)
                .method(RequestMethod.POST)
                .build();
    }

    @Override
    public ControllerTestConfiguration<Id> createFailedMergedConfig(ControllerTestConfiguration<Id> modification) throws InvalidConfigurationModificationException {
        ControllerTestConfiguration<Id> config = createSuccessfulDefaultConfig();
        NullAwareBeanUtils.copyProperties(config,modification);
        return config;
    }
}
