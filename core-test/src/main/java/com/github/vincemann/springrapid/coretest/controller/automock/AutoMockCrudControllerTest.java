package com.github.vincemann.springrapid.coretest.controller.automock;

import com.github.vincemann.springrapid.core.controller.GenericCrudController;
import com.github.vincemann.springrapid.coretest.controller.template.AbstractCrudControllerTestTemplate;
import com.github.vincemann.springrapid.coretest.controller.template.CrudControllerTestTemplate;

public class AutoMockCrudControllerTest<C extends GenericCrudController<?, ?, ?, ?, ?>>
        extends AbstractAutoMockCrudControllerTest<C, CrudControllerTestTemplate> {

}
