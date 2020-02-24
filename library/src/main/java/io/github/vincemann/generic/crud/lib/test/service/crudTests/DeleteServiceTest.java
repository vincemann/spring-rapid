package io.github.vincemann.generic.crud.lib.test.service.crudTests;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.test.equalChecker.FuzzyComparator;
import io.github.vincemann.generic.crud.lib.test.service.ServiceTest;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.Optional;

@Getter
public class DeleteServiceTest<E extends IdentifiableEntity<Id>, Id extends Serializable>
    extends ServiceTest<E,Id>
{

    public DeleteServiceTest(CrudService<E, Id, ? extends CrudRepository<E, Id>> crudService, CrudRepository<E, Id> repository, FuzzyComparator<E> defaultFuzzyEqualChecker, FuzzyComparator<E> defaultPartialUpdateFuzzyEqualChecker) {
        super(crudService, repository, defaultFuzzyEqualChecker, defaultPartialUpdateFuzzyEqualChecker);
    }

    public void deleteEntityById_ShouldSucceed(Id id) throws EntityNotFoundException, NoIdException {
        //given
        Assertions.assertNotNull(id);
        Optional<E> entityToDelete = repoFindById(id);
        Assertions.assertTrue(entityToDelete.isPresent());

        //when
        getServiceUnderTest().deleteById(id);
        //then
        Optional<E> deletedEntity = repoFindById(id);
        Assertions.assertFalse(deletedEntity.isPresent());
    }

    public  <T extends Throwable> T deleteEntityById_ShouldFail(Id id, Class<? extends T> expectedException) throws NoIdException {
        //given
        //entity is present
        Assertions.assertNotNull(id);

        Optional<E> repoEntity = repoFindById(id);
        Assertions.assertTrue(repoEntity.isPresent());

        //when
        T exception = Assertions.assertThrows(expectedException, () -> getServiceUnderTest().deleteById(id));
        //then
        //still present
        Optional<E> repoEntityAfterDelete = repoFindById(id);
        Assertions.assertTrue(repoEntityAfterDelete.isPresent());
        return exception;
    }
}
