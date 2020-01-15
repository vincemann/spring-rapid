package io.github.vincemann.generic.crud.lib.test.controller.springAdapter;

import io.github.vincemann.generic.crud.lib.controller.BasicDtoCrudController;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.exception.EntityMappingException;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.DtoCrudController_SpringAdapter;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.idFetchingStrategy.UrlParamIdFetchingStrategy;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.test.ServiceEagerFetch_ControllerIntegrationTestContext;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.CreateControllerTest;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.requestEntityFactory.RequestEntityFactory;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.requestEntityFactory.UrlParamIdRequestEntityFactory;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.CrudController_TestCase;
import io.github.vincemann.generic.crud.lib.test.equalChecker.EqualChecker;
import io.github.vincemann.generic.crud.lib.test.forceEagerFetch.proxy.CrudService_HibernateForceEagerFetch_Proxy;
import io.github.vincemann.generic.crud.lib.test.testExecutionListeners.ResetDatabaseTestExecutionListener;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestExecutionListeners;

import java.io.Serializable;
import java.util.*;

/**
 * Integration Test for a {@link DtoCrudController_SpringAdapter} with {@link UrlParamIdFetchingStrategy}, that tests typical Crud operations
 * <p>
 * Wraps Controllers {@link BasicDtoCrudController#getCrudService()} with {@link CrudService_HibernateForceEagerFetch_Proxy}.
 * -> No LazyInit Exceptions possible.
 *
 * @param <E>
 * @param <Id>
 */
@Slf4j
//clear database after each test
@TestExecutionListeners(
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS,
        listeners = {ResetDatabaseTestExecutionListener.class}
)
public abstract class UrlParamIdControllerIntegrationTest
        <
                E extends IdentifiableEntity<Id>,
                Id extends Serializable
        >
        extends ServiceEagerFetch_ControllerIntegrationTestContext<E,Id>
{

    private CreateControllerTest<E,Id> createControllerTest;

    public UrlParamIdControllerIntegrationTest() {
        Assertions.assertTrue(UrlParamIdFetchingStrategy.class.isAssignableFrom(getController().getIdIdFetchingStrategy().getClass()));
        RequestEntityFactory<Id> requestEntityFactory = new UrlParamIdRequestEntityFactory<>(
                this,
                ((UrlParamIdFetchingStrategy<Id>) getController().getIdIdFetchingStrategy()).getIdUrlParamKey()
        );
        this.createControllerTest= new CreateControllerTest<>(this);
    }

//    @Qualifier("default")
//    @Autowired
//    public void injectDefaultDtoEqualChecker(EqualChecker<? extends IdentifiableEntity<Id>> defaultDtoEqualChecker) {
//        setDefaultDtoEqualChecker(defaultDtoEqualChecker);
//    }

    @Autowired
    public void injectCrudController(DtoCrudController_SpringAdapter<E, Id, CrudRepository<E,Id>> crudController) {
        setController(crudController);
    }








    protected ResponseEntity<String> findAllEntities_ShouldFail() throws Exception {
        return findAllEntities_ShouldFail(null);
    }


    protected ResponseEntity<String> findAllEntities_ShouldFail(TestRequestEntity_Modification... modifications) throws Exception {
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(
                CrudController_TestCase.FAILED_FIND_ALL,
                null,
                modifications);
        return findAllEntities(testRequestEntity);
    }

    protected <Dto extends IdentifiableEntity<Id>> Set<Dto> findAllEntities_ShouldSucceed(Set<E> entitiesExpectedToBeFound, TestRequestEntity_Modification... modifications) throws Exception {
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(
                CrudController_TestCase.SUCCESSFUL_FIND_ALL,
                null,
                modifications);
        ResponseEntity<String> responseEntity = findAllEntities(testRequestEntity);

        @SuppressWarnings("unchecked")
        Set<IdentifiableEntity<Id>> httpResponseDtos = controller.getMediaTypeStrategy().readDtosFromBody(responseEntity.getBody(), mappingContext().getFindAllReturnDtoClass(), Set.class);

        Assertions.assertEquals(entitiesExpectedToBeFound.size(), httpResponseDtos.size());
        List<Id> idsSeen = new ArrayList<>();
        for (IdentifiableEntity<Id> dto : httpResponseDtos) {
            Assertions.assertEquals(mappingContext().getFindAllReturnDtoClass(),dto.getClass());
            //prevent duplicates
            Assertions.assertFalse(idsSeen.contains(dto.getId()));
            idsSeen.add(dto.getId());
        }
        return (Set<Dto>) httpResponseDtos;
    }

    protected <Dto extends IdentifiableEntity<Id>> Set<Dto> findAllEntities_ShouldSucceed(Set<E> entitiesExpectedToBeFound) throws Exception {
        return findAllEntities_ShouldSucceed(entitiesExpectedToBeFound, null);
    }

    protected ResponseEntity<String> findAllEntities(TestRequestEntity testRequestEntity) {
        ResponseEntity<String> responseEntity = getRestTemplate().exchange(RequestEntityMapper.map(testRequestEntity, null), String.class);
        Assertions.assertEquals(testRequestEntity.getExpectedHttpStatus(), responseEntity.getStatusCode(), "Status was : " + responseEntity.getStatusCode() + " response Body: " + responseEntity.getBody());
        return responseEntity;
    }





    protected ResponseEntity<String> deleteEntity_ShouldSucceed(Id id) throws Exception {
        return deleteEntity_ShouldSucceed(id, null);
    }

    protected ResponseEntity<String> deleteEntity_ShouldSucceed(Id id, TestRequestEntity_Modification... modifications) throws Exception {
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(
                CrudController_TestCase.SUCCESSFUL_DELETE,
                id,
                modifications);
        //Entity muss vorher auch schon da sein
        Optional<E> serviceFoundEntityBeforeDelete = testCrudService.findById(id);
        Assertions.assertTrue(serviceFoundEntityBeforeDelete.isPresent(), "Entity to delete was not present");

        //onBeforeDeleteEntityShouldSucceed(id);
        ResponseEntity<String> responseEntity = deleteEntity(id, testRequestEntity);
        Assertions.assertEquals(testRequestEntity.getExpectedHttpStatus(), responseEntity.getStatusCode(), responseEntity.getBody());
        //onAfterDeleteEntityShouldSucceed(id, responseEntity);
        return responseEntity;
    }

    protected ResponseEntity<String> deleteEntity_ShouldFail(Id id) throws Exception {
        return deleteEntity_ShouldFail(id, null);
    }

    protected ResponseEntity<String> deleteEntity_ShouldFail(Id id, TestRequestEntity_Modification... modifications) throws Exception {
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(
                CrudController_TestCase.FAILED_DELETE,
                id,
                modifications);
        //Entity muss vorher auch schon da sein
        Optional<E> serviceFoundEntityBeforeDelete = testCrudService.findById(id);
        Assertions.assertTrue(serviceFoundEntityBeforeDelete.isPresent(), "Entity to delete was not present");
        ResponseEntity<String> responseEntity = deleteEntity(id, testRequestEntity);
        Assertions.assertEquals(testRequestEntity.getExpectedHttpStatus(), responseEntity.getStatusCode(), responseEntity.getBody());

        return responseEntity;
    }

    protected ResponseEntity<String> deleteEntity(Id id, TestRequestEntity testRequestEntity) {
        return getRestTemplate().exchange(RequestEntityMapper.map(testRequestEntity, id), String.class);
    }




    protected <Dto extends IdentifiableEntity<Id>> Dto mapEntityToDto(E entity, Class<Dto> dtoClass) throws EntityMappingException {
        return controller().findMapperAndMapToDto(entity, dtoClass);
    }

    protected E mapDtoToEntity(IdentifiableEntity<Id> dto, Class<? extends IdentifiableEntity<Id>> dtoClass) throws EntityMappingException {
        return controller().findMapperAndMapToEntity(dto, dtoClass);
    }


    protected E saveServiceEntity(E e) throws BadEntityException {
        return testCrudService.save(e);
    }

    protected Collection<E> saveServiceEntities(Collection<E> ECollection) throws BadEntityException {
        Collection<E> savedEntities = new ArrayList<>();
        for (E e : ECollection) {
            E savedEntity = saveServiceEntity(e);
            savedEntities.add(savedEntity);
        }
        return savedEntities;
    }
}