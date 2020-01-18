package io.github.vincemann.generic.crud.lib.test.service.crudTests;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.abs.AbstractServiceTest;
import io.github.vincemann.generic.crud.lib.test.service.ServiceTest;
import org.junit.jupiter.api.Assertions;

import java.io.Serializable;
import java.util.Optional;

public class FindServiceTest<E extends IdentifiableEntity<Id>, Id extends Serializable>
        extends AbstractServiceTest<E,Id> {

    public FindServiceTest(ServiceTest<E, Id> testContext) {
        super(testContext);
    }

    public E findEntityById_ShouldSucceed(Id id) throws NoIdException {
        Assertions.assertNotNull(id);
        Assertions.assertTrue(getRootContext().repoFindById(id).isPresent());
        Optional<E> foundEntity = getRootContext().serviceFindById(id);
        Assertions.assertTrue(foundEntity.isPresent());
        return foundEntity.get();
    }

    public <T extends Throwable> T findEntityById_ShouldFail(Id id, Class<? extends T> expectedException) throws NoIdException {
        Assertions.assertNotNull(id);
        Assertions.assertTrue(getRootContext().repoFindById(id).isPresent());
        return Assertions.assertThrows(expectedException,() -> getRootContext().serviceFindById(id));
    }

    public void findEntityById_ShouldFail(Id id) throws NoIdException {
        Assertions.assertNotNull(id);
        Assertions.assertTrue(getRootContext().repoFindById(id).isPresent());
        Optional<E> foundEntity = getRootContext().serviceFindById(id);
        Assertions.assertFalse(foundEntity.isPresent());
    }
}
