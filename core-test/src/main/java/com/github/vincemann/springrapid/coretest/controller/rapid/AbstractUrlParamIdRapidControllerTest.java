package com.github.vincemann.springrapid.coretest.controller.rapid;

import com.github.vincemann.springrapid.core.controller.RapidController;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;

/**
 * Extension of {@link AbstractMvcRapidControllerTest} expecting {@link com.github.vincemann.springrapid.core.controller.idFetchingStrategy.UrlParamIdFetchingStrategy}.
 */
public abstract class AbstractUrlParamIdRapidControllerTest
        <C extends RapidController<?,Id,?>,
        Id extends Serializable>
             extends AbstractMvcRapidControllerTest<C>
                     implements UrlParamIdRapidControllerTest<C,Id>{

}
