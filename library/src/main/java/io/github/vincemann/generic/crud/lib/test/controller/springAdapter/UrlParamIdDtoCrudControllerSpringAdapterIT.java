package io.github.vincemann.generic.crud.lib.test.controller.springAdapter;

import io.github.vincemann.generic.crud.lib.controller.springAdapter.DtoCrudControllerSpringAdapter;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.idFetchingStrategy.UrlParamIdFetchingStrategy;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.test.IntegrationTest;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.findAllEntitesTestProvider.FindAllTestEntitiesProvider;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.successfulTestBundles.SucceedingTestEntityBundle;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntityModification;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.TestRequestEntityFactory;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.RequestEntityMapper;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.FindAllTestBundle;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.successfulTestBundles.UpdatableSucceedingTestEntityBundle;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.UpdateTestEntityBundle;
import io.github.vincemann.generic.crud.lib.util.BeanUtils;
import io.github.vincemann.generic.crud.lib.util.TestLogUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.util.*;

import static io.github.vincemann.generic.crud.lib.util.BeanUtils.isDeepEqual;

/**
 * Integration Test for a {@link DtoCrudControllerSpringAdapter} with {@link UrlParamIdFetchingStrategy}, that tests typical Crud operations
 *
 * @param <ServiceE>
 * @param <Dto>
 * @param <Service>
 * @param <Controller>
 * @param <Id>
 */
@Slf4j
public abstract class UrlParamIdDtoCrudControllerSpringAdapterIT<ServiceE extends IdentifiableEntity<Id>, Dto extends IdentifiableEntity<Id>, Service extends CrudService<ServiceE, Id>, Controller extends DtoCrudControllerSpringAdapter<ServiceE, Dto, Id, Service>, Id extends Serializable> extends IntegrationTest {

    @Getter
    private Controller crudController;
    @Getter
    private Class<Dto> dtoEntityClass;
    @Getter
    private String entityIdParamKey;
    @Getter
    private List<UpdatableSucceedingTestEntityBundle<Dto>> succeedingUpdatableTestEntityBundles;
    @Getter
    private List<SucceedingTestEntityBundle<Dto>> failingCreateUpdatableSucceedingTestEntityBundles;

    @Getter
    @Setter
    private NonExistingIdProvider<Id> nonExistingIdProvider;
    private List<Plugin<? super Dto,? super Id>> plugins = new ArrayList<>();
    private TestRequestEntityFactory requestEntityFactory;


    /**
     * @param url
     * @param crudController
     * @param nonExistingId  this can be null, if you want to set your own {@link NonExistingIdProvider} with {@link #setNonExistingIdProvider(NonExistingIdProvider)}
     */
    public UrlParamIdDtoCrudControllerSpringAdapterIT(String url, Controller crudController, Id nonExistingId, TestRequestEntityFactory requestEntityFactory, Plugin<? super Dto,? super Id>... plugins) {
        super(url);
        constructorInit(crudController,nonExistingId,requestEntityFactory,plugins);
    }

    public UrlParamIdDtoCrudControllerSpringAdapterIT(Controller crudController, Id nonExistingId, TestRequestEntityFactory requestEntityFactory, Plugin<? super Dto,? super Id>... plugins) {
        super();
        this.requestEntityFactory=requestEntityFactory;
        constructorInit(crudController,nonExistingId,requestEntityFactory,plugins);
    }

    private void constructorInit(Controller crudController, Id nonExistingId, TestRequestEntityFactory requestEntityFactory, Plugin<? super Dto,? super Id>... plugins){
        Assertions.assertTrue(crudController.getIdIdFetchingStrategy() instanceof UrlParamIdFetchingStrategy, "Controller must have an UrlParamIdFetchingStrategy");
        this.crudController = crudController;
        this.requestEntityFactory=requestEntityFactory;
        this.dtoEntityClass = crudController.getDtoClass();
        this.entityIdParamKey = ((UrlParamIdFetchingStrategy) crudController.getIdIdFetchingStrategy()).getIdUrlParamKey();
        this.nonExistingIdProvider = () -> nonExistingId;
        initPlugins(plugins);
    }

    private void initPlugins(Plugin<? super Dto,? super Id>... plugins){
        this.plugins.addAll(Arrays.asList(plugins));
        this.plugins.forEach(plugin -> plugin.setIntegrationTest(this));
    }



    @BeforeEach
    public void before() throws Exception {
        this.succeedingUpdatableTestEntityBundles = provideSucceedingTestBundles();
        this.failingCreateUpdatableSucceedingTestEntityBundles = provideFailingTestBundles();
    }


    protected abstract List<UpdatableSucceedingTestEntityBundle<Dto>> provideSucceedingTestBundles();
    protected abstract List<SucceedingTestEntityBundle<Dto>> provideFailingTestBundles();


    protected abstract List<FindAllTestBundle<ServiceE>> provideSucceedingFindAllTestBundles();
    protected abstract List<FindAllTestBundle<ServiceE>> provideFailingingFindAllTestBundles();


    /**
     * All Entities provided by {@link UrlParamIdDtoCrudControllerSpringAdapterIT#provideSucceedingTestBundles()} will be created.
     * Then a findAll Request will be send to the server.
     * All Entities, provided by {@link FindAllTestBundle#getFindAllTestEntitiesProvider()} should be returned by the server,
     * in order for this test to pass.
     * This will be repeated for all provided {@link FindAllTestBundle}s.
     * @throws Exception
     */
    @Test
    protected void findAllEntities_shouldSucceedTest() throws Exception {
        Assumptions.assumeTrue(getCrudController().getEndpointsExposureDetails().isFindAllEndpointExposed());
        Assumptions.assumeTrue(!succeedingUpdatableTestEntityBundles.isEmpty());



        //save all entities from bundles
        Collection<Dto> savedDtos = new ArrayList<>();
        for (UpdatableSucceedingTestEntityBundle<Dto> bundle : succeedingUpdatableTestEntityBundles) {
            Dto savedEntity = createEntityShouldSucceed(bundle.getEntity(),bundle.getCreateTestRequestEntityModification());
            savedDtos.add(savedEntity);
        }

        //test find all
        for (FindAllTestBundle<ServiceE> provideFindAllTestBundle : provideSucceedingFindAllTestBundles()) {
            findAllEntitiesShouldSucceed(requestEntityFactory.createInstance(CrudControllerTestCase.SUCCESSFUL_FIND_ALL,
                    provideFindAllTestBundle.getRequestEntityModification(),null),
                    provideFindAllTestBundle.getFindAllTestEntitiesProvider());
        }
    }

    protected Collection<Dto> findAllEntitiesShouldSucceed(TestRequestEntity testRequestEntity, FindAllTestEntitiesProvider<ServiceE> findAllTestEntitiesProvider) throws Exception {
        onBeforeFindAllEntitiesShouldSucceed();
        ResponseEntity<String> responseEntity = findAllEntities(testRequestEntity);

        @SuppressWarnings("unchecked")
        Set<Dto> dtos = crudController.getMediaTypeStrategy().readDtosFromBody(responseEntity.getBody(), getDtoEntityClass(),Set.class);
        onAfterFindAllEntitiesShouldSucceed(dtos);

        Set<ServiceE> allServiceEntitiesShouldHaveBeenFound = findAllTestEntitiesProvider.provideEntitiesShouldBeFound();
        Assertions.assertEquals(allServiceEntitiesShouldHaveBeenFound.size(),dtos.size());
        List<Id> idsSeen = new ArrayList<>();
        for (Dto dto : dtos) {
            //prevent duplicates
            Assertions.assertFalse(idsSeen.contains(dto.getId()));
            idsSeen.add(dto.getId());
        }
        return dtos;
    }


    private ResponseEntity<String> findAllEntities(TestRequestEntity testRequestEntity){
        ResponseEntity<String> responseEntity = getRestTemplate().exchange(RequestEntityMapper.map(testRequestEntity,null),String.class);
        Assertions.assertEquals(testRequestEntity.getExpectedHttpStatus(),responseEntity.getStatusCode(),"Status was : " + responseEntity.getStatusCode() + " response Body: " + responseEntity.getBody());
        return responseEntity;
    }









    @Test
    protected void findNonExistentEntityTest() throws Exception {
        Assumptions.assumeTrue(getCrudController().getEndpointsExposureDetails().isFindEndpointExposed());
        Id nonExistingId = nonExistingIdProvider.provideId();
        ResponseEntity<String> responseEntity = findEntityShouldFail(nonExistingId,CrudControllerTestCase.FIND_NON_EXISTENT,null);
        Assertions.assertFalse(isBodyOfDtoType(responseEntity.getBody()));
    }

    @Test
    protected void deleteNonExistentEntityTest() throws Exception {
        Assumptions.assumeTrue(getCrudController().getEndpointsExposureDetails().isDeleteEndpointExposed());
        Id id = nonExistingIdProvider.provideId();
        deleteEntityShouldFail(id,
                requestEntityFactory.createInstance(CrudControllerTestCase.DELETE_NON_EXISTENT,null,id));
    }









    @Test
    protected void updateEntity_shouldSucceedTest() throws Exception {
        Assumptions.assumeTrue(getCrudController().getEndpointsExposureDetails().isUpdateEndpointExposed());
        Assumptions.assumeTrue(!succeedingUpdatableTestEntityBundles.isEmpty());
        for (UpdatableSucceedingTestEntityBundle<Dto> bundle : this.succeedingUpdatableTestEntityBundles) {

            List<UpdateTestEntityBundle<Dto>> updateTestEntityBundles = bundle.getUpdateTestEntityBundles();
            if (updateTestEntityBundles.isEmpty()) {
                log.info("No update tests for testDto : " + bundle.getEntity());
                return;
            }
            for (UpdateTestEntityBundle<Dto> updateBundle : updateTestEntityBundles) {
                Dto modifiedDto = updateBundle.getModifiedEntity();
                TestLogUtils.logTestStart(log, "updateEntity", new AbstractMap.SimpleEntry<>("testDto", bundle.getEntity()), new AbstractMap.SimpleEntry<>("modifiedDto", modifiedDto));

                //save old dto
                Assertions.assertNull(bundle.getEntity().getId());
                Dto savedDtoEntity = createEntityShouldSucceed(bundle.getEntity(),bundle.getCreateTestRequestEntityModification());
                modifiedDto.setId(savedDtoEntity.getId());
                //update dto
                Dto dbUpdatedDto = updateEntityShouldSucceed(savedDtoEntity, modifiedDto,updateBundle.getTestRequestEntityModification());
                updateBundle.getPostUpdateCallback().callback(dbUpdatedDto);
                //remove dto -> clean for next iteration
                deleteEntityShouldSucceed(savedDtoEntity.getId(),bundle.getDeleteTestRequestEntityModification());

                TestLogUtils.logTestSucceeded(log, "updateEntity", new AbstractMap.SimpleEntry<>("testDto", bundle.getEntity()), new AbstractMap.SimpleEntry<>("modifiedDto", modifiedDto));
            }
        }
    }
    /**
     * @param oldEntityDto entityDto already saved that should be updated
     * @param newEntityDto entityDto that should replace/update old entity
     * @return updated entityDto returned by backend
     * @throws Exception
     */
    protected Dto updateEntityShouldSucceed(Dto oldEntityDto, Dto newEntityDto, TestRequestEntityModification bundleMod) throws Exception {
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(CrudControllerTestCase.SUCCESSFUL_UPDATE, bundleMod, oldEntityDto.getId());
        Assertions.assertNotNull(oldEntityDto.getId());
        Assertions.assertNotNull(newEntityDto.getId());
        Assertions.assertEquals(oldEntityDto.getId(), newEntityDto.getId());
        //Entity muss vorher auch schon da sein
        Optional<ServiceE> serviceFoundEntityBeforeUpdate = crudController.getCrudService().findById(newEntityDto.getId());
        Assertions.assertTrue(serviceFoundEntityBeforeUpdate.isPresent(), "Entity to delete was not present");
        return _updateEntityShouldSucceed(oldEntityDto, newEntityDto, testRequestEntity);
    }


    private Dto _updateEntityShouldSucceed(Dto oldEntityDto, Dto newEntityDto, TestRequestEntity testRequestEntity) throws Exception {
        //trotzdem müssen changes vorliegen
        Assertions.assertFalse(isDeepEqual(oldEntityDto, newEntityDto));
        onBeforeUpdateEntityShouldSucceed(oldEntityDto,newEntityDto);
        ResponseEntity<String> responseEntity = updateEntity(newEntityDto,testRequestEntity);
        Assertions.assertEquals(testRequestEntity.getExpectedHttpStatus(), responseEntity.getStatusCode());
        Dto httpResponseDto = crudController.getMediaTypeStrategy().readDtoFromBody(responseEntity.getBody(), dtoEntityClass);
        Assertions.assertNotNull(httpResponseDto);

        onAfterUpdateEntityShouldSucceed(oldEntityDto,newEntityDto,httpResponseDto);
        //response http entity must match modTestEntity
        validateDtosAreDeepEqual(httpResponseDto, newEntityDto);
        //entity fetched from service at start of test (before update) must not match httpResponseEntity (since it got updated)
        boolean deepEqual = isDeepEqual(oldEntityDto, httpResponseDto);
        Assertions.assertFalse(deepEqual, "Entites did match but must not -> entity was not updated");
        return httpResponseDto;
    }

    /**
     * The entity found by id of {@param newEntityDto}, is updated with newEntityDto
     *
     * @param newEntityDto
     * @return
     * @throws Exception
     */
    protected Dto updateEntityShouldSucceed(Dto newEntityDto, TestRequestEntity testRequestEntity) throws Exception {
        Assertions.assertNotNull(newEntityDto.getId());
        //Entity must be saved already
        Optional<ServiceE> serviceFoundEntityBeforeUpdate = crudController.getCrudService().findById(newEntityDto.getId());
        Assertions.assertTrue(serviceFoundEntityBeforeUpdate.isPresent(), "Entity to delete was not present");
        //there must be changes
        Dto oldEntityDtoFromService = getCrudController().getDtoMapper().mapServiceEntityToDto(serviceFoundEntityBeforeUpdate.get(), getDtoEntityClass());
        return _updateEntityShouldSucceed(oldEntityDtoFromService, newEntityDto, testRequestEntity);
    }

    protected void updateEntityShouldFail(Dto oldEntity, Dto newEntity, TestRequestEntity testRequestEntity) throws Exception {
        Assertions.assertNotNull(oldEntity.getId());
        Assertions.assertNotNull(newEntity.getId());
        Assertions.assertEquals(oldEntity.getId(), newEntity.getId());
        //Entity muss vorher auch schon da sein
        Optional<ServiceE> serviceFoundEntityBeforeUpdate = crudController.getCrudService().findById(newEntity.getId());
        Assertions.assertTrue(serviceFoundEntityBeforeUpdate.isPresent(), "Entity to update was not present");
        //id muss gleich sein
        //trotzdem müssen changes vorliegen
        Assertions.assertFalse(isDeepEqual(oldEntity, newEntity));
        onBeforeUpdateEntityShouldFail(oldEntity,newEntity);

        ResponseEntity<String> responseEntity = updateEntity(newEntity,testRequestEntity);
        Assertions.assertEquals(responseEntity.getStatusCode(),testRequestEntity.getExpectedHttpStatus(), "Status was : " + responseEntity.getStatusCode());

        onAfterUpdateEntityShouldFail(oldEntity,newEntity,responseEntity);
        //entity aus Service muss immernoch die gleiche sein wie vorher
    }

    /**
     * Send update Entity Request to Backend
     *
     * @param newEntity updated entityDto
     * @return backend Response
     */
    private ResponseEntity<String> updateEntity(Dto newEntity, TestRequestEntity testRequestEntity) {
        Assertions.assertNotNull(newEntity.getId());
        //todo yodoc
        /*UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getBaseUrl() + crudController.getUpdateMethodName())
                .queryParam(entityIdParamKey, newEntity.getId());

        TestRequestEntity<Dto> testRequestEntity = new TestRequestEntity<Dto>(newEntity, HttpMethod.PUT, builder.build().toUri());*/
        return getRestTemplate().exchange(RequestEntityMapper.map(testRequestEntity,newEntity), String.class);
    }











    @Test
    protected void createEntity_shouldSucceedTest() throws Exception {
        Assumptions.assumeTrue(getCrudController().getEndpointsExposureDetails().isCreateEndpointExposed());
        Assumptions.assumeTrue(!succeedingUpdatableTestEntityBundles.isEmpty());
        for (UpdatableSucceedingTestEntityBundle<Dto> bundle : this.succeedingUpdatableTestEntityBundles) {

            TestLogUtils.logTestStart(log, "createEntity_shouldSucceed", new AbstractMap.SimpleEntry<>("testDto", bundle.getEntity()));


            Dto savedDto = createEntityShouldSucceed(bundle.getEntity(),bundle.getCreateTestRequestEntityModification());
            bundle.getPostCreateCallback().callback(savedDto);

            TestLogUtils.logTestSucceeded(log, "createEntity_shouldSucceed", new AbstractMap.SimpleEntry<>("testDto", bundle.getEntity()));
        }
    }


    @Test
    protected void createEntity_shouldFailTest() throws Exception {
        Assumptions.assumeTrue(getCrudController().getEndpointsExposureDetails().isCreateEndpointExposed());
        Assumptions.assumeTrue(!failingCreateUpdatableSucceedingTestEntityBundles.isEmpty());

        for (SucceedingTestEntityBundle<Dto> bundle : this.failingCreateUpdatableSucceedingTestEntityBundles) {

            TestLogUtils.logTestStart(log, "createEntity_shouldFail", new AbstractMap.SimpleEntry<>("testDto", bundle.getEntity()));


            ResponseEntity<String> responseEntity = createEntityShouldFail(bundle.getEntity(),CrudControllerTestCase.FAILED_CREATE,bundle.getCreateTestRequestEntityModification());
            bundle.getPostCreateCallback().callback(savedDto);

            TestLogUtils.logTestSucceeded(log, "createEntity_shouldFail", new AbstractMap.SimpleEntry<>("testDto", bundle.getEntity()));
        }
    }




    protected Dto createEntityShouldSucceed(Dto dto, TestRequestEntityModification bundleMod) throws Exception {
        Assertions.assertNull(dto.getId());
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(CrudControllerTestCase.SUCCESSFUL_CREATE, bundleMod, null);
        onBeforeCreateEntityShouldSucceed(dto);
        ResponseEntity<String> responseEntity = createEntity(dto,testRequestEntity);
        Assertions.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        Assertions.assertEquals(responseEntity.getStatusCode(),testRequestEntity.getExpectedHttpStatus(), "Status was : " + responseEntity.getStatusCode() + " response Body: " + responseEntity.getBody());
        Dto httpResponseEntity = crudController.getMediaTypeStrategy().readDtoFromBody(responseEntity.getBody(), dtoEntityClass);
        onAfterCreateEntityShouldSucceed(dto,httpResponseEntity);
        return httpResponseEntity;
    }

    protected ResponseEntity<String> createEntityShouldFail(Dto dto,CrudControllerTestCase crudControllerTestCase, TestRequestEntityModification bundleMod){
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(crudControllerTestCase, bundleMod, null);
        onBeforeCreateEntityShouldFail(dto);
        ResponseEntity<String> responseEntity = createEntity(dto,testRequestEntity);
        Assertions.assertFalse(responseEntity.getStatusCode().is2xxSuccessful());
        Assertions.assertEquals(responseEntity.getStatusCode(),testRequestEntity.getExpectedHttpStatus(), "Status was : " + responseEntity.getStatusCode() + " response Body: " + responseEntity.getBody());
        onAfterCreateEntityShouldFail(dto,responseEntity);
        return responseEntity;
    }


    /**
     * Send create Entity Request to Backend, Response is returned
     *
     * @param dtoEntity the Dto entity that should be stored
     * @return
     */
    private ResponseEntity<String> createEntity(Dto dtoEntity, TestRequestEntity testRequestEntity) {
        return getRestTemplate().exchange(RequestEntityMapper.map(testRequestEntity,dtoEntity), String.class);
    }










    @Test
    protected void deleteEntity_shouldSucceedTest() throws Exception {
        Assumptions.assumeTrue(getCrudController().getEndpointsExposureDetails().isDeleteEndpointExposed());
        Assumptions.assumeTrue(!succeedingUpdatableTestEntityBundles.isEmpty());
        for (UpdatableSucceedingTestEntityBundle<Dto> bundle : this.succeedingUpdatableTestEntityBundles) {
            TestLogUtils.logTestStart(log, "deleteEntity", new AbstractMap.SimpleEntry<>("testDto", bundle.getEntity()));


            Dto savedEntity = createEntityShouldSucceed(bundle.getEntity(),bundle.getCreateTestRequestEntityModification());
            deleteEntityShouldSucceed(savedEntity.getId(),bundle.getDeleteTestRequestEntityModification());
            bundle.getPostDeleteCallback().callback(savedEntity);

            TestLogUtils.logTestSucceeded(log, "deleteEntity", new AbstractMap.SimpleEntry<>("testDto", bundle.getEntity()));
        }
    }

    protected ResponseEntity deleteEntityShouldSucceed(Id id,TestRequestEntityModification bunldeMod) throws Exception {
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(CrudControllerTestCase.SUCCESSFUL_DELETE, bunldeMod, id);
        //Entity muss vorher auch schon da sein
        Optional<ServiceE> serviceFoundEntityBeforeDelete = crudController.getCrudService().findById(id);
        Assertions.assertTrue(serviceFoundEntityBeforeDelete.isPresent(), "Entity to delete was not present");

        onBeforeDeleteEntityShouldSucceed(id);
        ResponseEntity<String> responseEntity = deleteEntity(id,testRequestEntity);
        Assertions.assertEquals(responseEntity.getStatusCode(),testRequestEntity.getExpectedHttpStatus(), "Status was : " + responseEntity.getStatusCode() + " response Body: " + responseEntity.getBody());
        onAfterDeleteEntityShouldSucceed(id,responseEntity);
        return responseEntity;
    }

    protected ResponseEntity deleteEntityShouldFail(Id id, TestRequestEntity testRequestEntity) throws Exception {
        //Entity muss vorher auch schon da sein
        Optional<ServiceE> serviceFoundEntityBeforeDelete = crudController.getCrudService().findById(id);
        Assertions.assertTrue(serviceFoundEntityBeforeDelete.isPresent(), "Entity to delete was not present");

        onBeforeDeleteEntityShouldFail(id);
        ResponseEntity<String> responseEntity = deleteEntity(id,testRequestEntity);
        Assertions.assertEquals(responseEntity.getStatusCode(),testRequestEntity.getExpectedHttpStatus(), "Status was : " + responseEntity.getStatusCode() + " response Body: " + responseEntity.getBody());

        onAfterDeleteEntityShouldFail(id,responseEntity);
        return responseEntity;
    }

    private ResponseEntity<String> deleteEntity(Id id, TestRequestEntity testRequestEntity) {
        /*UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getBaseUrl() + crudController.getDeleteMethodName())
                .queryParam(entityIdParamKey, id);
        TestRequestEntity testRequestEntity = new TestRequestEntity(HttpMethod.DELETE, builder.build().toUri());*/
        return getRestTemplate().exchange(RequestEntityMapper.map(testRequestEntity,id), String.class);
    }








    @Test
    protected void findEntity_shouldSucceedTest() throws Exception {
        Assumptions.assumeTrue(getCrudController().getEndpointsExposureDetails().isFindEndpointExposed());
        Assumptions.assumeTrue(!succeedingUpdatableTestEntityBundles.isEmpty());
        for (UpdatableSucceedingTestEntityBundle<Dto> bundle : this.succeedingUpdatableTestEntityBundles) {
            TestLogUtils.logTestStart(log, "findEntity", new AbstractMap.SimpleEntry<>("testDto", bundle.getEntity()));

            Dto savedEntity = createEntityShouldSucceed(bundle.getEntity(),bundle.getCreateTestRequestEntityModification());
            Dto responseDto = findEntityShouldSucceed(savedEntity.getId(),bundle.getFindTestRequestEntityModification());
            validateDtosAreDeepEqual(responseDto, savedEntity);
            bundle.getPostFindCallback().callback(responseDto);

            TestLogUtils.logTestSucceeded(log, "findEntity", new AbstractMap.SimpleEntry<>("testDto", bundle.getEntity()));
        }
    }

    /**
     *
     * @param id            id of the entity, that should be found
     * @return              the dto of the requested entity found on backend with given id
     * @throws Exception
     */
    protected Dto findEntityShouldSucceed(Id id, TestRequestEntityModification bundleMod) throws Exception {
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(CrudControllerTestCase.SUCCESSFUL_FIND, bundleMod, id);
        onBeforeFindEntityShouldSucceed(id);
        ResponseEntity<String> responseEntity = findEntity(id,testRequestEntity);
        Assertions.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        Assertions.assertEquals(responseEntity.getStatusCode(),testRequestEntity.getExpectedHttpStatus(), "Status was : " + responseEntity.getStatusCode() + " response Body: " + responseEntity.getBody());
        Dto responseDto = crudController.getMediaTypeStrategy().readDtoFromBody(responseEntity.getBody(), dtoEntityClass);
        Assertions.assertNotNull(responseDto);
        onAfterFindEntityShouldSucceed(id,responseDto);
        return responseDto;
    }

    protected ResponseEntity<String> findEntityShouldFail(Id id, CrudControllerTestCase crudControllerTestCase, TestRequestEntityModification bundleMod) throws Exception {
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(crudControllerTestCase, bundleMod, id);
        onBeforeFindEntityShouldFail(id);
        ResponseEntity<String> responseEntity = findEntity(id,testRequestEntity);
        Assertions.assertFalse(responseEntity.getStatusCode().is2xxSuccessful());
        Assertions.assertEquals(responseEntity.getStatusCode(),testRequestEntity.getExpectedHttpStatus(), "Status was : " + responseEntity.getStatusCode() + " response Body: " + responseEntity.getBody());
        onAfterFindEntityShouldFail(id,responseEntity);
        return responseEntity;
    }

    /*protected ResponseEntity findEntityShouldFail(Id id) {
        ResponseEntity responseEntity = findEntity(id);
        Assertions.assertFalse(responseEntity.getStatusCode().is2xxSuccessful(), "Status was : " + responseEntity.getStatusCode());
        return responseEntity;
    }*/

    /**
     * send find Entity Request to backend
     *
     * @param id
     * @return backend Response
     */
    private ResponseEntity<String> findEntity(Id id, TestRequestEntity testRequestEntity) {
        Assertions.assertNotNull(id);
        return getRestTemplate().exchange(RequestEntityMapper.map(testRequestEntity,id), String.class);
    }








    //updateTests callbacks
    protected void onAfterUpdateEntityShouldFail(Dto oldEntity, Dto newEntity, ResponseEntity<String> responseEntity) throws Exception {
        for (Plugin< ? super Dto, ? super Id> plugin : plugins) {
            plugin.onAfterUpdateEntityShouldFail(oldEntity, newEntity, responseEntity);
        }
    }
    protected void onBeforeUpdateEntityShouldFail(Dto oldEntity, Dto newEntity) throws Exception {
        for (Plugin< ? super Dto, ? super Id> plugin : plugins) {
            plugin.onBeforeUpdateEntityShouldFail(oldEntity, newEntity);
        }
    }
    protected void onAfterUpdateEntityShouldSucceed(Dto oldEntity, Dto newEntity, Dto responseDto) throws Exception {
        for (Plugin< ? super Dto, ? super Id> plugin : plugins) {
            plugin.onAfterUpdateEntityShouldSucceed(oldEntity, newEntity, responseDto);
        }
    }
    protected void onBeforeUpdateEntityShouldSucceed(Dto oldEntity, Dto newEntity) throws Exception {
        for (Plugin< ? super Dto, ? super Id> plugin : plugins) {
            plugin.onBeforeUpdateEntityShouldSucceed(oldEntity, newEntity);
        }
    }

    protected void onAfterCreateEntityShouldSucceed(Dto dtoToCreate, Dto responseDto) throws Exception {
        for (Plugin< ? super Dto, ? super Id> plugin : plugins) {
            plugin.onAfterCreateEntityShouldSucceed(dtoToCreate, responseDto);
        }
    }
    protected void onBeforeCreateEntityShouldSucceed(Dto dtoToCreate) throws Exception {
        for (Plugin< ? super Dto, ? super Id> plugin : plugins) {
            plugin.onBeforeCreateEntityShouldSucceed(dtoToCreate);
        }
    }

    protected void onAfterCreateEntityShouldFail(Dto dtoToCreate,ResponseEntity<String> responseEntity){}
    protected void onBeforeCreateEntityShouldFail(Dto dtoToCreate){}

    protected void onAfterDeleteEntityShouldSucceed(Id id, ResponseEntity<String> responseEntity) throws Exception {
        for (Plugin< ? super Dto, ? super Id> plugin : plugins) {
            plugin.onAfterDeleteEntityShouldSucceed(id, responseEntity);
        }
    }
    protected void onBeforeDeleteEntityShouldSucceed(Id id) throws Exception {
        for (Plugin< ? super Dto, ? super Id> plugin : plugins) {
            plugin.onBeforeDeleteEntityShouldSucceed(id);
        }
    }
    protected void onAfterDeleteEntityShouldFail(Id id,ResponseEntity<String> responseEntity) throws Exception {
        for (Plugin< ? super Dto, ? super Id> plugin : plugins) {
            plugin.onAfterDeleteEntityShouldFail(id, responseEntity);
        }
    }
    protected void onBeforeDeleteEntityShouldFail(Id id) throws Exception {
        for (Plugin< ? super Dto, ? super Id> plugin : plugins) {
            plugin.onBeforeDeleteEntityShouldFail(id);
        }
    }
    protected void onAfterFindEntityShouldSucceed(Id id, Dto responseDto) throws Exception {
        for (Plugin< ? super Dto, ? super Id> plugin : plugins) {
            plugin.onAfterFindEntityShouldSucceed(id, responseDto);
        }
    }
    protected void onBeforeFindEntityShouldSucceed(Id id) throws Exception {
        for (Plugin< ? super Dto, ? super Id> plugin : plugins) {
            plugin.onBeforeFindEntityShouldSucceed(id);
        }
    }

    protected void onAfterFindEntityShouldFail(Id id, ResponseEntity<String> responseEntity) throws Exception {
        for (Plugin< ? super Dto, ? super Id> plugin : plugins) {
            plugin.onAfterFindEntityShouldFail(id, responseEntity);
        }
    }
    protected void onBeforeFindEntityShouldFail(Id id) throws Exception {
        for (Plugin< ? super Dto, ? super Id> plugin : plugins) {
            plugin.onBeforeFindEntityShouldFail(id);
        }
    }
    protected void onBeforeFindAllEntitiesShouldSucceed() throws Exception {
        for (Plugin< ? super Dto, ? super Id> plugin : plugins) {
            plugin.onBeforeFindAllEntitiesShouldSucceed();
        }
    }
    protected void onAfterFindAllEntitiesShouldSucceed(Set<Dto> dtos) throws Exception {
        for (Plugin< ? super Dto, ? super Id> plugin : plugins) {
            plugin.onAfterFindAllEntitiesShouldSucceed(dtos);
        }
    }


    @Getter
    @Setter
    public static class Plugin<dto extends IdentifiableEntity<id>, id extends Serializable> {

        private UrlParamIdDtoCrudControllerSpringAdapterIT integrationTest;

        //todo sollte an test methoden gebunden sein und nicht an helper methoden ...

        //updateTests callbacks
        public void onAfterUpdateEntityShouldFail(dto oldEntity, dto newEntity, ResponseEntity<String> responseEntity) throws Exception{}
        public void onBeforeUpdateEntityShouldFail(dto oldEntity, dto newEntity) throws Exception{}
        public void onAfterUpdateEntityShouldSucceed(dto oldEntity, dto newEntity, dto responseDto) throws Exception{}
        public void onBeforeUpdateEntityShouldSucceed(dto oldEntity, dto newEntity) throws Exception{}
        public void onAfterCreateEntityShouldSucceed(dto dtoToCreate, dto responseDto) throws Exception{}
        public void onBeforeCreateEntityShouldSucceed(dto dtoToCreate) throws Exception{}
        public void onAfterCreateEntityShouldFail(dto dtoToCreate) throws Exception{}
        public void onBeforeCreateEntityShouldFail(dto dtoToCreate,ResponseEntity<String> responseEntity) throws Exception{}
        public void onAfterDeleteEntityShouldSucceed(id id, ResponseEntity<String> responseEntity) throws Exception{}
        public void onBeforeDeleteEntityShouldSucceed(id id) throws Exception{}
        public void onAfterDeleteEntityShouldFail(id id,ResponseEntity<String> responseEntity) throws Exception{}
        public void onBeforeDeleteEntityShouldFail(id id) throws Exception{}
        public void onAfterFindEntityShouldSucceed(id id, dto responseDto) throws Exception{}
        public void onBeforeFindEntityShouldSucceed(id id) throws Exception{}
        public void onAfterFindEntityShouldFail(id id, ResponseEntity<String> responseEntity) throws Exception{}
        public void onBeforeFindEntityShouldFail(id id) throws Exception{}
        public void onBeforeFindAllEntitiesShouldSucceed() throws Exception{}
        public void onAfterFindAllEntitiesShouldSucceed(Set<? extends dto> dtos) throws Exception{}

    }


    //todo auslagern in interface , this might also belong into a plugin entirely..
    /**
     * see {@link BeanUtils#isDeepEqual(Object, Object)}
     *
     * @param httpResponseEntity
     * @param prevSavedEntity
     */
    protected void validateDtosAreDeepEqual(Dto httpResponseEntity, Dto prevSavedEntity) {
        boolean deepEqual = areDtosDeepEqual(httpResponseEntity, prevSavedEntity);
        Assertions.assertTrue(deepEqual, "Entities did not match");
    }

    /**
     * see {@link BeanUtils#isDeepEqual(Object, Object)}
     *
     * @param httpResponseEntity
     * @param prevSavedEntity
     * @return
     */
    protected boolean areDtosDeepEqual(Dto httpResponseEntity, Dto prevSavedEntity) {
        return isDeepEqual(httpResponseEntity, prevSavedEntity);
    }



    /**
     * removes all Entites from given {@link Service}
     */
    @AfterEach
    public void tearDown() throws Exception {
        Set<ServiceE> allEntities = crudController.getCrudService().findAll();
        for (ServiceE entityToDelete : allEntities) {
            crudController.getCrudService().deleteById(entityToDelete.getId());
        }
        Set<ServiceE> allEntitiesAfterDeleting = crudController.getCrudService().findAll();
        Assertions.assertTrue(allEntitiesAfterDeleting.isEmpty());
    }

    protected boolean isBodyOfDtoType(String body) {
        return getCrudController().getMediaTypeStrategy().isBodyOfGivenType(body, getDtoEntityClass());
    }


    public String getBaseUrl() {
        return getUrlWithPort() + "/" + crudController.getEntityNameInUrl() + "/";
    }

}