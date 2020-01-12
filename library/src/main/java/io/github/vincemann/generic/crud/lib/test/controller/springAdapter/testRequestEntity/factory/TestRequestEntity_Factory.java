package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory;


import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.CrudController_TestCase;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.UrlParamId_ControllerIntegrationTest;
import org.springframework.lang.Nullable;


public interface TestRequestEntity_Factory {
    //todo sketchy af needs refactoring

    /**
     * Creates an default Instance of {@link TestRequestEntity} based on the testMethod ({@link CrudController_TestCase}).
     * If the user specified a {@link TestRequestEntity_Modification},
     * then this should alter the default {@link TestRequestEntity} created.
     * @param id of the request represented by returned {@link TestRequestEntity}, can be null if request does not carry and id
     * @return
     */
    public TestRequestEntity createInstance(CrudController_TestCase crudControllerTestCase,@Nullable Object id, @Nullable TestRequestEntity_Modification... bundleTestRequestEntityModifications);
    public void setTest(UrlParamId_ControllerIntegrationTest test);

}
