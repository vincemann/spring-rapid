package io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.factory.save;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.service.RootServiceTestContext;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.exception.InvalidConfigurationModificationException;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.factory.abs.AbstractTestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.save.SuccessfulSaveTestConfiguration;
import io.github.vincemann.generic.crud.lib.util.NullAwareBeanUtils;

import java.io.Serializable;

public class SuccessfulSaveTestConfigurationFactory<E extends IdentifiableEntity<Id>, Id extends Serializable>
        extends AbstractTestConfigurationFactory<E, Id, SuccessfulSaveTestConfiguration<E, Id>> {

    public SuccessfulSaveTestConfigurationFactory(RootServiceTestContext<E, Id> context) {
        super(context);
    }

    @Override
    public SuccessfulSaveTestConfiguration<E, Id> createDefaultConfig() {
        return SuccessfulSaveTestConfiguration.<E, Id>builder()
                .repoEntityEqualChecker(getContext().getDefaultEqualChecker())
                .returnedEntityEqualChecker(getContext().getDefaultEqualChecker())
                .build();
    }

    @Override
    public SuccessfulSaveTestConfiguration<E, Id> createMergedConfig(SuccessfulSaveTestConfiguration<E, Id> modification) {
        SuccessfulSaveTestConfiguration<E, Id> config = createDefaultConfig();
        NullAwareBeanUtils.copyProperties(config,modification);
        return config;
    }
}
