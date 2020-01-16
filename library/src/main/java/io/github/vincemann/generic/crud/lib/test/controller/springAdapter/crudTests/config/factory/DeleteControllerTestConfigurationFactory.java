package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.factory;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.ControllerIntegrationTestContext;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.abs.ControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.factory.abs.AbstractControllerTestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.exception.InvalidConfigurationModificationException;
import io.github.vincemann.generic.crud.lib.util.NullAwareBeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.Serializable;

public class DeleteControllerTestConfigurationFactory<E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends AbstractControllerTestConfigurationFactory<E, Id, ControllerTestConfiguration<Id>,ControllerTestConfiguration<Id>> {

    public DeleteControllerTestConfigurationFactory(ControllerIntegrationTestContext<E, Id> context) {
        super(context);
    }

    @Override
    public ControllerTestConfiguration<Id> createFailedDefaultConfig() {
        return ControllerTestConfiguration.<Id>builder()
                .expectedHttpStatus(HttpStatus.NOT_FOUND)
                .method(RequestMethod.DELETE)
                .build();
    }

    @Override
    public ControllerTestConfiguration<Id> createFailedMergedConfig(ControllerTestConfiguration<Id> modification) throws InvalidConfigurationModificationException {
        ControllerTestConfiguration<Id> config = createSuccessfulDefaultConfig();
        NullAwareBeanUtils.copyProperties(config,modification);
        return config;
    }

    @Override
    public ControllerTestConfiguration<Id> createSuccessfulDefaultConfig() {
        return ControllerTestConfiguration.<Id>builder()
                .expectedHttpStatus(HttpStatus.OK)
                .method(RequestMethod.DELETE)
                .build();
    }

    @Override
    public ControllerTestConfiguration<Id> createSuccessfulMergedConfig(ControllerTestConfiguration<Id> modification) throws InvalidConfigurationModificationException {
        ControllerTestConfiguration<Id> config = createSuccessfulDefaultConfig();
        NullAwareBeanUtils.copyProperties(config,modification);
        return config;
    }
}
