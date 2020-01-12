package io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.factory.update;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.service.ServiceTestContext;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.factory.abs.AbstractServiceTestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.update.FailedUpdateServiceTestConfiguration;
import io.github.vincemann.generic.crud.lib.util.NullAwareBeanUtils;

import java.io.Serializable;

public class FailedServiceTestConfigurationFactory<E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends AbstractServiceTestConfigurationFactory<E, Id, FailedUpdateServiceTestConfiguration<E,Id>> {

    public FailedServiceTestConfigurationFactory(ServiceTestContext<E, Id> context) {
        super(context);
    }

    @Override
    public FailedUpdateServiceTestConfiguration<E, Id> createDefaultConfig() {
        return FailedUpdateServiceTestConfiguration.<E, Id>builder()
                .fullUpdate(true)
                .repoEntityEqualChecker(getContext().getDefaultEqualChecker())
                .postUpdateCallback((r,a)-> {})
                .expectedException(Exception.class)
                .build();
    }

    @Override
    public FailedUpdateServiceTestConfiguration<E, Id> createMergedConfig(FailedUpdateServiceTestConfiguration<E, Id> modification) {
        FailedUpdateServiceTestConfiguration<E, Id> config = createDefaultConfig();
        if(config.equals(modification)){
            return config;
        }
        NullAwareBeanUtils.copyProperties(config,modification);
        return config;
    }
}
