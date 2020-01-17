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
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class UpdateControllerTestConfigurationFactory<E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends AbstractControllerTestConfigurationFactory<E, Id, UpdateControllerTestConfiguration<E,Id>, UpdateControllerTestConfiguration<E,Id>> {

    public UpdateControllerTestConfigurationFactory(ControllerIntegrationTestContext<E, Id> context) {
        super(context);
    }

    @Override
    public UpdateControllerTestConfiguration<E,Id> createFailedDefaultConfig() {
        return UpdateControllerTestConfiguration.<E,Id>Builder()
                .expectedHttpStatus(HttpStatus.BAD_REQUEST)
                .fullUpdate(true)
                .method(RequestMethod.PUT)
                .postUpdateCallback((a)->{})
                .build();
    }

    @Override
    public UpdateControllerTestConfiguration<E,Id> createFailedMergedConfig(UpdateControllerTestConfiguration<E,Id> modification) throws InvalidConfigurationModificationException {
        UpdateControllerTestConfiguration<E,Id> config = createFailedDefaultConfig();
        NullAwareBeanUtils.copyProperties(config,modification);
        return config;
    }

    @Override
    public UpdateControllerTestConfiguration<E, Id> createSuccessfulDefaultConfig() {
        return UpdateControllerTestConfiguration.<E,Id>Builder()
                .method(RequestMethod.PUT)
                .expectedHttpStatus(HttpStatus.OK)
                .fullUpdate(true)
                .postUpdateCallback((a)->{})
                .build();
    }

    @Override
    public UpdateControllerTestConfiguration<E, Id> createSuccessfulMergedConfig(UpdateControllerTestConfiguration<E, Id> modification) throws InvalidConfigurationModificationException {
        UpdateControllerTestConfiguration<E, Id> config = createSuccessfulDefaultConfig();
        NullAwareBeanUtils.copyProperties(config,modification);
        return config;
    }

}
