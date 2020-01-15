package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.factory.update;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.ControllerIntegrationTestContext;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.SuccessfulUpdateControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.abs.ControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.factory.abs.AbstractControllerTestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.exception.InvalidConfigurationModificationException;
import io.github.vincemann.generic.crud.lib.util.NullAwareBeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.Serializable;

public class SuccessfulUpdateControllerTestConfigurationFactory<E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends AbstractControllerTestConfigurationFactory<E, Id, SuccessfulUpdateControllerTestConfiguration<E,Id>> {
    public SuccessfulUpdateControllerTestConfigurationFactory(ControllerIntegrationTestContext<E, Id> context) {
        super(context);
    }

    @Override
    public SuccessfulUpdateControllerTestConfiguration<E, Id> createDefaultConfig() {
        return SuccessfulUpdateControllerTestConfiguration.<E,Id>Builder()
                .method(RequestMethod.PUT)
                .expectedHttpStatus(HttpStatus.OK)
                .fullUpdate(true)
                .postUpdateCallback((a,b)->{})
                .build();
    }

    @Override
    public SuccessfulUpdateControllerTestConfiguration<E, Id> createMergedConfig(SuccessfulUpdateControllerTestConfiguration<E, Id> modification) throws InvalidConfigurationModificationException {
        SuccessfulUpdateControllerTestConfiguration<E, Id> config = createDefaultConfig();
        NullAwareBeanUtils.copyProperties(config,modification);
    }
}
