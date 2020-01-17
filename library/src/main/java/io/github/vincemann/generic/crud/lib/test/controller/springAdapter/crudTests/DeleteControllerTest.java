package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.ControllerIntegrationTestContext;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.ControllerTestMethod;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.abs.AbstractControllerTest;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.abs.ControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.factory.abs.AbstractControllerTestConfigurationFactory;
import lombok.Builder;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.util.Optional;

public class DeleteControllerTest<E extends IdentifiableEntity<Id>, Id extends Serializable>
        extends AbstractControllerTest<E,Id> {


    private AbstractControllerTestConfigurationFactory<E,Id, ControllerTestConfiguration<Id>,ControllerTestConfiguration<Id>> testConfigFactory;

    @Builder
    public DeleteControllerTest(ControllerIntegrationTestContext<E, Id> testContext, AbstractControllerTestConfigurationFactory<E, Id, ControllerTestConfiguration<Id>, ControllerTestConfiguration<Id>> testConfigFactory) {
        super(testContext);
        this.testConfigFactory = testConfigFactory;
    }


    public ResponseEntity<String> deleteEntity_ShouldSucceed(Id id) throws Exception {
        return deleteEntity_ShouldSucceed(id, testConfigFactory.createSuccessfulDefaultConfig());
    }

    public ResponseEntity<String> deleteEntity_ShouldSucceed(Id id, ControllerTestConfiguration<Id> modifications) throws Exception {
        ControllerTestConfiguration<Id> config = testConfigFactory.createSuccessfulMergedConfig(modifications);
        //Entity muss vorher auch schon da sein
        Optional<E> serviceFoundEntityBeforeDelete = getTestContext().getTestService().findById(id);
        Assertions.assertTrue(serviceFoundEntityBeforeDelete.isPresent(), "Entity to delete was not present");

        ResponseEntity<String> responseEntity = deleteEntity(id, config);
        Assertions.assertEquals(config.getExpectedHttpStatus(), responseEntity.getStatusCode(), responseEntity.getBody());
        return responseEntity;
    }

    public ResponseEntity<String> deleteEntity_ShouldFail(Id id) throws Exception {
        return deleteEntity_ShouldFail(id, testConfigFactory.createFailedDefaultConfig());
    }

    public ResponseEntity<String> deleteEntity_ShouldFail(Id id, ControllerTestConfiguration<Id> modifications) throws Exception {
        ControllerTestConfiguration<Id> config = testConfigFactory.createFailedMergedConfig(modifications);
        //Entity muss vorher auch schon da sein
        Optional<E> serviceFoundEntityBeforeDelete = getTestContext().getTestService().findById(id);
        Assertions.assertTrue(serviceFoundEntityBeforeDelete.isPresent(), "Entity to delete was not present");
        ResponseEntity<String> responseEntity = deleteEntity(id, config);
        Assertions.assertEquals(config.getExpectedHttpStatus(), responseEntity.getStatusCode(), responseEntity.getBody());
        return responseEntity;
    }

    public ResponseEntity<String> deleteEntity(Id id, ControllerTestConfiguration<Id> config) {
        return sendRequest(getTestContext().getRequestEntityFactory().create(config,null,id, ControllerTestMethod.DELETE));
    }
}
