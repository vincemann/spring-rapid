package com.github.vincemann.springrapid.coretest.controller;

import com.github.vincemann.springrapid.core.controller.GenericCrudController;

import java.io.Serializable;

/**
 * Extension of {@link AbstractCrudControllerTest} expecting {@link com.github.vincemann.springrapid.core.controller.idFetchingStrategy.UrlParamIdFetchingStrategy}.
 */
public abstract class AbstractUrlParamIdCrudControllerTest
        <C extends GenericCrudController<?,Id,?,?,?>,
        Id extends Serializable>
             extends AbstractCrudControllerTest<C>
                     implements UrlParamIdCrudControllerTest<C,Id> {

}
