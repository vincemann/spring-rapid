package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.ControllerIntegrationTest;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.ControllerTestMethod;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.abs.AbstractControllerTest;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.UpdateControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.abs.ControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.factory.abs.AbstractControllerTestConfigurationFactory;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.util.Optional;

@Setter
@Getter
public class UpdateControllerTest<E extends IdentifiableEntity<Id>, Id extends Serializable>
        extends AbstractControllerTest<E, Id> {

    private AbstractControllerTestConfigurationFactory<E, Id, UpdateControllerTestConfiguration<E, Id>, UpdateControllerTestConfiguration<E, Id>> testConfigFactory;


    @Builder
    public UpdateControllerTest(ControllerIntegrationTest<E, Id> testContext, AbstractControllerTestConfigurationFactory<E, Id, UpdateControllerTestConfiguration<E, Id>, UpdateControllerTestConfiguration<E, Id>> testConfigFactory) {
        super(testContext);
        this.testConfigFactory = testConfigFactory;
    }


    public <Dto extends IdentifiableEntity<Id>> Dto updateEntity_ShouldSucceed(IdentifiableEntity<Id> updateRequestDto) throws Exception {
        return updateEntity_ShouldSucceed(updateRequestDto, testConfigFactory.createSuccessfulDefaultConfig());
    }


    public <Dto extends IdentifiableEntity<Id>> Dto updateEntity_ShouldSucceed(E saveBefore, IdentifiableEntity<Id> updateRequest) throws Exception {
        E savedEntityToUpdate = getTestContext().getTestService().save(saveBefore);
        updateRequest.setId(savedEntityToUpdate.getId());
        return updateEntity_ShouldSucceed(updateRequest, testConfigFactory.createSuccessfulDefaultConfig());
    }

    public <Dto extends IdentifiableEntity<Id>> Dto updateEntity_ShouldSucceed(E saveBefore, IdentifiableEntity<Id> updateRequest, ControllerTestConfiguration<Id> modifications) throws Exception {
        E savedEntityToUpdate = getTestContext().getTestService().save(saveBefore);
        updateRequest.setId(savedEntityToUpdate.getId());
        return updateEntity_ShouldSucceed(updateRequest, testConfigFactory.createSuccessfulMergedConfig(modifications));
    }


    public <Dto extends IdentifiableEntity<Id>> Dto updateEntity_ShouldSucceed(IdentifiableEntity<Id> updateRequestDto, ControllerTestConfiguration<Id> modifications) throws Exception {
        UpdateControllerTestConfiguration<E, Id> config = testConfigFactory.createSuccessfulMergedConfig(modifications);

        Assertions.assertNotNull(updateRequestDto.getId());
        Assertions.assertTrue(mappingContext().getPartialUpdateRequestDtoClass().equals(updateRequestDto.getClass()) || mappingContext().getFullUpdateRequestDtoClass().equals(updateRequestDto.getClass()));


        //Entity to update must be saved already
        Optional<E> entityBeforeUpdate = getTestContext().getTestService().findById(updateRequestDto.getId());
        Assertions.assertTrue(entityBeforeUpdate.isPresent(), "Entity to update was not present");
        //update request
        ResponseEntity<String> responseEntity = updateEntity(updateRequestDto, config);
        Assertions.assertEquals(config.getExpectedHttpStatus(), responseEntity.getStatusCode(), responseEntity.getBody());
        //validate response Dto
        IdentifiableEntity<Id> responseDto = readFromBody(responseEntity.getBody(), mappingContext().getUpdateReturnDtoClass());
        Assertions.assertNotNull(responseDto);

        E entityAfterUpdate = getTestContext().getTestService().findById(responseDto.getId()).get();
        config.getPostUpdateCallback().callback(entityAfterUpdate);
        Assertions.assertEquals(mappingContext().getUpdateReturnDtoClass(), responseDto.getClass());
        return (Dto) responseDto;
    }

    public ResponseEntity<String> updateEntity_ShouldFail(IdentifiableEntity<Id> updateRequestDto) throws Exception {
        return updateEntity_ShouldFail(updateRequestDto, testConfigFactory.createFailedDefaultConfig());
    }

    public ResponseEntity<String> updateEntity_ShouldFail(IdentifiableEntity<Id> updateRequestDto, ControllerTestConfiguration<Id> modifications) throws Exception {
        UpdateControllerTestConfiguration<E, Id> config = testConfigFactory.createFailedMergedConfig(modifications);

        Assertions.assertNotNull(updateRequestDto.getId());
        //Entity muss vorher auch schon da sein
        Optional<E> entityBeforeUpdate = getTestContext().getTestService().findById(updateRequestDto.getId());
        Assertions.assertTrue(entityBeforeUpdate.isPresent(), "Entity to update was not present");

        ResponseEntity<String> responseEntity = updateEntity(updateRequestDto, config);
        Assertions.assertEquals(config.getExpectedHttpStatus(), responseEntity.getStatusCode(), responseEntity.getBody());
        return responseEntity;
    }


    public ResponseEntity<String> updateEntity_ShouldFail(E entityToUpdate, IdentifiableEntity<Id> updateRequest) throws Exception {
        E savedEntityToUpdate = getTestContext().getTestService().save(entityToUpdate);
        updateRequest.setId(savedEntityToUpdate.getId());
        return updateEntity_ShouldFail(updateRequest, testConfigFactory.createFailedDefaultConfig());
    }

    public ResponseEntity<String> updateEntity_ShouldFail(E entityToUpdate, IdentifiableEntity<Id> updateRequest, ControllerTestConfiguration<Id> modifications) throws Exception {
        E savedEntityToUpdate = getTestContext().getTestService().save(entityToUpdate);
        updateRequest.setId(savedEntityToUpdate.getId());
        return updateEntity_ShouldFail(updateRequest, modifications);
    }


    /**
     * Send update Entity Request to Backend
     *
     * @param updateRequestDto updated entityDto
     * @return backend Response
     */
    public ResponseEntity<String> updateEntity(IdentifiableEntity<Id> updateRequestDto, ControllerTestConfiguration<Id> config) {
        Assertions.assertNotNull(updateRequestDto.getId());
        return sendRequest(getTestContext().getRequestEntityFactory().create(config, updateRequestDto, updateRequestDto.getId(), ControllerTestMethod.UPDATE));
    }
}
