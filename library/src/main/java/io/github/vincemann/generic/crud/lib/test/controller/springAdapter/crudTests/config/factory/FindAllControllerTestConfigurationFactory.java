package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.factory;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.ControllerIntegrationTest;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.abs.ControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.factory.abs.AbstractControllerTestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.exception.InvalidConfigurationModificationException;
import io.github.vincemann.generic.crud.lib.util.NullAwareBeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.Serializable;

public class FindAllControllerTestConfigurationFactory<E extends IdentifiableEntity<Id>,
                Id extends Serializable>
        extends AbstractControllerTestConfigurationFactory<E, Id, ControllerTestConfiguration<Id>, ControllerTestConfiguration<Id>> {

    public FindAllControllerTestConfigurationFactory(ControllerIntegrationTest<E, Id> context) {
        super(context);
    }

    @Override
    public ControllerTestConfiguration<Id> createSuccessfulDefaultConfig() {
        return ControllerTestConfiguration.<Id>builder()
                .method(RequestMethod.GET)
                .expectedHttpStatus(HttpStatus.OK)
                .build();
    }

    @Override
    public ControllerTestConfiguration<Id> createSuccessfulMergedConfig(ControllerTestConfiguration<Id> modification) throws InvalidConfigurationModificationException {
        ControllerTestConfiguration<Id> config = createSuccessfulDefaultConfig();
        NullAwareBeanUtils.copyProperties(config,modification);
        return config;
    }

    @Override
    public ControllerTestConfiguration<Id> createFailedDefaultConfig() {
        return ControllerTestConfiguration.<Id>builder()
                .method(RequestMethod.GET)
                .expectedHttpStatus(HttpStatus.BAD_REQUEST)
                .build();
    }

    @Override
    public ControllerTestConfiguration<Id> createFailedMergedConfig(ControllerTestConfiguration<Id> modification) throws InvalidConfigurationModificationException {
        ControllerTestConfiguration<Id> config = createFailedDefaultConfig();
        NullAwareBeanUtils.copyProperties(config,modification);
        return config;
    }
}
