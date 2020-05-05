package io.github.vincemann.springrapid.coretest.controller.rapid;

import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.service.CrudService;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.Serializable;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * Extension of {@link AbstractMvcRapidControllerTest} expecting {@link io.github.vincemann.springrapid.core.controller.rapid.idFetchingStrategy.UrlParamIdFetchingStrategy}.
 */
public abstract class AbstractUrlParamIdRapidControllerTest<S extends CrudService<E,Id,? extends CrudRepository<E,Id>>
        ,E extends IdentifiableEntity<Id>,
        Id extends Serializable>
        extends AbstractMvcRapidControllerTest<S, E, Id> implements UrlParamIdRapidControllerTest<S,E,Id>{

}
