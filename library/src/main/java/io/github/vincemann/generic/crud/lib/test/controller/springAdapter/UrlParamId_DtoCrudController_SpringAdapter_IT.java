package io.github.vincemann.generic.crud.lib.test.controller.springAdapter;

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
import io.github.vincemann.generic.crud.lib.test.deepEqualChecker.EqualChecker;
import io.github.vincemann.generic.crud.lib.test.testExecutionListeners.ResetDatabaseTestExecutionListener;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.test.context.TestExecutionListeners;

import java.io.Serializable;
import java.util.*;

/**
 * Integration Test for a {@link DtoCrudController_SpringAdapter} with {@link UrlParamIdFetchingStrategy}, that tests typical Crud operations
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
public abstract class UrlParamId_DtoCrudController_SpringAdapter_IT
                <
                        ServiceE extends IdentifiableEntity<Id>,
                        Dto extends IdentifiableEntity<Id>,
                        Repo extends CrudRepository<ServiceE, Id>,
                        Service extends CrudService<ServiceE, Id,Repo>,
                        Controller extends DtoCrudController_SpringAdapter<ServiceE, Dto, Id,Repo, Service>,
                        Id extends Serializable
                >
        extends IntegrationTest /*implements TransactionManagedTest*/ {



            private List<Plugin<? super Dto,? super ServiceE, ? super Id>> plugins = new ArrayList<>();
            private TestRequestEntity_Factory requestEntityFactory;
    @Getter private Controller crudController;
    @Getter private Class<Dto> dtoEntityClass;
    @Getter private Class<ServiceE> serviceEntityClass;
    @Getter private String entityIdParamKey;

            /**
             * Put all your Code in here that is saving test Entities need for a test to run and call this {@link #runPreTestRunnables()}
             * in your TestMethod, so the init code is in the same Transaction as the test code (useful for lazy loading)
             * @return
             */
     @Getter private List<Runnable> preTestRunnables = new ArrayList<>();
    //@Getter private PlatformTransactionManager transactionManager;



    /*public UrlParamIdDtoCrudControllerSpringAdapterIT(String url, Controller crudController, TestRequestEntityFactory requestEntityFactory, Plugin<? super Dto,? super ServiceE, ? super Id>... plugins) {
        super(url);
        constructorInit(crudController, requestEntityFactory, plugins);
    }*/

    public UrlParamId_DtoCrudController_SpringAdapter_IT(Controller crudController,
                                                         TestRequestEntity_Factory requestEntityFactory,
                                                         Plugin<? super Dto,? super ServiceE, ? super Id>... plugins)
    {
        this.requestEntityFactory = requestEntityFactory;
        constructorInit(crudController, requestEntityFactory, plugins);
    }

    private void constructorInit(Controller crudController,
                                 TestRequestEntity_Factory requestEntityFactory,
                                 Plugin<? super Dto,? super ServiceE, ? super Id>... plugins) {
        Assertions.assertTrue(crudController.getIdIdFetchingStrategy() instanceof UrlParamIdFetchingStrategy, "Controller must have an UrlParamIdFetchingStrategy");
        requestEntityFactory.setTest(this);
        this.crudController = crudController;
        this.requestEntityFactory = requestEntityFactory;
        this.dtoEntityClass = crudController.getDtoClass();
        this.serviceEntityClass = crudController.getServiceEntityClass();
        this.entityIdParamKey = ((UrlParamIdFetchingStrategy) crudController.getIdIdFetchingStrategy()).getIdUrlParamKey();
        initPlugins(plugins);
    }
    private void initPlugins(Plugin<? super Dto,? super ServiceE, ? super Id>... plugins) {
        this.plugins.addAll(Arrays.asList(plugins));
        this.plugins.forEach(plugin -> plugin.setIntegrationTest(this));
    }

    protected void addPreTestRunnable(Runnable runnable){
        getPreTestRunnables().add(runnable);
    }

    protected void runPreTestRunnables(){
        getPreTestRunnables().forEach(Runnable::run);
    }

    protected ResponseEntity<String> findAllEntities_ShouldFail() throws Exception {
        return findAllEntities_ShouldFail(null);
    }


    protected ResponseEntity<String> findAllEntities_ShouldFail(TestRequestEntity_Modification requestEntity_Modification) throws Exception {
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(
                CrudController_TestCase.FAILED_FIND_ALL,
                requestEntity_Modification,
                null);
        onBeforeFindAllEntitiesShouldFail();
        ResponseEntity<String> responseEntity = findAllEntities(testRequestEntity);
        onAfterFindAllEntitiesShouldFail(responseEntity);
        return responseEntity;
    }

    protected Set<Dto> findAllEntities_ShouldSucceed(Set<ServiceE> entitiesExpectedToBeFound,TestRequestEntity_Modification testRequestEntityModification) throws Exception {
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(
                CrudController_TestCase.SUCCESSFUL_FIND_ALL,
                testRequestEntityModification,
                null);
        onBeforeFindAllEntitiesShouldSucceed();
        ResponseEntity<String> responseEntity = findAllEntities(testRequestEntity);

        @SuppressWarnings("unchecked")
        Set<Dto> httpResponseDtos = crudController.getMediaTypeStrategy().readDtosFromBody(responseEntity.getBody(), getDtoEntityClass(), Set.class);
        onAfterFindAllEntitiesShouldSucceed(httpResponseDtos);


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


    protected Dto updateEntity_ShouldSucceed(Dto updateRequestDto, @Nullable TestRequestEntity_Modification testRequestEntityModification,@Nullable EqualChecker<ServiceE> updatedValuesEqualChecker) throws Exception {
        Assertions.assertNotNull(updateRequestDto.getId());
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(
                CrudController_TestCase.SUCCESSFUL_UPDATE,
                testRequestEntityModification,
                updateRequestDto.getId());
        //Entity to update must be saved already
        Optional<ServiceE> serviceEntityToUpdate = crudController.getCrudService().findById(updateRequestDto.getId());
        Assertions.assertTrue(serviceEntityToUpdate.isPresent(), "Entity to update was not present");
        //update request
        onBeforeUpdateEntityShouldSucceed(serviceEntityToUpdate.get(), updateRequestDto);
        ResponseEntity<String> responseEntity = updateEntity(updateRequestDto, testRequestEntity);
        Assertions.assertEquals(testRequestEntity.getExpectedHttpStatus(), responseEntity.getStatusCode());
        //validate response Dto
        Dto httpResponseDto = crudController.getMediaTypeStrategy().readDtoFromBody(responseEntity.getBody(), dtoEntityClass);
        Assertions.assertNotNull(httpResponseDto);
        if(updatedValuesEqualChecker!=null){
            //check that Changes were actually applied -> relevant attribute values specified by EqualChecker Impl are equal now
            ServiceE serviceUpdateRequestEntity = mapDtoToServiceEntity(updateRequestDto);
            Optional<ServiceE> updatedServiceEntity = getService().findById(httpResponseDto.getId());
            Assertions.assertTrue(updatedValuesEqualChecker.isEqual(serviceUpdateRequestEntity,updatedServiceEntity.get()));
        }
        onAfterUpdateEntityShouldSucceed(serviceEntityToUpdate.get(), updateRequestDto, httpResponseDto);
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

    protected Dto updateEntity_ShouldSucceed(Dto updateRequestDto, TestRequestEntity_Modification testRequestEntity_modification) throws Exception {
        return updateEntity_ShouldSucceed(updateRequestDto,testRequestEntity_modification,null);
    }

    protected Dto updateEntity_ShouldSucceed(Dto updateRequestDto, EqualChecker<ServiceE> equalChecker) throws Exception {
        return updateEntity_ShouldSucceed(updateRequestDto,null,equalChecker);
    }

    protected ResponseEntity<String> updateEntity_ShouldFail(Dto updateRequestDto, TestRequestEntity_Modification testRequestEntityModification,@Nullable EqualChecker<ServiceE> updatedValuesEqualChecker) throws Exception {
        Assertions.assertNotNull(updateRequestDto.getId());
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(
                CrudController_TestCase.FAILED_UPDATE,
                testRequestEntityModification,
                updateRequestDto.getId());
        //Entity muss vorher auch schon da sein
        Optional<ServiceE> serviceEntityToUpdate = getService().findById(updateRequestDto.getId());
        Assertions.assertTrue(serviceEntityToUpdate.isPresent(), "Entity to update was not present");

        onBeforeUpdateEntityShouldFail(serviceEntityToUpdate.get(),updateRequestDto);

        ResponseEntity<String> responseEntity = updateEntity(updateRequestDto, testRequestEntity);
        Assertions.assertEquals(responseEntity.getStatusCode(), testRequestEntity.getExpectedHttpStatus(), "Status was : " + responseEntity.getStatusCode());

        if(updatedValuesEqualChecker!=null){
            //check that Changes were not applied -> relevant attribute values specified by EqualChecker Impl are still the same as before update request
            Optional<ServiceE> serviceEntityAfterUpdate = getService().findById(updateRequestDto.getId());
            Assertions.assertTrue(updatedValuesEqualChecker.isEqual(serviceEntityToUpdate.get(),serviceEntityAfterUpdate.get()));
        }

        onAfterUpdateEntityShouldFail(updateRequestDto, responseEntity);
        return responseEntity;
    }

    protected ResponseEntity<String> updateEntity_ShouldFail(Dto newEntity) throws Exception {
        return updateEntity_ShouldFail(newEntity,null,null);
    }

    protected ResponseEntity<String> updateEntity_ShouldFail(Dto newEntity,TestRequestEntity_Modification testRequestEntity_modification) throws Exception {
        return updateEntity_ShouldFail(newEntity,testRequestEntity_modification,null);
    }

    protected ResponseEntity<String> updateEntity_ShouldFail(Dto newEntity, EqualChecker<ServiceE> equalChecker) throws Exception {
        return updateEntity_ShouldFail(newEntity,null,equalChecker);
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

    protected Dto createEntity_ShouldSucceed(Dto dto, TestRequestEntity_Modification modification) throws Exception {
        Assertions.assertNull(dto.getId());
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(CrudController_TestCase.SUCCESSFUL_CREATE, modification, null);
        onBeforeCreateEntityShouldSucceed(dto);
        ResponseEntity<String> responseEntity = createEntity(dto, testRequestEntity);
        Assertions.assertEquals(responseEntity.getStatusCode(), testRequestEntity.getExpectedHttpStatus(), "Status was : " + responseEntity.getStatusCode() + " response Body: " + responseEntity.getBody());
        Dto httpResponseEntity = crudController.getMediaTypeStrategy().readDtoFromBody(responseEntity.getBody(), dtoEntityClass);
        onAfterCreateEntityShouldSucceed(dto, httpResponseEntity);
        return httpResponseEntity;
    }

    protected ResponseEntity<String> createEntity_ShouldFail(Dto dto) throws Exception {
        return createEntity_ShouldFail(dto,null);
    }

    protected ResponseEntity<String> createEntity_ShouldFail(Dto dto, TestRequestEntity_Modification modification) throws Exception {
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(
                CrudController_TestCase.FAILED_CREATE,
                modification,
                null);
        onBeforeCreateEntityShouldFail(dto);
        ResponseEntity<String> responseEntity = createEntity(dto, testRequestEntity);
        Assertions.assertEquals(responseEntity.getStatusCode(), testRequestEntity.getExpectedHttpStatus(), "Status was : " + responseEntity.getStatusCode() + " response Body: " + responseEntity.getBody());
        onAfterCreateEntityShouldFail(dto, responseEntity);
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

    protected ResponseEntity<String> deleteEntity_ShouldSucceed(Id id, TestRequestEntity_Modification modification) throws Exception {
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(
                CrudController_TestCase.SUCCESSFUL_DELETE,
                modification,
                id);
        //Entity muss vorher auch schon da sein
        Optional<ServiceE> serviceFoundEntityBeforeDelete = crudController.getCrudService().findById(id);
        Assertions.assertTrue(serviceFoundEntityBeforeDelete.isPresent(), "Entity to delete was not present");

        onBeforeDeleteEntityShouldSucceed(id);
        ResponseEntity<String> responseEntity = deleteEntity(id, testRequestEntity);
        Assertions.assertEquals(responseEntity.getStatusCode(), testRequestEntity.getExpectedHttpStatus(), "Status was : " + responseEntity.getStatusCode() + " response Body: " + responseEntity.getBody());
        onAfterDeleteEntityShouldSucceed(id, responseEntity);
        return responseEntity;
    }

    protected ResponseEntity<String> deleteEntity_ShouldFail(Id id) throws Exception {
        return deleteEntity_ShouldFail(id,null);
    }

    protected ResponseEntity<String> deleteEntity_ShouldFail(Id id, TestRequestEntity_Modification testRequestEntityModification) throws Exception {
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(
                CrudController_TestCase.FAILED_DELETE,
                testRequestEntityModification,
                id);
        //Entity muss vorher auch schon da sein
        Optional<ServiceE> serviceFoundEntityBeforeDelete = crudController.getCrudService().findById(id);
        Assertions.assertTrue(serviceFoundEntityBeforeDelete.isPresent(), "Entity to delete was not present");

        onBeforeDeleteEntityShouldFail(id);
        ResponseEntity<String> responseEntity = deleteEntity(id, testRequestEntity);
        Assertions.assertEquals(responseEntity.getStatusCode(), testRequestEntity.getExpectedHttpStatus(), "Status was : " + responseEntity.getStatusCode() + " response Body: " + responseEntity.getBody());

        onAfterDeleteEntityShouldFail(id, responseEntity);
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
    protected Dto findEntity_ShouldSucceed(Id id, TestRequestEntity_Modification modification) throws Exception {
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(
                CrudController_TestCase.SUCCESSFUL_FIND,
                modification,
                id);
        onBeforeFindEntityShouldSucceed(id);
        ResponseEntity<String> responseEntity = findEntity(id, testRequestEntity);
        Assertions.assertEquals(responseEntity.getStatusCode(), testRequestEntity.getExpectedHttpStatus(), "Status was : " + responseEntity.getStatusCode() + " response Body: " + responseEntity.getBody());
        Dto responseDto = crudController.getMediaTypeStrategy().readDtoFromBody(responseEntity.getBody(), dtoEntityClass);
        Assertions.assertNotNull(responseDto);
        onAfterFindEntityShouldSucceed(id, responseDto);
        return responseDto;
    }

    protected Dto findEntity_ShouldSucceed(Id id) throws Exception {
        return findEntity_ShouldSucceed(id,null);
    }

    protected ResponseEntity<String> findEntity_ShouldFail(Id id, TestRequestEntity_Modification modification) throws Exception {
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(
                CrudController_TestCase.FAILED_FIND,
                modification,
                id);
        onBeforeFindEntityShouldFail(id);
        ResponseEntity<String> responseEntity = findEntity(id, testRequestEntity);
        Assertions.assertEquals(responseEntity.getStatusCode(), testRequestEntity.getExpectedHttpStatus(), "Status was : " + responseEntity.getStatusCode() + " response Body: " + responseEntity.getBody());
        onAfterFindEntityShouldFail(id, responseEntity);
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


    //PLUGIN CALLBACKS##################################################################################################
    //UPDATE
    protected void onBeforeUpdateEntityShouldSucceed(ServiceE oldEntity, Dto newEntity) throws Exception {
        for (Plugin<? super Dto,? super ServiceE, ? super Id> plugin : plugins) {
            plugin.onBeforeUpdateEntityShouldSucceed(oldEntity, newEntity);
        }
    }
    protected void onAfterUpdateEntityShouldSucceed(ServiceE oldEntity, Dto newEntity, Dto responseDto) throws Exception {
        for (Plugin<? super Dto,? super ServiceE, ? super Id> plugin : plugins) {
            plugin.onAfterUpdateEntityShouldSucceed(oldEntity, newEntity, responseDto);
        }
    }
    protected void onBeforeUpdateEntityShouldFail(ServiceE oldEntity, Dto newEntity) throws Exception {
        for (Plugin<? super Dto,? super ServiceE, ? super Id> plugin : plugins) {
            plugin.onBeforeUpdateEntityShouldFail(oldEntity,newEntity);
        }
    }
    protected void onAfterUpdateEntityShouldFail(Dto newEntity, ResponseEntity<String> responseEntity) throws Exception {
        for (Plugin<? super Dto,? super ServiceE, ? super Id> plugin : plugins) {
            plugin.onAfterUpdateEntityShouldFail(newEntity, responseEntity);
        }
    }
    //CREATE
    protected void onBeforeCreateEntityShouldSucceed(Dto dtoToCreate) throws Exception {
        for (Plugin<? super Dto,? super ServiceE, ? super Id> plugin : plugins) {
            plugin.onBeforeCreateEntityShouldSucceed(dtoToCreate);
        }
    }
    protected void onAfterCreateEntityShouldSucceed(Dto dtoToCreate, Dto responseDto) throws Exception {
        for (Plugin<? super Dto,? super ServiceE, ? super Id> plugin : plugins) {
            plugin.onAfterCreateEntityShouldSucceed(dtoToCreate, responseDto);
        }
    }
    protected void onBeforeCreateEntityShouldFail(Dto dtoToCreate) throws Exception {
        for (Plugin<? super Dto,? super ServiceE, ? super Id> plugin : plugins) {
            plugin.onBeforeCreateEntityShouldFail(dtoToCreate);
        }
    }
    protected void onAfterCreateEntityShouldFail(Dto dtoToCreate, ResponseEntity<String> responseEntity) throws Exception {
        for (Plugin<? super Dto,? super ServiceE, ? super Id> plugin : plugins) {
            plugin.onAfterCreateEntityShouldFail(dtoToCreate, responseEntity);
        }
    }
    //DELETE
    protected void onBeforeDeleteEntityShouldSucceed(Id id) throws Exception {
        for (Plugin<? super Dto,? super ServiceE, ? super Id> plugin : plugins) {
            plugin.onBeforeDeleteEntityShouldSucceed(id);
        }
    }
    protected void onAfterDeleteEntityShouldSucceed(Id id, ResponseEntity<String> responseEntity) throws Exception {
        for (Plugin<? super Dto,? super ServiceE, ? super Id> plugin : plugins) {
            plugin.onAfterDeleteEntityShouldSucceed(id, responseEntity);
        }
    }
    protected void onBeforeDeleteEntityShouldFail(Id id) throws Exception {
        for (Plugin<? super Dto,? super ServiceE, ? super Id> plugin : plugins) {
            plugin.onBeforeDeleteEntityShouldFail(id);
        }
    }
    protected void onAfterDeleteEntityShouldFail(Id id, ResponseEntity<String> responseEntity) throws Exception {
        for (Plugin<? super Dto,? super ServiceE, ? super Id> plugin : plugins) {
            plugin.onAfterDeleteEntityShouldFail(id, responseEntity);
        }
    }
    //FIND
    protected void onBeforeFindEntityShouldSucceed(Id id) throws Exception {
        for (Plugin<? super Dto,? super ServiceE, ? super Id> plugin : plugins) {
            plugin.onBeforeFindEntityShouldSucceed(id);
        }
    }
    protected void onAfterFindEntityShouldSucceed(Id id, Dto responseDto) throws Exception {
        for (Plugin<? super Dto,? super ServiceE, ? super Id> plugin : plugins) {
            plugin.onAfterFindEntityShouldSucceed(id, responseDto);
        }
    }
    protected void onBeforeFindEntityShouldFail(Id id) throws Exception {
        for (Plugin<? super Dto,? super ServiceE, ? super Id> plugin : plugins) {
            plugin.onBeforeFindEntityShouldFail(id);
        }
    }
    protected void onAfterFindEntityShouldFail(Id id, ResponseEntity<String> responseEntity) throws Exception {
        for (Plugin<? super Dto,? super ServiceE, ? super Id> plugin : plugins) {
            plugin.onAfterFindEntityShouldFail(id, responseEntity);
        }
    }
    //FIND ALL
    protected void onBeforeFindAllEntitiesShouldSucceed() throws Exception {
        for (Plugin<? super Dto,? super ServiceE, ? super Id> plugin : plugins) {
            plugin.onBeforeFindAllEntitiesShouldSucceed();
        }
    }
    protected void onAfterFindAllEntitiesShouldSucceed(Set<Dto> dtos) throws Exception {
        for (Plugin<? super Dto,? super ServiceE, ? super Id> plugin : plugins) {
            plugin.onAfterFindAllEntitiesShouldSucceed(dtos);
        }
    }
    protected void onBeforeFindAllEntitiesShouldFail() throws Exception {
        for (Plugin<? super Dto,? super ServiceE, ? super Id> plugin : plugins) {
            plugin.onBeforeFindAllEntitiesShouldFail();
        }
    }
    protected void onAfterFindAllEntitiesShouldFail(ResponseEntity<String> responseEntity) throws Exception {
        for (Plugin<? super Dto,? super ServiceE, ? super Id> plugin : plugins) {
            plugin.onAfterFindAllEntitiesShouldFail(responseEntity);
        }
    }
    //##################################################################################################################


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

    public Service getService(){
        return getCrudController().getCrudService();
    }


    /**
     * Plugin for helper methods in this class.
     * @param <dto>
     * @param <id>
     */
    @Getter
    @Setter
    public static class Plugin<dto extends IdentifiableEntity<id>,serviceE extends IdentifiableEntity<id>, id extends Serializable> {
        private UrlParamId_DtoCrudController_SpringAdapter_IT integrationTest;

        public void onAfterUpdateEntityShouldFail(dto newEntity, ResponseEntity<String> responseEntity) throws Exception { }
        public void onBeforeUpdateEntityShouldFail(serviceE oldEntity, dto newEntity) throws Exception { }
        public void onAfterUpdateEntityShouldSucceed(serviceE oldEntity, dto newEntity, dto responseDto) throws Exception { }
        public void onBeforeUpdateEntityShouldSucceed(serviceE oldEntity, dto newEntity) throws Exception { }
        public void onAfterCreateEntityShouldSucceed(dto dtoToCreate, dto responseDto) throws Exception { }
        public void onBeforeCreateEntityShouldSucceed(dto dtoToCreate) throws Exception { }
        public void onAfterCreateEntityShouldFail(dto dtoToCreate, ResponseEntity<String> responseEntity) throws Exception { }
        public void onBeforeCreateEntityShouldFail(dto dtoToCreate) throws Exception { }
        public void onAfterDeleteEntityShouldSucceed(id id, ResponseEntity<String> responseEntity) throws Exception { }
        public void onBeforeDeleteEntityShouldSucceed(id id) throws Exception { }
        public void onAfterDeleteEntityShouldFail(id id, ResponseEntity<String> responseEntity) throws Exception { }
        public void onBeforeDeleteEntityShouldFail(id id) throws Exception { }
        public void onAfterFindEntityShouldSucceed(id id, dto responseDto) throws Exception { }
        public void onBeforeFindEntityShouldSucceed(id id) throws Exception { }
        public void onAfterFindEntityShouldFail(id id, ResponseEntity<String> responseEntity) throws Exception { }
        public void onBeforeFindEntityShouldFail(id id) throws Exception { }
        public void onBeforeFindAllEntitiesShouldSucceed() throws Exception { }
        public void onBeforeFindAllEntitiesShouldFail() throws Exception { }
        public void onAfterFindAllEntitiesShouldFail(ResponseEntity<String> responseEntity) throws Exception { }
        public void onAfterFindAllEntitiesShouldSucceed(Set<? extends dto> dtos) throws Exception { }
    }
}