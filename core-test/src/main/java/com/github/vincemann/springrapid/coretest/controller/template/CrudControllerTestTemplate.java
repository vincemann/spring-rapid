package com.github.vincemann.springrapid.coretest.controller.template;

import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.controller.GenericCrudController;
import org.checkerframework.checker.units.qual.C;

import java.io.Serializable;

public class CrudControllerTestTemplate extends AbstractCrudControllerTestTemplate<CrudController> {

    // dont autowire, must be set
    @Override
    public void injectController(CrudController controller) {
        super.injectController(controller);
    }


}
