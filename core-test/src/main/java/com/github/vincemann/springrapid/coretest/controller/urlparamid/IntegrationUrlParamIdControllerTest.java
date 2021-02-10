package com.github.vincemann.springrapid.coretest.controller.urlparamid;

import com.github.vincemann.springrapid.core.controller.GenericCrudController;
import com.github.vincemann.springrapid.coretest.controller.IntegrationControllerTest;
import com.github.vincemann.springrapid.coretest.controller.UrlParamIdCrudControllerTest;
import com.github.vincemann.springrapid.coretest.controller.automock.AutoMockControllerTest;

import java.io.Serializable;

public class IntegrationUrlParamIdControllerTest <C extends GenericCrudController<?,Id,?,?,?>,
        Id extends Serializable>
        extends IntegrationControllerTest<C,Id>
        implements UrlParamIdCrudControllerTest<C,Id>
{
}
