package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.factory.update;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.ControllerIntegrationTestContext;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.FailedUpdateControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.abs.ControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.factory.abs.AbstractControllerTestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.exception.InvalidConfigurationModificationException;
import io.github.vincemann.generic.crud.lib.util.NullAwareBeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.Serializable;

public class FailedUpdateControllerTestConfigurationFactory<E extends IdentifiableEntity<Id>,Id extends Serializable,C extends ControllerTestConfiguration<Id>>
        extends AbstractControllerTestConfigurationFactory<E, Id, FailedUpdateControllerTestConfiguration<Id>> {
    public FailedUpdateControllerTestConfigurationFactory(ControllerIntegrationTestContext<E, Id> context) {
        super(context);
    }

    @Override
    public FailedUpdateControllerTestConfiguration<Id> createDefaultConfig() {
        return FailedUpdateControllerTestConfiguration.<Id>Builder()
                .expectedHttpStatus(HttpStatus.BAD_REQUEST)
                .fullUpdate(true)
                .method(RequestMethod.PUT)
                .build();
    }

    @Override
    public FailedUpdateControllerTestConfiguration<Id> createMergedConfig(FailedUpdateControllerTestConfiguration<Id> modification) throws InvalidConfigurationModificationException {
        FailedUpdateControllerTestConfiguration<Id> config = createDefaultConfig();
        NullAwareBeanUtils.copyProperties(config,modification);
        return config;
    }
}
