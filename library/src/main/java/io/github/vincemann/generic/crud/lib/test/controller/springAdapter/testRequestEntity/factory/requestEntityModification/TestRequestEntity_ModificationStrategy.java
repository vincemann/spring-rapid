package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.requestEntityModification;

public interface TestRequestEntity_ModificationStrategy {
    public void process(TestRequestEntity requestEntity, TestRequestEntity_Modification... modifications);
}
