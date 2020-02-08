package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.factory;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.ControllerIntegrationTest;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.UpdateControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.abs.ControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.factory.abs.AbstractControllerTestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.exception.InvalidConfigurationModificationException;
import io.github.vincemann.generic.crud.lib.util.NullAwareBeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.Serializable;

public class UpdateControllerTestConfigurationFactory<E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends AbstractControllerTestConfigurationFactory<E, Id, UpdateControllerTestConfiguration<E,Id>, UpdateControllerTestConfiguration<E,Id>> {

    public UpdateControllerTestConfigurationFactory(ControllerIntegrationTest<E, Id> context) {
        super(context);
    }

    @Override
    public UpdateControllerTestConfiguration<E,Id> createDefaultFailedConfig() {
        return UpdateControllerTestConfiguration.<E,Id>Builder()
                .expectedHttpStatus(HttpStatus.BAD_REQUEST)
                .fullUpdate(true)
                .method(RequestMethod.PUT)
                .postUpdateCallback((a)->{})
                .build();
    }


    @Override
    public UpdateControllerTestConfiguration<E,Id> createMergedSuccessfulConfig(ControllerTestConfiguration<Id>... modifications) throws InvalidConfigurationModificationException {
        UpdateControllerTestConfiguration<E,Id> config = createDefaultSuccessfulConfig();
        for (ControllerTestConfiguration<Id> modification : modifications) {
            NullAwareBeanUtils.copyProperties(config,modification);
        }
        return config;
    }

    @Override
    public UpdateControllerTestConfiguration<E, Id> createDefaultSuccessfulConfig() {
        return UpdateControllerTestConfiguration.<E,Id>Builder()
                .method(RequestMethod.PUT)
                .expectedHttpStatus(HttpStatus.OK)
                .fullUpdate(true)
                .postUpdateCallback((a)->{})
                .build();
    }

    @Override
    public UpdateControllerTestConfiguration<E, Id> createMergedFailedConfig(ControllerTestConfiguration<Id>... modifications) throws InvalidConfigurationModificationException {
        UpdateControllerTestConfiguration<E, Id> config = createDefaultFailedConfig();
        for (ControllerTestConfiguration<Id> modification : modifications) {
            NullAwareBeanUtils.copyProperties(config,modification);
        }
        return config;
    }

}
