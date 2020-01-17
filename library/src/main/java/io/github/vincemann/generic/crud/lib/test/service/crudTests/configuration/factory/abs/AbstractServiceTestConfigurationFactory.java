package io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.factory.abs;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.TestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.abs.ControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.service.ServiceTestContext;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.abs.ServiceTestConfiguration;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public abstract class AbstractServiceTestConfigurationFactory

                <E extends IdentifiableEntity<Id>,
                Id extends Serializable,
                SuccessfulC extends ServiceTestConfiguration<E,Id>,
                FailedC extends ServiceTestConfiguration<E,Id>>

                            implements TestConfigurationFactory<SuccessfulC,FailedC, ServiceTestConfiguration<E,Id>,ServiceTestConfiguration<E,Id>>
{
    private ServiceTestContext<E,Id> context;
}
