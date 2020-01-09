package io.github.vincemann.generic.crud.lib.test.controller.springAdapter;

import io.github.vincemann.generic.crud.lib.controller.BasicDtoCrudController;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.MappingContext;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.exception.EntityMappingException;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.DtoCrudController_SpringAdapter;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.idFetchingStrategy.UrlParamIdFetchingStrategy;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.test.IntegrationTest;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.RequestEntityMapper;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntity_Modification;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.TestRequestEntity_Factory;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.postUpdateCallback.PostUpdateCallback;
import io.github.vincemann.generic.crud.lib.test.forceEagerFetch.Hibernate_ForceEagerFetch_Helper;
import io.github.vincemann.generic.crud.lib.test.forceEagerFetch.proxy.CrudService_HibernateForceEagerFetch_Proxy;
import io.github.vincemann.generic.crud.lib.test.testExecutionListeners.ResetDatabaseTestExecutionListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
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
public abstract class UrlParamId_ControllerIntegrationTest
        <
                E extends IdentifiableEntity<Id>,
                Id extends Serializable,
                R extends CrudRepository<E, Id>
        >
        extends IntegrationTest
        implements InitializingBean {


    private TestRequestEntity_Factory requestEntityFactory;
    private DtoCrudController_SpringAdapter<E, Id, R> controller;
    @Getter
    private Class<E> entityClass = (Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    @Getter
    private String entityIdParamKey;
    private Hibernate_ForceEagerFetch_Helper hibernate_forceEagerFetch_helper;
    private CrudService<E, Id, R> testCrudService;
    private MappingContext<Id> mappingContext;


    @Autowired
    public void injectCrudController(DtoCrudController_SpringAdapter<E, Id, R> crudController) {
        this.controller = crudController;
    }

    protected <E extends IdentifiableEntity<Id>,
            Id extends Serializable,
            R extends CrudRepository<E, Id>
            > CrudService<E, Id, R> wrapWithEagerFetchProxy(CrudService<E, Id, R> crudService) {
        return new CrudService_HibernateForceEagerFetch_Proxy<>(crudService, hibernate_forceEagerFetch_helper);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        this.entityIdParamKey = ((UrlParamIdFetchingStrategy) controller().getIdIdFetchingStrategy()).getIdUrlParamKey();
        mappingContext = controller().getMappingContext();
        if (testCrudService == null) {
            testCrudService = controller().getCrudService();
        }
    }

    public void setTestCrudService(CrudService<E, Id, R> testService) {
        this.testCrudService = testService;
    }

    @Autowired
    public void injectRequestEntityFactory(TestRequestEntity_Factory requestEntityFactory) {
        requestEntityFactory.setTest(this);
        this.requestEntityFactory = requestEntityFactory;
    }

    @Autowired
    public void injectHibernate_forceEagerFetch_helper(Hibernate_ForceEagerFetch_Helper hibernate_forceEagerFetch_helper) {
        this.hibernate_forceEagerFetch_helper = hibernate_forceEagerFetch_helper;
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

    protected <Dto extends IdentifiableEntity<Id>> Dto updateEntity_ShouldSucceed(E entityToUpdate, IdentifiableEntity<Id> updateRequest,boolean fullUpdate, @Nullable PostUpdateCallback<E,Id> updatedValuesPostUpdateCallback) throws Exception {
        E savedEntityToUpdate = saveServiceEntity(entityToUpdate);
        updateRequest.setId(savedEntityToUpdate.getId());
        return updateEntity_ShouldSucceed(updateRequest,fullUpdate, updatedValuesPostUpdateCallback);
    }

    protected <Dto extends IdentifiableEntity<Id>> Dto updateEntity_ShouldSucceed(E entityToUpdate, IdentifiableEntity<Id> updateRequest,boolean fullUpdate, @Nullable TestRequestEntity_Modification... modifications) throws Exception {
        E savedEntityToUpdate = saveServiceEntity(entityToUpdate);
        updateRequest.setId(savedEntityToUpdate.getId());
        return updateEntity_ShouldSucceed(updateRequest,fullUpdate, modifications);
    }

    protected <Dto extends IdentifiableEntity<Id>> Dto updateEntity_ShouldSucceed(E entityToUpdate, IdentifiableEntity<Id> updateRequest, @Nullable PostUpdateCallback<E,Id> updatedValuesPostUpdateCallback,boolean fullUpdate, @Nullable TestRequestEntity_Modification... modifications) throws Exception {
        E savedEntityToUpdate = saveServiceEntity(entityToUpdate);
        updateRequest.setId(savedEntityToUpdate.getId());
        return updateEntity_ShouldSucceed(updateRequest, updatedValuesPostUpdateCallback,fullUpdate, modifications);
    }


    protected <Dto extends IdentifiableEntity<Id>> Dto updateEntity_ShouldSucceed(IdentifiableEntity<Id> updateRequestDto, @Nullable PostUpdateCallback<E,Id> updatedValuesPostUpdateCallback,boolean fullUpdate, @Nullable TestRequestEntity_Modification... modifications) throws Exception {
        Assertions.assertNotNull(updateRequestDto.getId());
        Assertions.assertEquals(mappingContext().getUpdateArgDtoClass(),updateRequestDto.getClass());

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
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(
                CrudController_TestCase.SUCCESSFUL_UPDATE,
                updateRequestDto.getId(),
                modifications
        );
        //Entity to update must be saved already
        Optional<E> entityBeforeUpdate = testCrudService.findById(updateRequestDto.getId());
        Assertions.assertTrue(entityBeforeUpdate.isPresent(), "Entity to update was not present");
        //update request
        ResponseEntity<String> responseEntity = updateEntity(updateRequestDto, testRequestEntity);
        Assertions.assertEquals(testRequestEntity.getExpectedHttpStatus(), responseEntity.getStatusCode());
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

    private TestRequestEntity_Modification fullUpdate(){
        MultiValueMap<String,String> fullUpdateQueryParam = new LinkedMultiValueMap<>();
        fullUpdateQueryParam.put(DtoCrudController_SpringAdapter.FULL_UPDATE_QUERY_PARAM,Arrays.asList("true"));
        return TestRequestEntity_Modification.builder()
                .additionalQueryParams(fullUpdateQueryParam)
                .build();
    }


    protected <Dto extends IdentifiableEntity<Id>> Dto updateEntity_ShouldSucceed(IdentifiableEntity<Id> updateRequestDto,boolean fullUpdate) throws Exception {
        return updateEntity_ShouldSucceed(updateRequestDto, null, fullUpdate,null);
    }

    protected <Dto extends IdentifiableEntity<Id>> Dto updateEntity_ShouldSucceed(IdentifiableEntity<Id> updateRequestDto,boolean fullUpdate, TestRequestEntity_Modification... modifications) throws Exception {
        return updateEntity_ShouldSucceed(updateRequestDto, null, fullUpdate,modifications);
    }

    protected ResponseEntity<String> updateEntity_ShouldFail(IdentifiableEntity<Id> updateRequestDto, @Nullable PostUpdateCallback<E,Id> updatedValuesPostUpdateCallback, TestRequestEntity_Modification... modifications) throws Exception {
        Assertions.assertNotNull(updateRequestDto.getId());

        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(
                CrudController_TestCase.FAILED_UPDATE,
                updateRequestDto.getId(),
                modifications);
        //Entity muss vorher auch schon da sein
        Optional<E> entityBeforeUpdate = getTestService().findById(updateRequestDto.getId());
        Assertions.assertTrue(entityBeforeUpdate.isPresent(), "Entity to update was not present");

        ResponseEntity<String> responseEntity = updateEntity(updateRequestDto, testRequestEntity);
        Assertions.assertEquals(testRequestEntity.getExpectedHttpStatus(), responseEntity.getStatusCode(), "Status was : " + responseEntity.getStatusCode());

        if (updatedValuesPostUpdateCallback != null) {
            //check that Changes were not applied -> relevant attribute values specified by UpdateSuccessfulChecker Impl are still the same as before update request
            Optional<E> entityAfterUpdate = getTestService().findById(updateRequestDto.getId());
            updatedValuesPostUpdateCallback.callback(entityAfterUpdate.get());
        }
        return responseEntity;
    }

    protected ResponseEntity<String> updateEntity_ShouldFail(IdentifiableEntity<Id> newEntity) throws Exception {
        return updateEntity_ShouldFail(newEntity, null, null);
    }

    protected ResponseEntity<String> updateEntity_ShouldFail(IdentifiableEntity<Id> newEntity, TestRequestEntity_Modification... modifications) throws Exception {
        return updateEntity_ShouldFail(newEntity, null, modifications);
    }

    protected ResponseEntity<String> updateEntity_ShouldFail(IdentifiableEntity<Id> newEntity, PostUpdateCallback<E,Id> equalChecker) throws Exception {
        return updateEntity_ShouldFail(newEntity, equalChecker, null);
    }

    protected ResponseEntity<String> updateEntity_ShouldFail(E entityToUpdate, IdentifiableEntity<Id> updateRequest) throws Exception {
        E savedEntityToUpdate = saveServiceEntity(entityToUpdate);
        updateRequest.setId(savedEntityToUpdate.getId());
        return updateEntity_ShouldFail(updateRequest);
    }

    protected ResponseEntity<String> updateEntity_ShouldFail(E entityToUpdate, IdentifiableEntity<Id> updateRequest, @Nullable PostUpdateCallback<E,Id> updatedValuesPostUpdateCallback) throws Exception {
        E savedEntityToUpdate = saveServiceEntity(entityToUpdate);
        updateRequest.setId(savedEntityToUpdate.getId());
        return updateEntity_ShouldFail(updateRequest, updatedValuesPostUpdateCallback);
    }

    protected ResponseEntity<String> updateEntity_ShouldFail(E entityToUpdate, IdentifiableEntity<Id> updateRequest, @Nullable TestRequestEntity_Modification... modifications) throws Exception {
        E savedEntityToUpdate = saveServiceEntity(entityToUpdate);
        updateRequest.setId(savedEntityToUpdate.getId());
        return updateEntity_ShouldFail(updateRequest, modifications);
    }

    protected ResponseEntity<String> updateEntity_ShouldFail(E entityToUpdate, IdentifiableEntity<Id> updateRequest, @Nullable PostUpdateCallback<E,Id> updatedValuesPostUpdateCallback, @Nullable TestRequestEntity_Modification... modifications) throws Exception {
        E savedEntityToUpdate = saveServiceEntity(entityToUpdate);
        updateRequest.setId(savedEntityToUpdate.getId());
        return updateEntity_ShouldFail(updateRequest, updatedValuesPostUpdateCallback, modifications);
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

    protected <Dto extends IdentifiableEntity<Id>> Dto createEntity_ShouldSucceed(IdentifiableEntity<Id> returnDto) throws Exception {
        return createEntity_ShouldSucceed(returnDto, null);
    }

    protected <Dto extends IdentifiableEntity<Id>> Dto createEntity_ShouldSucceed(IdentifiableEntity<Id> createRequestDto, TestRequestEntity_Modification... modifications) throws Exception {
        Assertions.assertNull(createRequestDto.getId());
        Assertions.assertEquals(mappingContext().getCreateArgDtoClass(),createRequestDto.getClass());

        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(CrudController_TestCase.SUCCESSFUL_CREATE, null, modifications);
        ResponseEntity<String> responseEntity = createEntity(createRequestDto, testRequestEntity);
        Assertions.assertEquals(testRequestEntity.getExpectedHttpStatus(), responseEntity.getStatusCode(), "Status was : " + responseEntity.getStatusCode() + " response Body: " + responseEntity.getBody());
        IdentifiableEntity<Id> responseDto = controller.getMediaTypeStrategy().readDtoFromBody(responseEntity.getBody(), mappingContext().getCreateReturnDtoClass());

        Assertions.assertNotNull(responseDto);
        Assertions.assertEquals(mappingContext().getCreateReturnDtoClass(),responseDto.getClass());
        return (Dto) responseDto;
    }

    protected ResponseEntity<String> createEntity_ShouldFail(IdentifiableEntity<Id> dto) throws Exception {
        return createEntity_ShouldFail(dto, null);
    }

    protected ResponseEntity<String> createEntity_ShouldFail(IdentifiableEntity<Id> dto, TestRequestEntity_Modification... modifications) throws Exception {
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(
                CrudController_TestCase.FAILED_CREATE,
                null,
                modifications);
        ResponseEntity<String> responseEntity = createEntity(dto, testRequestEntity);
        Assertions.assertEquals(testRequestEntity.getExpectedHttpStatus(), responseEntity.getStatusCode(), "Status was : " + responseEntity.getStatusCode() + " response Body: " + responseEntity.getBody());
        return responseEntity;
    }

    /**
     * Send create Entity Request to Backend, raw Response is returned
     *
     * @param dto the Dto entity that should be stored
     * @return
     */
    protected ResponseEntity<String> createEntity(IdentifiableEntity<Id> dto, TestRequestEntity testRequestEntity) {
        return getRestTemplate().exchange(RequestEntityMapper.map(testRequestEntity, dto), String.class);
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
        Assertions.assertEquals(testRequestEntity.getExpectedHttpStatus(), responseEntity.getStatusCode(), "Status was : " + responseEntity.getStatusCode() + " response Body: " + responseEntity.getBody());
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
        Assertions.assertEquals(testRequestEntity.getExpectedHttpStatus(), responseEntity.getStatusCode(), "Status was : " + responseEntity.getStatusCode() + " response Body: " + responseEntity.getBody());

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
        Assertions.assertEquals(testRequestEntity.getExpectedHttpStatus(), responseEntity.getStatusCode(), "Status was : " + responseEntity.getStatusCode() + " response Body: " + responseEntity.getBody());
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
        Assertions.assertEquals(testRequestEntity.getExpectedHttpStatus(), responseEntity.getStatusCode(), "Status was : " + responseEntity.getStatusCode() + " response Body: " + responseEntity.getBody());
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


    /**
     * Service used by Test, this does not need to be the Service, the controller is using
     * @return
     */
    public CrudService<E, Id, R> getTestService() {
        return testCrudService;
    }

    public <S extends CrudService<E,Id, R>> S getCastedService() {
        return controller().getCastedCrudService();
    }

    public DtoCrudController_SpringAdapter<E, Id, R> controller() {
        return controller;
    }

    public MappingContext<Id> mappingContext() {
        return mappingContext;
    }
}