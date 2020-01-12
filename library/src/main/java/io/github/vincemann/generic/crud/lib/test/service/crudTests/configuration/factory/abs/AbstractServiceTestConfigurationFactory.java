package io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.factory.abs;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.TestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.service.ServiceTestContext;
import io.github.vincemann.generic.crud.lib.test.exception.InvalidConfigurationModificationException;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.abs.AbstractServiceTestConfiguration;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public abstract class AbstractServiceTestConfigurationFactory
        <E extends IdentifiableEntity<Id>,Id extends Serializable,C extends AbstractServiceTestConfiguration<E,Id>>
                      implements TestConfigurationFactory<C>
{
    private ServiceTestContext<E,Id> context;
}
