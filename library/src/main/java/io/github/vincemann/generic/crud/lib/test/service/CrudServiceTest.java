package io.github.vincemann.generic.crud.lib.test.service;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.test.TransactionManagedTest;
import io.github.vincemann.generic.crud.lib.test.testBundles.service.delete.failed.FailedDeleteByIdServiceTestBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.service.delete.successful.SuccessfulDeleteByIdServiceTestBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.service.find.FailedFindByIdServiceTestBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.service.find.SuccessfulFindByIdServiceTestBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.service.findAll.FailedFindAllServiceTestBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.service.findAll.SuccessfulFindAllServiceTestBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.service.save.FailedSaveServiceTestEntityBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.service.save.SuccessfulSaveServiceTestBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.service.update.updateIteration.SuccessfulServiceUpdateTestEntityBundleIteration;
import io.github.vincemann.generic.crud.lib.test.testExecutionListeners.ResetDatabaseTestExecutionListener;
import io.github.vincemann.generic.crud.lib.test.testBundles.service.update.updateIteration.FailedServiceUpdateTestEntityBundleIteration;
import io.github.vincemann.generic.crud.lib.test.testBundles.service.update.FailedUpdateServiceTestEntityBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.service.update.SuccessfulUpdateServiceTestEntityBundle;
import io.github.vincemann.generic.crud.lib.util.BeanUtils;
import io.github.vincemann.generic.crud.lib.util.TestLogUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.Serializable;
import java.util.*;
import java.util.function.Predicate;

import static io.github.vincemann.generic.crud.lib.util.SetterUtils.returnIfNotNull;

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
@Getter
public abstract class CrudServiceTest<S extends CrudService<E,Id>,E extends IdentifiableEntity<Id>,Id extends Serializable>
    implements TransactionManagedTest
{



    private List<SuccessfulUpdateServiceTestEntityBundle<E>> successfulUpdatableTestEntityBundles;
    private List<FailedUpdateServiceTestEntityBundle<E>> failedUpdatableTestEntityBundles;
    protected List<SuccessfulUpdateServiceTestEntityBundle<E>> provideSuccessfulUpdateTestEntityBundles(){return null;}
    protected List<FailedUpdateServiceTestEntityBundle<E>> provideFailedUpdateTestEntityBundles(){return null;}

    private List<SuccessfulFindByIdServiceTestBundle<Id,E>> successfulFindByIdTestEntityBundles;
    private List<FailedFindByIdServiceTestBundle<Id,E>> failedFindByIdTestEntityBundles;
    protected List<SuccessfulFindByIdServiceTestBundle<Id,E>> provideSuccessfulFindByIdTestEntityBundles(){return null;}
    protected List<FailedFindByIdServiceTestBundle<Id,E>> provideFailedFindByIdTestEntityBundles(){return null;}

    private List<SuccessfulSaveServiceTestBundle<E>> successfulSaveTestEntityBundles;
    private List<FailedSaveServiceTestEntityBundle<E,Id>> failedSaveTestEntityBundles;
    protected List<SuccessfulSaveServiceTestBundle<E>> provideSuccessfulSaveTestEntityBundles(){return null;}
    protected List<FailedSaveServiceTestEntityBundle<E,Id>> provideFailedSaveTestEntityBundles(){return null;}

    private List<SuccessfulDeleteByIdServiceTestBundle<E,Id>> successfulDeleteByIdTestEntityBundles;
    private List<FailedDeleteByIdServiceTestBundle<Id>> failedDeleteByIdTestEntityBundles;
    protected List<SuccessfulDeleteByIdServiceTestBundle<E,Id>> provideSuccessfulDeleteTestEntityBundles(){return null;}
    protected List<FailedDeleteByIdServiceTestBundle<Id>> provideFailedDeleteTestEntityBundles(){return null;}

    private List<SuccessfulFindAllServiceTestBundle<E>> successfulFindAllServiceTestBundles;
    private List<FailedFindAllServiceTestBundle<E>> failedFindAllServiceTestBundles;
    protected List<SuccessfulFindAllServiceTestBundle<E>> provideSuccessfulFindAllTestEntityBundles(){return null;}
    protected List<FailedFindAllServiceTestBundle<E>> provideFailedFindAllTestEntityBundles(){return null;}


    private S crudService;
    @Getter private PlatformTransactionManager transactionManager;

    public CrudServiceTest(S crudService, PlatformTransactionManager transactionManager) {
        this.crudService = crudService;
        this.transactionManager = transactionManager;
    }

    @Override
    public void provideBundles() throws Exception {
        successfulUpdatableTestEntityBundles=returnIfNotNull(successfulUpdatableTestEntityBundles,provideSuccessfulUpdateTestEntityBundles());
        failedUpdatableTestEntityBundles=returnIfNotNull(failedUpdatableTestEntityBundles,provideFailedUpdateTestEntityBundles());

        successfulFindByIdTestEntityBundles =returnIfNotNull(successfulFindByIdTestEntityBundles, provideSuccessfulFindByIdTestEntityBundles());
        failedFindByIdTestEntityBundles =returnIfNotNull(failedFindByIdTestEntityBundles, provideFailedFindByIdTestEntityBundles());

        successfulSaveTestEntityBundles =returnIfNotNull(successfulSaveTestEntityBundles,provideSuccessfulSaveTestEntityBundles());
        failedSaveTestEntityBundles =returnIfNotNull(failedSaveTestEntityBundles,provideFailedSaveTestEntityBundles());

        successfulDeleteByIdTestEntityBundles =returnIfNotNull(successfulDeleteByIdTestEntityBundles, provideSuccessfulDeleteTestEntityBundles());
        failedDeleteByIdTestEntityBundles =returnIfNotNull(failedDeleteByIdTestEntityBundles,provideFailedDeleteTestEntityBundles());

        successfulFindAllServiceTestBundles =returnIfNotNull(successfulFindAllServiceTestBundles, provideSuccessfulFindAllTestEntityBundles());
        failedFindAllServiceTestBundles =returnIfNotNull(failedFindAllServiceTestBundles, provideFailedFindAllTestEntityBundles());
    }

    @Test
    void findById_shouldSucceed() throws NoIdException, BadEntityException {
        Assumptions.assumeFalse(isTestIgnored(successfulFindByIdTestEntityBundles));


        for (SuccessfulFindByIdServiceTestBundle<Id,E> bundle : successfulFindByIdTestEntityBundles) {
            //given
            E entityUnderTest = bundle.getEntity();
            TestLogUtils.logTestStart(log,"findById",new AbstractMap.SimpleEntry<>("testEntity",entityUnderTest));


            Assertions.assertNull(entityUnderTest.getId());
            E savedTestEntity = saveEntityShouldSucceed(entityUnderTest);

            bundle.callPreTestCallback(savedTestEntity);
            //when
            Optional<E> foundEntity = crudService.findById(savedTestEntity.getId());
            bundle.callPostTestCallback(foundEntity);

            //then
            Assertions.assertTrue(foundEntity.isPresent());
            Assertions.assertNotNull(foundEntity.get().getId());
            Assertions.assertTrue(BeanUtils.isDeepEqual(savedTestEntity,foundEntity.get()));

            TestLogUtils.logTestSucceeded(log,"findById",new AbstractMap.SimpleEntry<>("testEntity",entityUnderTest));
        }
    }

    @Test
    void findById_shouldFail() throws NoIdException {
        Assumptions.assumeFalse(isTestIgnored(failedFindByIdTestEntityBundles));
        for (FailedFindByIdServiceTestBundle<Id,E> bundle : failedFindByIdTestEntityBundles) {
            //given
            TestLogUtils.logTestStart(log,"findById",new AbstractMap.SimpleEntry<>("testId",bundle.getId()));

            bundle.callPreTestCallback(bundle.getId());
            if(bundle.getExpectedException()!=null) {
                Assertions.assertThrows(bundle.getExpectedException(),() -> {
                    crudService.findById(bundle.getId());
                });
                bundle.callPostTestCallback(null);
            }else {
                Optional<E> foundEntityOptional = crudService.findById(bundle.getId());
                bundle.callPostTestCallback(foundEntityOptional);
                Assertions.assertFalse(foundEntityOptional.isPresent());
            }

            TestLogUtils.logTestSucceeded(log,"findById",new AbstractMap.SimpleEntry<>("testId",bundle.getId()));
        }
    }


    @Test
    void update_shouldSucceed() throws NoIdException, BadEntityException, EntityNotFoundException {
        Assumptions.assumeFalse(isTestIgnored(successfulUpdatableTestEntityBundles));
        for (SuccessfulUpdateServiceTestEntityBundle<E> bundle : successfulUpdatableTestEntityBundles) {
            //given
            E entityUnderTest = bundle.getEntity();
            E savedEntity = saveEntityShouldSucceed(entityUnderTest);
            bundle.callPreTestCallback(savedEntity);


            for (SuccessfulServiceUpdateTestEntityBundleIteration<E> bundleIteration : bundle.getUpdateTestEntityBundleIterations()) {
                //given
                E modifiedEntity = bundleIteration.getModifiedEntity();
                TestLogUtils.logTestStart(log, "update", new AbstractMap.SimpleEntry<>("testEntity", entityUnderTest), new AbstractMap.SimpleEntry<>("modified Entity", modifiedEntity));

                modifiedEntity.setId(savedEntity.getId());

                bundleIteration.callPreTestCallback(modifiedEntity);
                //when
                E updatedEntity = crudService.update(modifiedEntity);

                bundleIteration.callPostTestCallback(updatedEntity);

                //then
                Assertions.assertTrue(BeanUtils.isDeepEqual(modifiedEntity, updatedEntity));
                E updatedEntityFromService = crudService.findById(savedEntity.getId()).get();
                Assertions.assertTrue(BeanUtils.isDeepEqual(modifiedEntity, updatedEntityFromService));

                TestLogUtils.logTestSucceeded(log, "update", new AbstractMap.SimpleEntry<>("testEntity", entityUnderTest), new AbstractMap.SimpleEntry<>("modified Entity", modifiedEntity));
            }
        }
    }

    @Test
    void update_shouldFail() throws NoIdException, BadEntityException, EntityNotFoundException {
        Assumptions.assumeFalse(isTestIgnored(failedUpdatableTestEntityBundles));
        for (FailedUpdateServiceTestEntityBundle<E> bundle : failedUpdatableTestEntityBundles) {
            //given
            E entityUnderTest = bundle.getEntity();
            E savedEntity = saveEntityShouldSucceed(entityUnderTest);
            bundle.callPreTestCallback(savedEntity);

            for (FailedServiceUpdateTestEntityBundleIteration<E> bundleIteration : bundle.getUpdateTestEntityBundleIterations()) {
                //given
                E modifiedEntity = bundleIteration.getModifiedEntity();
                TestLogUtils.logTestStart(log,"update",new AbstractMap.SimpleEntry<>("testEntity",entityUnderTest),new AbstractMap.SimpleEntry<>("modified Entity",modifiedEntity));

                modifiedEntity.setId(savedEntity.getId());

                bundleIteration.callPreTestCallback(modifiedEntity);
                //when
                if(bundleIteration.getExpectedException()!=null) {
                    Assertions.assertThrows(bundleIteration.getExpectedException(), () -> crudService.update(modifiedEntity));
                    bundleIteration.callPostTestCallback(null);
                }else {
                    E updatedEntity = crudService.update(modifiedEntity);
                    bundleIteration.callPostTestCallback(updatedEntity);
                }

                //then
                E updatedEntityFromService = crudService.findById(savedEntity.getId()).get();
                Assertions.assertFalse(BeanUtils.isDeepEqual(modifiedEntity,updatedEntityFromService));

                TestLogUtils.logTestSucceeded(log,"update",new AbstractMap.SimpleEntry<>("testEntity",entityUnderTest),new AbstractMap.SimpleEntry<>("modified Entity",modifiedEntity));
            }
        }
    }




    @Test
    void save_shouldSucceed() throws BadEntityException, NoIdException {
        Assumptions.assumeFalse(isTestIgnored(successfulSaveTestEntityBundles));
        for (SuccessfulSaveServiceTestBundle<E> bundle : successfulSaveTestEntityBundles) {
            //given
            E entityUnderTest = bundle.getEntity();
            TestLogUtils.logTestStart(log,"save",new AbstractMap.SimpleEntry<>("testEntity",entityUnderTest));
            //when
            bundle.callPreTestCallback(entityUnderTest);
            E savedTestEntity = saveEntityShouldSucceed(entityUnderTest);
            bundle.callPostTestCallback(savedTestEntity);



            //then
            TestLogUtils.logTestSucceeded(log,"save",new AbstractMap.SimpleEntry<>("testEntity",entityUnderTest));
        }
    }


    @Test
    void findAll_shouldSucceed() throws BadEntityException, NoIdException {
        Assumptions.assumeFalse(isTestIgnored(successfulFindAllServiceTestBundles));

        for (SuccessfulFindAllServiceTestBundle<E> bundle : successfulFindAllServiceTestBundles) {
            saveEntitiesShouldSucceed(bundle.getEntitiesSavedBeforeRequest());
            bundle.callPreTestCallback();
            Set<E> foundEntities = crudService.findAll();
            bundle.callPostTestCallback(foundEntities);
            Set<E> entitiesShouldBeFound = bundle.getFindAllTestEntitiesProvider().provideEntitiesShouldBeFound();
            Assertions.assertEquals(
                    entitiesShouldBeFound.size(),
                    foundEntities.size()
            );

            List<Id> idsSeen = new ArrayList<>();
            //todo in TestEntitiesProvider auslagern oder in utilClass
            for (E entity : foundEntities) {
                //prevent duplicates
                Assertions.assertFalse(idsSeen.contains(entity.getId()));
                idsSeen.add(entity.getId());
            }

            // entity from should-be-found-list must be in actually-found-list
            // and those two entities must also be deep equal
            for (E entity : entitiesShouldBeFound) {
                Optional<E> foundEntityOptional = foundEntities.stream().filter(Predicate.isEqual(entity)).findFirst();
                Assertions.assertTrue(foundEntityOptional.isPresent());
                E foundEntity = foundEntityOptional.get();
                Assertions.assertTrue(BeanUtils.isDeepEqual(entity,foundEntity));
            }

        }
    }

    /*@Test
    void delete_shouldSucceed() throws  EntityNotFoundException, NoIdException, BadEntityException {
        Assumptions.assumeFalse(isTestIgnored(successfulDeleteByIdTestEntityBundles));
        for (SuccessfulDeleteByIdServiceTestBundle<Id> bundle : successfulDeleteByIdTestEntityBundles) {
            E entityUnderTest = bundle.getId()
            TestLogUtils.logTestStart(log,"delete",new AbstractMap.SimpleEntry<>("testEntity",entityUnderTest));
            //given
            Assertions.assertNull(entityUnderTest.getId());

            //when
            E savedTestEntity = saveEntityShouldSucceed(entityUnderTest);
            crudService.delete(savedTestEntity);

            //then
            Set<E> foundEntities = crudService.findAll();
            Assertions.assertEquals(0, foundEntities.size());

            TestLogUtils.logTestSucceeded(log,"delete",new AbstractMap.SimpleEntry<>("testEntity",entityUnderTest));
        }
    }*/


    @Test
    void deleteById_shouldSucceed() throws EntityNotFoundException, NoIdException, BadEntityException {
        Assumptions.assumeFalse(isTestIgnored(successfulDeleteByIdTestEntityBundles));
        for (SuccessfulDeleteByIdServiceTestBundle<E,Id> bundle : successfulDeleteByIdTestEntityBundles) {
            E entityUnderTest = bundle.getEntity();
            TestLogUtils.logTestStart(log,"deleteById",new AbstractMap.SimpleEntry<>("testEntity",entityUnderTest));
            //given
            Assertions.assertNull(entityUnderTest.getId());

            //when
            E savedTestEntity = saveEntityShouldSucceed(entityUnderTest);
            bundle.callPreTestCallback(savedTestEntity);
            deleteEntityByIdShouldSucceed(savedTestEntity.getId());
            bundle.callPostTestCallback(savedTestEntity.getId());

            //then
            Set<E> foundEntities = crudService.findAll();
            Assertions.assertEquals(0, foundEntities.size());

            TestLogUtils.logTestSucceeded(log,"deleteById",new AbstractMap.SimpleEntry<>("testEntity",entityUnderTest));
        }
    }

    protected Collection<E> saveEntitiesShouldSucceed(Collection<E> entitiesToSave) throws NoIdException, BadEntityException {
        Collection<E> savedEntities = new ArrayList<>();
        for (E entity : entitiesToSave) {
            E savedEntity = saveEntityShouldSucceed(entity);
            savedEntities.add(savedEntity);
        }
        return savedEntities;
    }

    protected E saveEntityShouldSucceed(E entityToSave) throws BadEntityException, NoIdException {
        //given
        Assertions.assertNull(entityToSave.getId());

        //when
        E savedTestEntity = crudService.save(entityToSave);

        //then
        Assertions.assertNotNull(savedTestEntity);
        Assertions.assertNotNull(savedTestEntity.getId());
        Assertions.assertNotEquals(0,savedTestEntity.getId());
        entityToSave.setId(savedTestEntity.getId());
        E savedEntityFromService = crudService.findById(savedTestEntity.getId()).get();
        Assertions.assertTrue(BeanUtils.isDeepEqual(entityToSave,savedEntityFromService));
        return savedTestEntity;
    }



    protected void deleteEntityByIdShouldSucceed(Id id) throws EntityNotFoundException, NoIdException {
        Assertions.assertNotNull(id);
        //when
        crudService.deleteById(id);
        //then
        Optional<E> deletedEntity = crudService.findById(id);
        Assertions.assertFalse(deletedEntity.isPresent());
    }

    private boolean isTestIgnored(Collection testEntityBundles){
        if(testEntityBundles==null){
            return true;
        }
        if(testEntityBundles.isEmpty()){
            return true;
        }
        return false;
    }
    /*@AfterEach
    void tearDown() throws EntityNotFoundException, NoIdException {
        for(E entityToDelete : crudService.findAll()){
            deleteEntityByIdShouldSucceed(entityToDelete.getId());
        }
        Assertions.assertTrue(crudService.findAll().isEmpty());
    }*/

}