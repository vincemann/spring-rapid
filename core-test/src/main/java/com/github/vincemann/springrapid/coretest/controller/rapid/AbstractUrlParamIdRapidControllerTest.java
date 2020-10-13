package com.github.vincemann.springrapid.coretest.controller.rapid;

import com.github.vincemann.springrapid.core.controller.CrudController;

import java.io.Serializable;

/**
 * Extension of {@link AbstractMvcCrudControllerTest} expecting {@link com.github.vincemann.springrapid.core.controller.idFetchingStrategy.UrlParamIdFetchingStrategy}.
 */
public abstract class AbstractUrlParamIdRapidControllerTest
        <C extends CrudController<?,Id,?>,
        Id extends Serializable>
             extends AbstractMvcCrudControllerTest<C>
                     implements UrlParamIdRapidControllerTest<C,Id>{

}
