package io.github.vincemann.generic.crud.lib.test.service;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.TestEntityBundle;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.UpdateTestBundle;
import io.github.vincemann.generic.crud.lib.util.BeanUtils;
import lombok.Getter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 *
 * @param <S>       CrudServiceImplType
 * @param <E>       TestEntityType
 * @param <Id>      Id Type of TestEntityType
 */
public abstract class CrudServiceTest<S extends CrudService<E,Id>,E extends IdentifiableEntity<Id>,Id extends Serializable> {

    @Getter
    private List<TestEntityBundle<E>> testEntityBundles;
    @Getter
    private S crudService;

    public CrudServiceTest(S crudService) {
        this.crudService = crudService;
    }

    @BeforeEach
    public void setUp() throws Exception{
        testEntityBundles = provideTestEntityBundles();
        Assertions.assertFalse(testEntityBundles.isEmpty(),"At least one testEntityBundle must be provided");
    }

    protected abstract List<TestEntityBundle<E>> provideTestEntityBundles();



    @Test
    void findById() throws NoIdException, BadEntityException {
        for (TestEntityBundle<E> testEntityBundle : testEntityBundles) {
            E entityUnderTest = testEntityBundle.getEntity();
            System.err.println("find entity test with entity: "+ entityUnderTest);
            //given
            Assertions.assertNull(entityUnderTest.getId());

            //when
            E savedTestEntity = saveEntityShouldSucceed(entityUnderTest);
            Optional<E> foundEntity = crudService.findById(savedTestEntity.getId());

            //then
            Assertions.assertTrue(foundEntity.isPresent());
            Assertions.assertNotNull(foundEntity.get().getId());
            Assertions.assertTrue(BeanUtils.isDeepEqual(savedTestEntity,foundEntity.get()));
            System.err.println("Test succeeded");
        }
    }


    @Test
    void update() throws NoIdException, BadEntityException, EntityNotFoundException {
        for (TestEntityBundle<E> testEntityBundle : testEntityBundles) {
            //given
            E entityUnderTest = testEntityBundle.getEntity();
            System.err.println("update entity test with entity: "+ entityUnderTest);
            E savedEntity = saveEntityShouldSucceed(entityUnderTest);

            for (UpdateTestBundle<E> updateTestBundle : testEntityBundle.getUpdateTestBundles()) {
                //given
                E modifiedEntity = updateTestBundle.getModifiedEntity();
                System.err.println("update test with modified entity: " + modifiedEntity);
                modifiedEntity.setId(savedEntity.getId());

                //when
                E updatedEntity = crudService.update(modifiedEntity);

                //then
                Assertions.assertTrue(BeanUtils.isDeepEqual(modifiedEntity,updatedEntity));
                E updatedEntityFromService = crudService.findById(savedEntity.getId()).get();
                Assertions.assertTrue(BeanUtils.isDeepEqual(modifiedEntity,updatedEntityFromService));
                System.err.println("Test succeeded");
            }
            System.err.println("Test succeeded");

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
        for (TestEntityBundle<E> testEntityBundle : testEntityBundles) {
            //given
            E entityUnderTest = testEntityBundle.getEntity();
            System.err.println("save entity test with entity: "+ entityUnderTest);
            //when
            E savedTestEntity = saveEntityShouldSucceed(entityUnderTest);

            //then
            System.err.println("Test succeeded");
        }
    }


    @Test
    void findAll() throws BadEntityException, NoIdException {
        E entityUnderTest = testEntityBundles.stream().findFirst().get().getEntity();
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
        for (TestEntityBundle<E> testEntityBundle : testEntityBundles) {
            E entityUnderTest = testEntityBundle.getEntity();
            System.err.println("delete entity test with entity: "+ entityUnderTest);
            //given
            Assertions.assertNull(entityUnderTest.getId());

            //when
            E savedTestEntity = saveEntityShouldSucceed(entityUnderTest);
            crudService.delete(savedTestEntity);

            //then
            Set<E> foundEntities = crudService.findAll();
            Assertions.assertEquals(0, foundEntities.size());
            System.err.println("Test succeeded");
        }
    }


    @Test
    void deleteById() throws EntityNotFoundException, NoIdException, BadEntityException {
        for (TestEntityBundle<E> testEntityBundle : testEntityBundles) {
            E entityUnderTest = testEntityBundle.getEntity();
            System.err.println("delete by id entity test with entity: "+ entityUnderTest);
            //given
            Assertions.assertNull(entityUnderTest.getId());

            //when
            E savedTestEntity = saveEntityShouldSucceed(entityUnderTest);
            deleteEntityByIdShouldSucceed(savedTestEntity.getId());

            //then
            Set<E> foundEntities = crudService.findAll();
            Assertions.assertEquals(0, foundEntities.size());
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