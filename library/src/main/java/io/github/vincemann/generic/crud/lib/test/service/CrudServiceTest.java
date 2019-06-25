package io.github.vincemann.generic.crud.lib.test.service;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
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
    private CrudServiceTestEntry<S,E,Id> crudServiceTestEntry;

    protected abstract CrudServiceTestEntry<S,E,Id> provideTestEntity();
    private List<Id> savedEntitiesIds = new ArrayList<>();

    @BeforeEach
    void setUp() {
        crudServiceTestEntry=provideTestEntity();
    }


    @Test
    void findById() throws NoIdException, BadEntityException {
        //given
        Assertions.assertNull(crudServiceTestEntry.getTestEntity().getId());
        CrudService<E,Id> crudService = crudServiceTestEntry.getCrudService();


        //when
        E savedTestEntity = saveEntity(crudServiceTestEntry.getTestEntity());
        savedEntitiesIds.add(savedTestEntity.getId());
        Optional<E> foundEntity = crudService.findById(savedTestEntity.getId());

        //then
        Assertions.assertTrue(foundEntity.isPresent());
        Assertions.assertNotNull(foundEntity.get().getId());
        Assertions.assertEquals(savedTestEntity,foundEntity.get());
    }

    protected E saveEntity(E entityToSave) throws  BadEntityException {
        //given
        Assertions.assertNull(entityToSave.getId());
        CrudService<E,Id> crudService = crudServiceTestEntry.getCrudService();

        //when
        E savedTestEntity = crudService.save(entityToSave);

        //then
        Assertions.assertNotNull(savedTestEntity);
        Assertions.assertNotNull(savedTestEntity.getId());
        Assertions.assertNotEquals(0,savedTestEntity.getId());
        return savedTestEntity;
    }

    protected void deleteEntityById(Id id) throws EntityNotFoundException, NoIdException {
        Assertions.assertNotNull(id);
        CrudService<E,Id> crudService = crudServiceTestEntry.getCrudService();
        //when
        crudService.deleteById(id);
        //then
        Optional<E> deletedEntity = crudService.findById(id);
        Assertions.assertFalse(deletedEntity.isPresent());
    }


    @Test
    void save() throws  BadEntityException {
        E savedTestEntity = saveEntity(crudServiceTestEntry.getTestEntity());
        savedEntitiesIds.add(savedTestEntity.getId());
    }


    @Test
    void findAll() throws  BadEntityException {
        //given
        Assertions.assertNull(crudServiceTestEntry.getTestEntity().getId());
        CrudService<E,Id> crudService = crudServiceTestEntry.getCrudService();

        E savedTestEntity = saveEntity(crudServiceTestEntry.getTestEntity());
        savedEntitiesIds.add(savedTestEntity.getId());
        Set<E> foundEntities = crudService.findAll();

        //then
        Assertions.assertEquals(1,foundEntities.size());
        for(E foundEntity: foundEntities){
            Assertions.assertEquals(savedTestEntity,foundEntity);
        }
    }

    @Test
    void delete() throws  EntityNotFoundException, NoIdException, BadEntityException {
        //given
        Assertions.assertNull(crudServiceTestEntry.getTestEntity().getId());
        CrudService<E,Id> crudService = crudServiceTestEntry.getCrudService();

        //when
        E savedTestEntity = saveEntity(crudServiceTestEntry.getTestEntity());
        crudService.delete(savedTestEntity);

        //then
        Set<E> foundEntities = crudService.findAll();
        Assertions.assertEquals(0,foundEntities.size());
    }


    @Test
    void deleteById() throws EntityNotFoundException, NoIdException, BadEntityException {
        //given
        Assertions.assertNull(crudServiceTestEntry.getTestEntity().getId());
        CrudService<E,Id> crudService = crudServiceTestEntry.getCrudService();

        //when
        E savedTestEntity = saveEntity(crudServiceTestEntry.getTestEntity());
        deleteEntityById(savedTestEntity.getId());

        //then
        Set<E> foundEntities = crudService.findAll();
        Assertions.assertEquals(0,foundEntities.size());
    }

    @AfterEach
    void tearDown() throws EntityNotFoundException, NoIdException {
        for(Id entityToDeleteId : savedEntitiesIds){
            deleteEntityById(entityToDeleteId);
        }
        savedEntitiesIds.clear();
    }

    /**
     *
     * @param <S>       CrudeServiceImpl that should be tested
     * @param <E>       BaseEntityImpl that will be used as a Test entity for Crud Operations
     * @param <Id>      id Type of BaseEntityImpl
     */
    protected static class CrudServiceTestEntry<S extends CrudService,E extends IdentifiableEntity<Id>,Id extends Serializable>{
        public CrudServiceTestEntry(S crudService, E testEntity) {
            this.crudService = crudService;
            this.testEntity = testEntity;
        }

        private S crudService;
        private E testEntity;

        public S getCrudService() {
            return crudService;
        }

        public E getTestEntity() {
            return testEntity;
        }
    }

}