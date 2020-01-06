package io.github.vincemann.generic.crud.lib.test.controller.springAdapter;

import io.github.vincemann.generic.crud.lib.controller.BasicDtoCrudController;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.EntityMappingException;
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
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.test.context.TestExecutionListeners;

import java.io.Serializable;
import java.util.*;

/**
 * Integration Test for a {@link DtoCrudController_SpringAdapter} with {@link UrlParamIdFetchingStrategy}, that tests typical Crud operations
 *
 * Wraps Controllers {@link BasicDtoCrudController#getCrudService()} with {@link CrudService_HibernateForceEagerFetch_Proxy}.
 * -> No LazyInit Exceptions possible.
 *
 * @param <ServiceE>
 * @param <Dto>
 * @param <Service>
 * @param <Controller>
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
                        ServiceE extends IdentifiableEntity<Id>,
                        Dto extends IdentifiableEntity<Id>,
                        Repo extends CrudRepository<ServiceE, Id>,
                        Service extends CrudService<ServiceE, Id,Repo>,
                        Controller extends DtoCrudController_SpringAdapter<ServiceE, Dto, Id,Repo, Service>,
                        Id extends Serializable
                >
        extends IntegrationTest
            implements InitializingBean {



            //private List<Plugin<? super Dto,? super ServiceE, ? super Id>> plugins = new ArrayList<>();
            private TestRequestEntity_Factory requestEntityFactory;
    @Getter private Controller crudController;
    @Getter private Class<Dto> dtoEntityClass;
    @Getter private Class<ServiceE> serviceEntityClass;
    @Getter private String entityIdParamKey;
            private Hibernate_ForceEagerFetch_Helper hibernate_forceEagerFetch_helper;
            private CrudService<ServiceE,Id,Repo> userInjectedCrudService;


    @Autowired
    public void injectCrudController(Controller crudController) {
        this.crudController = crudController;
    }

    protected  <E extends IdentifiableEntity<Id>,Id extends Serializable,R extends CrudRepository<E,Id>> CrudService<E,Id,R> wrapWithEagerFetchProxy(CrudService<E,Id,R> crudService){
        return new CrudService_HibernateForceEagerFetch_Proxy<>(crudService,hibernate_forceEagerFetch_helper);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        this.dtoEntityClass=crudController.getDtoClass();
        this.serviceEntityClass = crudController.getServiceEntityClass();
        this.entityIdParamKey = ((UrlParamIdFetchingStrategy) crudController.getIdIdFetchingStrategy()).getIdUrlParamKey();
        if(userInjectedCrudService!=null){
            getCrudController().setCrudService(userInjectedCrudService);
        }
    }

    public void setTestService(CrudService<ServiceE,Id,Repo> testService){
        if(getCrudController()==null){
            //might happen when this method is overridden and @Autowired
            this.userInjectedCrudService=testService;
        }else {
            getCrudController().setCrudService(testService);
        }
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

    protected Set<Dto> findAllEntities_ShouldSucceed(Set<ServiceE> entitiesExpectedToBeFound,TestRequestEntity_Modification... modifications) throws Exception {
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(
                CrudController_TestCase.SUCCESSFUL_FIND_ALL,
                null,
                modifications);
        ResponseEntity<String> responseEntity = findAllEntities(testRequestEntity);

        @SuppressWarnings("unchecked")
        Set<Dto> httpResponseDtos = crudController.getMediaTypeStrategy().readDtosFromBody(responseEntity.getBody(), getDtoEntityClass(), Set.class);


        Assertions.assertEquals(entitiesExpectedToBeFound.size(), httpResponseDtos.size());
        List<Id> idsSeen = new ArrayList<>();
        for (Dto dto : httpResponseDtos) {
            //prevent duplicates
            Assertions.assertFalse(idsSeen.contains(dto.getId()));
            idsSeen.add(dto.getId());
        }
        return httpResponseDtos;
    }

    protected Set<Dto> findAllEntities_ShouldSucceed(Set<ServiceE> entitiesExpectedToBeFound) throws Exception {
        return findAllEntities_ShouldSucceed(entitiesExpectedToBeFound,null);
    }

    protected ResponseEntity<String> findAllEntities(TestRequestEntity testRequestEntity) {
        ResponseEntity<String> responseEntity = getRestTemplate().exchange(RequestEntityMapper.map(testRequestEntity, null), String.class);
        Assertions.assertEquals(testRequestEntity.getExpectedHttpStatus(), responseEntity.getStatusCode(), "Status was : " + responseEntity.getStatusCode() + " response Body: " + responseEntity.getBody());
        return responseEntity;
    }

    protected Dto updateEntity_ShouldSucceed(ServiceE entityToUpdate, Dto updateRequest) throws Exception {
        ServiceE savedEntityToUpdate = saveServiceEntity(entityToUpdate);
        updateRequest.setId(savedEntityToUpdate.getId());
        return updateEntity_ShouldSucceed(updateRequest);
    }

    protected Dto updateEntity_ShouldSucceed(ServiceE entityToUpdate, Dto updateRequest,@Nullable PostUpdateCallback<ServiceE> updatedValuesPostUpdateCallback) throws Exception {
        ServiceE savedEntityToUpdate = saveServiceEntity(entityToUpdate);
        updateRequest.setId(savedEntityToUpdate.getId());
        return updateEntity_ShouldSucceed(updateRequest,updatedValuesPostUpdateCallback);
    }

    protected Dto updateEntity_ShouldSucceed(ServiceE entityToUpdate, Dto updateRequest,@Nullable TestRequestEntity_Modification... modifications) throws Exception {
        ServiceE savedEntityToUpdate = saveServiceEntity(entityToUpdate);
        updateRequest.setId(savedEntityToUpdate.getId());
        return updateEntity_ShouldSucceed(updateRequest,modifications);
    }

    protected Dto updateEntity_ShouldSucceed(ServiceE entityToUpdate, Dto updateRequest,@Nullable PostUpdateCallback<ServiceE> updatedValuesPostUpdateCallback,@Nullable TestRequestEntity_Modification... modifications) throws Exception {
        ServiceE savedEntityToUpdate = saveServiceEntity(entityToUpdate);
        updateRequest.setId(savedEntityToUpdate.getId());
        return updateEntity_ShouldSucceed(updateRequest,updatedValuesPostUpdateCallback,modifications);
    }


    protected Dto updateEntity_ShouldSucceed(Dto updateRequestDto,@Nullable PostUpdateCallback<ServiceE> updatedValuesPostUpdateCallback, @Nullable TestRequestEntity_Modification... modifications) throws Exception {
        Assertions.assertNotNull(updateRequestDto.getId());
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(
                CrudController_TestCase.SUCCESSFUL_UPDATE,
                updateRequestDto.getId(),
                modifications
                );
        //Entity to update must be saved already
        Optional<ServiceE> serviceEntityToUpdate = crudController.getCrudService().findById(updateRequestDto.getId());
        Assertions.assertTrue(serviceEntityToUpdate.isPresent(), "Entity to update was not present");
        //update request
        ResponseEntity<String> responseEntity = updateEntity(updateRequestDto, testRequestEntity);
        Assertions.assertEquals(testRequestEntity.getExpectedHttpStatus(), responseEntity.getStatusCode());
        //validate response Dto
        Dto httpResponseDto = crudController.getMediaTypeStrategy().readDtoFromBody(responseEntity.getBody(), dtoEntityClass);
        Assertions.assertNotNull(httpResponseDto);
        if(updatedValuesPostUpdateCallback !=null){
            //check that Changes were actually applied -> relevant attribute values specified by UpdateSuccessfulChecker Impl are equal now
            ServiceE serviceUpdateRequestEntity = mapDtoToServiceEntity(updateRequestDto);
            Optional<ServiceE> updatedServiceEntity = getService().findById(httpResponseDto.getId());
            updatedValuesPostUpdateCallback.callback(serviceUpdateRequestEntity,updatedServiceEntity.get());
        }
        return httpResponseDto;
    }

    protected Dto mapServiceEntityToDto(ServiceE serviceEntity) throws EntityMappingException {
        return getCrudController().getDtoMapper().mapServiceEntityToDto(serviceEntity,getDtoEntityClass());
    }

    protected ServiceE mapDtoToServiceEntity(Dto dto) throws EntityMappingException {
        return getCrudController().getDtoMapper().mapServiceEntityToDto(dto,getServiceEntityClass());
    }

    protected Dto updateEntity_ShouldSucceed(Dto updateRequestDto) throws Exception {
        return updateEntity_ShouldSucceed(updateRequestDto,null,null);
    }

    protected Dto updateEntity_ShouldSucceed(Dto updateRequestDto, TestRequestEntity_Modification... modifications) throws Exception {
        return updateEntity_ShouldSucceed(updateRequestDto,null,modifications);
    }

    protected ResponseEntity<String> updateEntity_ShouldFail(Dto updateRequestDto, @Nullable PostUpdateCallback<ServiceE> updatedValuesPostUpdateCallback,TestRequestEntity_Modification... modifications) throws Exception {
        Assertions.assertNotNull(updateRequestDto.getId());
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(
                CrudController_TestCase.FAILED_UPDATE,
                updateRequestDto.getId(),
                modifications);
        //Entity muss vorher auch schon da sein
        Optional<ServiceE> serviceEntityToUpdate = getService().findById(updateRequestDto.getId());
        Assertions.assertTrue(serviceEntityToUpdate.isPresent(), "Entity to update was not present");

        ResponseEntity<String> responseEntity = updateEntity(updateRequestDto, testRequestEntity);
        Assertions.assertEquals(responseEntity.getStatusCode(), testRequestEntity.getExpectedHttpStatus(), "Status was : " + responseEntity.getStatusCode());

        if(updatedValuesPostUpdateCallback !=null){
            //check that Changes were not applied -> relevant attribute values specified by UpdateSuccessfulChecker Impl are still the same as before update request
            Optional<ServiceE> serviceEntityAfterUpdate = getService().findById(updateRequestDto.getId());
            updatedValuesPostUpdateCallback.callback(serviceEntityToUpdate.get(),serviceEntityAfterUpdate.get());
        }

        return responseEntity;
    }

    protected ResponseEntity<String> updateEntity_ShouldFail(Dto newEntity) throws Exception {
        return updateEntity_ShouldFail(newEntity,null,null);
    }

    protected ResponseEntity<String> updateEntity_ShouldFail(Dto newEntity,TestRequestEntity_Modification... modifications) throws Exception {
        return updateEntity_ShouldFail(newEntity,null,modifications);
    }

    protected ResponseEntity<String> updateEntity_ShouldFail(Dto newEntity, PostUpdateCallback<ServiceE> equalChecker) throws Exception {
        return updateEntity_ShouldFail(newEntity,equalChecker,null);
    }

    protected ResponseEntity<String> updateEntity_ShouldFail(ServiceE entityToUpdate, Dto updateRequest) throws Exception {
        ServiceE savedEntityToUpdate = saveServiceEntity(entityToUpdate);
        updateRequest.setId(savedEntityToUpdate.getId());
        return updateEntity_ShouldFail(updateRequest);
    }

    protected ResponseEntity<String> updateEntity_ShouldFail(ServiceE entityToUpdate, Dto updateRequest,@Nullable PostUpdateCallback<ServiceE> updatedValuesPostUpdateCallback) throws Exception {
        ServiceE savedEntityToUpdate = saveServiceEntity(entityToUpdate);
        updateRequest.setId(savedEntityToUpdate.getId());
        return updateEntity_ShouldFail(updateRequest,updatedValuesPostUpdateCallback);
    }

    protected ResponseEntity<String> updateEntity_ShouldFail(ServiceE entityToUpdate, Dto updateRequest,@Nullable TestRequestEntity_Modification... modifications) throws Exception {
        ServiceE savedEntityToUpdate = saveServiceEntity(entityToUpdate);
        updateRequest.setId(savedEntityToUpdate.getId());
        return updateEntity_ShouldFail(updateRequest,modifications);
    }

    protected ResponseEntity<String> updateEntity_ShouldFail(ServiceE entityToUpdate, Dto updateRequest,@Nullable PostUpdateCallback<ServiceE> updatedValuesPostUpdateCallback,@Nullable TestRequestEntity_Modification... modifications) throws Exception {
        ServiceE savedEntityToUpdate = saveServiceEntity(entityToUpdate);
        updateRequest.setId(savedEntityToUpdate.getId());
        return updateEntity_ShouldFail(updateRequest,updatedValuesPostUpdateCallback,modifications);
    }


    /**
     * Send update Entity Request to Backend
     *
     * @param newEntity updated entityDto
     * @return backend Response
     */
    protected ResponseEntity<String> updateEntity(Dto newEntity, TestRequestEntity testRequestEntity) {
        Assertions.assertNotNull(newEntity.getId());
        return getRestTemplate().exchange(RequestEntityMapper.map(testRequestEntity, newEntity), String.class);
    }

    protected Dto createEntity_ShouldSucceed(Dto dto) throws Exception {
        return createEntity_ShouldSucceed(dto,null);
    }

    protected Dto createEntity_ShouldSucceed(Dto dto, TestRequestEntity_Modification... modifications) throws Exception {
        Assertions.assertNull(dto.getId());
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(CrudController_TestCase.SUCCESSFUL_CREATE, null, modifications);
        ResponseEntity<String> responseEntity = createEntity(dto, testRequestEntity);
        Assertions.assertEquals(responseEntity.getStatusCode(), testRequestEntity.getExpectedHttpStatus(), "Status was : " + responseEntity.getStatusCode() + " response Body: " + responseEntity.getBody());
        Dto httpResponseEntity = crudController.getMediaTypeStrategy().readDtoFromBody(responseEntity.getBody(), dtoEntityClass);
        return httpResponseEntity;
    }

    protected ResponseEntity<String> createEntity_ShouldFail(Dto dto) throws Exception {
        return createEntity_ShouldFail(dto,null);
    }

    protected ResponseEntity<String> createEntity_ShouldFail(Dto dto, TestRequestEntity_Modification... modifications) throws Exception {
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(
                CrudController_TestCase.FAILED_CREATE,
                null,
                modifications);
        ResponseEntity<String> responseEntity = createEntity(dto, testRequestEntity);
        Assertions.assertEquals(responseEntity.getStatusCode(), testRequestEntity.getExpectedHttpStatus(), "Status was : " + responseEntity.getStatusCode() + " response Body: " + responseEntity.getBody());
        return responseEntity;
    }

    /**
     * Send create Entity Request to Backend, raw Response is returned
     *
     * @param dtoEntity the Dto entity that should be stored
     * @return
     */
    protected ResponseEntity<String> createEntity(Dto dtoEntity, TestRequestEntity testRequestEntity) {
        return getRestTemplate().exchange(RequestEntityMapper.map(testRequestEntity, dtoEntity), String.class);
    }


    protected ResponseEntity<String> deleteEntity_ShouldSucceed(Id id) throws Exception {
        return deleteEntity_ShouldSucceed(id,null);
    }

    protected ResponseEntity<String> deleteEntity_ShouldSucceed(Id id, TestRequestEntity_Modification... modifications) throws Exception {
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(
                CrudController_TestCase.SUCCESSFUL_DELETE,
                id,
                modifications);
        //Entity muss vorher auch schon da sein
        Optional<ServiceE> serviceFoundEntityBeforeDelete = crudController.getCrudService().findById(id);
        Assertions.assertTrue(serviceFoundEntityBeforeDelete.isPresent(), "Entity to delete was not present");

        //onBeforeDeleteEntityShouldSucceed(id);
        ResponseEntity<String> responseEntity = deleteEntity(id, testRequestEntity);
        Assertions.assertEquals(responseEntity.getStatusCode(), testRequestEntity.getExpectedHttpStatus(), "Status was : " + responseEntity.getStatusCode() + " response Body: " + responseEntity.getBody());
        //onAfterDeleteEntityShouldSucceed(id, responseEntity);
        return responseEntity;
    }

    protected ResponseEntity<String> deleteEntity_ShouldFail(Id id) throws Exception {
        return deleteEntity_ShouldFail(id,null);
    }

    protected ResponseEntity<String> deleteEntity_ShouldFail(Id id, TestRequestEntity_Modification... modifications) throws Exception {
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(
                CrudController_TestCase.FAILED_DELETE,
                id,
                modifications);
        //Entity muss vorher auch schon da sein
        Optional<ServiceE> serviceFoundEntityBeforeDelete = crudController.getCrudService().findById(id);
        Assertions.assertTrue(serviceFoundEntityBeforeDelete.isPresent(), "Entity to delete was not present");
        ResponseEntity<String> responseEntity = deleteEntity(id, testRequestEntity);
        Assertions.assertEquals(responseEntity.getStatusCode(), testRequestEntity.getExpectedHttpStatus(), "Status was : " + responseEntity.getStatusCode() + " response Body: " + responseEntity.getBody());

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
    protected Dto findEntity_ShouldSucceed(Id id, TestRequestEntity_Modification... modifications) throws Exception {
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(
                CrudController_TestCase.SUCCESSFUL_FIND,
                id,
                modifications);
        ResponseEntity<String> responseEntity = findEntity(id, testRequestEntity);
        Assertions.assertEquals(responseEntity.getStatusCode(), testRequestEntity.getExpectedHttpStatus(), "Status was : " + responseEntity.getStatusCode() + " response Body: " + responseEntity.getBody());
        Dto responseDto = crudController.getMediaTypeStrategy().readDtoFromBody(responseEntity.getBody(), dtoEntityClass);
        Assertions.assertNotNull(responseDto);
        return responseDto;
    }

    protected Dto findEntity_ShouldSucceed(Id id) throws Exception {
        return findEntity_ShouldSucceed(id,null);
    }

    protected ResponseEntity<String> findEntity_ShouldFail(Id id, TestRequestEntity_Modification... modifications) throws Exception {
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(
                CrudController_TestCase.FAILED_FIND,
                id,
                modifications);
        ResponseEntity<String> responseEntity = findEntity(id, testRequestEntity);
        Assertions.assertEquals(responseEntity.getStatusCode(), testRequestEntity.getExpectedHttpStatus(), "Status was : " + responseEntity.getStatusCode() + " response Body: " + responseEntity.getBody());
        return responseEntity;
    }

    protected ResponseEntity<String> findEntity_ShouldFail(Id id) throws Exception {
        return findEntity_ShouldFail(id,null);
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

    protected ServiceE saveServiceEntity(ServiceE serviceE) throws BadEntityException {
        return crudController.getCrudService().save(serviceE);
    }

    protected Collection<ServiceE> saveServiceEntities(Collection<ServiceE> serviceECollection) throws BadEntityException {
        Collection<ServiceE> savedEntities = new ArrayList<>();
        for (ServiceE serviceE : serviceECollection) {
            ServiceE savedEntity = saveServiceEntity(serviceE);
            savedEntities.add(savedEntity);
        }
        return savedEntities;
    }


    public CrudService<ServiceE, Id, Repo> getService(){
        return getCrudController().getCrudService();
    }

    public Service getCastedService(){
        return getCrudController().getCastedCrudService();
    }
}