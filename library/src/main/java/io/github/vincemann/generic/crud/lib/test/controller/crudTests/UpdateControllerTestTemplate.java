package io.github.vincemann.generic.crud.lib.test.controller.crudTests;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.ControllerIntegrationTest;
import io.github.vincemann.generic.crud.lib.test.controller.crudTests.config.UpdateControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.crudTests.abs.AbstractControllerTestTemplate;
import io.github.vincemann.generic.crud.lib.test.controller.crudTests.config.abs.ControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.crudTests.config.factory.abs.AbstractControllerTestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.controller.requestEntityFactory.RequestEntityFactory;
import io.github.vincemann.generic.crud.lib.test.controller.requestEntityFactory.UpdateTest;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.util.Optional;

@Setter
@Getter
@TestComponent
public class UpdateControllerTestTemplate<E extends IdentifiableEntity<Id>, Id extends Serializable>
        extends AbstractControllerTestTemplate<E, Id,UpdateControllerTestConfiguration<E,Id>> {

    private AbstractControllerTestConfigurationFactory<E, Id, UpdateControllerTestConfiguration<E, Id>, UpdateControllerTestConfiguration<E, Id>> testConfigFactory;


    public UpdateControllerTestTemplate(@UpdateTest RequestEntityFactory<Id, UpdateControllerTestConfiguration<E, Id>> requestEntityFactory, @UpdateTest AbstractControllerTestConfigurationFactory<E, Id, UpdateControllerTestConfiguration<E, Id>, UpdateControllerTestConfiguration<E, Id>> testConfigFactory) {
        super(requestEntityFactory);
        this.testConfigFactory = testConfigFactory;
    }

    public <Dto extends IdentifiableEntity<Id>> Dto updateEntity_ShouldSucceed(IdentifiableEntity<Id> updateRequestDto) throws Exception {
        return updateEntity_ShouldSucceed(updateRequestDto, testConfigFactory.createDefaultSuccessfulConfig());
    }


    public <Dto extends IdentifiableEntity<Id>> Dto updateEntity_ShouldSucceed(E saveBefore, IdentifiableEntity<Id> updateRequest) throws Exception {
        E savedEntityToUpdate = getTestContext().getTestService().save(saveBefore);
        updateRequest.setId(savedEntityToUpdate.getId());
        return updateEntity_ShouldSucceed(updateRequest, testConfigFactory.createDefaultSuccessfulConfig());
    }

    public <Dto extends IdentifiableEntity<Id>> Dto updateEntity_ShouldSucceed(E saveBefore, IdentifiableEntity<Id> updateRequest, ControllerTestConfiguration<Id>... modifications) throws Exception {
        E savedEntityToUpdate = getTestContext().getTestService().save(saveBefore);
        updateRequest.setId(savedEntityToUpdate.getId());
        return updateEntity_ShouldSucceed(updateRequest, modifications);
    }


    public <Dto extends IdentifiableEntity<Id>> Dto updateEntity_ShouldSucceed(IdentifiableEntity<Id> updateRequestDto, ControllerTestConfiguration<Id>... modifications) throws Exception {
        UpdateControllerTestConfiguration<E, Id> config = testConfigFactory.createMergedSuccessfulConfig(modifications);

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
        return updateEntity_ShouldFail(updateRequestDto, testConfigFactory.createDefaultFailedConfig());
    }

    public ResponseEntity<String> updateEntity_ShouldFail(IdentifiableEntity<Id> updateRequestDto, ControllerTestConfiguration<Id>... modifications) throws Exception {
        UpdateControllerTestConfiguration<E, Id> config = testConfigFactory.createMergedFailedConfig(modifications);

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
        return updateEntity_ShouldFail(updateRequest, testConfigFactory.createDefaultFailedConfig());
    }

    public ResponseEntity<String> updateEntity_ShouldFail(E entityToUpdate, IdentifiableEntity<Id> updateRequest, ControllerTestConfiguration<Id>... modifications) throws Exception {
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
    public ResponseEntity<String> updateEntity(IdentifiableEntity<Id> updateRequestDto, UpdateControllerTestConfiguration<E,Id> config) {
        Assertions.assertNotNull(updateRequestDto.getId());
        return sendRequest(getRequestEntityFactory().create(config, updateRequestDto, updateRequestDto.getId()));
    }
}
