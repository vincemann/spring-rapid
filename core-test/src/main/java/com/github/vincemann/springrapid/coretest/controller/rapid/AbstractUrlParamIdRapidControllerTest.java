package com.github.vincemann.springrapid.coretest.controller.rapid;

import com.github.vincemann.springrapid.core.controller.GenericCrudController;

import java.io.Serializable;

/**
 * Extension of {@link AbstractMvcCrudControllerTest} expecting {@link com.github.vincemann.springrapid.core.controller.idFetchingStrategy.UrlParamIdFetchingStrategy}.
 */
public abstract class AbstractUrlParamIdRapidControllerTest
        <C extends GenericCrudController<?,Id,?>,
        Id extends Serializable>
             extends AbstractMvcCrudControllerTest<C>
                     implements UrlParamIdRapidControllerTest<C,Id>{

}
