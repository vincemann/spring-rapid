package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.factory.find;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.ControllerIntegrationTestContext;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.SuccessfulFindControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.abs.ControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.factory.abs.AbstractControllerTestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.exception.InvalidConfigurationModificationException;
import io.github.vincemann.generic.crud.lib.util.NullAwareBeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.Serializable;

public class SuccessfulFindControllerTestConfigurationFactory<E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends AbstractControllerTestConfigurationFactory<E, Id, SuccessfulFindControllerTestConfiguration<E,Id>> {


    public SuccessfulFindControllerTestConfigurationFactory(ControllerIntegrationTestContext<E, Id> context) {
        super(context);
    }

    @Override
    public SuccessfulFindControllerTestConfiguration<E, Id> createDefaultConfig() {
        return SuccessfulFindControllerTestConfiguration.<E, Id>builder()
                .expectedHttpStatus(HttpStatus.OK)
                .postFindCallback((a,b)-> {})
                .method(RequestMethod.GET)
                .build();
    }

    @Override
    public SuccessfulFindControllerTestConfiguration<E, Id> createMergedConfig(SuccessfulFindControllerTestConfiguration<E, Id> modification) throws InvalidConfigurationModificationException {
        SuccessfulFindControllerTestConfiguration<E, Id> config = createDefaultConfig();
        NullAwareBeanUtils.copyProperties(config,modification);
        return config;
    }
}
