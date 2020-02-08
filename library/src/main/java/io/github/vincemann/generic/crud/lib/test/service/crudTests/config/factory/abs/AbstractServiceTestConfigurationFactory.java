package io.github.vincemann.generic.crud.lib.test.service.crudTests.config.factory.abs;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.TestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.service.ServiceTest;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.config.abs.ServiceTestConfiguration;
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
    private ServiceTest<E,Id> context;
}
