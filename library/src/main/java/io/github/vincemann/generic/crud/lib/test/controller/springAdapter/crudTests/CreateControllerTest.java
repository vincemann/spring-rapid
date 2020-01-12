package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.ControllerIntegrationTestContext;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.CrudController_TestCase;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.abs.AbstractControllerTest;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.RequestEntityMapper;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;

public class CreateControllerTest<E extends IdentifiableEntity<Id>, Id extends Serializable>
        extends AbstractControllerTest<E,Id> {



    public CreateControllerTest(ControllerIntegrationTestContext<E, Id> rootContext) {
        super(rootContext);
    }


    public  <Dto extends IdentifiableEntity<Id>> Dto createEntity_ShouldSucceed(IdentifiableEntity<Id> returnDto) throws Exception {
        return createEntity_ShouldSucceed(returnDto, null);
    }

    public <Dto extends IdentifiableEntity<Id>> Dto createEntity_ShouldSucceed(IdentifiableEntity<Id> createRequestDto, TestRequestEntity_Modification... modifications) throws Exception {
        Assertions.assertNull(createRequestDto.getId());
        Assertions.assertEquals(mappingContext().getCreateArgDtoClass(),createRequestDto.getClass());

        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(CrudController_TestCase.SUCCESSFUL_CREATE, null, modifications);
        ResponseEntity<String> responseEntity = createEntity(createRequestDto, testRequestEntity);
        Assertions.assertEquals(testRequestEntity.getExpectedHttpStatus(), responseEntity.getStatusCode(), responseEntity.getBody());
        IdentifiableEntity<Id> responseDto = controller.getMediaTypeStrategy().readDtoFromBody(responseEntity.getBody(), mappingContext().getCreateReturnDtoClass());

        Assertions.assertNotNull(responseDto);
        Assertions.assertEquals(mappingContext().getCreateReturnDtoClass(),responseDto.getClass());
        return (Dto) responseDto;
    }

    public ResponseEntity<String> createEntity_ShouldFail(IdentifiableEntity<Id> dto) throws Exception {
        return createEntity_ShouldFail(dto, null);
    }

    public ResponseEntity<String> createEntity_ShouldFail(IdentifiableEntity<Id> dto, TestRequestEntity_Modification... modifications) throws Exception {
        TestRequestEntity testRequestEntity = requestEntityFactory.createInstance(
                CrudController_TestCase.FAILED_CREATE,
                null,
                modifications);
        ResponseEntity<String> responseEntity = createEntity(dto, testRequestEntity);
        Assertions.assertEquals(testRequestEntity.getExpectedHttpStatus(), responseEntity.getStatusCode(), responseEntity.getBody());
        return responseEntity;
    }

    /**
     * Send create Entity Request to Backend, raw Response is returned
     *
     * @param dto the Dto entity that should be stored
     * @return
     */
    public ResponseEntity<String> createEntity(IdentifiableEntity<Id> dto, TestRequestEntity testRequestEntity) {
        return getRestTemplate().exchange(RequestEntityMapper.map(testRequestEntity, dto), String.class);
    }
}
