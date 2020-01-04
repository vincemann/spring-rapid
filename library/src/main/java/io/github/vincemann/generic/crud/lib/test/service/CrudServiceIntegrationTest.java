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
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.TestExecutionListeners;

import java.io.Serializable;
import java.util.Optional;

/**
 * Abstract Test Class, offering many convenience methods for crud operation testing.
 * It is expected that Repository-Layer works properly.
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
public abstract class CrudServiceIntegrationTest
                <
                        S extends CrudService<E,Id,R>,
                        R extends CrudRepository<E,Id>,
                        E extends IdentifiableEntity<Id>,
                        Id extends Serializable
                >
{

    private CrudService<E,Id,R> crudService;

    @Getter
    private EqualChecker<E> equalChecker;
    @Getter
    private R repository;
    

    @Autowired
    public void injectEqualChecker(EqualChecker<E> equalChecker) {
        this.equalChecker = equalChecker;
    }

    @Autowired
    public void injectCrudService(CrudService<E, Id, R> crudService) {
        setCrudService(crudService);
    }

    public void setCrudService(CrudService<E, Id, R> crudService) {
        this.crudService = crudService;
    }

    @Autowired
    public void injectRepository(R repository) {
        this.repository = repository;
    }

    protected E saveEntity_ShouldSucceed(E entityToSave) throws BadEntityException {
        return saveEntity_ShouldSucceed(entityToSave,equalChecker);
    }

    protected E saveEntity_ShouldSucceed(E entityToSave, EqualChecker<E> equalChecker) throws BadEntityException {
        return saveEntity_ShouldSucceed(entityToSave,equalChecker,equalChecker);
    }

    protected E saveEntity_ShouldSucceed(E entityToSave, EqualChecker<E> repoEntityEqualChecker, EqualChecker<E> returnedEntityEqualChecker) throws BadEntityException {
        //given
        Assertions.assertNull(entityToSave.getId());

        //when
        E savedTestEntity = serviceSave(entityToSave);

        //then
        Assertions.assertNotNull(savedTestEntity);
        Assertions.assertNotNull(savedTestEntity.getId());
        Assertions.assertNotEquals(0,savedTestEntity.getId());

        //if service save method does not copy entityToSave, then entitytoSaveRef = savedEntityFromServiceRef, otherwise not
        Optional<E> repoEntity = repoFindById(savedTestEntity.getId());
        Assertions.assertTrue(repoEntity.isPresent());

        Id oldId_EntityToSave = entityToSave.getId();
        entityToSave.setId(savedTestEntity.getId());

        Assertions.assertTrue(repoEntityEqualChecker.isEqual(entityToSave,repoEntity.get()));
        Assertions.assertTrue(returnedEntityEqualChecker.isEqual(entityToSave,savedTestEntity));

        //restore old id
        entityToSave.setId(oldId_EntityToSave);

        return savedTestEntity;
    }

    protected <T extends Throwable> T saveEntity_ShouldFail(E entityToSave, Class<? extends T> expectedException) {
        //when
        return Assertions.assertThrows(expectedException, () -> serviceSave(entityToSave));
    }

    protected Optional<E> serviceFindById(Id id) throws NoIdException {
        return getCrudService().findById(id);
    }

    protected E serviceSave(E entity) throws BadEntityException {
        return getCrudService().save(entity);
    }

    protected Optional<E> repoFindById(Id id){
        return getRepository().findById(id);
    }

    protected E repoSave(E entityToSave){
        return getRepository().save(entityToSave);
    }

    protected E serviceUpdate(E entity) throws EntityNotFoundException, BadEntityException, NoIdException {
        return getCrudService().update(entity);
    }

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

    protected E updateEntity_ShouldSucceed(E entityToUpdate, E newEntity) throws BadEntityException, EntityNotFoundException, NoIdException {
        saveEntityForUpdate(entityToUpdate,newEntity);
        return updateEntity_ShouldSucceed(newEntity,equalChecker);
    }

    private void saveEntityForUpdate(E entityToUpdate, E newEntity) throws BadEntityException {
        Assertions.assertNull(entityToUpdate.getId());
        Assertions.assertNull(newEntity.getId());
        E savedEntityToUpdate = repoSave(entityToUpdate);
        newEntity.setId(savedEntityToUpdate.getId());
    }
    protected E updateEntity_ShouldSucceed(E entityToUpdate, E newEntity,EqualChecker<E> equalChecker) throws BadEntityException, EntityNotFoundException, NoIdException {
        saveEntityForUpdate(entityToUpdate,newEntity);
        return updateEntity_ShouldSucceed(newEntity,equalChecker);
    }

    protected E updateEntity_ShouldSucceed(E newEntity) throws BadEntityException, EntityNotFoundException, NoIdException {
        return updateEntity_ShouldSucceed(newEntity,equalChecker);
    }
    protected E updateEntity_ShouldSucceed(E newEntity, EqualChecker<E> equalChecker) throws BadEntityException, EntityNotFoundException, NoIdException {
        return updateEntity_ShouldSucceed(newEntity,equalChecker,equalChecker);
    }

    protected E updateEntity_ShouldSucceed(E newEntity, EqualChecker<E> repoEntityEqualChecker, EqualChecker<E> returnedEntityEqualChecker) throws NoIdException, BadEntityException, EntityNotFoundException {
        //given
        Assertions.assertNotNull(newEntity);
        Assertions.assertNotNull(newEntity.getId());
        Optional<E> entityToUpdate = repoFindById(newEntity.getId());
        Assertions.assertTrue(entityToUpdate.isPresent());
        Assertions.assertFalse(repoEntityEqualChecker.isEqual(newEntity,entityToUpdate.get()));

        //when
        E updatedEntity = serviceUpdate(newEntity);

        //then
        Assertions.assertEquals(updatedEntity.getId(),newEntity.getId());
        Optional<E> updatedRepoEntity = repoFindById(updatedEntity.getId());
        Assertions.assertTrue(updatedRepoEntity.isPresent());


        Assertions.assertTrue(repoEntityEqualChecker.isEqual(newEntity,updatedEntity));
        Optional<E> updatedEntityFromRepo = repoFindById(newEntity.getId());
        Assertions.assertTrue(updatedEntityFromRepo.isPresent());
        Assertions.assertTrue(returnedEntityEqualChecker.isEqual(newEntity,updatedEntity));

        return updatedEntity;
    }

    protected <T extends Throwable> T updateExistingEntity_ShouldFail(E newEntity, Class<? extends T> expectedException) throws NoIdException {
        return updateExistingEntity_ShouldFail(newEntity,expectedException,equalChecker);
    }

    protected <T extends Throwable> T updateExistingEntity_ShouldFail(E entityToUpdate, E newEntity, Class<? extends T> expectedException) throws NoIdException, BadEntityException {
        saveEntityForUpdate(entityToUpdate,newEntity);
        return updateExistingEntity_ShouldFail(newEntity,expectedException,equalChecker);
    }

    protected <T extends Throwable> T updateExistingEntity_ShouldFail(E entityToUpdate, E newEntity, Class<? extends T> expectedException,EqualChecker<E> equalChecker) throws NoIdException, BadEntityException {
        saveEntityForUpdate(entityToUpdate, newEntity);
        return updateExistingEntity_ShouldFail(newEntity, expectedException, equalChecker);
    }

    protected <T extends Throwable> T updateExistingEntity_ShouldFail(E newEntity, Class<? extends T> expectedException, EqualChecker<E> equalChecker) throws NoIdException{
        //given
        //entity to update is present
        Assertions.assertNotNull(newEntity);
        Assertions.assertNotNull(newEntity.getId());
        Optional<E> entityToUpdate = repoFindById(newEntity.getId());
        Assertions.assertTrue(entityToUpdate.isPresent());
        Assertions.assertFalse(equalChecker.isEqual(newEntity,entityToUpdate.get()));


        //when
        T exception = Assertions.assertThrows(expectedException, () -> serviceUpdate(newEntity));

        //then
        Optional<E> updatedRepoEntity = repoFindById(newEntity.getId());
        Assertions.assertTrue(updatedRepoEntity.isPresent());
        //still the same
        Assertions.assertTrue(equalChecker.isEqual(entityToUpdate.get(),updatedRepoEntity.get()));
        return exception;
    }

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

    public CrudService<E, Id, R> getCrudService() {
        return crudService;
    }
    
    public S getCastedCrudService(){
        return (S) crudService;
    }
    
}