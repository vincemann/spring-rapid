package com.github.vincemann.springrapid.coretest.controller.urlparamid;

import com.github.vincemann.springrapid.core.controller.GenericCrudController;
import com.github.vincemann.springrapid.coretest.controller.UrlParamIdCrudControllerTest;
import com.github.vincemann.springrapid.coretest.controller.automock.AutoMockControllerTest;

import java.io.Serializable;

/**
 * Extension of {@link AutoMockControllerTest} expecting {@link com.github.vincemann.springrapid.core.controller.idFetchingStrategy.UrlParamIdFetchingStrategy}.
 */
public abstract class AutoMockUrlParamIdControllerTest
        <C extends GenericCrudController<?,Id,?,?,?>,
        Id extends Serializable>
             extends AutoMockControllerTest<C,Id>
                     implements UrlParamIdCrudControllerTest<C,Id> {

}
