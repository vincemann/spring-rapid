package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.ControllerIntegrationTestContext;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.abs.AbstractControllerTest;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.FailedUpdateControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.SuccessfulUpdateControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.abs.ControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.factory.abs.AbstractControllerTestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.requestEntityFactory.RequestEntityFactory;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.util.Optional;

public class UpdateControllerTest<E extends IdentifiableEntity<Id>, Id extends Serializable>
        extends AbstractControllerTest<E,Id> {

    private AbstractControllerTestConfigurationFactory<E,Id, SuccessfulUpdateControllerTestConfiguration<E,Id>> successfulTestConfigFactory;
    private AbstractControllerTestConfigurationFactory<E,Id, FailedUpdateControllerTestConfiguration<Id>> failedTestConfigFactory;

    public UpdateControllerTest(ControllerIntegrationTestContext<E, Id> rootContext, RequestEntityFactory<Id> requestEntityFactory, AbstractControllerTestConfigurationFactory<E, Id, SuccessfulUpdateControllerTestConfiguration<E, Id>> successfulTestConfigFactory, AbstractControllerTestConfigurationFactory<E, Id, FailedUpdateControllerTestConfiguration<Id>> failedTestConfigFactory) {
        super(rootContext, requestEntityFactory);
        this.successfulTestConfigFactory = successfulTestConfigFactory;
        this.failedTestConfigFactory = failedTestConfigFactory;
    }



    protected <Dto extends IdentifiableEntity<Id>> Dto updateEntity_ShouldSucceed(IdentifiableEntity<Id> updateRequestDto) throws Exception {
        return updateEntity_ShouldSucceed(updateRequestDto, successfulTestConfigFactory.createDefaultConfig());
    }


    protected <Dto extends IdentifiableEntity<Id>> Dto updateEntity_ShouldSucceed(E saveBefore, IdentifiableEntity<Id> updateRequest) throws Exception {
        E savedEntityToUpdate = getTestContext().getTestService().save(saveBefore);
        updateRequest.setId(savedEntityToUpdate.getId());
        return updateEntity_ShouldSucceed(updateRequest,successfulTestConfigFactory.createDefaultConfig());
    }


    protected <Dto extends IdentifiableEntity<Id>> Dto updateEntity_ShouldSucceed(IdentifiableEntity<Id> updateRequestDto, SuccessfulUpdateControllerTestConfiguration<E,Id> modifications) throws Exception {
        SuccessfulUpdateControllerTestConfiguration<E, Id> config = successfulTestConfigFactory.createMergedConfig(modifications);

        Assertions.assertNotNull(updateRequestDto.getId());
        Assertions.assertEquals(mappingContext().getUpdateRequestDtoClass(),updateRequestDto.getClass());

        //Entity to update must be saved already
        Optional<E> entityBeforeUpdate = getTestContext().getTestService().findById(updateRequestDto.getId());
        Assertions.assertTrue(entityBeforeUpdate.isPresent(), "Entity to update was not present");
        //update request
        ResponseEntity<String> responseEntity = updateEntity(updateRequestDto, config);
        Assertions.assertEquals(config.getExpectedHttpStatus(), responseEntity.getStatusCode(),responseEntity.getBody());
        //validate response Dto
        IdentifiableEntity<Id> responseDto = readFromBody(responseEntity.getBody(), mappingContext().getUpdateReturnDtoClass());
        Assertions.assertNotNull(responseDto);

        E entityAfterUpdate = getTestContext().getTestService().findById(responseDto.getId()).get();
        config.getPostUpdateCallback().callback(updateRequestDto,entityAfterUpdate);
        Assertions.assertEquals(mappingContext().getUpdateReturnDtoClass(),responseDto.getClass());
        return (Dto) responseDto;
    }

    protected ResponseEntity<String> updateEntity_ShouldFail(IdentifiableEntity<Id> updateRequestDto) throws Exception {
        return updateEntity_ShouldFail(updateRequestDto,failedTestConfigFactory.createDefaultConfig());
    }

    protected ResponseEntity<String> updateEntity_ShouldFail(IdentifiableEntity<Id> updateRequestDto, FailedUpdateControllerTestConfiguration<Id> modifications) throws Exception {
        FailedUpdateControllerTestConfiguration<Id> config = failedTestConfigFactory.createMergedConfig(modifications);

        Assertions.assertNotNull(updateRequestDto.getId());
        //Entity muss vorher auch schon da sein
        Optional<E> entityBeforeUpdate = getTestContext().getTestService().findById(updateRequestDto.getId());
        Assertions.assertTrue(entityBeforeUpdate.isPresent(), "Entity to update was not present");

        ResponseEntity<String> responseEntity = updateEntity(updateRequestDto, config);
        Assertions.assertEquals(config.getExpectedHttpStatus(), responseEntity.getStatusCode(), responseEntity.getBody());
        return responseEntity;
    }


    protected ResponseEntity<String> updateEntity_ShouldFail(E entityToUpdate, IdentifiableEntity<Id> updateRequest) throws Exception {
        E savedEntityToUpdate = getTestContext().getTestService().save(entityToUpdate);
        updateRequest.setId(savedEntityToUpdate.getId());
        return updateEntity_ShouldFail(updateRequest,failedTestConfigFactory.createDefaultConfig());
    }
    protected ResponseEntity<String> updateEntity_ShouldFail(E entityToUpdate, IdentifiableEntity<Id> updateRequest, FailedUpdateControllerTestConfiguration<Id> modifications) throws Exception {
        E savedEntityToUpdate = getTestContext().getTestService().save(entityToUpdate);
        updateRequest.setId(savedEntityToUpdate.getId());
        return updateEntity_ShouldFail(updateRequest,modifications);
    }


    /**
     * Send update Entity Request to Backend
     *
     * @param updateRequestDto updated entityDto
     * @return backend Response
     */
    protected ResponseEntity<String> updateEntity(IdentifiableEntity<Id> updateRequestDto, ControllerTestConfiguration<Id> config) {
        Assertions.assertNotNull(updateRequestDto.getId());
        return sendRequest(getRequestEntityFactory().create(config,updateRequestDto));
    }
}
