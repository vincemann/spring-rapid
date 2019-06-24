package vincemann.github.generic.crud.lib.test.controller.springAdapter;

import vincemann.github.generic.crud.lib.controller.exception.EntityMappingException;
import vincemann.github.generic.crud.lib.controller.springAdapter.DTOCrudControllerSpringAdatper;
import vincemann.github.generic.crud.lib.controller.springAdapter.idFetchingStrategy.LongUrlParamIdFetchingStrategy;
import vincemann.github.generic.crud.lib.controller.springAdapter.idFetchingStrategy.UrlParamIdFetchingStrategy;
import vincemann.github.generic.crud.lib.model.IdentifiableEntity;
import vincemann.github.generic.crud.lib.service.CrudService;
import vincemann.github.generic.crud.lib.service.exception.NoIdException;
import junit.framework.AssertionFailedError;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.reflectionassert.ReflectionComparatorMode;
import vincemann.github.generic.crud.lib.test.IntegrationTest;
import vincemann.github.generic.crud.lib.util.BeanUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Integration Test for a {@link DTOCrudControllerSpringAdatper} with {@link vincemann.github.generic.crud.lib.controller.springAdapter.idFetchingStrategy.UrlParamIdFetchingStrategy}
 * Removes all Entries in Database, that is connected to the {@link CrudService} after each test.
 *
 * @param <ServiceE>
 * @param <DTO>
 * @param <Service>
 * @param <Controller>
 * @param <Id>
 */
public abstract class UrlParamIdDTOCrudControllerSpringAdapterIT<ServiceE extends IdentifiableEntity<Id>, DTO extends IdentifiableEntity<Id>, Service extends CrudService<ServiceE, Id>, Controller extends DTOCrudControllerSpringAdatper<ServiceE, DTO, Id, Service>, Id extends Serializable> extends IntegrationTest {

    /**
     * This is a security feature.
     * If there are more entities in the database than this value, the database wont be cleared after the test, and the test will fail.
     */
    private static final int MAX_AMOUNT_ENTITIES_IN_REPO_WHEN_DELETING_ALL = 200;

    private final Controller crudController;
    private final Class<DTO> dtoEntityClass;
    private final String entityIdParamKey;
    private final Id nonExistingId;
    private int safetyCheckMaxAmountEntitiesInRepo = MAX_AMOUNT_ENTITIES_IN_REPO_WHEN_DELETING_ALL;
    private List<DTO> testDTOs;

    public UrlParamIdDTOCrudControllerSpringAdapterIT(String url, Controller crudController, Id nonExistingId) {
        super(url);
        Assertions.assertTrue(crudController.getIdIdFetchingStrategy() instanceof LongUrlParamIdFetchingStrategy, "Controller must have LongUrlParamIdFetchingStrategy");
        this.crudController = crudController;
        this.dtoEntityClass = crudController.getDtoClass();
        this.entityIdParamKey = ((UrlParamIdFetchingStrategy) crudController.getIdIdFetchingStrategy()).getIdUrlParamKey();
        this.nonExistingId = nonExistingId;
    }

    public UrlParamIdDTOCrudControllerSpringAdapterIT(Controller crudController, Id nonExistingId) {
        super();
        Assertions.assertTrue(crudController.getIdIdFetchingStrategy() instanceof LongUrlParamIdFetchingStrategy, "Controller must have LongUrlParamIdFetchingStrategy");
        this.crudController = crudController;
        this.dtoEntityClass = crudController.getDtoClass();
        this.entityIdParamKey = ((UrlParamIdFetchingStrategy) crudController.getIdIdFetchingStrategy()).getIdUrlParamKey();
        this.nonExistingId = nonExistingId;
    }

    @BeforeEach
    public void before() throws Exception {
        this.testDTOs = provideValidTestDTOs();
    }

    protected DTO mapServiceEntityToDTO(ServiceE serviceEntity) {
        try {
            return getCrudController().getServiceEntityToDTOMapper().map(serviceEntity);
        } catch (EntityMappingException e) {
            throw new RuntimeException(e);
        }
    }



    protected abstract List<DTO> provideValidTestDTOs();

    @Test
    protected void findEntityTest() throws Exception {
        for (DTO testEntityDTO : this.testDTOs) {
            System.err.println("findEntityTest with testDTO: " + testEntityDTO);
            DTO savedEntity = createEntityShouldSucceed(testEntityDTO, HttpStatus.OK);
            DTO responseDTO = findEntityShouldSucceed(savedEntity.getId(), HttpStatus.OK);
            validateDTOsAreDeepEqual(responseDTO, savedEntity);
            System.err.println("Test succeeded");
        }
    }

    @Test
    protected void findNonExistentEntityTest() {
        ResponseEntity<String> responseEntity = findEntity(nonExistingId, HttpStatus.NOT_FOUND);
        Assertions.assertFalse(isBodyOfDtoType(responseEntity.getBody()));
    }

    @Test
    protected void deleteNonExistentEntityTest() {
        deleteEntity(nonExistingId, HttpStatus.NOT_FOUND);
    }

    protected abstract void modifyTestEntity(DTO testEntityDTO);

    @Test
    protected void updateEntityTest() throws Exception {
        for (DTO testEntityDTO : this.testDTOs) {
            System.err.println("updateEntityTest with testDTO: " + testEntityDTO);
            Assertions.assertNull(testEntityDTO.getId());
            DTO savedDTOEntity = createEntityShouldSucceed(testEntityDTO, HttpStatus.OK);
            //they have to differ in other attribute values than id
            DTO modTestEntityDTO = BeanUtils.createDeepCopy(savedDTOEntity, dtoEntityClass);
            modifyTestEntity(modTestEntityDTO);
            updateEntityShouldSucceed(savedDTOEntity, modTestEntityDTO, HttpStatus.OK);
            System.err.println("Test succeeded");
        }
    }

    @Test
    protected void deleteEntityTest() throws Exception {
        for (DTO testEntityDTO : this.testDTOs) {
            System.err.println("deleteEntityTest with testDTO: " + testEntityDTO);
            DTO savedEntity = createEntityShouldSucceed(testEntityDTO, HttpStatus.OK);
            deleteExistingEntityShouldSucceed(savedEntity.getId());
            System.err.println("Test succeeded");
        }
    }

    @Test
    protected void createEntityTest() throws Exception {
        for (DTO testEntityDTO : this.testDTOs) {
            System.err.println("createEntityTest with testDTO: " + testEntityDTO);
            createEntityShouldSucceed(testEntityDTO, HttpStatus.OK);
            System.err.println("Test succeeded");
        }
    }

    protected ResponseEntity deleteExistingEntityShouldSucceed(Id id) throws NoIdException {
        //Entity muss vorher auch schon da sein
        Optional<ServiceE> serviceFoundEntityBeforeDelete = crudController.getCrudService().findById(id);
        Assertions.assertTrue(serviceFoundEntityBeforeDelete.isPresent(), "Entity to delete was not present");

        ResponseEntity responseEntity = deleteEntity(id);
        Assertions.assertTrue(responseEntity.getStatusCode().is2xxSuccessful(), "Status was : " + responseEntity.getStatusCode());

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
        Assertions.assertFalse(responseEntity.getStatusCode().is2xxSuccessful(), "Status was : " + responseEntity.getStatusCode());

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
        Assertions.assertEquals(httpStatus, responseEntity.getStatusCode(), "Status was : " + responseEntity.getStatusCode());
        return responseEntity;
    }

    /**
     * send find Entity Request to backend
     * expect {@link HttpStatus} status code to be 2xx
     * expect status code to be specified {@link HttpStatus} statuscode
     * parse Body to {@link DTO} dtoObject
     *
     * @param id
     * @return parsed {@link DTO} dtoObject
     * @throws Exception
     */
    protected DTO findEntityShouldSucceed(Id id, HttpStatus httpStatus) throws Exception {
        ResponseEntity<String> responseEntity = findEntity(id);
        Assertions.assertTrue(responseEntity.getStatusCode().is2xxSuccessful(), "Status was : " + responseEntity.getStatusCode());
        Assertions.assertEquals(httpStatus, responseEntity.getStatusCode());
        DTO httpResponseEntity = crudController.getMediaTypeStrategy().readDTOFromBody(responseEntity.getBody(), dtoEntityClass);
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
        Assertions.assertEquals(httpStatus, responseEntity.getStatusCode(), "Status was : " + responseEntity.getStatusCode());
        return responseEntity;
    }


    /**
     * 1. Send create Entity Request to Backend
     * 2. Expect 2xx {@link HttpStatus} statuscode from backend
     * 3. Expect the specified {@link HttpStatus}  statuscode from backend
     * 4. assert returned DTO entity is deep equal to ServiceEntity via  {@link #isSavedServiceEntityDeepEqual(IdentifiableEntity)}
     *
     * @param dtoEntity
     * @return
     * @throws Exception
     */
    protected DTO createEntityShouldSucceed(DTO dtoEntity, HttpStatus httpStatus) throws Exception {
        Assertions.assertNull(dtoEntity.getId());
        ResponseEntity<String> responseEntity = createEntity(dtoEntity);
        Assertions.assertTrue(responseEntity.getStatusCode().is2xxSuccessful(), "Status was : " + responseEntity.getStatusCode());
        Assertions.assertEquals(responseEntity.getStatusCode(), httpStatus);
        DTO httpResponseEntity = crudController.getMediaTypeStrategy().readDTOFromBody(responseEntity.getBody(), dtoEntityClass);
        Assertions.assertTrue(isSavedServiceEntityDeepEqual(httpResponseEntity));
        return httpResponseEntity;
    }

    /**
     * Send create Entity Request to Backend, Response is returned
     *
     * @param dtoEntity the DTO entity that should be stored
     * @return
     */
    protected ResponseEntity<String> createEntity(DTO dtoEntity) {
        return getRestTemplate().postForEntity(getBaseUrl() + crudController.getCreateMethodName(), dtoEntity, String.class);
    }

    /**
     * Same as {@link #createEntity(IdentifiableEntity dtoEntity)} but the specified {@link HttpStatus} must be returned by Backend
     *
     * @param dtoEntity  the DTO entity that should be stored
     * @param httpStatus the expected http Status
     * @return
     */
    protected ResponseEntity<String> createEntity(DTO dtoEntity, HttpStatus httpStatus) {
        ResponseEntity<String> responseEntity = createEntity(dtoEntity);
        Assertions.assertEquals(httpStatus, responseEntity.getStatusCode());
        return responseEntity;
    }

    /**
     * 1. expect oldEntityDTO and newEntityDTO to not be deepEqual {@link #isDeepEqual(Object, Object)}
     * 2. expect oldEntityDTO and newEntityDTO to have same id
     * 3. expect oldEntityDTO to be already persisted -> can be found by id
     * 4. make update request to backend
     * 5. expect {@link HttpStatus} response status code to be 2xx
     * 6. expect {@link HttpStatus} response status code to be specified {@link HttpStatus} statuscode
     * 7. returned EntityDTO by backend must be deepEqual to newEntityDTO and persisted entityDTO with the id of newEntityDTO
     * 8. oldEntityDTO must differ from returned EntityDTO by backend
     *
     * @param oldEntityDTO entityDTO already saved that should be updated
     * @param newEntityDTO entityDTO that should replace/update old entity
     * @return updated entityDTO returned by backend
     * @throws Exception
     */
    protected DTO updateEntityShouldSucceed(DTO oldEntityDTO, DTO newEntityDTO, HttpStatus httpStatus) throws Exception {
        Assertions.assertNotNull(oldEntityDTO.getId());
        Assertions.assertNotNull(newEntityDTO.getId());
        Assertions.assertEquals(oldEntityDTO.getId(), newEntityDTO.getId());
        //Entity muss vorher auch schon da sein
        Optional<ServiceE> serviceFoundEntityBeforeUpdate = crudController.getCrudService().findById(newEntityDTO.getId());
        Assertions.assertTrue(serviceFoundEntityBeforeUpdate.isPresent(), "Entity to delete was not present");
        //trotzdem müssen changes vorliegen
        Assertions.assertFalse(isDeepEqual(oldEntityDTO, newEntityDTO));

        ResponseEntity<String> responseEntity = updateEntity(newEntityDTO);
        Assertions.assertTrue(responseEntity.getStatusCode().is2xxSuccessful(), "Status was : " + responseEntity.getStatusCode());
        Assertions.assertEquals(httpStatus, responseEntity.getStatusCode());
        DTO httpResponseDTO = crudController.getMediaTypeStrategy().readDTOFromBody(responseEntity.getBody(), dtoEntityClass);
        Assertions.assertNotNull(httpResponseDTO);
        //response http entity must match modTestEntity
        validateDTOsAreDeepEqual(httpResponseDTO, newEntityDTO);
        //entity fetched from vincemann.github.generic.crud.lib.service by id must match httpResponseEntity
        Assertions.assertTrue(isSavedServiceEntityDeepEqual(httpResponseDTO));
        //entity fetched from vincemann.github.generic.crud.lib.service at start of test (before update) must not match httpResponseEntity (since it got updated)
        boolean deepEqual = isDeepEqual(oldEntityDTO, httpResponseDTO);
        Assertions.assertFalse(deepEqual, "Entites did match but must not -> entity was not updated");
        return httpResponseDTO;
    }

    protected void updateEntityShouldFail(DTO oldEntity, DTO newEntity, HttpStatus httpStatus) throws Exception {
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
     * @param newEntity updated entityDTO
     * @return backend Response
     */
    private ResponseEntity<String> updateEntity(DTO newEntity) {
        Assertions.assertNotNull(newEntity.getId());
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getBaseUrl() + crudController.getUpdateMethodName())
                .queryParam(entityIdParamKey, newEntity.getId());

        RequestEntity<DTO> requestEntity = new RequestEntity<DTO>(newEntity, HttpMethod.PUT, builder.build().toUri());
        return getRestTemplate().exchange(requestEntity, String.class);
    }

    /**
     * Send update Entity Request to Backend and expect specified {@link HttpStatus} status code
     *
     * @param newEntity  updated entityDTO
     * @param httpStatus expected status code
     * @return backend Response
     */
    private ResponseEntity<String> updateEntity(DTO newEntity, HttpStatus httpStatus) {
        Assertions.assertNotNull(newEntity.getId());
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getBaseUrl() + crudController.getUpdateMethodName())
                .queryParam(entityIdParamKey, newEntity.getId());

        RequestEntity<DTO> requestEntity = new RequestEntity<DTO>(newEntity, HttpMethod.PUT, builder.build().toUri());
        ResponseEntity<String> responseEntity = getRestTemplate().exchange(requestEntity, String.class);
        Assertions.assertEquals(httpStatus, responseEntity.getStatusCode());
        return responseEntity;
    }


    /**
     * 1. Map DTOEntity to ServiceEntity = RequestServiceEntity
     * 2. Fetch ServiceEntity from Service (ultimately from the persistence layer) by Id = dbServiceEntity
     * 3. Validate that RequestServiceEntity and dbServiceEntity are deep equal via {@link #isDeepEqual(Object, Object)}
     *
     * @param httpResponseEntity the DTO entity returned by Backend after http request
     * @return
     * @throws EntityMappingException
     * @throws NoIdException
     */
    protected boolean isSavedServiceEntityDeepEqual(DTO httpResponseEntity) throws EntityMappingException, NoIdException {
        ServiceE serviceHttpResponseEntity = crudController.getDtoToServiceEntityMapper().map(httpResponseEntity);
        Assertions.assertNotNull(serviceHttpResponseEntity);
        Id httpResponseEntityId = serviceHttpResponseEntity.getId();
        Assertions.assertNotNull(httpResponseEntityId);

        //Compare httpEntity with saved Entity From Service
        Optional<ServiceE> entityFromService = crudController.getCrudService().findById(httpResponseEntityId);
        Assertions.assertTrue(entityFromService.isPresent());
        return isDeepEqual(entityFromService.get(), serviceHttpResponseEntity);
    }

    /**
     * see {@link #isDeepEqual(Object, Object)}
     *
     * @param httpResponseEntity
     * @param prevSavedEntity
     */
    protected void validateDTOsAreDeepEqual(DTO httpResponseEntity, DTO prevSavedEntity) {
        boolean deepEqual = areDTOsDeepEqual(httpResponseEntity, prevSavedEntity);
        Assertions.assertTrue(deepEqual, "Entities did not match");
    }

    /**
     * see {@link #isDeepEqual(Object, Object)}
     *
     * @param httpResponseEntity
     * @param prevSavedEntity
     * @return
     */
    protected boolean areDTOsDeepEqual(DTO httpResponseEntity, DTO prevSavedEntity) {
        return isDeepEqual(httpResponseEntity, prevSavedEntity);
    }


    /**
     * removes all Entites from given {@link Service}
     * checks whether specified max amount of entities is exeeded : {@link UrlParamIdDTOCrudControllerSpringAdapterIT#setSafetyCheckMaxAmountEntitiesInRepo(int)}
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


    public Class<DTO> getDtoEntityClass() {
        return dtoEntityClass;
    }

    public List<DTO> getTestDTOs() {
        return testDTOs;
    }

    /**
     * checks whether two Objects are equal by properties
     * -> equals method of object is not used, but property values are compared reflectively
     * order in Collections is ignored
     *
     * @param o1
     * @param o2
     * @return
     */
    protected boolean isDeepEqual(Object o1, Object o2) {
        try {
            //Reihenfolge in Lists wird hier ignored
            ReflectionAssert.assertReflectionEquals(o1, o2, ReflectionComparatorMode.LENIENT_ORDER);
            return true;
        } catch (AssertionFailedError e) {
            e.printStackTrace();
            return false;
        }

    }

}