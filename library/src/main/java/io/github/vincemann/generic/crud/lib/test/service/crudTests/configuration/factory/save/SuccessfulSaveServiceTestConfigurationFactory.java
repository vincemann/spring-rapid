package io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.factory.save;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.service.ServiceTestContext;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.factory.abs.AbstractServiceTestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.save.SuccessfulSaveServiceTestConfiguration;
import io.github.vincemann.generic.crud.lib.util.NullAwareBeanUtils;

import java.io.Serializable;

public class SuccessfulSaveServiceTestConfigurationFactory<E extends IdentifiableEntity<Id>, Id extends Serializable>
        extends AbstractServiceTestConfigurationFactory<E, Id, SuccessfulSaveServiceTestConfiguration<E, Id>> {

    public SuccessfulSaveServiceTestConfigurationFactory(ServiceTestContext<E, Id> context) {
        super(context);
    }

    @Override
    public SuccessfulSaveServiceTestConfiguration<E, Id> createDefaultConfig() {
        return SuccessfulSaveServiceTestConfiguration.<E, Id>builder()
                .repoEntityEqualChecker(getContext().getDefaultEqualChecker())
                .returnedEntityEqualChecker(getContext().getDefaultEqualChecker())
                .build();
    }

    @Override
    public SuccessfulSaveServiceTestConfiguration<E, Id> createMergedConfig(SuccessfulSaveServiceTestConfiguration<E, Id> modification) {
        SuccessfulSaveServiceTestConfiguration<E, Id> config = createDefaultConfig();
        NullAwareBeanUtils.copyProperties(config,modification);
        return config;
    }
}
