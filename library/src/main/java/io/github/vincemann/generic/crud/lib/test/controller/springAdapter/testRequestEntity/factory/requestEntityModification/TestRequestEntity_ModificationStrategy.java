package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.requestEntityModification;

import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntity_Modification;

public interface TestRequestEntity_ModificationStrategy {
    public void process(TestRequestEntity requestEntity, TestRequestEntity_Modification... modifications);
}
