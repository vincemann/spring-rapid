package io.github.vincemann.generic.crud.lib.test.controller.crudTests;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.ControllerIntegrationTest;
import io.github.vincemann.generic.crud.lib.test.controller.crudTests.abs.AbstractControllerTestTemplate;
import io.github.vincemann.generic.crud.lib.test.controller.crudTests.config.abs.ControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.crudTests.config.factory.abs.AbstractControllerTestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.controller.requestEntityFactory.DeleteTest;
import io.github.vincemann.generic.crud.lib.test.controller.requestEntityFactory.RequestEntityFactory;
import lombok.Builder;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.util.Optional;
@TestComponent
public class DeleteControllerTestTemplate<E extends IdentifiableEntity<Id>, Id extends Serializable>
        extends AbstractControllerTestTemplate<E,Id,ControllerTestConfiguration<Id>> {


    private AbstractControllerTestConfigurationFactory<E,Id, ControllerTestConfiguration<Id>,ControllerTestConfiguration<Id>> testConfigFactory;

    public DeleteControllerTestTemplate(@DeleteTest RequestEntityFactory<Id, ControllerTestConfiguration<Id>> requestEntityFactory,@DeleteTest AbstractControllerTestConfigurationFactory<E, Id, ControllerTestConfiguration<Id>, ControllerTestConfiguration<Id>> testConfigFactory) {
        super(requestEntityFactory);
        this.testConfigFactory = testConfigFactory;
    }

    public ResponseEntity<String> deleteEntity_ShouldSucceed(Id id) throws Exception {
        return deleteEntity_ShouldSucceed(id, testConfigFactory.createDefaultSuccessfulConfig());
    }

    public ResponseEntity<String> deleteEntity_ShouldSucceed(Id id, ControllerTestConfiguration<Id>... modifications) throws Exception {
        ControllerTestConfiguration<Id> config = testConfigFactory.createMergedSuccessfulConfig(modifications);
        //Entity muss vorher auch schon da sein
        Optional<E> serviceFoundEntityBeforeDelete = getTestContext().getTestService().findById(id);
        Assertions.assertTrue(serviceFoundEntityBeforeDelete.isPresent(), "Entity to delete was not present");

        ResponseEntity<String> responseEntity = deleteEntity(id, config);
        Assertions.assertEquals(config.getExpectedHttpStatus(), responseEntity.getStatusCode(), responseEntity.getBody());
        return responseEntity;
    }

    public ResponseEntity<String> deleteEntity_ShouldFail(Id id) throws Exception {
        return deleteEntity_ShouldFail(id, testConfigFactory.createDefaultFailedConfig());
    }

    public ResponseEntity<String> deleteEntity_ShouldFail(Id id, ControllerTestConfiguration<Id>... modifications) throws Exception {
        ControllerTestConfiguration<Id> config = testConfigFactory.createMergedFailedConfig(modifications);
        //Entity muss vorher auch schon da sein
        Optional<E> serviceFoundEntityBeforeDelete = getTestContext().getTestService().findById(id);
        Assertions.assertTrue(serviceFoundEntityBeforeDelete.isPresent(), "Entity to delete was not present");
        ResponseEntity<String> responseEntity = deleteEntity(id, config);
        Assertions.assertEquals(config.getExpectedHttpStatus(), responseEntity.getStatusCode(), responseEntity.getBody());
        return responseEntity;
    }

    public ResponseEntity<String> deleteEntity(Id id, ControllerTestConfiguration<Id> config) {
        return sendRequest(getRequestEntityFactory().create(config,null,id));
    }
}
