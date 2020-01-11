package io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.factory.update;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.service.RootServiceTestContext;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.exception.InvalidConfigurationModificationException;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.factory.abs.AbstractTestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.update.SuccessfulUpdateTestConfiguration;
import io.github.vincemann.generic.crud.lib.util.NullAwareBeanUtils;

import java.io.Serializable;

public class SuccessfulUpdateTestConfigurationFactory<E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends AbstractTestConfigurationFactory<E, Id, SuccessfulUpdateTestConfiguration<E,Id>> {

    public SuccessfulUpdateTestConfigurationFactory(RootServiceTestContext<E, Id> context) {
        super(context);
    }

    @Override
    public SuccessfulUpdateTestConfiguration<E, Id> createDefaultConfig() {
        return SuccessfulUpdateTestConfiguration.<E,Id>builder()
                .full(true)
                .repoEntityEqualChecker(getContext().getDefaultEqualChecker())
                .returnedEntityEqualChecker(getContext().getDefaultEqualChecker())
                .postUpdateCallback((r,a)-> {})
                .build();
    }

    public SuccessfulUpdateTestConfiguration<E, Id> createMergedConfig(SuccessfulUpdateTestConfiguration<E, Id> modification) throws InvalidConfigurationModificationException {
        SuccessfulUpdateTestConfiguration<E, Id> config = createDefaultConfig();
        if(config.equals(modification)){
            return config;
        }
        NullAwareBeanUtils.copyProperties(config,modification);
        if(modification.getFullUpdate()!=null && modification.getReturnedEntityEqualChecker()!=null){
            if(!modification.getFullUpdate()) {
                throw new InvalidConfigurationModificationException("partial update and repo Entity equal checker must not be combined");
            }
        }
        return config;
    }
}
