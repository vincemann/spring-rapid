package com.github.vincemann.springrapid.acldemo.controller.suite.templates;

import com.github.vincemann.springrapid.acldemo.controller.PetController;
import com.github.vincemann.springrapid.coretest.controller.template.CrudControllerTestTemplate;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.stereotype.Component;

@TestComponent
public class PetControllerTestTemplate extends CrudControllerTestTemplate<PetController> {
}
