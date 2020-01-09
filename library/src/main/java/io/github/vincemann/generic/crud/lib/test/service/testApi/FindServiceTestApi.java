package io.github.vincemann.generic.crud.lib.test.service.testApi;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.test.service.testApi.abs.ServiceTestApi;
import org.junit.jupiter.api.Assertions;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.Optional;

public class FindServiceTestApi<E extends IdentifiableEntity<Id>, Id extends Serializable,R extends CrudRepository<E,Id>>
        extends ServiceTestApi<E,Id,R> {

    protected E findEntityById_ShouldSucceed(Id id) throws NoIdException {
        Assertions.assertNotNull(id);
        Assertions.assertTrue(repoFindById(id).isPresent());
        Optional<E> foundEntity = serviceFindById(id);
        Assertions.assertTrue(foundEntity.isPresent());
        return foundEntity.get();
    }

    protected <T extends Throwable> T findExistingEntityById_ShouldFail(Id id, Class<? extends T> expectedException) throws NoIdException {
        Assertions.assertNotNull(id);
        Assertions.assertTrue(repoFindById(id).isPresent());
        return Assertions.assertThrows(expectedException,() -> serviceFindById(id));
    }

    protected void findExistingEntityById_ShouldFail(Id id) throws NoIdException {
        Assertions.assertNotNull(id);
        Assertions.assertTrue(repoFindById(id).isPresent());
        Optional<E> foundEntity = serviceFindById(id);
        Assertions.assertFalse(foundEntity.isPresent());
    }
}
