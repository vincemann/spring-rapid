package io.github.vincemann.generic.crud.lib.test.service.crudTests.config.factory;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.exception.InvalidConfigurationModificationException;
import io.github.vincemann.generic.crud.lib.test.service.ServiceTest;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.config.abs.ServiceTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.config.factory.abs.AbstractServiceTestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.config.update.FailedUpdateServiceTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.config.update.SuccessfulUpdateServiceTestConfiguration;
import io.github.vincemann.generic.crud.lib.util.NullAwareBeanUtils;
import org.modelmapper.ModelMapper;

import java.io.Serializable;

public class UpdateServiceTestConfigurationFactory<E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends AbstractServiceTestConfigurationFactory<E, Id, SuccessfulUpdateServiceTestConfiguration<E,Id>,FailedUpdateServiceTestConfiguration<E,Id>> {

    public UpdateServiceTestConfigurationFactory(ServiceTest<E, Id> context) {
        super(context);
    }

    @Override
    public SuccessfulUpdateServiceTestConfiguration<E, Id> createDefaultSuccessfulConfig() {
        return SuccessfulUpdateServiceTestConfiguration.<E,Id>builder()
                .fullUpdate(true)
                .repoEntityEqualChecker(getContext().getDefaultEqualChecker())
                .returnedEntityEqualChecker(getContext().getDefaultEqualChecker())
                .postUpdateCallback((r,a)-> {})
                .build();
    }


    public SuccessfulUpdateServiceTestConfiguration<E, Id> createMergedSuccessfulConfig(ServiceTestConfiguration<E, Id>... modifications) throws InvalidConfigurationModificationException {
        SuccessfulUpdateServiceTestConfiguration<E, Id> config = createDefaultSuccessfulConfig();
        for (ServiceTestConfiguration<E, Id> modification : modifications) {
            ModelMapper mapper = new ModelMapper();
            //mapping here so we have the more concrete type
            SuccessfulUpdateServiceTestConfiguration<E, Id> mappedModification = mapper.map(modification,SuccessfulUpdateServiceTestConfiguration.class);
            if(config.equals(modification)){
                continue;
            }
            NullAwareBeanUtils.copyProperties(config,mappedModification);
            verifySuccessfulConfig(config,mappedModification);
        }
        return config;
    }

    private void verifySuccessfulConfig(SuccessfulUpdateServiceTestConfiguration<E, Id> base, SuccessfulUpdateServiceTestConfiguration<E, Id>... modifications){
        for (SuccessfulUpdateServiceTestConfiguration<E, Id> modification : modifications) {
            if(modification.getFullUpdate()!=null && modification.getReturnedEntityEqualChecker()!=null){
                if(!modification.getFullUpdate()) {
                    throw new InvalidConfigurationModificationException("partial update and repo Entity equal checker must not be combined");
                }
            }
            //partial update should use partialUpdateEqualChecker, if user did not specify own
            if(base.getFullUpdate()!=null){
                if(!base.getFullUpdate()){
                    if(modification.getReturnedEntityEqualChecker()==null){
                        base.setReturnedEntityEqualChecker(getContext().getDefaultPartialUpdateEqualChecker());
                    }
                    if(modification.getRepoEntityEqualChecker()==null){
                        base.setRepoEntityEqualChecker(getContext().getDefaultPartialUpdateEqualChecker());
                    }
                }
            }
        }
    }



    @Override
    public FailedUpdateServiceTestConfiguration<E, Id> createDefaultFailedConfig() {
        return FailedUpdateServiceTestConfiguration.<E, Id>builder()
                .fullUpdate(true)
                .repoEntityEqualChecker(getContext().getDefaultEqualChecker())
                .postUpdateCallback((r,a)-> {})
                .expectedException(Exception.class)
                .build();
    }

    @Override
    public FailedUpdateServiceTestConfiguration<E, Id> createMergedFailedConfig(ServiceTestConfiguration<E, Id>... modifications) {
        FailedUpdateServiceTestConfiguration<E, Id> config = createDefaultFailedConfig();
        for (ServiceTestConfiguration<E, Id> modification : modifications) {
            if(config.equals(modification)){
                continue;
            }
            NullAwareBeanUtils.copyProperties(config,modification);
            verifyFailedConfig(config,modification);
        }
        return config;
    }

    private void verifyFailedConfig(FailedUpdateServiceTestConfiguration<E, Id> base, ServiceTestConfiguration<E, Id> modification){
        //partial update should use partialUpdateEqualChecker, if user did not specify own
        if(base.getFullUpdate()!=null){
            if(!base.getFullUpdate()){
                if(modification.getRepoEntityEqualChecker()==null){
                    base.setRepoEntityEqualChecker(getContext().getDefaultPartialUpdateEqualChecker());
                }
            }
        }
    }
}
