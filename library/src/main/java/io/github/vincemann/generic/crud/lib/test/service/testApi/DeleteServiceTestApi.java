package io.github.vincemann.generic.crud.lib.test.service.testApi;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.test.service.testApi.abs.ServiceTestApi;
import org.junit.jupiter.api.Assertions;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.Optional;

public class DeleteServiceTestApi<E extends IdentifiableEntity<Id>, Id extends Serializable,R extends CrudRepository<E,Id>>
        extends ServiceTestApi<E,Id,R>
{

    protected void deleteEntityById_ShouldSucceed(Id id) throws EntityNotFoundException, NoIdException {
        //given
        Assertions.assertNotNull(id);
        Optional<E> entityToDelete = repoFindById(id);
        Assertions.assertTrue(entityToDelete.isPresent());

        //when
        getCrudService().deleteById(id);
        //then
        Optional<E> deletedEntity = repoFindById(id);
        Assertions.assertFalse(deletedEntity.isPresent());
    }

    protected <T extends Throwable> T deleteExistingEntityById_ShouldFail(Id id, Class<? extends T> expectedException) throws NoIdException {
        //given
        //entity is present
        Assertions.assertNotNull(id);

        Optional<E> repoEntity = repoFindById(id);
        Assertions.assertTrue(repoEntity.isPresent());

        //when
        T exception = Assertions.assertThrows(expectedException, () -> getCrudService().deleteById(id));
        //then
        //still present
        Optional<E> repoEntityAfterDelete = repoFindById(id);
        Assertions.assertTrue(repoEntityAfterDelete.isPresent());
        return exception;
    }
}
