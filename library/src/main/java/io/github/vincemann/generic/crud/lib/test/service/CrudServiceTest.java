package io.github.vincemann.generic.crud.lib.test.service;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.test.equalChecker.EqualChecker;
import io.github.vincemann.generic.crud.lib.test.testExecutionListeners.ResetDatabaseTestExecutionListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Assertions;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.TestExecutionListeners;

import java.io.Serializable;
import java.util.Optional;

/**
 *
 * @param <S>       CrudServiceImplType
 * @param <E>       TestEntityType
 * @param <Id>      Id Type of TestEntityType
 */
@Slf4j
@TestExecutionListeners(
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS,
        listeners = {ResetDatabaseTestExecutionListener.class}
)
public abstract class CrudServiceTest
                <
                        S extends CrudService<E,Id,R>,
                        R extends CrudRepository<E,Id>,
                        E extends IdentifiableEntity<Id>,
                        Id extends Serializable
                >
{


    private S crudService;
    @Getter
    private EqualChecker<E> equalChecker;
    @Getter
    private R repository;

    public CrudServiceTest(S crudService, EqualChecker<E> equalChecker, R repository) {
        this.crudService = crudService;
        this.equalChecker = equalChecker;
        this.repository = repository;
    }


    protected E saveEntity_ShouldSucceed(E entityToSave) throws NoIdException, BadEntityException {
        return saveEntity_ShouldSucceed(entityToSave,equalChecker);
    }
    protected E saveEntity_ShouldSucceed(E entityToSave, EqualChecker<E> equalChecker) throws BadEntityException, NoIdException {
        //given
        Assertions.assertNull(entityToSave.getId());

        //when
        E savedTestEntity = crudService.save(entityToSave);

        //then
        //savedTestEntity = entityToSafe (equal by reference)
        Assertions.assertNotNull(savedTestEntity);
        Assertions.assertNotNull(savedTestEntity.getId());
        Assertions.assertNotEquals(0,savedTestEntity.getId());
        //check if entityToSave and savedTestEntity have same id, should be true
        Assertions.assertEquals(savedTestEntity.getId(),entityToSave.getId());
        Optional<E> savedEntityFromService = crudService.findById(savedTestEntity.getId());
        Assertions.assertTrue(savedEntityFromService.isPresent());
        Assertions.assertTrue(equalChecker.isEqual(entityToSave,savedEntityFromService.get()));

        Optional<E> repoEntity = repository.findById(savedTestEntity.getId());
        Assertions.assertTrue(repoEntity.isPresent());
        return savedTestEntity;
    }

    protected void saveEntity_ShouldFail(E entityToSave, Class<Exception> expectedException) {
        //when
        Assertions.assertThrows(expectedException, () -> crudService.save(entityToSave));
    }



    protected void deleteEntityById_ShouldSucceed(Id id) throws EntityNotFoundException, NoIdException {
        //given
        Assertions.assertNotNull(id);
        Optional<E> entityToDelete = crudService.findById(id);
        Assertions.assertTrue(entityToDelete.isPresent());

        Optional<E> repoEntity = repository.findById(id);
        Assertions.assertTrue(repoEntity.isPresent());

        //when
        crudService.deleteById(id);
        //then
        Optional<E> deletedEntity = crudService.findById(id);
        Assertions.assertFalse(deletedEntity.isPresent());

        Optional<E> repoEntityAfterDelete = repository.findById(id);
        Assertions.assertFalse(repoEntityAfterDelete.isPresent());
    }

    protected void deleteExistingEntityById_ShouldFail(Id id, Class<Exception> expectedException) throws NoIdException {
        //given
        //entity is present
        Assertions.assertNotNull(id);
        Optional<E> entityToDelete = crudService.findById(id);
        Assertions.assertTrue(entityToDelete.isPresent());

        Optional<E> repoEntity = repository.findById(id);
        Assertions.assertTrue(repoEntity.isPresent());

        //when
        Assertions.assertThrows(expectedException,() -> crudService.deleteById(id));
        //then
        //still present
        Optional<E> deletedEntity = crudService.findById(id);
        Assertions.assertTrue(deletedEntity.isPresent());

        Optional<E> repoEntityAfterDelete = repository.findById(id);
        Assertions.assertTrue(repoEntityAfterDelete.isPresent());
    }

    protected E updateEntity_ShouldSucceed(E entityToUpdate, E newEntity) throws BadEntityException, EntityNotFoundException, NoIdException {
        saveEntityForUpdate(entityToUpdate,newEntity);
        return updateEntity_ShouldSucceed(newEntity,equalChecker);
    }

    private void saveEntityForUpdate(E entityToUpdate, E newEntity) throws NoIdException, BadEntityException {
        Assertions.assertNull(entityToUpdate.getId());
        Assertions.assertNull(newEntity.getId());
        E savedEntityToUpdate = saveEntity_ShouldSucceed(entityToUpdate);
        newEntity.setId(savedEntityToUpdate.getId());
    }
    protected E updateEntity_ShouldSucceed(E entityToUpdate, E newEntity,EqualChecker<E> equalChecker) throws BadEntityException, EntityNotFoundException, NoIdException {
        saveEntityForUpdate(entityToUpdate,newEntity);
        return updateEntity_ShouldSucceed(newEntity,equalChecker);
    }

    protected E updateEntity_ShouldSucceed(E newEntity) throws BadEntityException, EntityNotFoundException, NoIdException {
        return updateEntity_ShouldSucceed(newEntity,equalChecker);
    }

    protected E updateEntity_ShouldSucceed(E newEntity, EqualChecker<E> equalChecker) throws NoIdException, BadEntityException, EntityNotFoundException {
        //given
        Assertions.assertNotNull(newEntity);
        Assertions.assertNotNull(newEntity.getId());
        Optional<E> entityToUpdate = crudService.findById(newEntity.getId());
        Assertions.assertTrue(entityToUpdate.isPresent());
        Assertions.assertFalse(equalChecker.isEqual(entityToUpdate.get(),newEntity));

        Optional<E> repoEntity = repository.findById(newEntity.getId());
        Assertions.assertTrue(repoEntity.isPresent());

        //when
        E updatedEntity = crudService.update(newEntity);

        //then
        Assertions.assertEquals(updatedEntity.getId(),newEntity.getId());
        Optional<E> updatedRepoEntity = repository.findById(updatedEntity.getId());
        Assertions.assertTrue(updatedRepoEntity.isPresent());
        Assertions.assertFalse(equalChecker.isEqual(repoEntity.get(),updatedRepoEntity.get()));


        Assertions.assertTrue(equalChecker.isEqual(updatedEntity,newEntity));
        Optional<E> updatedEntityFromService = crudService.findById(newEntity.getId());
        Assertions.assertTrue(updatedEntityFromService.isPresent());
        Assertions.assertTrue(equalChecker.isEqual(updatedEntity,updatedEntityFromService.get()));

        return updatedEntity;
    }

    protected void updateExistingEntity_ShouldFail(E newEntity, Class<Exception> expectedException) throws NoIdException {
        updateExistingEntity_ShouldFail(newEntity,expectedException,equalChecker);
    }

    protected void updateExistingEntity_ShouldFail(E entityToUpdate, E newEntity, Class<Exception> expectedException) throws NoIdException, BadEntityException {
        saveEntityForUpdate(entityToUpdate,newEntity);
        updateExistingEntity_ShouldFail(newEntity,expectedException,equalChecker);
    }

    protected void updateExistingEntity_ShouldFail(E entityToUpdate, E newEntity, Class<Exception> expectedException,EqualChecker<E> equalChecker) throws NoIdException, BadEntityException {
        saveEntityForUpdate(entityToUpdate, newEntity);
        updateExistingEntity_ShouldFail(newEntity, expectedException, equalChecker);
    }

    protected void updateExistingEntity_ShouldFail(E newEntity, Class<Exception> expectedException, EqualChecker<E> equalChecker) throws NoIdException{
        //given
        //entity to update is present
        Assertions.assertNotNull(newEntity);
        Assertions.assertNotNull(newEntity.getId());
        Optional<E> entityToUpdate = crudService.findById(newEntity.getId());
        Assertions.assertTrue(entityToUpdate.isPresent());
        Assertions.assertFalse(equalChecker.isEqual(entityToUpdate.get(),newEntity));

        Optional<E> repoEntity = repository.findById(newEntity.getId());
        Assertions.assertTrue(repoEntity.isPresent());

        //when
        Assertions.assertThrows(expectedException,() -> crudService.update(newEntity));

        //then
        Optional<E> updatedRepoEntity = repository.findById(newEntity.getId());
        Assertions.assertTrue(updatedRepoEntity.isPresent());
        //still the same (repo)
        Assertions.assertTrue(equalChecker.isEqual(repoEntity.get(),updatedRepoEntity.get()));
        //still the same (service)
        Optional<E> updatedEntityFromService = crudService.findById(newEntity.getId());
        Assertions.assertTrue(updatedEntityFromService.isPresent());
        Assertions.assertTrue(equalChecker.isEqual(newEntity,updatedEntityFromService.get()));
    }

    protected E findEntityById_ShouldSucceed(Id id) throws NoIdException {
        Assertions.assertNotNull(id);
        Assertions.assertTrue(repository.findById(id).isPresent());
        Optional<E> foundEntity = crudService.findById(id);
        Assertions.assertTrue(foundEntity.isPresent());
        return foundEntity.get();
    }

    protected void findExistingEntityById_ShouldFail(Id id, Class<Exception> expectedException) throws NoIdException {
        Assertions.assertNotNull(id);
        Assertions.assertTrue(repository.findById(id).isPresent());
        Assertions.assertThrows(expectedException,() -> crudService.findById(id));
    }

    protected void findExistingEntityById_ShouldFail(Id id) throws NoIdException {
        Assertions.assertNotNull(id);
        Assertions.assertTrue(repository.findById(id).isPresent());
        Optional<E> foundEntity = crudService.findById(id);
        Assertions.assertFalse(foundEntity.isPresent());
    }


    public S getCrudService() {
        return crudService;
    }

    protected void setCrudService(S crudService) {
        this.crudService = crudService;
    }
}