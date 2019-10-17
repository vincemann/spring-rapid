package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.testRequestEntityModificationStrategy;

import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntityModification;

public interface TestRequestEntityModificationStrategy {
    public void modify(TestRequestEntity requestEntity, TestRequestEntityModification modification);
}
