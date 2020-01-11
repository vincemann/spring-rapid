package io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.factory.abs;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.service.RootServiceTestContext;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.exception.InvalidConfigurationModificationException;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.abs.AbstractTestConfiguration;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public abstract class AbstractTestConfigurationFactory<E extends IdentifiableEntity<Id>,Id extends Serializable,C extends AbstractTestConfiguration<E,Id>> {
    private RootServiceTestContext<E,Id> context;

    public abstract C createDefaultConfig();
    public abstract C createMergedConfig(C modification) throws InvalidConfigurationModificationException;
}
