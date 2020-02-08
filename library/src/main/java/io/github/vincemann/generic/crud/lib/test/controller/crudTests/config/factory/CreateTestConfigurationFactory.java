package io.github.vincemann.generic.crud.lib.test.controller.crudTests.config.factory;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.ControllerIntegrationTest;
import io.github.vincemann.generic.crud.lib.test.controller.crudTests.config.SuccessfulCreateControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.crudTests.config.abs.ControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.crudTests.config.factory.abs.AbstractControllerTestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.exception.InvalidConfigurationModificationException;
import io.github.vincemann.generic.crud.lib.util.NullAwareBeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.Serializable;

public class CreateTestConfigurationFactory<E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends AbstractControllerTestConfigurationFactory<E, Id, SuccessfulCreateControllerTestConfiguration<E,Id>,ControllerTestConfiguration<Id>> {


    public CreateTestConfigurationFactory(ControllerIntegrationTest<E, Id> context) {
        super(context);
    }

    @Override
    public SuccessfulCreateControllerTestConfiguration<E,Id> createDefaultSuccessfulConfig() {
        return SuccessfulCreateControllerTestConfiguration.<E,Id>Builder()
                .expectedHttpStatus(HttpStatus.OK)
                .method(RequestMethod.POST)
                .postCreateCallback((a,b)->{})
                .build();
    }


    @Override
    public SuccessfulCreateControllerTestConfiguration<E,Id> createMergedSuccessfulConfig(ControllerTestConfiguration<Id>... modifications) throws InvalidConfigurationModificationException {
        SuccessfulCreateControllerTestConfiguration<E,Id> config = createDefaultSuccessfulConfig();
        for (ControllerTestConfiguration<Id> modification : modifications) {
            NullAwareBeanUtils.copyProperties(config,modification);
        }
        return config;
    }


    @Override
    public ControllerTestConfiguration<Id> createDefaultFailedConfig() {
        return ControllerTestConfiguration.<Id>builder()
                .expectedHttpStatus(HttpStatus.BAD_REQUEST)
                .method(RequestMethod.POST)
                .build();
    }

    @Override
    public ControllerTestConfiguration<Id> createMergedFailedConfig(ControllerTestConfiguration<Id>... modifications) throws InvalidConfigurationModificationException {
        ControllerTestConfiguration<Id> config = createDefaultSuccessfulConfig();
        for (ControllerTestConfiguration<Id> modification : modifications) {
            NullAwareBeanUtils.copyProperties(config,modification);
        }
        return config;
    }
}
