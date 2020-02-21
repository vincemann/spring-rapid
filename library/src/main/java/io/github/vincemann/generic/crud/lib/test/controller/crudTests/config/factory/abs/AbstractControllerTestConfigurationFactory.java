package io.github.vincemann.generic.crud.lib.test.controller.crudTests.config.factory.abs;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.InitializingTest;
import io.github.vincemann.generic.crud.lib.test.TestContextAware;
import io.github.vincemann.generic.crud.lib.test.controller.ControllerIntegrationTest;
import io.github.vincemann.generic.crud.lib.test.TestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.controller.crudTests.config.SuccessfulCreateControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.crudTests.config.abs.ControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.abs.AbstractControllerTest;
import io.github.vincemann.generic.crud.lib.test.exception.InvalidConfigurationModificationException;
import io.github.vincemann.generic.crud.lib.util.NullAwareBeanUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public abstract class AbstractControllerTestConfigurationFactory
                <E extends IdentifiableEntity<Id>,
                Id extends Serializable, SuccessfulC extends ControllerTestConfiguration<Id>,
                FailedC extends ControllerTestConfiguration<Id>>

                    implements TestConfigurationFactory<SuccessfulC,FailedC,ControllerTestConfiguration<Id>,ControllerTestConfiguration<Id>>,
        TestContextAware<ControllerIntegrationTest<E,Id>>
{
    private ControllerIntegrationTest<E,Id> testContext;

    @Override
    public boolean supports(Class<? extends InitializingTest> contextClass) {
        return ControllerIntegrationTest.class.isAssignableFrom(contextClass);
    }

    @Override
    public FailedC createMergedFailedConfig(ControllerTestConfiguration<Id>... modifications) throws InvalidConfigurationModificationException {
        FailedC config = createDefaultFailedConfig();
        for (ControllerTestConfiguration<Id> modification : modifications) {
            NullAwareBeanUtils.copyProperties(config,modification);
        }
        return config;
    }

    @Override
    public SuccessfulC createMergedSuccessfulConfig(ControllerTestConfiguration<Id>... modifications) throws InvalidConfigurationModificationException {
        SuccessfulC config = createDefaultSuccessfulConfig();
        for (ControllerTestConfiguration<Id> modification : modifications) {
            NullAwareBeanUtils.copyProperties(config,modification);
        }
        return config;
    }
}
