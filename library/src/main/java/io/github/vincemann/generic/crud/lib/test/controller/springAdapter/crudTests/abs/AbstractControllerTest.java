package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.abs;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.ControllerIntegrationTestContext;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.requestEntityFactory.RequestEntityFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public abstract class AbstractControllerTest<E extends IdentifiableEntity<Id>, Id extends Serializable> {
    private ControllerIntegrationTestContext<E,Id> rootContext;
    private RequestEntityFactory<Id> requestEntityFactory;
}
