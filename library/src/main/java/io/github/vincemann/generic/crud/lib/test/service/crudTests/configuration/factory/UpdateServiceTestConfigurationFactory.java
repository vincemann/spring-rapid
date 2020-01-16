package io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.factory;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.exception.InvalidConfigurationModificationException;
import io.github.vincemann.generic.crud.lib.test.service.ServiceTestContext;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.factory.abs.AbstractServiceTestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.update.FailedUpdateServiceTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.update.SuccessfulUpdateServiceTestConfiguration;
import io.github.vincemann.generic.crud.lib.util.NullAwareBeanUtils;

import java.io.Serializable;

public class UpdateServiceTestConfigurationFactory<E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends AbstractServiceTestConfigurationFactory<E, Id, SuccessfulUpdateServiceTestConfiguration<E,Id>,FailedUpdateServiceTestConfiguration<E,Id>> {

    public UpdateServiceTestConfigurationFactory(ServiceTestContext<E, Id> context) {
        super(context);
    }

    @Override
    public SuccessfulUpdateServiceTestConfiguration<E, Id> createSuccessfulDefaultConfig() {
        return SuccessfulUpdateServiceTestConfiguration.<E,Id>builder()
                .fullUpdate(true)
                .repoEntityEqualChecker(getContext().getDefaultEqualChecker())
                .returnedEntityEqualChecker(getContext().getDefaultEqualChecker())
                .postUpdateCallback((r,a)-> {})
                .build();
    }

    public SuccessfulUpdateServiceTestConfiguration<E, Id> createSuccessfulMergedConfig(SuccessfulUpdateServiceTestConfiguration<E, Id> modification) throws InvalidConfigurationModificationException {
        SuccessfulUpdateServiceTestConfiguration<E, Id> config = createSuccessfulDefaultConfig();
        if(config.equals(modification)){
            return config;
        }
        NullAwareBeanUtils.copyProperties(config,modification);
        if(modification.getFullUpdate()!=null && modification.getReturnedEntityEqualChecker()!=null){
            if(!modification.getFullUpdate()) {
                throw new InvalidConfigurationModificationException("partial update and repo Entity equal checker must not be combined");
            }
        }
        //partial update should use partialUpdateEqualChecker, if user did not specify own
        if(config.getFullUpdate()!=null){
            if(!config.getFullUpdate()){
                if(modification.getReturnedEntityEqualChecker()==null){
                    config.setReturnedEntityEqualChecker(getContext().getDefaultPartialUpdateEqualChecker());
                }
                if(modification.getRepoEntityEqualChecker()==null){
                    config.setRepoEntityEqualChecker(getContext().getDefaultPartialUpdateEqualChecker());
                }
            }
        }
        return config;
    }



    @Override
    public FailedUpdateServiceTestConfiguration<E, Id> createFailedDefaultConfig() {
        return FailedUpdateServiceTestConfiguration.<E, Id>builder()
                .fullUpdate(true)
                .repoEntityEqualChecker(getContext().getDefaultEqualChecker())
                .postUpdateCallback((r,a)-> {})
                .expectedException(Exception.class)
                .build();
    }

    @Override
    public FailedUpdateServiceTestConfiguration<E, Id> createFailedMergedConfig(FailedUpdateServiceTestConfiguration<E, Id> modification) {
        FailedUpdateServiceTestConfiguration<E, Id> config = createFailedDefaultConfig();
        if(config.equals(modification)){
            return config;
        }
        NullAwareBeanUtils.copyProperties(config,modification);
        //partial update should use partialUpdateEqualChecker, if user did not specify own
        if(config.getFullUpdate()!=null){
            if(!config.getFullUpdate()){
                if(modification.getRepoEntityEqualChecker()==null){
                    config.setRepoEntityEqualChecker(getContext().getDefaultPartialUpdateEqualChecker());
                }
            }
        }
        return config;
    }
}
