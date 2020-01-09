package io.github.vincemann.generic.crud.lib.test.service.testApi.abs;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.service.testApi.abs.RootServiceTestContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;

/**
 * TestServiceApis provide convenience methods for testing a service.
 * They have a root Context and may specify their own more concrete Contexts.
 * @param <E>
 * @param <Id>
 * @param <R>
 */
@Getter
@AllArgsConstructor
public class AbstractServiceTestApi<E extends IdentifiableEntity<Id>, Id extends Serializable,R extends CrudRepository<E,Id>> {
    private RootServiceTestContext<E,Id,R> rootContext;
}
