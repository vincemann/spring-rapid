package io.github.vincemann.generic.crud.lib.test.controller.springAdapter;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.EntityMappingException;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.DtoCrudControllerSpringAdapter;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.idFetchingStrategy.UrlParamIdFetchingStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.mediaTypeStrategy.DtoReadingException;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.test.IntegrationTest;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.TestEntityBundle;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.UpdateTestBundle;
import io.github.vincemann.generic.crud.lib.util.BeanUtils;
import io.github.vincemann.generic.crud.lib.util.TestLogUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

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

    /**
     * This is a security feature.
     * If there are more entities in the database than this value, the database wont be cleared after the test, and the test will fail.
     */
    private static final int MAX_AMOUNT_ENTITIES_IN_REPO_WHEN_DELETING_ALL = 200;

    private final Controller crudController;
    private final Class<Dto> dtoEntityClass;
    private final String entityIdParamKey;
    private int safetyCheckMaxAmountEntitiesInRepo = MAX_AMOUNT_ENTITIES_IN_REPO_WHEN_DELETING_ALL;
    private List<TestEntityBundle<Dto>> testEntityBundles;
    private NonExistingIdFinder<Id> nonExistingIdFinder;

    /**
     * @param url
     * @param crudController
     * @param nonExistingId  this can be null, if you want to set your own {@link NonExistingIdFinder} with {@link #setNonExistingIdFinder(NonExistingIdFinder)}
     */
    public UrlParamIdDtoCrudControllerSpringAdapterIT(String url, Controller crudController, Id nonExistingId) {
        super(url);
        Assertions.assertTrue(crudController.getIdIdFetchingStrategy() instanceof UrlParamIdFetchingStrategy, "Controller must have an UrlParamIdFetchingStrategy");
        this.crudController = crudController;
        this.dtoEntityClass = crudController.getDtoClass();
        this.entityIdParamKey = ((UrlParamIdFetchingStrategy) crudController.getIdIdFetchingStrategy()).getIdUrlParamKey();
        this.nonExistingIdFinder = () -> nonExistingId;
    }

    public UrlParamIdDtoCrudControllerSpringAdapterIT(Controller crudController, Id nonExistingId) {
        super();
        Assertions.assertTrue(crudController.getIdIdFetchingStrategy() instanceof UrlParamIdFetchingStrategy, "Controller must have an UrlParamIdFetchingStrategy");
        this.crudController = crudController;
        this.dtoEntityClass = crudController.getDtoClass();
        this.entityIdParamKey = ((UrlParamIdFetchingStrategy) crudController.getIdIdFetchingStrategy()).getIdUrlParamKey();
        this.nonExistingIdFinder = () -> nonExistingId;
    }


    @BeforeEach
    public void before() throws Exception {
        this.testEntityBundles = provideValidTestDtos();
    }

    /**
     * @return a list of {@link TestEntityBundle}s with valid {@link TestEntityBundle#getEntity()} according to the provided {@link io.github.vincemann.generic.crud.lib.controller.springAdapter.validationStrategy.ValidationStrategy}
     * These Dto's will be used for all tests in this class
     * The {@link TestEntityBundle#getUpdateTestBundles()} should have valid modified dto {@link UpdateTestBundle#getModifiedEntity()} get used for update tests, that should be successful
     */
    protected abstract List<TestEntityBundle<Dto>> provideValidTestDtos();

    @Test
    protected void findEntityTest() throws Exception {
        Assumptions.assumeTrue(getCrudController().getEndpointsExposureDetails().isFindEndpointExposed());
        Assumptions.assumeTrue(!testEntityBundles.isEmpty());
        for (TestEntityBundle<Dto> bundle : this.testEntityBundles) {
            TestLogUtils.logTestStart(log, "findEntity", new AbstractMap.SimpleEntry<>("testDto", bundle.getEntity()));

            Dto savedEntity = createEntityShouldSucceed(bundle.getEntity(), HttpStatus.OK);
            Dto responseDto = findEntityShouldSucceed(savedEntity.getId(), HttpStatus.OK);
            validateDtosAreDeepEqual(responseDto, savedEntity);
            bundle.getPostFindCallback().callback(responseDto);

            TestLogUtils.logTestSucceeded(log, "findEntity", new AbstractMap.SimpleEntry<>("testDto", bundle.getEntity()));
        }
    }

    @Test
    protected void findAllEntitiesTest() throws Exception {
        Assumptions.assumeTrue(getCrudController().getEndpointsExposureDetails().isFindAllEndpointExposed());
        Assumptions.assumeTrue(!testEntityBundles.isEmpty());

        //save all entities from bundles
        Collection<Dto> savedDtos = new ArrayList<>();
        for (TestEntityBundle<Dto> bundle : testEntityBundles) {
            Dto savedEntity = createEntityShouldSucceed(bundle.getEntity(), HttpStatus.OK);
            savedDtos.add(savedEntity);
        }

        //test find all
        findAllEntitiesShouldSucceed(HttpStatus.OK);
    }


    @Test
    protected void findNonExistentEntityTest() {
        Assumptions.assumeTrue(getCrudController().getEndpointsExposureDetails().isFindEndpointExposed());
        ResponseEntity<String> responseEntity = findEntity(nonExistingIdFinder.findNonExistingId(), HttpStatus.NOT_FOUND);
        Assertions.assertFalse(isBodyOfDtoType(responseEntity.getBody()));
    }

    @Test
    protected void deleteNonExistentEntityTest() {
        Assumptions.assumeTrue(getCrudController().getEndpointsExposureDetails().isDeleteEndpointExposed());
        deleteEntity(nonExistingIdFinder.findNonExistingId(), HttpStatus.NOT_FOUND);
    }

    @Test
    protected void updateEntityTest() throws Exception {
        Assumptions.assumeTrue(getCrudController().getEndpointsExposureDetails().isUpdateEndpointExposed());
        Assumptions.assumeTrue(!testEntityBundles.isEmpty());
        for (TestEntityBundle<Dto> bundle : this.testEntityBundles) {

            List<UpdateTestBundle<Dto>> updateTestBundles = bundle.getUpdateTestBundles();
            if (updateTestBundles.isEmpty()) {
                log.info("No update tests for testDto : " + bundle.getEntity());
                return;
            }
            for (UpdateTestBundle<Dto> updateTestBundle : updateTestBundles) {
                Dto modifiedDto = updateTestBundle.getModifiedEntity();
                TestLogUtils.logTestStart(log, "updateEntity", new AbstractMap.SimpleEntry<>("testDto", bundle.getEntity()), new AbstractMap.SimpleEntry<>("modifiedDto", modifiedDto));

                //save old dto
                Assertions.assertNull(bundle.getEntity().getId());
                Dto savedDtoEntity = createEntityShouldSucceed(bundle.getEntity(), HttpStatus.OK);
                modifiedDto.setId(savedDtoEntity.getId());
                //update dto
                Dto dbUpdatedDto = updateEntityShouldSucceed(savedDtoEntity, modifiedDto, HttpStatus.OK);
                updateTestBundle.getPostUpdateCallback().callback(dbUpdatedDto);
                //remove dto -> clean for next iteration
                deleteExistingEntityShouldSucceed(savedDtoEntity.getId());

                TestLogUtils.logTestSucceeded(log, "updateEntity", new AbstractMap.SimpleEntry<>("testDto", bundle.getEntity()), new AbstractMap.SimpleEntry<>("modifiedDto", modifiedDto));
            }
        }
    }

    @Test
    protected void deleteEntityTest() throws Exception {
        Assumptions.assumeTrue(getCrudController().getEndpointsExposureDetails().isDeleteEndpointExposed());
        Assumptions.assumeTrue(!testEntityBundles.isEmpty());
        for (TestEntityBundle<Dto> bundle : this.testEntityBundles) {
            TestLogUtils.logTestStart(log, "deleteEntity", new AbstractMap.SimpleEntry<>("testDto", bundle.getEntity()));


            Dto savedEntity = createEntityShouldSucceed(bundle.getEntity(), HttpStatus.OK);
            deleteExistingEntityShouldSucceed(savedEntity.getId());
            bundle.getPostDeleteCallback().callback(savedEntity);

            TestLogUtils.logTestSucceeded(log, "deleteEntity", new AbstractMap.SimpleEntry<>("testDto", bundle.getEntity()));
        }
    }

    @Test
    protected void createEntityTest() throws Exception {
        Assumptions.assumeTrue(getCrudController().getEndpointsExposureDetails().isCreateEndpointExposed());
        Assumptions.assumeTrue(!testEntityBundles.isEmpty());
        for (TestEntityBundle<Dto> bundle : this.testEntityBundles) {

            TestLogUtils.logTestStart(log, "createEntity", new AbstractMap.SimpleEntry<>("testDto", bundle.getEntity()));


            Dto savedDto = createEntityShouldSucceed(bundle.getEntity(), HttpStatus.OK);
            bundle.getPostCreateCallback().callback(savedDto);

            TestLogUtils.logTestSucceeded(log, "createEntity", new AbstractMap.SimpleEntry<>("testDto", bundle.getEntity()));
        }
    }

    protected ResponseEntity deleteExistingEntityShouldSucceed(Id id) throws NoIdException {
        //Entity muss vorher auch schon da sein
        Optional<ServiceE> serviceFoundEntityBeforeDelete = crudController.getCrudService().findById(id);
        Assertions.assertTrue(serviceFoundEntityBeforeDelete.isPresent(), "Entity to delete was not present");

        ResponseEntity responseEntity = deleteEntity(id);
        Assertions.assertTrue(responseEntity.getStatusCode().is2xxSuccessful(), "Status was : " + responseEntity.getStatusCode() + " response Body: " + responseEntity.getBody());

        //is it really deleted?
        Optional<ServiceE> serviceFoundEntity = crudController.getCrudService().findById(id);
        Assertions.assertFalse(serviceFoundEntity.isPresent());
        return responseEntity;
    }

    protected ResponseEntity deleteExistingEntityShouldFail(Id id) throws NoIdException {
        //Entity muss vorher auch schon da sein
        Optional<ServiceE> serviceFoundEntityBeforeDelete = crudController.getCrudService().findById(id);
        Assertions.assertTrue(serviceFoundEntityBeforeDelete.isPresent(), "Entity to delete was not present");

        ResponseEntity responseEntity = deleteEntity(id);
        Assertions.assertFalse(responseEntity.getStatusCode().is2xxSuccessful(), "Status was : " + responseEntity.getStatusCode() + " response Body: " + responseEntity.getBody());

        //is it really not deleted?
        Optional<ServiceE> serviceFoundEntity = crudController.getCrudService().findById(id);
        Assertions.assertTrue(serviceFoundEntity.isPresent());
        return responseEntity;
    }

    protected ResponseEntity deleteEntity(Id id) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getBaseUrl() + crudController.getDeleteMethodName())
                .queryParam(entityIdParamKey, id);
        RequestEntity requestEntity = new RequestEntity(HttpMethod.DELETE, builder.build().toUri());
        return getRestTemplate().exchange(requestEntity, Object.class);
    }

    protected ResponseEntity deleteEntity(Id id, HttpStatus httpStatus) {
        ResponseEntity responseEntity = deleteEntity(id);
        Assertions.assertEquals(httpStatus, responseEntity.getStatusCode(), "Status was : " + responseEntity.getStatusCode() + " response Body: " + responseEntity.getBody());
        return responseEntity;
    }

    protected Collection<Dto> findAllEntitiesShouldSucceed(HttpStatus httpStatus) throws DtoReadingException, EntityMappingException {
        ResponseEntity<String> responseEntity = findAllEntities(httpStatus);

        @SuppressWarnings("unchecked")
        Set<Dto> dtos = crudController.getMediaTypeStrategy().readDtosFromBody(responseEntity.getBody(), getDtoEntityClass(),Set.class);

        Set<ServiceE> allServiceEntities = crudController.getCrudService().findAll();
        Assertions.assertEquals(allServiceEntities.size(),dtos.size());
        List<Id> idsSeen = new ArrayList<>();
        for (Dto dto : dtos) {
            //prevent duplicates
            Assertions.assertFalse(idsSeen.contains(dto.getId()));
            idsSeen.add(dto.getId());
            Assertions.assertTrue(isSavedServiceEntityDeepEqual(dto));
        }
        return dtos;
    }

    /**
     * send find Entity Request to backend
     * expect {@link HttpStatus} status code to be 2xx
     * expect status code to be specified {@link HttpStatus} statuscode
     * parse Body to {@link Dto} dtoObject
     *
     * @param id
     * @return parsed {@link Dto} dtoObject
     * @throws Exception
     */
    protected Dto findEntityShouldSucceed(Id id, HttpStatus httpStatus) throws Exception {
        ResponseEntity<String> responseEntity = findEntity(id);
        Assertions.assertTrue(responseEntity.getStatusCode().is2xxSuccessful(), "Status was : " + responseEntity.getStatusCode() + " response Body: " + responseEntity.getBody());
        Assertions.assertEquals(httpStatus, responseEntity.getStatusCode());
        Dto httpResponseEntity = crudController.getMediaTypeStrategy().readDtoFromBody(responseEntity.getBody(), dtoEntityClass);
        Assertions.assertNotNull(httpResponseEntity);
        Assertions.assertTrue(isSavedServiceEntityDeepEqual(httpResponseEntity));
        return httpResponseEntity;
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
    protected ResponseEntity<String> findEntity(Id id) {
        Assertions.assertNotNull(id);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getBaseUrl() + crudController.getFindMethodName())
                .queryParam(entityIdParamKey, id);
        return getRestTemplate().getForEntity(builder.build().toUriString(), String.class);
    }

    /**
     * send find Entity Request to backend and expect specified {@link HttpStatus} status code
     *
     * @param id
     * @param httpStatus
     * @return backend Response
     */
    protected ResponseEntity<String> findEntity(Id id, HttpStatus httpStatus) {
        ResponseEntity<String> responseEntity = findEntity(id);
        Assertions.assertEquals(httpStatus, responseEntity.getStatusCode(), "Status was : " + responseEntity.getStatusCode() + " response Body: " + responseEntity.getBody());
        return responseEntity;
    }

    protected ResponseEntity<String> findAllEntities(HttpStatus httpStatus){
        ResponseEntity<String> responseEntity = findAllEntities();
        Assertions.assertEquals(httpStatus,responseEntity.getStatusCode(),"Status was : " + responseEntity.getStatusCode() + " response Body: " + responseEntity.getBody());
        return responseEntity;
    }

    protected ResponseEntity<String> findAllEntities(){
        return getRestTemplate().getForEntity(getBaseUrl()+crudController.getFindAllMethodName(),String.class);
    }



    /**
     * 1. Send create Entity Request to Backend
     * 2. Expect 2xx {@link HttpStatus} statuscode from backend
     * 3. Expect the specified {@link HttpStatus}  statuscode from backend
     * 4. assert returned Dto entity is deep equal to ServiceEntity via  {@link #isSavedServiceEntityDeepEqual(IdentifiableEntity)}
     *
     * @param dtoEntity
     * @return
     * @throws Exception
     */
    protected Dto createEntityShouldSucceed(Dto dtoEntity, HttpStatus httpStatus) throws Exception {
        Assertions.assertNull(dtoEntity.getId());
        ResponseEntity<String> responseEntity = createEntity(dtoEntity);
        Assertions.assertTrue(responseEntity.getStatusCode().is2xxSuccessful(), "Status was : " + responseEntity.getStatusCode() + " response Body: " + responseEntity.getBody());
        Assertions.assertEquals(responseEntity.getStatusCode(), httpStatus);
        Dto httpResponseEntity = crudController.getMediaTypeStrategy().readDtoFromBody(responseEntity.getBody(), dtoEntityClass);
        Assertions.assertTrue(isSavedServiceEntityDeepEqual(httpResponseEntity));
        return httpResponseEntity;
    }

    /**
     * Send create Entity Request to Backend, Response is returned
     *
     * @param dtoEntity the Dto entity that should be stored
     * @return
     */
    protected ResponseEntity<String> createEntity(Dto dtoEntity) {
        return getRestTemplate().postForEntity(getBaseUrl() + crudController.getCreateMethodName(), dtoEntity, String.class);
    }

    /**
     * Same as {@link #createEntity(IdentifiableEntity dtoEntity)} but the specified {@link HttpStatus} must be returned by Backend
     *
     * @param dtoEntity  the Dto entity that should be stored
     * @param httpStatus the expected http Status
     * @return
     */
    protected ResponseEntity<String> createEntity(Dto dtoEntity, HttpStatus httpStatus) {
        ResponseEntity<String> responseEntity = createEntity(dtoEntity);
        Assertions.assertEquals(httpStatus, responseEntity.getStatusCode());
        return responseEntity;
    }

    /**
     * @param oldEntityDto entityDto already saved that should be updated
     * @param newEntityDto entityDto that should replace/update old entity
     * @return updated entityDto returned by backend
     * @throws Exception
     */
    protected Dto updateEntityShouldSucceed(Dto oldEntityDto, Dto newEntityDto, HttpStatus httpStatus) throws Exception {
        Assertions.assertNotNull(oldEntityDto.getId());
        Assertions.assertNotNull(newEntityDto.getId());
        Assertions.assertEquals(oldEntityDto.getId(), newEntityDto.getId());
        //Entity muss vorher auch schon da sein
        Optional<ServiceE> serviceFoundEntityBeforeUpdate = crudController.getCrudService().findById(newEntityDto.getId());
        Assertions.assertTrue(serviceFoundEntityBeforeUpdate.isPresent(), "Entity to delete was not present");
        return _updateEntityShouldSucceed(oldEntityDto, newEntityDto, httpStatus);
    }


    private Dto _updateEntityShouldSucceed(Dto oldEntityDto, Dto newEntityDto, HttpStatus httpStatus) throws DtoReadingException, EntityMappingException {
        //trotzdem müssen changes vorliegen
        Assertions.assertFalse(isDeepEqual(oldEntityDto, newEntityDto));

        ResponseEntity<String> responseEntity = updateEntity(newEntityDto);
        Assertions.assertTrue(responseEntity.getStatusCode().is2xxSuccessful(), "Status was : " + responseEntity.getStatusCode());
        Assertions.assertEquals(httpStatus, responseEntity.getStatusCode());
        Dto httpResponseDto = crudController.getMediaTypeStrategy().readDtoFromBody(responseEntity.getBody(), dtoEntityClass);
        Assertions.assertNotNull(httpResponseDto);
        //response http entity must match modTestEntity
        validateDtosAreDeepEqual(httpResponseDto, newEntityDto);
        //entity fetched from vincemann.github.generic.crud.lib.service by id must match httpResponseEntity
        Assertions.assertTrue(isSavedServiceEntityDeepEqual(httpResponseDto));
        //entity fetched from vincemann.github.generic.crud.lib.service at start of test (before update) must not match httpResponseEntity (since it got updated)
        boolean deepEqual = isDeepEqual(oldEntityDto, httpResponseDto);
        Assertions.assertFalse(deepEqual, "Entites did match but must not -> entity was not updated");
        return httpResponseDto;
    }

    /**
     * The entity found by id of {@param newEntityDto}, is updated with newEntityDto
     *
     * @param newEntityDto
     * @param httpStatus
     * @return
     * @throws Exception
     */
    protected Dto updateEntityShouldSucceed(Dto newEntityDto, HttpStatus httpStatus) throws Exception {
        Assertions.assertNotNull(newEntityDto.getId());
        //Entity must be saved already
        Optional<ServiceE> serviceFoundEntityBeforeUpdate = crudController.getCrudService().findById(newEntityDto.getId());
        Assertions.assertTrue(serviceFoundEntityBeforeUpdate.isPresent(), "Entity to delete was not present");
        //there must be changes
        Dto oldEntityDtoFromService = getCrudController().getDtoMapper().mapServiceEntityToDto(serviceFoundEntityBeforeUpdate.get(), getDtoEntityClass());

        return _updateEntityShouldSucceed(oldEntityDtoFromService, newEntityDto, httpStatus);
    }

    protected void updateEntityShouldFail(Dto oldEntity, Dto newEntity, HttpStatus httpStatus) throws Exception {
        Assertions.assertNotNull(oldEntity.getId());
        Assertions.assertNotNull(newEntity.getId());
        Assertions.assertEquals(oldEntity.getId(), newEntity.getId());
        //Entity muss vorher auch schon da sein
        Optional<ServiceE> serviceFoundEntityBeforeUpdate = crudController.getCrudService().findById(newEntity.getId());
        Assertions.assertTrue(serviceFoundEntityBeforeUpdate.isPresent(), "Entity to delete was not present");
        //id muss gleich sein

        //trotzdem müssen changes vorliegen
        Assertions.assertFalse(isDeepEqual(oldEntity, newEntity));

        ResponseEntity<String> responseEntity = updateEntity(newEntity);
        Assertions.assertFalse(responseEntity.getStatusCode().is2xxSuccessful(), "Status was : " + responseEntity.getStatusCode());
        Assertions.assertEquals(httpStatus, responseEntity.getStatusCode());

        //entity aus Service muss immernoch die gleiche sein wie vorher
        Assertions.assertTrue(isSavedServiceEntityDeepEqual(oldEntity));
    }

    /**
     * Send update Entity Request to Backend
     *
     * @param newEntity updated entityDto
     * @return backend Response
     */
    private ResponseEntity<String> updateEntity(Dto newEntity) {
        Assertions.assertNotNull(newEntity.getId());
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getBaseUrl() + crudController.getUpdateMethodName())
                .queryParam(entityIdParamKey, newEntity.getId());

        RequestEntity<Dto> requestEntity = new RequestEntity<Dto>(newEntity, HttpMethod.PUT, builder.build().toUri());
        return getRestTemplate().exchange(requestEntity, String.class);
    }

    /**
     * Send update Entity Request to Backend and expect specified {@link HttpStatus} status code
     *
     * @param newEntity  updated entityDto
     * @param httpStatus expected status code
     * @return backend Response
     */
    protected ResponseEntity<String> updateEntity(Dto newEntity, HttpStatus httpStatus) {
        Assertions.assertNotNull(newEntity.getId());
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getBaseUrl() + crudController.getUpdateMethodName())
                .queryParam(entityIdParamKey, newEntity.getId());

        RequestEntity<Dto> requestEntity = new RequestEntity<Dto>(newEntity, HttpMethod.PUT, builder.build().toUri());
        ResponseEntity<String> responseEntity = getRestTemplate().exchange(requestEntity, String.class);
        Assertions.assertEquals(httpStatus, responseEntity.getStatusCode());
        return responseEntity;
    }


    /**
     * 1. Map DtoEntity to ServiceEntity = RequestServiceEntity
     * 2. Fetch ServiceEntity from Service (ultimately from the persistence layer) by Id = dbServiceEntity
     * 3. Validate that RequestServiceEntity and dbServiceEntity are deep equal via {@link BeanUtils#isDeepEqual(Object, Object)}
     *
     * @param httpResponseEntity the Dto entity returned by Backend after http request
     * @return
     * @throws NoIdException
     */
    protected boolean isSavedServiceEntityDeepEqual(Dto httpResponseEntity) throws EntityMappingException {
        try {
            ServiceE serviceHttpResponseEntity = crudController.getDtoMapper().mapDtoToServiceEntity(httpResponseEntity, crudController.getServiceEntityClass());
            Assertions.assertNotNull(serviceHttpResponseEntity);
            Id httpResponseEntityId = serviceHttpResponseEntity.getId();
            Assertions.assertNotNull(httpResponseEntityId);

            //Compare httpEntity with saved Entity From Service
            Optional<ServiceE> entityFromService = crudController.getCrudService().findById(httpResponseEntityId);
            Assertions.assertTrue(entityFromService.isPresent());
            return isDeepEqual(entityFromService.get(), serviceHttpResponseEntity);
        } catch (NoIdException e) {
            throw new EntityMappingException(e);
        }

    }

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
     * checks whether specified max amount of entities is exeeded : {@link UrlParamIdDtoCrudControllerSpringAdapterIT#setSafetyCheckMaxAmountEntitiesInRepo(int)}
     */
    @AfterEach
    public void tearDown() throws Exception {
        Set<ServiceE> allEntities = crudController.getCrudService().findAll();
        if (allEntities.size() >= safetyCheckMaxAmountEntitiesInRepo) {
            throw new RuntimeException("max amount of entities in repo exceeded, tried to delete " + allEntities.size() + " entites. Do you have the wrong datasource?");
        }
        for (ServiceE entityToDelete : allEntities) {
            crudController.getCrudService().deleteById(entityToDelete.getId());
        }
        Set<ServiceE> allEntitiesAfterDeleting = crudController.getCrudService().findAll();
        Assertions.assertTrue(allEntitiesAfterDeleting.isEmpty());
    }

    protected boolean isBodyOfDtoType(String body) {
        return getCrudController().getMediaTypeStrategy().isBodyOfGivenType(body, getDtoEntityClass());
    }

    /**
     * use with caution
     *
     * @param safetyCheckMaxAmountEntitiesInRepo
     */
    protected void setSafetyCheckMaxAmountEntitiesInRepo(int safetyCheckMaxAmountEntitiesInRepo) {
        this.safetyCheckMaxAmountEntitiesInRepo = safetyCheckMaxAmountEntitiesInRepo;
    }

    private String getBaseUrl() {
        return getUrlWithPort() + "/" + crudController.getEntityNameInUrl() + "/";
    }

    protected Controller getCrudController() {
        return crudController;
    }


    public Class<Dto> getDtoEntityClass() {
        return dtoEntityClass;
    }

    public List<TestEntityBundle<Dto>> getTestEntityBundles() {
        return testEntityBundles;
    }

    public NonExistingIdFinder<Id> getNonExistingIdFinder() {
        return nonExistingIdFinder;
    }

    public void setNonExistingIdFinder(NonExistingIdFinder<Id> nonExistingIdFinder) {
        this.nonExistingIdFinder = nonExistingIdFinder;
    }
}