package io.github.vincemann.generic.crud.lib.test.service;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.update.UpdateTestEntityBundle;
import io.github.vincemann.generic.crud.lib.util.BeanUtils;
import io.github.vincemann.generic.crud.lib.util.TestLogUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.*;

/**
 *
 * @param <S>       CrudServiceImplType
 * @param <E>       TestEntityType
 * @param <Id>      Id Type of TestEntityType
 */
@Slf4j
public abstract class CrudServiceTest<S extends CrudService<E,Id>,E extends IdentifiableEntity<Id>,Id extends Serializable> {

    @Getter
    private List<UpdatableSucceedingTestEntityBundle<E>> updatableTestEntityBundles;
    @Getter
    private S crudService;

    public CrudServiceTest(S crudService) {
        this.crudService = crudService;
    }

    @BeforeEach
    public void setUp() throws Exception{
        updatableTestEntityBundles = provideTestEntityBundles();
        Assertions.assertFalse(updatableTestEntityBundles.isEmpty(),"At least one testEntityBundle must be provided");
    }

    protected abstract List<UpdatableSucceedingTestEntityBundle<E>> provideTestEntityBundles();



    @Test
    void findById() throws NoIdException, BadEntityException {
        for (UpdatableSucceedingTestEntityBundle<E> updatableTestEntityBundle : updatableTestEntityBundles) {
            E entityUnderTest = updatableTestEntityBundle.getEntity();
            TestLogUtils.logTestStart(log,"findById",new AbstractMap.SimpleEntry<>("testEntity",entityUnderTest));

            //given
            Assertions.assertNull(entityUnderTest.getId());

            //when
            E savedTestEntity = saveEntityShouldSucceed(entityUnderTest);
            Optional<E> foundEntity = crudService.findById(savedTestEntity.getId());

            //then
            Assertions.assertTrue(foundEntity.isPresent());
            Assertions.assertNotNull(foundEntity.get().getId());
            Assertions.assertTrue(BeanUtils.isDeepEqual(savedTestEntity,foundEntity.get()));

            TestLogUtils.logTestSucceeded(log,"findById",new AbstractMap.SimpleEntry<>("testEntity",entityUnderTest));
        }
    }


    @Test
    void update() throws NoIdException, BadEntityException, EntityNotFoundException {
        for (UpdatableSucceedingTestEntityBundle<E> updatableTestEntityBundle : updatableTestEntityBundles) {
            //given
            E entityUnderTest = updatableTestEntityBundle.getEntity();
            E savedEntity = saveEntityShouldSucceed(entityUnderTest);

            for (UpdateTestEntityBundle<E> updateTestEntityBundle : updatableTestEntityBundle.getUpdateTestEntityBundles()) {
                //given
                E modifiedEntity = updateTestEntityBundle.getModifiedEntity();
                TestLogUtils.logTestStart(log,"update",new AbstractMap.SimpleEntry<>("testEntity",entityUnderTest),new AbstractMap.SimpleEntry<>("modified Entity",modifiedEntity));

                modifiedEntity.setId(savedEntity.getId());

                //when
                E updatedEntity = crudService.update(modifiedEntity);

                //then
                Assertions.assertTrue(BeanUtils.isDeepEqual(modifiedEntity,updatedEntity));
                E updatedEntityFromService = crudService.findById(savedEntity.getId()).get();
                Assertions.assertTrue(BeanUtils.isDeepEqual(modifiedEntity,updatedEntityFromService));

                TestLogUtils.logTestSucceeded(log,"update",new AbstractMap.SimpleEntry<>("testEntity",entityUnderTest),new AbstractMap.SimpleEntry<>("modified Entity",modifiedEntity));
            }
        }
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


    @Test
    void save() throws BadEntityException, NoIdException {
        for (UpdatableSucceedingTestEntityBundle<E> updatableTestEntityBundle : updatableTestEntityBundles) {
            //given
            E entityUnderTest = updatableTestEntityBundle.getEntity();
            TestLogUtils.logTestStart(log,"save",new AbstractMap.SimpleEntry<>("testEntity",entityUnderTest));
            //when
            E savedTestEntity = saveEntityShouldSucceed(entityUnderTest);

            //then
            TestLogUtils.logTestSucceeded(log,"save",new AbstractMap.SimpleEntry<>("testEntity",entityUnderTest));
        }
    }


    @Test
    void findAll() throws BadEntityException, NoIdException {
        E entityUnderTest = updatableTestEntityBundles.stream().findFirst().get().getEntity();
        //given
        Assertions.assertNull(entityUnderTest.getId());

        E savedTestEntity = saveEntityShouldSucceed(entityUnderTest);
        Set<E> foundEntities = crudService.findAll();

        //then
        Assertions.assertEquals(1,foundEntities.size());
        for(E foundEntity: foundEntities){
            Assertions.assertTrue(BeanUtils.isDeepEqual(savedTestEntity,foundEntity));
        }
    }

    @Test
    void delete() throws  EntityNotFoundException, NoIdException, BadEntityException {
        for (UpdatableSucceedingTestEntityBundle<E> updatableTestEntityBundle : updatableTestEntityBundles) {
            E entityUnderTest = updatableTestEntityBundle.getEntity();
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
    }


    @Test
    void deleteById() throws EntityNotFoundException, NoIdException, BadEntityException {
        for (UpdatableSucceedingTestEntityBundle<E> updatableTestEntityBundle : updatableTestEntityBundles) {
            E entityUnderTest = updatableTestEntityBundle.getEntity();
            TestLogUtils.logTestStart(log,"deleteById",new AbstractMap.SimpleEntry<>("testEntity",entityUnderTest));
            //given
            Assertions.assertNull(entityUnderTest.getId());

            //when
            E savedTestEntity = saveEntityShouldSucceed(entityUnderTest);
            deleteEntityByIdShouldSucceed(savedTestEntity.getId());

            //then
            Set<E> foundEntities = crudService.findAll();
            Assertions.assertEquals(0, foundEntities.size());

            TestLogUtils.logTestSucceeded(log,"deleteById",new AbstractMap.SimpleEntry<>("testEntity",entityUnderTest));
        }
    }

    @AfterEach
    void tearDown() throws EntityNotFoundException, NoIdException {
        for(E entityToDelete : crudService.findAll()){
            deleteEntityByIdShouldSucceed(entityToDelete.getId());
        }
        Assertions.assertTrue(crudService.findAll().isEmpty());
    }

}