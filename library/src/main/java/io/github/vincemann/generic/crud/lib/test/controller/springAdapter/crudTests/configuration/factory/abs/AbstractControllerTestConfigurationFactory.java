package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.configuration.factory.abs;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.ControllerIntegrationTestContext;
import io.github.vincemann.generic.crud.lib.test.TestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.configuration.abs.AbstractControllerTestConfiguration;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public abstract class AbstractControllerTestConfigurationFactory
        <E extends IdentifiableEntity<Id>,Id extends Serializable,C extends AbstractControllerTestConfiguration>
                    implements TestConfigurationFactory<C>
{
    private ControllerIntegrationTestContext<E,Id> context;
}
