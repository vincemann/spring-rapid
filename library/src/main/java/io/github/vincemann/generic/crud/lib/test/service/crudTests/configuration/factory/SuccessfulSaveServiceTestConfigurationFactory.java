package io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.factory;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.service.ServiceTestContext;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.abs.ServiceTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.factory.abs.AbstractServiceTestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.SuccessfulSaveServiceTestConfiguration;
import io.github.vincemann.generic.crud.lib.util.NullAwareBeanUtils;

import java.io.Serializable;

public class SuccessfulSaveServiceTestConfigurationFactory<E extends IdentifiableEntity<Id>, Id extends Serializable>
        extends AbstractServiceTestConfigurationFactory<E, Id, SuccessfulSaveServiceTestConfiguration<E, Id>, ServiceTestConfiguration<E,Id>> {

    public SuccessfulSaveServiceTestConfigurationFactory(ServiceTestContext<E, Id> context) {
        super(context);
    }

    @Override
    public SuccessfulSaveServiceTestConfiguration<E, Id> createSuccessfulDefaultConfig() {
        return SuccessfulSaveServiceTestConfiguration.<E, Id>builder()
                .repoEntityEqualChecker(getContext().getDefaultEqualChecker())
                .returnedEntityEqualChecker(getContext().getDefaultEqualChecker())
                .build();
    }

    @Override
    public SuccessfulSaveServiceTestConfiguration<E, Id> createSuccessfulMergedConfig(SuccessfulSaveServiceTestConfiguration<E, Id> modification) {
        SuccessfulSaveServiceTestConfiguration<E, Id> config = createSuccessfulDefaultConfig();
        NullAwareBeanUtils.copyProperties(config,modification);
        return config;
    }

    @Override
    public ServiceTestConfiguration<E, Id> createFailedDefaultConfig() {
        return new ServiceTestConfiguration<>(getContext().getDefaultEqualChecker());
    }

    @Override
    public ServiceTestConfiguration<E, Id> createFailedMergedConfig(ServiceTestConfiguration<E, Id> modification) {
        ServiceTestConfiguration<E, Id> config = createFailedDefaultConfig();
        NullAwareBeanUtils.copyProperties(config,modification);
        return config;
    }
}
