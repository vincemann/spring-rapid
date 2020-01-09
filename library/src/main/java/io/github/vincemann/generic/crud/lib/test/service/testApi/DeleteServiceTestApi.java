package io.github.vincemann.generic.crud.lib.test.service.testApi;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.test.service.testApi.abs.AbstractServiceTestApi;
import io.github.vincemann.generic.crud.lib.test.service.testApi.abs.RootServiceTestContext;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.Optional;

@Getter
public class DeleteServiceTestApi<E extends IdentifiableEntity<Id>, Id extends Serializable,R extends CrudRepository<E,Id>>
    extends AbstractServiceTestApi<E,Id,R>
{
    public DeleteServiceTestApi(RootServiceTestContext<E, Id, R> serviceTestContext) {
        super(serviceTestContext);
    }

    protected void deleteEntityById_ShouldSucceed(Id id) throws EntityNotFoundException, NoIdException {
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

    protected <T extends Throwable> T deleteExistingEntityById_ShouldFail(Id id, Class<? extends T> expectedException) throws NoIdException {
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
