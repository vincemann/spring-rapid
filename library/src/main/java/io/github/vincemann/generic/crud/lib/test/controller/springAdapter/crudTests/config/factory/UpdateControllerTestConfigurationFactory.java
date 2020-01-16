package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.factory;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.ControllerIntegrationTestContext;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.UpdateControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.factory.abs.AbstractControllerTestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.exception.InvalidConfigurationModificationException;
import io.github.vincemann.generic.crud.lib.util.NullAwareBeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.Serializable;

public class UpdateControllerTestConfigurationFactory<E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends AbstractControllerTestConfigurationFactory<E, Id, UpdateControllerTestConfiguration<E,Id>, FailedUpdateControllerTestConfiguration<Id>> {

    public UpdateControllerTestConfigurationFactory(ControllerIntegrationTestContext<E, Id> context) {
        super(context);
    }

    @Override
    public FailedUpdateControllerTestConfiguration<Id> createFailedDefaultConfig() {
        return FailedUpdateControllerTestConfiguration.<Id>Builder()
                .expectedHttpStatus(HttpStatus.BAD_REQUEST)
                .fullUpdate(true)
                .method(RequestMethod.PUT)
                .build();
    }

    @Override
    public FailedUpdateControllerTestConfiguration<Id> createFailedMergedConfig(FailedUpdateControllerTestConfiguration<Id> modification) throws InvalidConfigurationModificationException {
        FailedUpdateControllerTestConfiguration<Id> config = createFailedDefaultConfig();
        NullAwareBeanUtils.copyProperties(config,modification);
        return config;
    }

    @Override
    public UpdateControllerTestConfiguration<E, Id> createSuccessfulDefaultConfig() {
        return UpdateControllerTestConfiguration.<E,Id>Builder()
                .method(RequestMethod.PUT)
                .expectedHttpStatus(HttpStatus.OK)
                .fullUpdate(true)
                .postUpdateCallback((a,b)->{})
                .build();
    }

    @Override
    public UpdateControllerTestConfiguration<E, Id> createSuccessfulMergedConfig(UpdateControllerTestConfiguration<E, Id> modification) throws InvalidConfigurationModificationException {
        UpdateControllerTestConfiguration<E, Id> config = createSuccessfulDefaultConfig();
        NullAwareBeanUtils.copyProperties(config,modification);
        return config;
    }
}
