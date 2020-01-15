package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMappingContext;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.ControllerIntegrationTestContext;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.SuccessfulCreateControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.abs.ControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.factory.abs.AbstractControllerTestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.requestEntityFactory.RequestEntityFactory;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.abs.AbstractControllerTest;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;

public class CreateControllerTest<E extends IdentifiableEntity<Id>, Id extends Serializable>
        extends AbstractControllerTest<E,Id> {

    private AbstractControllerTestConfigurationFactory<E,Id,SuccessfulCreateControllerTestConfiguration<E,Id>> successfulTestConfigFactory;
    private AbstractControllerTestConfigurationFactory<E,Id,ControllerTestConfiguration<Id>> failedTestConfigFactory;


    public CreateControllerTest(ControllerIntegrationTestContext<E, Id> rootContext, RequestEntityFactory<Id> requestEntityFactory, AbstractControllerTestConfigurationFactory<E, Id, SuccessfulCreateControllerTestConfiguration<E, Id>> successfulTestConfigFactory, AbstractControllerTestConfigurationFactory<E, Id, ControllerTestConfiguration<Id>> failedTestConfigFactory) {
        super(rootContext, requestEntityFactory);
        this.successfulTestConfigFactory = successfulTestConfigFactory;
        this.failedTestConfigFactory = failedTestConfigFactory;
    }

    public  <Dto extends IdentifiableEntity<Id>> Dto createEntity_ShouldSucceed(IdentifiableEntity<Id> returnDto) throws Exception {
        return createEntity_ShouldSucceed(returnDto, successfulTestConfigFactory.createDefaultConfig());
    }

    public <Dto extends IdentifiableEntity<Id>> Dto createEntity_ShouldSucceed(IdentifiableEntity<Id> createRequestDto, SuccessfulCreateControllerTestConfiguration<E,Id> modifications) throws Exception {
        SuccessfulCreateControllerTestConfiguration<E,Id> config = successfulTestConfigFactory.createMergedConfig(modifications);

        Assertions.assertNull(createRequestDto.getId());
        DtoMappingContext<Id> mappingContext = getTestContext().getDtoMappingContext();
        Assertions.assertEquals(mappingContext.getCreateRequestDtoClass(),createRequestDto.getClass());

        ResponseEntity<String> responseEntity = createEntity(createRequestDto, config);
        Assertions.assertEquals(config.getExpectedHttpStatus(), responseEntity.getStatusCode(), responseEntity.getBody());
        IdentifiableEntity<Id> responseDto = getTestContext().getController().getMediaTypeStrategy().readDtoFromBody(responseEntity.getBody(), mappingContext.getCreateReturnDtoClass());

        E savedEntity = getTestContext().getTestService().findById(createRequestDto.getId()).get();
        config.getPostCreateCallback().callback(savedEntity,responseDto);
        Assertions.assertNotNull(responseDto);
        Assertions.assertEquals(mappingContext.getCreateReturnDtoClass(),responseDto.getClass());
        return (Dto) responseDto;
    }

    public ResponseEntity<String> createEntity_ShouldFail(IdentifiableEntity<Id> dto) throws Exception {
        return createEntity_ShouldFail(dto, failedTestConfigFactory.createDefaultConfig());
    }

    public ResponseEntity<String> createEntity_ShouldFail(IdentifiableEntity<Id> dto, ControllerTestConfiguration<Id> modifications) throws Exception {
        ControllerTestConfiguration<Id> config = failedTestConfigFactory.createMergedConfig(modifications);
        ResponseEntity<String> responseEntity = createEntity(dto, config);
        Assertions.assertEquals(config.getExpectedHttpStatus(), responseEntity.getStatusCode(), responseEntity.getBody());
        return responseEntity;
    }

    /**
     * Send create Entity Request to Backend, string Response is returned
     *
     * @param dto the Dto entity that should be stored
     * @return
     */
    public ResponseEntity<String> createEntity(IdentifiableEntity<Id> dto, ControllerTestConfiguration<Id> config) {
        return sendRequest(getRequestEntityFactory().create(config,dto));
    }
}
