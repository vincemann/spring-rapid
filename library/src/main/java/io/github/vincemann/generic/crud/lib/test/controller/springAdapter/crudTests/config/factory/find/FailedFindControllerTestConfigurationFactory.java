package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.factory.find;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.ControllerIntegrationTestContext;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.abs.ControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.factory.abs.AbstractControllerTestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.exception.InvalidConfigurationModificationException;
import io.github.vincemann.generic.crud.lib.util.NullAwareBeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.Serializable;

public class FailedFindControllerTestConfigurationFactory<E extends IdentifiableEntity<Id>,Id extends Serializable,C extends ControllerTestConfiguration<Id>> extends AbstractControllerTestConfigurationFactory<E, Id, ControllerTestConfiguration<Id>> {
    public FailedFindControllerTestConfigurationFactory(ControllerIntegrationTestContext<E, Id> context) {
        super(context);
    }

    @Override
    public ControllerTestConfiguration<Id> createDefaultConfig() {
        return ControllerTestConfiguration.<Id>builder()
                .expectedHttpStatus(HttpStatus.NOT_FOUND)
                .method(RequestMethod.GET)
                .build();
    }

    @Override
    public ControllerTestConfiguration<Id> createMergedConfig(ControllerTestConfiguration<Id> modification) throws InvalidConfigurationModificationException {
        ControllerTestConfiguration<Id> config = createDefaultConfig();
        NullAwareBeanUtils.copyProperties(config,modification);
        return config;
    }
}
