package io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.factory.save;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.service.RootServiceTestContext;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.abs.AbstractTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.exception.InvalidConfigurationModificationException;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.factory.abs.AbstractTestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.save.FailedSaveAbstractTestConfiguration;
import io.github.vincemann.generic.crud.lib.util.NullAwareBeanUtils;

import java.io.Serializable;

/*
public class FailedSaveTestConfigurationFactory<E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends AbstractTestConfigurationFactory<E, Id, FailedSaveAbstractTestConfiguration<E,Id>> {

    public FailedSaveTestConfigurationFactory(RootServiceTestContext<E, Id> context) {
        super(context);
    }

    @Override
    public FailedSaveAbstractTestConfiguration<E, Id> createDefaultConfig() {
        return new FailedSaveAbstractTestConfiguration<>(getContext().getDefaultEqualChecker());
    }

    @Override
    public FailedSaveAbstractTestConfiguration<E, Id> createMergedConfig(FailedSaveAbstractTestConfiguration<E, Id> modification) {
        FailedSaveAbstractTestConfiguration<E, Id> config = createDefaultConfig();
        NullAwareBeanUtils.copyProperties(config,modification);
        return config;
    }
}
*/