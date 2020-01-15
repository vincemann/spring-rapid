package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.factory.create;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.ControllerIntegrationTestContext;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.SuccessfulCreateControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.factory.abs.AbstractControllerTestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.exception.InvalidConfigurationModificationException;
import io.github.vincemann.generic.crud.lib.util.NullAwareBeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.Serializable;

public class SuccessfulCreateTestConfigurationFactory<E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends AbstractControllerTestConfigurationFactory<E, Id, SuccessfulCreateControllerTestConfiguration<E,Id>> {


    public SuccessfulCreateTestConfigurationFactory(ControllerIntegrationTestContext<E, Id> context) {
        super(context);
    }

    @Override
    public SuccessfulCreateControllerTestConfiguration<E,Id> createDefaultConfig() {
        return SuccessfulCreateControllerTestConfiguration.<E,Id>builder()
                .expectedHttpStatus(HttpStatus.OK)
                .method(RequestMethod.POST)
                .build();
    }

    @Override
    public SuccessfulCreateControllerTestConfiguration<E,Id> createMergedConfig(SuccessfulCreateControllerTestConfiguration<E,Id> modification) throws InvalidConfigurationModificationException {
        SuccessfulCreateControllerTestConfiguration<E,Id> config = createDefaultConfig();
        NullAwareBeanUtils.copyProperties(config,modification);
        return config;
    }
}
