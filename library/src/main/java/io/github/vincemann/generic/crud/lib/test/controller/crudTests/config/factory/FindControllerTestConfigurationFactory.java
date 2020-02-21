package io.github.vincemann.generic.crud.lib.test.controller.crudTests.config.factory;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.ControllerIntegrationTest;
import io.github.vincemann.generic.crud.lib.test.controller.crudTests.config.SuccessfulFindControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.crudTests.config.abs.ControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.crudTests.config.factory.abs.AbstractControllerTestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.exception.InvalidConfigurationModificationException;
import io.github.vincemann.generic.crud.lib.util.NullAwareBeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.Serializable;

public class FindControllerTestConfigurationFactory<E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends AbstractControllerTestConfigurationFactory<E, Id, SuccessfulFindControllerTestConfiguration<E,Id>,ControllerTestConfiguration<Id>> {


    @Override
    public ControllerTestConfiguration<Id> createDefaultFailedConfig() {
        return ControllerTestConfiguration.<Id>builder()
                .expectedHttpStatus(HttpStatus.NOT_FOUND)
                .method(RequestMethod.GET)
                .build();
    }



    @Override
    public SuccessfulFindControllerTestConfiguration<E,Id> createDefaultSuccessfulConfig() {
        return SuccessfulFindControllerTestConfiguration.<E,Id>Builder()
                .expectedHttpStatus(HttpStatus.OK)
                .postFindCallback((a,b)-> {})
                .method(RequestMethod.GET)
                .build();
    }
}
