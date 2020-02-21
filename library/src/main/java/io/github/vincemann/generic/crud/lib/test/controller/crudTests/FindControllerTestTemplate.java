package io.github.vincemann.generic.crud.lib.test.controller.crudTests;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.ControllerIntegrationTest;
import io.github.vincemann.generic.crud.lib.test.controller.crudTests.abs.AbstractControllerTestTemplate;
import io.github.vincemann.generic.crud.lib.test.controller.crudTests.config.SuccessfulFindControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.crudTests.config.abs.ControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.crudTests.config.factory.abs.AbstractControllerTestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.controller.requestEntityFactory.FindTest;
import io.github.vincemann.generic.crud.lib.test.controller.requestEntityFactory.RequestEntityFactory;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.util.Optional;

@Slf4j
@Setter
@Getter
@TestComponent
public class FindControllerTestTemplate<E extends IdentifiableEntity<Id>, Id extends Serializable>
        extends AbstractControllerTestTemplate<E,Id,ControllerTestConfiguration<Id>> {


    private AbstractControllerTestConfigurationFactory<E,Id,SuccessfulFindControllerTestConfiguration<E,Id>,ControllerTestConfiguration<Id>> testConfigFactory;

    public FindControllerTestTemplate(@FindTest RequestEntityFactory<Id, ControllerTestConfiguration<Id>> requestEntityFactory,@FindTest AbstractControllerTestConfigurationFactory<E, Id, SuccessfulFindControllerTestConfiguration<E, Id>, ControllerTestConfiguration<Id>> testConfigFactory) {
        super(requestEntityFactory);
        this.testConfigFactory = testConfigFactory;
    }

    public <Dto extends IdentifiableEntity<Id>> Dto findEntity_ShouldSucceed(Id id) throws Exception {
        return findEntity_ShouldSucceed(id, testConfigFactory.createDefaultSuccessfulConfig());
    }

    /**
     * @param id id of the entity, that should be found
     * @return the dto of the requested entity found on backend with given id
     * @throws Exception
     */
    public <Dto extends IdentifiableEntity<Id>> Dto findEntity_ShouldSucceed(Id id, ControllerTestConfiguration<Id>... modifications) throws Exception {
        SuccessfulFindControllerTestConfiguration<E, Id> config = testConfigFactory.createMergedSuccessfulConfig(modifications);
        Optional<E> entityToFind = getTestContext().getTestService().findById(id);
        if(entityToFind.isEmpty()){
            log.warn("Entity that shall be found does not exist with id: " + id);
        }

        ResponseEntity<String> responseEntity = findEntity(id, config);
        Assertions.assertEquals(config.getExpectedHttpStatus(), responseEntity.getStatusCode(), responseEntity.getBody());
        IdentifiableEntity<Id> responseDto = readFromBody(responseEntity.getBody(), mappingContext().getFindReturnDtoClass());
        Assertions.assertNotNull(responseDto);
        Assertions.assertEquals(mappingContext().getFindReturnDtoClass(),responseDto.getClass());

        config.getPostFindCallback().callback(entityToFind.get(),responseDto);
        return (Dto) responseDto;
    }

    public ResponseEntity<String> findEntity_ShouldFail(Id id) throws Exception {
        return findEntity_ShouldFail(id, testConfigFactory.createDefaultFailedConfig());
    }

    public ResponseEntity<String> findEntity_ShouldFail(Id id, ControllerTestConfiguration<Id>... modifications) throws Exception {
        ControllerTestConfiguration<Id> config = testConfigFactory.createMergedFailedConfig(modifications);
        ResponseEntity<String> responseEntity = findEntity(id, config);
        Assertions.assertEquals(config.getExpectedHttpStatus(), responseEntity.getStatusCode(), responseEntity.getBody());
        return responseEntity;
    }

    public ResponseEntity<String> findEntity(Id id, ControllerTestConfiguration<Id> config) {
        Assertions.assertNotNull(id);
        return sendRequest(getRequestEntityFactory().create(config,null,id));
    }
}
