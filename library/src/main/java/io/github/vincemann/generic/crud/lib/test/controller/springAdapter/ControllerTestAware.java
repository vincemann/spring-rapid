package io.github.vincemann.generic.crud.lib.test.controller.springAdapter;

import io.github.vincemann.generic.crud.lib.test.ControllerIntegrationTest;

public interface ControllerTestAware {
    public void setTest(ControllerIntegrationTest test);
    public ControllerIntegrationTest getTest();
}
