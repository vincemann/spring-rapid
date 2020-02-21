package io.github.vincemann.generic.crud.lib.test.controller.crudTests;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMappingContext;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.ControllerIntegrationTest;
import io.github.vincemann.generic.crud.lib.test.controller.crudTests.abs.AbstractControllerTestTemplate;
import io.github.vincemann.generic.crud.lib.test.controller.crudTests.config.SuccessfulCreateControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.crudTests.config.abs.ControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.crudTests.config.factory.abs.AbstractControllerTestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.controller.requestEntityFactory.CreateTest;
import io.github.vincemann.generic.crud.lib.test.controller.requestEntityFactory.RequestEntityFactory;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;

@Setter
@Getter
@TestComponent
public class CreateControllerTestTemplate<E extends IdentifiableEntity<Id>, Id extends Serializable>
        extends AbstractControllerTestTemplate<E,Id,ControllerTestConfiguration<Id>> {

    private AbstractControllerTestConfigurationFactory<E,Id, SuccessfulCreateControllerTestConfiguration<E,Id>,ControllerTestConfiguration<Id>> testConfigFactory;



    public CreateControllerTestTemplate(@CreateTest RequestEntityFactory<Id, ControllerTestConfiguration<Id>> requestEntityFactory, @CreateTest AbstractControllerTestConfigurationFactory<E, Id, SuccessfulCreateControllerTestConfiguration<E, Id>, ControllerTestConfiguration<Id>> testConfigFactory) {
        super(requestEntityFactory);
        this.testConfigFactory = testConfigFactory;
    }

    @Override
    public void setTestContext(ControllerIntegrationTest<E, Id> testContext) {
        super.setTestContext(testContext);
        testConfigFactory.setTestContext(testContext);
    }

    public  <Dto extends IdentifiableEntity<Id>> Dto createEntity_ShouldSucceed(IdentifiableEntity<Id> returnDto) throws Exception {
        return createEntity_ShouldSucceed(returnDto, testConfigFactory.createDefaultSuccessfulConfig());
    }

    public <Dto extends IdentifiableEntity<Id>> Dto createEntity_ShouldSucceed(IdentifiableEntity<Id> createRequestDto, ControllerTestConfiguration<Id>... modifications) throws Exception {
        SuccessfulCreateControllerTestConfiguration<E,Id> config = testConfigFactory.createMergedSuccessfulConfig(modifications);

        Assertions.assertNull(createRequestDto.getId());
        DtoMappingContext<Id> mappingContext = getTestContext().getDtoMappingContext();
        Assertions.assertEquals(mappingContext.getCreateRequestDtoClass(),createRequestDto.getClass());

        ResponseEntity<String> responseEntity = createEntity(createRequestDto, config);
        Assertions.assertEquals(config.getExpectedHttpStatus(), responseEntity.getStatusCode(), responseEntity.getBody());
        IdentifiableEntity<Id> responseDto = getTestContext().getController().getMediaTypeStrategy().readDtoFromBody(responseEntity.getBody(), mappingContext.getCreateReturnDtoClass());

        E savedEntity = getTestContext().getTestService().findById(responseDto.getId()).get();
        config.getPostCreateCallback().callback(savedEntity,responseDto);
        Assertions.assertNotNull(responseDto);
        Assertions.assertEquals(mappingContext.getCreateReturnDtoClass(),responseDto.getClass());
        return (Dto) responseDto;
    }

    public ResponseEntity<String> createEntity_ShouldFail(IdentifiableEntity<Id> dto) throws Exception {
        return createEntity_ShouldFail(dto, testConfigFactory.createDefaultFailedConfig());
    }

    public ResponseEntity<String> createEntity_ShouldFail(IdentifiableEntity<Id> dto, ControllerTestConfiguration<Id>... modifications) throws Exception {
        ControllerTestConfiguration<Id> config = testConfigFactory.createMergedFailedConfig(modifications);
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
        return sendRequest(getRequestEntityFactory().create(config,dto,null));
    }
}
