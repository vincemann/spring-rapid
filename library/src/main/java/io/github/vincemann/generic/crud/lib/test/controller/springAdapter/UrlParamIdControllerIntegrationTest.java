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
import io.github.vincemann.generic.crud.lib.test.postUpdateCallback.PostUpdateCallback;
import io.github.vincemann.generic.crud.lib.test.forceEagerFetch.proxy.CrudService_HibernateForceEagerFetch_Proxy;
import io.github.vincemann.generic.crud.lib.test.testExecutionListeners.ResetDatabaseTestExecutionListener;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
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

    @Qualifier("default")
    @Autowired
    public void injectDefaultDtoEqualChecker(EqualChecker<? extends IdentifiableEntity<Id>> defaultDtoEqualChecker) {
        setDefaultDtoEqualChecker(defaultDtoEqualChecker);
    }

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

    protected <Dto extends IdentifiableEntity<Id>> Dto updateEntity_ShouldSucceed(E entityToUpdate, IdentifiableEntity<Id> updateRequest,boolean fullUpdate) throws Exception {
        E savedEntityToUpdate = saveServiceEntity(entityToUpdate);
        updateRequest.setId(savedEntityToUpdate.getId());
        return updateEntity_ShouldSucceed(updateRequest,fullUpdate);
    }

    protected <Dto extends IdentifiableEntity<Id>> Dto updateEntity_ShouldSucceed(E entityToUpdate, IdentifiableEntity<Id> updateRequestDto,boolean fullUpdate, @Nullable PostUpdateCallback<E,Id> updatedValuesPostUpdateCallback) throws Exception {
        E savedEntityToUpdate = saveServiceEntity(entityToUpdate);
        updateRequestDto.setId(savedEntityToUpdate.getId());
        return updateEntity_ShouldSucceed(updateRequestDto,fullUpdate,updatedValuesPostUpdateCallback);
    }

    protected <Dto extends IdentifiableEntity<Id>> Dto updateEntity_ShouldSucceed(E entityToUpdate, IdentifiableEntity<Id> updateRequestDto,boolean fullUpdate, @Nullable TestRequestEntity_Modification... modifications) throws Exception {
        E savedEntityToUpdate = saveServiceEntity(entityToUpdate);
        updateRequestDto.setId(savedEntityToUpdate.getId());
        return updateEntity_ShouldSucceed(updateRequestDto,fullUpdate, modifications);
    }

    protected <Dto extends IdentifiableEntity<Id>> Dto updateEntity_ShouldSucceed(E entityToUpdate, IdentifiableEntity<Id> updateRequestDto,boolean fullUpdate, @Nullable PostUpdateCallback<E,Id> updatedValuesPostUpdateCallback, @Nullable TestRequestEntity_Modification... modifications) throws Exception {
        E savedEntityToUpdate = saveServiceEntity(entityToUpdate);
        updateRequestDto.setId(savedEntityToUpdate.getId());
        return updateEntity_ShouldSucceed(updateRequestDto, fullUpdate,updatedValuesPostUpdateCallback, modifications);
    }


    protected <Dto extends IdentifiableEntity<Id>> Dto updateEntity_ShouldSucceed(IdentifiableEntity<Id> updateRequestDto,boolean fullUpdate, @Nullable PostUpdateCallback<E,Id> updatedValuesPostUpdateCallback, @Nullable TestRequestEntity_Modification... modifications) throws Exception {
        Assertions.assertNotNull(updateRequestDto.getId());
        Assertions.assertEquals(mappingContext().getUpdateArgDtoClass(),updateRequestDto.getClass());


        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(
                CrudController_TestCase.SUCCESSFUL_UPDATE,
                updateRequestDto.getId(),
                getFullUpdateAwareModifications(fullUpdate,modifications)
        );
        //Entity to update must be saved already
        Optional<E> entityBeforeUpdate = testCrudService.findById(updateRequestDto.getId());
        Assertions.assertTrue(entityBeforeUpdate.isPresent(), "Entity to update was not present");
        //update request
        ResponseEntity<String> responseEntity = updateEntity(updateRequestDto, testRequestEntity);
        Assertions.assertEquals(testRequestEntity.getExpectedHttpStatus(), responseEntity.getStatusCode(),responseEntity.getBody());
        //validate response Dto
        IdentifiableEntity<Id> responseDto = controller().getMediaTypeStrategy().readDtoFromBody(responseEntity.getBody(), mappingContext().getUpdateReturnDtoClass());
        Assertions.assertNotNull(responseDto);
        if (updatedValuesPostUpdateCallback != null) {
            //check that Changes were actually applied -> relevant attribute values specified by UpdateSuccessfulChecker Impl are equal now
            Optional<E> entityAfterUpdate = getTestService().findById(responseDto.getId());
            updatedValuesPostUpdateCallback.callback(entityAfterUpdate.get());
        }
        Assertions.assertEquals(mappingContext().getUpdateReturnDtoClass(),responseDto.getClass());
        return (Dto) responseDto;
    }

    protected TestRequestEntity_Modification[] getFullUpdateAwareModifications(boolean fullUpdate, TestRequestEntity_Modification... modifications){
        TestRequestEntity_Modification[] finalModifications = null;
        if(fullUpdate) {
            if (modifications != null) {
                int appendedLength = modifications.length + 1;
                finalModifications = new TestRequestEntity_Modification[appendedLength];
                for (int i = 0; i < modifications.length; i++) {
                    finalModifications[i]=modifications[i];
                }
                finalModifications[appendedLength-1]=fullUpdate();
            }
        }else {
            finalModifications=modifications;
        }
        return finalModifications;
    }

    private TestRequestEntity_Modification fullUpdate(){
        Map<String,String> fullUpdateQueryParam = new HashMap<>();
        fullUpdateQueryParam.put(DtoCrudController_SpringAdapter.FULL_UPDATE_QUERY_PARAM,"true");
        return TestRequestEntity_Modification.builder()
                .additionalQueryParams(fullUpdateQueryParam)
                .build();
    }


    protected <Dto extends IdentifiableEntity<Id>> Dto updateEntity_ShouldSucceed(IdentifiableEntity<Id> updateRequestDto,boolean fullUpdate) throws Exception {
        return updateEntity_ShouldSucceed(updateRequestDto, fullUpdate,null,null);
    }

    protected <Dto extends IdentifiableEntity<Id>> Dto updateEntity_ShouldSucceed(IdentifiableEntity<Id> updateRequestDto,boolean fullUpdate, TestRequestEntity_Modification... modifications) throws Exception {
        return updateEntity_ShouldSucceed(updateRequestDto, fullUpdate,null,modifications);
    }

    protected ResponseEntity<String> updateEntity_ShouldFail(IdentifiableEntity<Id> updateRequestDto, boolean fullUpdate, @Nullable PostUpdateCallback<E,Id> updatedValuesPostUpdateCallback, TestRequestEntity_Modification... modifications) throws Exception {
        Assertions.assertNotNull(updateRequestDto.getId());

        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(
                CrudController_TestCase.FAILED_UPDATE,
                updateRequestDto.getId(),
                modifications);
        //Entity muss vorher auch schon da sein
        Optional<E> entityBeforeUpdate = getTestService().findById(updateRequestDto.getId());
        Assertions.assertTrue(entityBeforeUpdate.isPresent(), "Entity to update was not present");

        ResponseEntity<String> responseEntity = updateEntity(updateRequestDto, testRequestEntity);
        Assertions.assertEquals(testRequestEntity.getExpectedHttpStatus(), responseEntity.getStatusCode(), responseEntity.getBody());

        if (updatedValuesPostUpdateCallback != null) {
            //check that Changes were not applied -> relevant attribute values specified by UpdateSuccessfulChecker Impl are still the same as before update request
            Optional<E> entityAfterUpdate = getTestService().findById(updateRequestDto.getId());
            updatedValuesPostUpdateCallback.callback(entityAfterUpdate.get());
        }
        return responseEntity;
    }

    protected ResponseEntity<String> updateEntity_ShouldFail(IdentifiableEntity<Id> newEntity,boolean fullUpdate) throws Exception {
        return updateEntity_ShouldFail(newEntity, fullUpdate,null, null);
    }

    protected ResponseEntity<String> updateEntity_ShouldFail(IdentifiableEntity<Id> newEntity,boolean fullUpdate, TestRequestEntity_Modification... modifications) throws Exception {
        return updateEntity_ShouldFail(newEntity, fullUpdate,null, modifications);
    }

    protected ResponseEntity<String> updateEntity_ShouldFail(IdentifiableEntity<Id> newEntity,boolean fullUpdate, PostUpdateCallback<E,Id> equalChecker) throws Exception {
        return updateEntity_ShouldFail(newEntity,fullUpdate, equalChecker, null);
    }

    protected ResponseEntity<String> updateEntity_ShouldFail(E entityToUpdate, IdentifiableEntity<Id> updateRequest,boolean fullUpdate) throws Exception {
        E savedEntityToUpdate = saveServiceEntity(entityToUpdate);
        updateRequest.setId(savedEntityToUpdate.getId());
        return updateEntity_ShouldFail(updateRequest,fullUpdate);
    }

    protected ResponseEntity<String> updateEntity_ShouldFail(E entityToUpdate, IdentifiableEntity<Id> updateRequest,boolean fullUpdate, @Nullable PostUpdateCallback<E,Id> updatedValuesPostUpdateCallback) throws Exception {
        E savedEntityToUpdate = saveServiceEntity(entityToUpdate);
        updateRequest.setId(savedEntityToUpdate.getId());
        return updateEntity_ShouldFail(updateRequest, fullUpdate,updatedValuesPostUpdateCallback);
    }

    protected ResponseEntity<String> updateEntity_ShouldFail(E entityToUpdate, IdentifiableEntity<Id> updateRequest,boolean fullUpdate, @Nullable TestRequestEntity_Modification... modifications) throws Exception {
        E savedEntityToUpdate = saveServiceEntity(entityToUpdate);
        updateRequest.setId(savedEntityToUpdate.getId());
        return updateEntity_ShouldFail(updateRequest, fullUpdate,modifications);
    }

    protected ResponseEntity<String> updateEntity_ShouldFail(E entityToUpdate, IdentifiableEntity<Id> updateRequest,boolean fullUpdate, @Nullable PostUpdateCallback<E,Id> updatedValuesPostUpdateCallback, @Nullable TestRequestEntity_Modification... modifications) throws Exception {
        E savedEntityToUpdate = saveServiceEntity(entityToUpdate);
        updateRequest.setId(savedEntityToUpdate.getId());
        return updateEntity_ShouldFail(updateRequest,fullUpdate, updatedValuesPostUpdateCallback, modifications);
    }


    /**
     * Send update Entity Request to Backend
     *
     * @param newEntity updated entityDto
     * @return backend Response
     */
    protected ResponseEntity<String> updateEntity(IdentifiableEntity<Id> newEntity, TestRequestEntity testRequestEntity) {
        Assertions.assertNotNull(newEntity.getId());
        return getRestTemplate().exchange(RequestEntityMapper.map(testRequestEntity, newEntity), String.class);
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

    /**
     * @param id id of the entity, that should be found
     * @return the dto of the requested entity found on backend with given id
     * @throws Exception
     */
    protected <Dto extends IdentifiableEntity<Id>> Dto findEntity_ShouldSucceed(Id id, TestRequestEntity_Modification... modifications) throws Exception {
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(
                CrudController_TestCase.SUCCESSFUL_FIND,
                id,
                modifications);
        ResponseEntity<String> responseEntity = findEntity(id, testRequestEntity);
        Assertions.assertEquals(testRequestEntity.getExpectedHttpStatus(), responseEntity.getStatusCode(), responseEntity.getBody());
        IdentifiableEntity<Id> responseDto = controller.getMediaTypeStrategy().readDtoFromBody(responseEntity.getBody(), mappingContext().getFindReturnDtoClass());
        Assertions.assertNotNull(responseDto);
        Assertions.assertEquals(mappingContext().getFindReturnDtoClass(),responseDto.getClass());
        return (Dto) responseDto;
    }

    protected <Dto extends IdentifiableEntity<Id>> Dto findEntity_ShouldSucceed(Id id) throws Exception {
        return findEntity_ShouldSucceed(id, null);
    }

    protected ResponseEntity<String> findEntity_ShouldFail(Id id, TestRequestEntity_Modification... modifications) throws Exception {
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(
                CrudController_TestCase.FAILED_FIND,
                id,
                modifications);
        ResponseEntity<String> responseEntity = findEntity(id, testRequestEntity);
        Assertions.assertEquals(testRequestEntity.getExpectedHttpStatus(), responseEntity.getStatusCode(), responseEntity.getBody());
        return responseEntity;
    }

    protected ResponseEntity<String> findEntity_ShouldFail(Id id) throws Exception {
        return findEntity_ShouldFail(id, null);
    }


    protected <Dto extends IdentifiableEntity<Id>> Dto mapEntityToDto(E entity, Class<Dto> dtoClass) throws EntityMappingException {
        return controller().findMapperAndMapToDto(entity, dtoClass);
    }

    protected E mapDtoToEntity(IdentifiableEntity<Id> dto, Class<? extends IdentifiableEntity<Id>> dtoClass) throws EntityMappingException {
        return controller().findMapperAndMapToEntity(dto, dtoClass);
    }

    /**
     * send find Entity Request to backend
     *
     * @param id
     * @return backend Response
     */
    protected ResponseEntity<String> findEntity(Id id, TestRequestEntity testRequestEntity) {
        Assertions.assertNotNull(id);
        return getRestTemplate().exchange(RequestEntityMapper.map(testRequestEntity, id), String.class);
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