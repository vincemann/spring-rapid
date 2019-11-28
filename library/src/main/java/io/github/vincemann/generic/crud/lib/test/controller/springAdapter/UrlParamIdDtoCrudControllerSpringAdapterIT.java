package io.github.vincemann.generic.crud.lib.test.controller.springAdapter;

import io.github.vincemann.generic.crud.lib.controller.springAdapter.DtoCrudControllerSpringAdapter;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.idFetchingStrategy.UrlParamIdFetchingStrategy;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.test.IntegrationTest;
import io.github.vincemann.generic.crud.lib.test.TransactionManagedTest;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.findAllEntitesTestProvider.FindAllTestEntitiesProvider;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.PostIntegrationTestCallbackIdBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.controller.create.FailedCreateIntegrationTestBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.controller.create.SuccessfulCreateIntegrationTestBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.controller.delete.DeleteIntegrationTestBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.controller.find.FailedFindIntegrationTestBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.controller.find.SuccessfulFindIntegrationTestBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.controller.findAll.FailedFindAllIntegrationTestBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.controller.findAll.SuccessfulFindAllIntegrationTestBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.controller.update.FailedUpdateIntegrationTestBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.controller.update.SuccessfulUpdateIntegrationTestBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.controller.update.updateIteration.FailedUpdateTestEntityBundleIteration;
import io.github.vincemann.generic.crud.lib.test.testBundles.controller.update.updateIteration.SuccessfulUpdateTestEntityBundleIteration;
import io.github.vincemann.generic.crud.lib.test.testBundles.controller.update.updateIteration.abs.UpdateTestEntityBundleIteration;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntityModification;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.TestRequestEntityFactory;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.RequestEntityMapper;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntity;
import io.github.vincemann.generic.crud.lib.test.testExecutionListeners.ResetDatabaseTestExecutionListener;
import io.github.vincemann.generic.crud.lib.util.BeanUtils;
import io.github.vincemann.generic.crud.lib.util.TestLogUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

import java.io.Serializable;
import java.util.*;

import static io.github.vincemann.generic.crud.lib.util.BeanUtils.isDeepEqual;
import static io.github.vincemann.generic.crud.lib.util.SetterUtils.returnIfNotNull;

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
//clear database after each test
@TestExecutionListeners(
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS,
        listeners = {ResetDatabaseTestExecutionListener.class}
)
public abstract class UrlParamIdDtoCrudControllerSpringAdapterIT<ServiceE extends IdentifiableEntity<Id>, Dto extends IdentifiableEntity<Id>, Service extends CrudService<ServiceE, Id>, Controller extends DtoCrudControllerSpringAdapter<ServiceE, Dto, Id, Service>, Id extends Serializable>
        extends IntegrationTest implements TransactionManagedTest {


    //TEST BUNDLES
    //##################################################################################################################
    //UPDATE
    @Getter private List<SuccessfulUpdateIntegrationTestBundle<ServiceE, Dto>> successfulUpdateTestEntityBundles = new ArrayList<>();
    @Getter private List<FailedUpdateIntegrationTestBundle<ServiceE, Dto,Id>> failedUpdateTestEntityBundles = new ArrayList<>();
    protected List<SuccessfulUpdateIntegrationTestBundle<ServiceE, Dto>> provideSuccessfulUpdateTestEntityBundles() { return null; }
    protected List<FailedUpdateIntegrationTestBundle<ServiceE, Dto,Id>> provideFailedUpdateTestBundles() { return null; }

    //CREATE
    @Getter private List<SuccessfulCreateIntegrationTestBundle<Dto>> successfulCreateTestEntityBundles = new ArrayList<>();
    @Getter private List<FailedCreateIntegrationTestBundle<Dto,Id>> failedCreateTestEntityBundles = new ArrayList<>();
    protected List<SuccessfulCreateIntegrationTestBundle<Dto>> provideSuccessfulCreateTestEntityBundles() { return null; }
    protected List<FailedCreateIntegrationTestBundle<Dto,Id>> provideFailingCreateTestBundles() { return null; }

    //DELETE
    @Getter private List<DeleteIntegrationTestBundle<ServiceE,Id>> successfulDeleteTestEntityBundles = new ArrayList<>();
    @Getter private List<DeleteIntegrationTestBundle<ServiceE,Id>> failedDeleteTestEntityBundles = new ArrayList<>();
    protected List<DeleteIntegrationTestBundle<ServiceE,Id>> provideSuccessfulDeleteTestEntityBundles() { return null; }
    protected List<DeleteIntegrationTestBundle<ServiceE,Id>> provideFailedDeleteTestBundles() { return null; }

    //FIND
    @Getter private List<SuccessfulFindIntegrationTestBundle<Dto, ServiceE>> successfulFindTestEntityBundles = new ArrayList<>();
    @Getter private List<FailedFindIntegrationTestBundle<ServiceE,Id>> failedFindTestEntityBundles = new ArrayList<>();
    protected List<SuccessfulFindIntegrationTestBundle<Dto, ServiceE>> provideSuccessfulFindTestEntityBundles() { return null; }
    protected List<FailedFindIntegrationTestBundle<ServiceE,Id>> provideFailedFindTestBundles() {
        return null;
    }

    //FIND ALL
    @Getter private List<SuccessfulFindAllIntegrationTestBundle<ServiceE,Dto>> successfulFindAllTestEntityBundles = new ArrayList<>();
    @Getter private List<FailedFindAllIntegrationTestBundle<ServiceE,Dto>> failedFindAllTestEntityBundles = new ArrayList<>();
    protected List<FailedFindAllIntegrationTestBundle<ServiceE,Dto>> provideFailedFindAllTestBundles() { return null; }
    protected List<SuccessfulFindAllIntegrationTestBundle<ServiceE,Dto>> provideSuccessfulFindAllTestBundles() { return null; }
    //##################################################################################################################





            private List<Plugin<? super Dto, ? super Id>> plugins = new ArrayList<>();
            private TestRequestEntityFactory requestEntityFactory;
    @Getter private Controller crudController;
    @Getter private Class<Dto> dtoEntityClass;
    @Getter private Class<ServiceE> serviceEntityClass;
    @Getter private String entityIdParamKey;
    @Getter private PlatformTransactionManager transactionManager;



    /*public UrlParamIdDtoCrudControllerSpringAdapterIT(String url, Controller crudController, TestRequestEntityFactory requestEntityFactory, Plugin<? super Dto, ? super Id>... plugins) {
        super(url);
        constructorInit(crudController, requestEntityFactory, plugins);
    }*/

    public UrlParamIdDtoCrudControllerSpringAdapterIT(Controller crudController, PlatformTransactionManager transactionManager, TestRequestEntityFactory requestEntityFactory, Plugin<? super Dto, ? super Id>... plugins) {
        this.transactionManager=transactionManager;
        this.requestEntityFactory = requestEntityFactory;
        constructorInit(crudController, requestEntityFactory, plugins);
    }

    private void constructorInit(Controller crudController, TestRequestEntityFactory requestEntityFactory, Plugin<? super Dto, ? super Id>... plugins) {
        Assertions.assertTrue(crudController.getIdIdFetchingStrategy() instanceof UrlParamIdFetchingStrategy, "Controller must have an UrlParamIdFetchingStrategy");
        requestEntityFactory.setTest(this);
        this.crudController = crudController;
        this.requestEntityFactory = requestEntityFactory;
        this.dtoEntityClass = crudController.getDtoClass();
        this.serviceEntityClass = crudController.getServiceEntityClass();
        this.entityIdParamKey = ((UrlParamIdFetchingStrategy) crudController.getIdIdFetchingStrategy()).getIdUrlParamKey();
        initPlugins(plugins);
    }

    private void initPlugins(Plugin<? super Dto, ? super Id>... plugins) {
        this.plugins.addAll(Arrays.asList(plugins));
        this.plugins.forEach(plugin -> plugin.setIntegrationTest(this));
    }






    @Override
    public void provideBundles() throws Exception {
        onBeforeProvideEntityBundles();
        failedCreateTestEntityBundles = returnIfNotNull(failedCreateTestEntityBundles, provideFailingCreateTestBundles());
        failedDeleteTestEntityBundles = returnIfNotNull(failedDeleteTestEntityBundles, provideFailedDeleteTestBundles());
        failedFindTestEntityBundles = returnIfNotNull(failedFindTestEntityBundles, provideFailedFindTestBundles());
        failedFindAllTestEntityBundles = returnIfNotNull(failedFindAllTestEntityBundles, provideFailedFindAllTestBundles());
        failedUpdateTestEntityBundles = returnIfNotNull(failedUpdateTestEntityBundles, provideFailedUpdateTestBundles());


        successfulFindAllTestEntityBundles = returnIfNotNull(successfulFindAllTestEntityBundles, provideSuccessfulFindAllTestBundles());
        successfulUpdateTestEntityBundles = returnIfNotNull(successfulUpdateTestEntityBundles, provideSuccessfulUpdateTestEntityBundles());
        successfulCreateTestEntityBundles = returnIfNotNull(successfulCreateTestEntityBundles, provideSuccessfulCreateTestEntityBundles());
        successfulDeleteTestEntityBundles = returnIfNotNull(successfulDeleteTestEntityBundles, provideSuccessfulDeleteTestEntityBundles());
        successfulFindTestEntityBundles = returnIfNotNull(successfulFindTestEntityBundles, provideSuccessfulFindTestEntityBundles());
    }
    protected void onBeforeProvideEntityBundles() throws Exception { }



    //TESTS ############################################################################################################
    //FIND ALL
    @Test
    public void findAllEntities_shouldSucceedTest() throws Exception {
        Assumptions.assumeTrue(getCrudController().getEndpointsExposureDetails().isFindAllEndpointExposed());
        Assumptions.assumeTrue(!successfulFindAllTestEntityBundles.isEmpty());

        for (SuccessfulFindAllIntegrationTestBundle<ServiceE,Dto> bundle : successfulFindAllTestEntityBundles) {
            saveServiceEntities(bundle.getEntitiesSavedBeforeRequest());
            bundle.callPreTestCallback();
            Set<Dto> foundDtos = findAllEntitiesShouldSucceed(bundle.getRequestEntityModification(), bundle.getFindAllTestEntitiesProvider());
            bundle.callPostTestCallback(foundDtos);
        }
    }

    @Test
    public void findAllEntities_shouldFailTest() throws Exception {
        Assumptions.assumeTrue(getCrudController().getEndpointsExposureDetails().isFindAllEndpointExposed());
        Assumptions.assumeTrue(!successfulFindAllTestEntityBundles.isEmpty());

        for (FailedFindAllIntegrationTestBundle<ServiceE,Dto> bundle : failedFindAllTestEntityBundles) {
            saveServiceEntities(bundle.getEntitiesSavedBeforeRequest());
            bundle.callPreTestCallback();
            ResponseEntity<String> responseEntity = findAllEntitiesShouldFail(bundle.getRequestEntityModification(),
                    bundle.getFindAllTestEntitiesProvider());
            bundle.callPostTestCallback(responseEntity);
        }
    }

    protected ResponseEntity<String> findAllEntitiesShouldFail(TestRequestEntityModification requestEntityModification, FindAllTestEntitiesProvider<ServiceE> findAllTestEntitiesProvider) throws Exception {
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(
                CrudControllerTestCase.FAILED_FIND_ALL,
                requestEntityModification,
                null);
        onBeforeFindAllEntitiesShouldFail();
        ResponseEntity<String> responseEntity = findAllEntities(testRequestEntity);
        onAfterFindAllEntitiesShouldFail(responseEntity);
        return responseEntity;
    }

    protected Set<Dto> findAllEntitiesShouldSucceed(TestRequestEntityModification testRequestEntityModification, FindAllTestEntitiesProvider<ServiceE> findAllTestEntitiesProvider) throws Exception {
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(
                CrudControllerTestCase.SUCCESSFUL_FIND_ALL,
                testRequestEntityModification, null);
        onBeforeFindAllEntitiesShouldSucceed();
        ResponseEntity<String> responseEntity = findAllEntities(testRequestEntity);

        @SuppressWarnings("unchecked")
        Set<Dto> dtos = crudController.getMediaTypeStrategy().readDtosFromBody(responseEntity.getBody(), getDtoEntityClass(), Set.class);
        onAfterFindAllEntitiesShouldSucceed(dtos);

        Set<ServiceE> allServiceEntitiesShouldHaveBeenFound = findAllTestEntitiesProvider.provideEntitiesShouldBeFound();
        Assertions.assertEquals(allServiceEntitiesShouldHaveBeenFound.size(), dtos.size());
        List<Id> idsSeen = new ArrayList<>();
        for (Dto dto : dtos) {
            //prevent duplicates
            Assertions.assertFalse(idsSeen.contains(dto.getId()));
            idsSeen.add(dto.getId());
        }
        return dtos;
    }

    private ResponseEntity<String> findAllEntities(TestRequestEntity testRequestEntity) {
        ResponseEntity<String> responseEntity = getRestTemplate().exchange(RequestEntityMapper.map(testRequestEntity, null), String.class);
        Assertions.assertEquals(testRequestEntity.getExpectedHttpStatus(), responseEntity.getStatusCode(), "Status was : " + responseEntity.getStatusCode() + " response Body: " + responseEntity.getBody());
        return responseEntity;
    }




    //UPDATE
    @Test
    public void updateEntity_shouldSucceedTest() throws Exception {
        Assumptions.assumeTrue(getCrudController().getEndpointsExposureDetails().isUpdateEndpointExposed());
        provideBundlesAndRollbackTransaction();
        Assumptions.assumeTrue(!successfulUpdateTestEntityBundles.isEmpty());


        for (int i = 0; i<this.successfulUpdateTestEntityBundles.size();i++){
            SuccessfulUpdateIntegrationTestBundle<ServiceE, Dto> tempBundle = successfulUpdateTestEntityBundles.get(i);
            List<SuccessfulUpdateTestEntityBundleIteration<Dto>> updateTestEntityBundleIterations = tempBundle.getUpdateTestEntityBundleIterations();
            if (updateTestEntityBundleIterations.isEmpty()) {
                log.info("No successful update tests for testDto : " + tempBundle.getEntity());
                return;
            }
            for(int j = 0; j<updateTestEntityBundleIterations.size();j++){
                //entities from provideBundles process need to be persisted in same transaction as saveServiceEntity
                // -> new transaction and new provideBundles call for each iteration
                TransactionStatus testTransaction = provideBundlesAndStartTransaction();
                //get bundle again because reference of successfulUpdateTestEntityBundles.get(i) has changed because of latest/new provideBundles() call
                SuccessfulUpdateIntegrationTestBundle<ServiceE, Dto> bundle = successfulUpdateTestEntityBundles.get(i);
                UpdateTestEntityBundleIteration<Dto,Dto,Dto> updateBundle = bundle.getUpdateTestEntityBundleIterations().get(j);
                Dto modifiedDto = updateBundle.getEntity();
                TestLogUtils.logTestStart(log, "updateEntity should succeed", new AbstractMap.SimpleEntry<>("test Service Entity", bundle.getEntity()), new AbstractMap.SimpleEntry<>("modifiedDto", modifiedDto));

                //save old dto
                ServiceE savedOldEntity = saveServiceEntity(bundle.getEntity());
                modifiedDto.setId(savedOldEntity.getId());
                //save all changes from provideBundle call and saveServiceEntity by committing transaction
                transactionManager.commit(testTransaction);

                updateBundle.callPreTestCallback(modifiedDto);
                //update dto
                Dto dbUpdatedDto = updateEntityShouldSucceed(modifiedDto, updateBundle.getTestRequestEntityModification());
                updateBundle.callPostTestCallback(dbUpdatedDto);

                //remove entity -> clean for next iteration
                getCrudController().getCrudService().deleteById(savedOldEntity.getId());
                TestLogUtils.logTestSucceeded(log, "updateEntity should succeed", new AbstractMap.SimpleEntry<>("test Service Entity", bundle.getEntity()), new AbstractMap.SimpleEntry<>("modifiedDto", modifiedDto));
            }
        }
    }

    @Test
    public void updateEntity_shouldFailTest() throws Exception {
        Assumptions.assumeTrue(getCrudController().getEndpointsExposureDetails().isUpdateEndpointExposed());
        provideBundlesAndRollbackTransaction();
        Assumptions.assumeTrue(!failedUpdateTestEntityBundles.isEmpty());

        for (int i = 0; i<this.failedUpdateTestEntityBundles.size();i++){
            FailedUpdateIntegrationTestBundle<ServiceE, Dto,Id> tempBundle = failedUpdateTestEntityBundles.get(i);
            List<FailedUpdateTestEntityBundleIteration<Dto,Id>> updateTestEntityBundleIterations = tempBundle.getUpdateTestEntityBundleIterations();
            if (updateTestEntityBundleIterations.isEmpty()) {
                log.info("No failed update tests for testDto : " + tempBundle.getEntityToUpdate());
                return;
            }
            for(int j = 0; j<updateTestEntityBundleIterations.size();j++){
                //entities from provideBundles process need to be persisted in same transaction as saveServiceEntity
                // -> new transaction and new provideBundles call for each iteration
                TransactionStatus testTransaction = provideBundlesAndStartTransaction();
                //get bundle again because reference of failedUpdateTestEntityBundles.get(i) has changed because of latest/new provideBundles() call
                FailedUpdateIntegrationTestBundle<ServiceE, Dto,Id> bundle = failedUpdateTestEntityBundles.get(i);
                FailedUpdateTestEntityBundleIteration<Dto,Id> iterationBundle = bundle.getUpdateTestEntityBundleIterations().get(j);

                Dto modifiedDto = iterationBundle.getEntity();
                TestLogUtils.logTestStart(log, "updateEntity should fail", new AbstractMap.SimpleEntry<>("testDto", bundle.getEntityToUpdate()), new AbstractMap.SimpleEntry<>("modifiedDto", modifiedDto));
                ServiceE savedOldEntity = saveServiceEntity(bundle.getEntityToUpdate());
                bundle.callPreTestCallback(savedOldEntity);
                modifiedDto.setId(savedOldEntity.getId());
                //save all changes from provideBundle call and saveServiceEntity by committing transaction
                transactionManager.commit(testTransaction);
                //update dto
                iterationBundle.callPreTestCallback(modifiedDto);
                ResponseEntity<String> responseEntity = updateEntityShouldFail(modifiedDto, iterationBundle.getTestRequestEntityModification());
                iterationBundle.callPostTestCallback(new PostIntegrationTestCallbackIdBundle<>(modifiedDto.getId(),responseEntity));

                //remove entity -> clean for next iteration
                getCrudController().getCrudService().deleteById(savedOldEntity.getId());
                TestLogUtils.logTestStart(log, "updateEntity should fail", new AbstractMap.SimpleEntry<>("testDto", bundle.getEntityToUpdate()), new AbstractMap.SimpleEntry<>("modifiedDto", modifiedDto));
            }
        }
    }


    //todo remove this useless method
    private Dto _updateEntityShouldSucceed(Dto oldEntityDto, Dto newEntityDto, TestRequestEntity testRequestEntity) throws Exception {
        //trotzdem müssen changes vorliegen
        Assertions.assertFalse(isDeepEqual(oldEntityDto, newEntityDto));
        onBeforeUpdateEntityShouldSucceed(oldEntityDto, newEntityDto);
        ResponseEntity<String> responseEntity = updateEntity(newEntityDto, testRequestEntity);
        Assertions.assertEquals(testRequestEntity.getExpectedHttpStatus(), responseEntity.getStatusCode());
        Dto httpResponseDto = crudController.getMediaTypeStrategy().readDtoFromBody(responseEntity.getBody(), dtoEntityClass);
        Assertions.assertNotNull(httpResponseDto);

        onAfterUpdateEntityShouldSucceed(oldEntityDto, newEntityDto, httpResponseDto);

        //todo auslagern?
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
    protected Dto updateEntityShouldSucceed(Dto newEntityDto, TestRequestEntityModification testRequestEntityModification) throws Exception {
        Assertions.assertNotNull(newEntityDto.getId());
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(CrudControllerTestCase.SUCCESSFUL_UPDATE, testRequestEntityModification, newEntityDto.getId());
        //Entity must be saved already
        Optional<ServiceE> serviceFoundEntityBeforeUpdate = crudController.getCrudService().findById(newEntityDto.getId());
        Assertions.assertTrue(serviceFoundEntityBeforeUpdate.isPresent(), "Entity to update was not present");
        //there must be changes
        Dto oldEntityDtoFromService = getCrudController().getDtoMapper().mapServiceEntityToDto(serviceFoundEntityBeforeUpdate.get(), getDtoEntityClass());
        return _updateEntityShouldSucceed(oldEntityDtoFromService, newEntityDto, testRequestEntity);
    }

    protected ResponseEntity<String> updateEntityShouldFail(Dto newEntity, TestRequestEntityModification testRequestEntityModification) throws Exception {
        Assertions.assertNotNull(newEntity.getId());
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(CrudControllerTestCase.FAILED_UPDATE, testRequestEntityModification, newEntity.getId());
        //Entity muss vorher auch schon da sein
        Optional<ServiceE> serviceFoundEntityBeforeUpdate = crudController.getCrudService().findById(newEntity.getId());
        Assertions.assertTrue(serviceFoundEntityBeforeUpdate.isPresent(), "Entity to update was not present");

        onBeforeUpdateEntityShouldFail(newEntity);

        ResponseEntity<String> responseEntity = updateEntity(newEntity, testRequestEntity);
        Assertions.assertEquals(responseEntity.getStatusCode(), testRequestEntity.getExpectedHttpStatus(), "Status was : " + responseEntity.getStatusCode());

        onAfterUpdateEntityShouldFail(newEntity, responseEntity);
        return responseEntity;
    }

    /**
     * Send update Entity Request to Backend
     *
     * @param newEntity updated entityDto
     * @return backend Response
     */
    private ResponseEntity<String> updateEntity(Dto newEntity, TestRequestEntity testRequestEntity) {
        Assertions.assertNotNull(newEntity.getId());
        return getRestTemplate().exchange(RequestEntityMapper.map(testRequestEntity, newEntity), String.class);
    }




    //CREATE
    @Test
    public void createEntity_shouldSucceedTest() throws Exception {
        Assumptions.assumeTrue(getCrudController().getEndpointsExposureDetails().isCreateEndpointExposed());
        provideBundlesAndCommitTransaction();
        Assumptions.assumeTrue(!successfulCreateTestEntityBundles.isEmpty());


        for (SuccessfulCreateIntegrationTestBundle<Dto> bundle : this.successfulCreateTestEntityBundles) {
            TestLogUtils.logTestStart(log, "createEntity_shouldSucceed", new AbstractMap.SimpleEntry<>("testDto", bundle.getEntity()));
            bundle.callPreTestCallback(bundle.getEntity());
            Dto savedDto = createEntityShouldSucceed(bundle.getEntity(), bundle.getTestRequestEntityModification());
            bundle.callPostTestCallback(savedDto);
            TestLogUtils.logTestSucceeded(log, "createEntity_shouldSucceed", new AbstractMap.SimpleEntry<>("testDto", bundle.getEntity()));
        }
    }

    @Test
    public void createEntity_shouldFailTest() throws Exception {
        Assumptions.assumeTrue(getCrudController().getEndpointsExposureDetails().isCreateEndpointExposed());
        provideBundlesAndCommitTransaction();
        Assumptions.assumeTrue(!failedCreateTestEntityBundles.isEmpty());

        for (FailedCreateIntegrationTestBundle<Dto,Id> bundle : this.failedCreateTestEntityBundles) {
            TestLogUtils.logTestStart(log, "createEntity_shouldFail", new AbstractMap.SimpleEntry<>("testDto", bundle.getEntity()));
            bundle.callPreTestCallback(bundle.getEntity());
            ResponseEntity<String> responseEntity = createEntityShouldFail(bundle.getEntity(), bundle.getTestRequestEntityModification());
            bundle.callPostTestCallback(new PostIntegrationTestCallbackIdBundle<>(bundle.getEntity().getId(),responseEntity));

            TestLogUtils.logTestSucceeded(log, "createEntity_shouldFail", new AbstractMap.SimpleEntry<>("testDto", bundle.getEntity()));
        }
    }

    protected Dto createEntityShouldSucceed(Dto dto, TestRequestEntityModification bundleMod) throws Exception {
        Assertions.assertNull(dto.getId());
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(CrudControllerTestCase.SUCCESSFUL_CREATE, bundleMod, null);
        onBeforeCreateEntityShouldSucceed(dto);
        ResponseEntity<String> responseEntity = createEntity(dto, testRequestEntity);
        Assertions.assertEquals(responseEntity.getStatusCode(), testRequestEntity.getExpectedHttpStatus(), "Status was : " + responseEntity.getStatusCode() + " response Body: " + responseEntity.getBody());
        Dto httpResponseEntity = crudController.getMediaTypeStrategy().readDtoFromBody(responseEntity.getBody(), dtoEntityClass);
        onAfterCreateEntityShouldSucceed(dto, httpResponseEntity);
        return httpResponseEntity;
    }

    protected ResponseEntity<String> createEntityShouldFail(Dto dto, TestRequestEntityModification bundleMod) throws Exception {
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(CrudControllerTestCase.FAILED_CREATE, bundleMod, null);
        onBeforeCreateEntityShouldFail(dto);
        ResponseEntity<String> responseEntity = createEntity(dto, testRequestEntity);
        Assertions.assertEquals(responseEntity.getStatusCode(), testRequestEntity.getExpectedHttpStatus(), "Status was : " + responseEntity.getStatusCode() + " response Body: " + responseEntity.getBody());
        onAfterCreateEntityShouldFail(dto, responseEntity);
        return responseEntity;
    }

    /**
     * Send create Entity Request to Backend, Response is returned
     *
     * @param dtoEntity the Dto entity that should be stored
     * @return
     */
    private ResponseEntity<String> createEntity(Dto dtoEntity, TestRequestEntity testRequestEntity) {
        return getRestTemplate().exchange(RequestEntityMapper.map(testRequestEntity, dtoEntity), String.class);
    }






    //DELETE
    @Test
    public void deleteEntity_shouldSucceedTest() throws Exception {
        Assumptions.assumeTrue(getCrudController().getEndpointsExposureDetails().isDeleteEndpointExposed());
        provideBundlesAndRollbackTransaction();
        Assumptions.assumeTrue(!successfulDeleteTestEntityBundles.isEmpty());

        for(int i = 0; i<this.successfulDeleteTestEntityBundles.size();i++){
            TransactionStatus transaction = provideBundlesAndStartTransaction();
            DeleteIntegrationTestBundle<ServiceE,Id> bundle = successfulDeleteTestEntityBundles.get(i);
            TestLogUtils.logTestStart(log, "deleteEntity should Succeed ", new AbstractMap.SimpleEntry<>("test Service Entity", bundle.getEntity()));

            ServiceE savedEntityToDelete = saveServiceEntity(bundle.getEntity());
            transactionManager.commit(transaction);
            Id idFromEntityToDelete = savedEntityToDelete.getId();
            bundle.callPreTestCallback(idFromEntityToDelete);
            ResponseEntity<String> responseEntity = deleteEntityShouldSucceed(idFromEntityToDelete, bundle.getTestRequestEntityModification());
            bundle.callPostTestCallback(new PostIntegrationTestCallbackIdBundle<>(idFromEntityToDelete,responseEntity));

            TestLogUtils.logTestSucceeded(log, "deleteEntity should Succeed", new AbstractMap.SimpleEntry<>("test Service Entity", bundle.getEntity()));
        }
    }

    @Test
    public void deleteEntity_shouldFailTest() throws Exception {
        Assumptions.assumeTrue(getCrudController().getEndpointsExposureDetails().isDeleteEndpointExposed());
        provideBundlesAndRollbackTransaction();
        Assumptions.assumeTrue(!failedDeleteTestEntityBundles.isEmpty());

        for(int i = 0; i<this.failedDeleteTestEntityBundles.size();i++){
            TransactionStatus transaction = provideBundlesAndStartTransaction();
            DeleteIntegrationTestBundle<ServiceE,Id> bundle = failedDeleteTestEntityBundles.get(i);
            TestLogUtils.logTestStart(log, "deleteEntity should Fail", new AbstractMap.SimpleEntry<>("testDto", bundle.getEntity()));

            ServiceE savedEntityToDelete = saveServiceEntity(bundle.getEntity());
            transactionManager.commit(transaction);
            Id idFromEntityToDelete = savedEntityToDelete.getId();
            bundle.callPreTestCallback(idFromEntityToDelete);
            ResponseEntity<String> responseEntity = deleteEntityShouldFail(idFromEntityToDelete, bundle.getTestRequestEntityModification());
            bundle.callPostTestCallback(new PostIntegrationTestCallbackIdBundle<>(idFromEntityToDelete,responseEntity));

            TestLogUtils.logTestSucceeded(log, "deleteEntity should Fail", new AbstractMap.SimpleEntry<>("testDto", bundle.getEntity()));
        }
    }

    protected ResponseEntity<String> deleteEntityShouldSucceed(Id id, TestRequestEntityModification bunldeMod) throws Exception {
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(CrudControllerTestCase.SUCCESSFUL_DELETE, bunldeMod, id);
        //Entity muss vorher auch schon da sein
        Optional<ServiceE> serviceFoundEntityBeforeDelete = crudController.getCrudService().findById(id);
        Assertions.assertTrue(serviceFoundEntityBeforeDelete.isPresent(), "Entity to delete was not present");

        onBeforeDeleteEntityShouldSucceed(id);
        ResponseEntity<String> responseEntity = deleteEntity(id, testRequestEntity);
        Assertions.assertEquals(responseEntity.getStatusCode(), testRequestEntity.getExpectedHttpStatus(), "Status was : " + responseEntity.getStatusCode() + " response Body: " + responseEntity.getBody());
        onAfterDeleteEntityShouldSucceed(id, responseEntity);
        return responseEntity;
    }

    protected ResponseEntity<String> deleteEntityShouldFail(Id id, TestRequestEntityModification testRequestEntityModification) throws Exception {
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(CrudControllerTestCase.FAILED_DELETE, testRequestEntityModification, id);
        //Entity muss vorher auch schon da sein
        Optional<ServiceE> serviceFoundEntityBeforeDelete = crudController.getCrudService().findById(id);
        Assertions.assertTrue(serviceFoundEntityBeforeDelete.isPresent(), "Entity to delete was not present");

        onBeforeDeleteEntityShouldFail(id);
        ResponseEntity<String> responseEntity = deleteEntity(id, testRequestEntity);
        Assertions.assertEquals(responseEntity.getStatusCode(), testRequestEntity.getExpectedHttpStatus(), "Status was : " + responseEntity.getStatusCode() + " response Body: " + responseEntity.getBody());

        onAfterDeleteEntityShouldFail(id, responseEntity);
        return responseEntity;
    }

    private ResponseEntity<String> deleteEntity(Id id, TestRequestEntity testRequestEntity) {
        return getRestTemplate().exchange(RequestEntityMapper.map(testRequestEntity, id), String.class);
    }








    //FIND
    @Test
    public void findEntity_shouldSucceedTest() throws Exception {
        Assumptions.assumeTrue(getCrudController().getEndpointsExposureDetails().isFindEndpointExposed());
        provideBundlesAndRollbackTransaction();
        Assumptions.assumeTrue(!successfulFindTestEntityBundles.isEmpty());

        for(int i=0;i<this.successfulFindTestEntityBundles.size();i++){
            TransactionStatus transaction = provideBundlesAndStartTransaction();
            SuccessfulFindIntegrationTestBundle<Dto, ServiceE> bundle = successfulFindTestEntityBundles.get(i);
            TestLogUtils.logTestStart(log, "findEntity should Succeed", new AbstractMap.SimpleEntry<>("test Service Entity", bundle.getEntity()));

            ServiceE entityToFind = saveServiceEntity(bundle.getEntity());
            transactionManager.commit(transaction);
            bundle.callPreTestCallback(entityToFind);
            Dto responseDto = findEntityShouldSucceed(entityToFind.getId(), bundle.getTestRequestEntityModification());
            //todo auslagern?
            validateDtosAreDeepEqual(responseDto, getCrudController().getDtoMapper().mapServiceEntityToDto(entityToFind, getDtoEntityClass()));
            bundle.callPostTestCallback(responseDto);

            TestLogUtils.logTestSucceeded(log, "findEntity should Succeed", new AbstractMap.SimpleEntry<>("test Service Entity", bundle.getEntity()));

        }
    }

    @Test
    public void findEntity_shouldFailTest() throws Exception {
        Assumptions.assumeTrue(getCrudController().getEndpointsExposureDetails().isFindEndpointExposed());
        provideBundlesAndRollbackTransaction();
        Assumptions.assumeTrue(!failedFindTestEntityBundles.isEmpty());

        for(int i=0;i<this.failedFindTestEntityBundles.size();i++){
            TransactionStatus transaction = provideBundlesAndStartTransaction();
            FailedFindIntegrationTestBundle<ServiceE,Id> bundle = failedFindTestEntityBundles.get(i);
            TestLogUtils.logTestStart(log, "findEntity should Fail", new AbstractMap.SimpleEntry<>("test Service Entity", bundle.getEntity()));

            ServiceE entityToFind = saveServiceEntity(bundle.getEntity());
            transactionManager.commit(transaction);
            bundle.callPreTestCallback(entityToFind);
            ResponseEntity<String> responseEntity = findEntityShouldFail(entityToFind.getId(), bundle.getTestRequestEntityModification());
            bundle.callPostTestCallback(new PostIntegrationTestCallbackIdBundle<>(entityToFind.getId(),responseEntity));

            TestLogUtils.logTestSucceeded(log, "findEntity should Fail", new AbstractMap.SimpleEntry<>("test Service Entity", bundle.getEntity()));
        }
    }

    /**
     * @param id id of the entity, that should be found
     * @return the dto of the requested entity found on backend with given id
     * @throws Exception
     */
    protected Dto findEntityShouldSucceed(Id id, TestRequestEntityModification bundleMod) throws Exception {
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(CrudControllerTestCase.SUCCESSFUL_FIND, bundleMod, id);
        onBeforeFindEntityShouldSucceed(id);
        ResponseEntity<String> responseEntity = findEntity(id, testRequestEntity);
        Assertions.assertEquals(responseEntity.getStatusCode(), testRequestEntity.getExpectedHttpStatus(), "Status was : " + responseEntity.getStatusCode() + " response Body: " + responseEntity.getBody());
        Dto responseDto = crudController.getMediaTypeStrategy().readDtoFromBody(responseEntity.getBody(), dtoEntityClass);
        Assertions.assertNotNull(responseDto);
        onAfterFindEntityShouldSucceed(id, responseDto);
        return responseDto;
    }

    protected ResponseEntity<String> findEntityShouldFail(Id id, TestRequestEntityModification bundleMod) throws Exception {
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(CrudControllerTestCase.FAILED_FIND, bundleMod, id);
        onBeforeFindEntityShouldFail(id);
        ResponseEntity<String> responseEntity = findEntity(id, testRequestEntity);
        Assertions.assertEquals(responseEntity.getStatusCode(), testRequestEntity.getExpectedHttpStatus(), "Status was : " + responseEntity.getStatusCode() + " response Body: " + responseEntity.getBody());
        onAfterFindEntityShouldFail(id, responseEntity);
        return responseEntity;
    }

    /**
     * send find Entity Request to backend
     *
     * @param id
     * @return backend Response
     */
    private ResponseEntity<String> findEntity(Id id, TestRequestEntity testRequestEntity) {
        Assertions.assertNotNull(id);
        return getRestTemplate().exchange(RequestEntityMapper.map(testRequestEntity, id), String.class);
    }


    //PLUGIN CALLBACKS##################################################################################################
    //UPDATE
    protected void onBeforeUpdateEntityShouldSucceed(Dto oldEntity, Dto newEntity) throws Exception {
        for (Plugin<? super Dto, ? super Id> plugin : plugins) {
            plugin.onBeforeUpdateEntityShouldSucceed(oldEntity, newEntity);
        }
    }
    protected void onAfterUpdateEntityShouldSucceed(Dto oldEntity, Dto newEntity, Dto responseDto) throws Exception {
        for (Plugin<? super Dto, ? super Id> plugin : plugins) {
            plugin.onAfterUpdateEntityShouldSucceed(oldEntity, newEntity, responseDto);
        }
    }
    protected void onBeforeUpdateEntityShouldFail(Dto newEntity) throws Exception {
        for (Plugin<? super Dto, ? super Id> plugin : plugins) {
            plugin.onBeforeUpdateEntityShouldFail(newEntity);
        }
    }
    protected void onAfterUpdateEntityShouldFail(Dto newEntity, ResponseEntity<String> responseEntity) throws Exception {
        for (Plugin<? super Dto, ? super Id> plugin : plugins) {
            plugin.onAfterUpdateEntityShouldFail(newEntity, responseEntity);
        }
    }
    //CREATE
    protected void onBeforeCreateEntityShouldSucceed(Dto dtoToCreate) throws Exception {
        for (Plugin<? super Dto, ? super Id> plugin : plugins) {
            plugin.onBeforeCreateEntityShouldSucceed(dtoToCreate);
        }
    }
    protected void onAfterCreateEntityShouldSucceed(Dto dtoToCreate, Dto responseDto) throws Exception {
        for (Plugin<? super Dto, ? super Id> plugin : plugins) {
            plugin.onAfterCreateEntityShouldSucceed(dtoToCreate, responseDto);
        }
    }
    protected void onBeforeCreateEntityShouldFail(Dto dtoToCreate) throws Exception {
        for (Plugin<? super Dto, ? super Id> plugin : plugins) {
            plugin.onBeforeCreateEntityShouldFail(dtoToCreate);
        }
    }
    protected void onAfterCreateEntityShouldFail(Dto dtoToCreate, ResponseEntity<String> responseEntity) throws Exception {
        for (Plugin<? super Dto, ? super Id> plugin : plugins) {
            plugin.onAfterCreateEntityShouldFail(dtoToCreate, responseEntity);
        }
    }
    //DELETE
    protected void onBeforeDeleteEntityShouldSucceed(Id id) throws Exception {
        for (Plugin<? super Dto, ? super Id> plugin : plugins) {
            plugin.onBeforeDeleteEntityShouldSucceed(id);
        }
    }
    protected void onAfterDeleteEntityShouldSucceed(Id id, ResponseEntity<String> responseEntity) throws Exception {
        for (Plugin<? super Dto, ? super Id> plugin : plugins) {
            plugin.onAfterDeleteEntityShouldSucceed(id, responseEntity);
        }
    }
    protected void onBeforeDeleteEntityShouldFail(Id id) throws Exception {
        for (Plugin<? super Dto, ? super Id> plugin : plugins) {
            plugin.onBeforeDeleteEntityShouldFail(id);
        }
    }
    protected void onAfterDeleteEntityShouldFail(Id id, ResponseEntity<String> responseEntity) throws Exception {
        for (Plugin<? super Dto, ? super Id> plugin : plugins) {
            plugin.onAfterDeleteEntityShouldFail(id, responseEntity);
        }
    }
    //FIND
    protected void onBeforeFindEntityShouldSucceed(Id id) throws Exception {
        for (Plugin<? super Dto, ? super Id> plugin : plugins) {
            plugin.onBeforeFindEntityShouldSucceed(id);
        }
    }
    protected void onAfterFindEntityShouldSucceed(Id id, Dto responseDto) throws Exception {
        for (Plugin<? super Dto, ? super Id> plugin : plugins) {
            plugin.onAfterFindEntityShouldSucceed(id, responseDto);
        }
    }
    protected void onBeforeFindEntityShouldFail(Id id) throws Exception {
        for (Plugin<? super Dto, ? super Id> plugin : plugins) {
            plugin.onBeforeFindEntityShouldFail(id);
        }
    }
    protected void onAfterFindEntityShouldFail(Id id, ResponseEntity<String> responseEntity) throws Exception {
        for (Plugin<? super Dto, ? super Id> plugin : plugins) {
            plugin.onAfterFindEntityShouldFail(id, responseEntity);
        }
    }
    //FIND ALL
    protected void onBeforeFindAllEntitiesShouldSucceed() throws Exception {
        for (Plugin<? super Dto, ? super Id> plugin : plugins) {
            plugin.onBeforeFindAllEntitiesShouldSucceed();
        }
    }
    protected void onAfterFindAllEntitiesShouldSucceed(Set<Dto> dtos) throws Exception {
        for (Plugin<? super Dto, ? super Id> plugin : plugins) {
            plugin.onAfterFindAllEntitiesShouldSucceed(dtos);
        }
    }
    protected void onBeforeFindAllEntitiesShouldFail() throws Exception {
        for (Plugin<? super Dto, ? super Id> plugin : plugins) {
            plugin.onBeforeFindAllEntitiesShouldFail();
        }
    }
    protected void onAfterFindAllEntitiesShouldFail(ResponseEntity<String> responseEntity) throws Exception {
        for (Plugin<? super Dto, ? super Id> plugin : plugins) {
            plugin.onAfterFindAllEntitiesShouldFail(responseEntity);
        }
    }
    //##################################################################################################################



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


    //todo auslagern in interface , this might also belong into a plugin entirely..

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

    /*
    /**
     * Removes all Entites from given {@link Service}
     *//*
    @AfterEach
    public void tearDown() throws Exception {
        Set<ServiceE> allEntities = crudController.getCrudService().findAll();
        for (ServiceE entityToDelete : allEntities) {
            crudController.getCrudService().deleteById(entityToDelete.getId());
        }
        Set<ServiceE> allEntitiesAfterDeleting = crudController.getCrudService().findAll();
        Assertions.assertTrue(allEntitiesAfterDeleting.isEmpty());
    }*/


    @Getter
    @Setter
    public static class Plugin<dto extends IdentifiableEntity<id>, id extends Serializable> {
        private UrlParamIdDtoCrudControllerSpringAdapterIT integrationTest;

        public void onAfterUpdateEntityShouldFail(dto newEntity, ResponseEntity<String> responseEntity) throws Exception { }
        public void onBeforeUpdateEntityShouldFail(dto newEntity) throws Exception { }
        public void onAfterUpdateEntityShouldSucceed(dto oldEntity, dto newEntity, dto responseDto) throws Exception { }
        public void onBeforeUpdateEntityShouldSucceed(dto oldEntity, dto newEntity) throws Exception { }
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