package io.github.vincemann.generic.crud.lib.test.service.crudTests.config.factory;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.service.ServiceTest;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.config.SuccessfulSaveServiceTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.config.abs.ServiceTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.config.factory.abs.AbstractServiceTestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.util.NullAwareBeanUtils;

import java.io.Serializable;

public class SaveServiceTestConfigurationFactory<E extends IdentifiableEntity<Id>, Id extends Serializable>
        extends AbstractServiceTestConfigurationFactory<E, Id, SuccessfulSaveServiceTestConfiguration<E, Id>, ServiceTestConfiguration<E,Id>> {

    public SaveServiceTestConfigurationFactory(ServiceTest<E, Id> context) {
        super(context);
    }

    @Override
    public SuccessfulSaveServiceTestConfiguration<E, Id> createDefaultSuccessfulConfig() {
        return SuccessfulSaveServiceTestConfiguration.<E, Id>builder()
                .repoEntityEqualChecker(getContext().getDefaultEqualChecker())
                .returnedEntityEqualChecker(getContext().getDefaultEqualChecker())
                .build();
    }

    @Override
    public SuccessfulSaveServiceTestConfiguration<E, Id> createMergedSuccessfulConfig(ServiceTestConfiguration<E, Id>... modifications) {
        SuccessfulSaveServiceTestConfiguration<E, Id> config = createDefaultSuccessfulConfig();
        for (ServiceTestConfiguration<E, Id> modification : modifications) {
            NullAwareBeanUtils.copyProperties(config,modification);
        }
        return config;
    }

    @Override
    public ServiceTestConfiguration<E, Id> createDefaultFailedConfig() {
        return new ServiceTestConfiguration<>(getContext().getDefaultEqualChecker());
    }

    @Override
    public ServiceTestConfiguration<E, Id> createMergedFailedConfig(ServiceTestConfiguration<E, Id>... modifications) {
        ServiceTestConfiguration<E, Id> config = createDefaultFailedConfig();
        for (ServiceTestConfiguration<E, Id> modification : modifications) {
            NullAwareBeanUtils.copyProperties(config,modification);
        }
        return config;
    }
}
