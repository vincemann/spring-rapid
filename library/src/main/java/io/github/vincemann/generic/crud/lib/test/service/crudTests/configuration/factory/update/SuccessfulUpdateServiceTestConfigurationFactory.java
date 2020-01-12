package io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.factory.update;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.service.ServiceTestContext;
import io.github.vincemann.generic.crud.lib.test.exception.InvalidConfigurationModificationException;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.factory.abs.AbstractServiceTestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.update.SuccessfulUpdateServiceTestConfiguration;
import io.github.vincemann.generic.crud.lib.util.NullAwareBeanUtils;

import java.io.Serializable;

public class SuccessfulUpdateServiceTestConfigurationFactory<E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends AbstractServiceTestConfigurationFactory<E, Id, SuccessfulUpdateServiceTestConfiguration<E,Id>> {

    public SuccessfulUpdateServiceTestConfigurationFactory(ServiceTestContext<E, Id> context) {
        super(context);
    }

    @Override
    public SuccessfulUpdateServiceTestConfiguration<E, Id> createDefaultConfig() {
        return SuccessfulUpdateServiceTestConfiguration.<E,Id>builder()
                .full(true)
                .repoEntityEqualChecker(getContext().getDefaultEqualChecker())
                .returnedEntityEqualChecker(getContext().getDefaultEqualChecker())
                .postUpdateCallback((r,a)-> {})
                .build();
    }

    public SuccessfulUpdateServiceTestConfiguration<E, Id> createMergedConfig(SuccessfulUpdateServiceTestConfiguration<E, Id> modification) throws InvalidConfigurationModificationException {
        SuccessfulUpdateServiceTestConfiguration<E, Id> config = createDefaultConfig();
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
