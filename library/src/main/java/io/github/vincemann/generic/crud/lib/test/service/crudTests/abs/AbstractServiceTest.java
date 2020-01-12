package io.github.vincemann.generic.crud.lib.test.service.crudTests.abs;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.service.ServiceTestContext;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * TestServiceApis provide convenience methods for testing a service.
 * They have a root Context and may specify their own more concrete Contexts.
 * @param <E>
 * @param <Id>
 */
@Getter
@AllArgsConstructor
public class AbstractServiceTest<E extends IdentifiableEntity<Id>, Id extends Serializable> {
    private ServiceTestContext<E,Id> rootContext;
}
