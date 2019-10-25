package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory;


import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.CrudControllerTestCase;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.UrlParamIdDtoCrudControllerSpringAdapterIT;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.update.UpdateTestEntityBundleIteration;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntityModification;
import org.springframework.lang.Nullable;


public interface TestRequestEntityFactory {
    //todo sketchy af needs refactoring

    /**
     * Creates an default Instance of {@link TestRequestEntity} based on the testMethod ({@link CrudControllerTestCase}).
     * If the user specified a {@link TestRequestEntityModification} in one of the {@link io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.TestEntityBundle}s or {@link UpdateTestEntityBundleIteration}s,
     * then this should alter the default {@link TestRequestEntity} created.
     * @param id of the request represented by returned {@link TestRequestEntity}, can be null if request does not carry and id
     * @return
     */
    public TestRequestEntity createInstance(CrudControllerTestCase crudControllerTestCase, @Nullable TestRequestEntityModification bundleTestRequestEntityModification, @Nullable Object id);
    public void setTest(UrlParamIdDtoCrudControllerSpringAdapterIT test);

}
