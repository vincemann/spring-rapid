package io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.factory.update;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.service.RootServiceTestContext;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.factory.abs.AbstractTestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.update.FailedUpdateTestConfiguration;
import io.github.vincemann.generic.crud.lib.util.NullAwareBeanUtils;

import java.io.Serializable;

public class FailedAbstractTestConfigurationFactory<E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends AbstractTestConfigurationFactory<E, Id, FailedUpdateTestConfiguration<E,Id>> {

    public FailedAbstractTestConfigurationFactory(RootServiceTestContext<E, Id> context) {
        super(context);
    }

    @Override
    public FailedUpdateTestConfiguration<E, Id> createDefaultConfig() {
        return FailedUpdateTestConfiguration.<E, Id>builder()
                .full(true)
                .repoEntityEqualChecker(getContext().getDefaultEqualChecker())
                .postUpdateCallback((r,a)-> {})
                .expectedException(Exception.class)
                .build();
    }

    @Override
    public FailedUpdateTestConfiguration<E, Id> createMergedConfig(FailedUpdateTestConfiguration<E, Id> modification) {
        FailedUpdateTestConfiguration<E, Id> config = createDefaultConfig();
        if(config.equals(modification)){
            return config;
        }
        NullAwareBeanUtils.copyProperties(config,modification);
        return config;
    }
}
