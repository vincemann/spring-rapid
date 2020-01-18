package io.github.vincemann.generic.crud.lib.test.service.crudTests;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.abs.AbstractServiceTest;
import io.github.vincemann.generic.crud.lib.test.service.ServiceTest;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;

import java.io.Serializable;
import java.util.Optional;

@Getter
public class DeleteServiceTest<E extends IdentifiableEntity<Id>, Id extends Serializable>
    extends AbstractServiceTest<E,Id>
{
    public DeleteServiceTest(ServiceTest<E, Id> testContext) {
        super(testContext);
    }

    public void deleteEntityById_ShouldSucceed(Id id) throws EntityNotFoundException, NoIdException {
        //given
        Assertions.assertNotNull(id);
        Optional<E> entityToDelete = getRootContext().repoFindById(id);
        Assertions.assertTrue(entityToDelete.isPresent());

        //when
        getRootContext().getCrudService().deleteById(id);
        //then
        Optional<E> deletedEntity = getRootContext().repoFindById(id);
        Assertions.assertFalse(deletedEntity.isPresent());
    }

    public  <T extends Throwable> T deleteEntityById_ShouldFail(Id id, Class<? extends T> expectedException) throws NoIdException {
        //given
        //entity is present
        Assertions.assertNotNull(id);

        Optional<E> repoEntity = getRootContext().repoFindById(id);
        Assertions.assertTrue(repoEntity.isPresent());

        //when
        T exception = Assertions.assertThrows(expectedException, () -> getRootContext().getCrudService().deleteById(id));
        //then
        //still present
        Optional<E> repoEntityAfterDelete = getRootContext().repoFindById(id);
        Assertions.assertTrue(repoEntityAfterDelete.isPresent());
        return exception;
    }
}
