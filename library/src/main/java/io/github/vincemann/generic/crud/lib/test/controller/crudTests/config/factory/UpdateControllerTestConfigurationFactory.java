package io.github.vincemann.generic.crud.lib.test.controller.crudTests.config.factory;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.ControllerIntegrationTest;
import io.github.vincemann.generic.crud.lib.test.controller.crudTests.config.UpdateControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.crudTests.config.abs.ControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.crudTests.config.factory.abs.AbstractControllerTestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.exception.InvalidConfigurationModificationException;
import io.github.vincemann.generic.crud.lib.util.NullAwareBeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.Serializable;

public class UpdateControllerTestConfigurationFactory<E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends AbstractControllerTestConfigurationFactory<E, Id, UpdateControllerTestConfiguration<E,Id>, UpdateControllerTestConfiguration<E,Id>>
{
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
    public UpdateControllerTestConfiguration<E, Id> createDefaultSuccessfulConfig() {
        return UpdateControllerTestConfiguration.<E,Id>Builder()
                .method(RequestMethod.PUT)
                .expectedHttpStatus(HttpStatus.OK)
                .fullUpdate(true)
                .postUpdateCallback((a)->{})
                .build();
    }
}
