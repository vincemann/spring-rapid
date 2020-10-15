package com.github.vincemann.springrapid.core.controller;

import com.github.vincemann.springrapid.core.controller.dto.mapper.context.CrudDtoMappingContextBuilder;
import com.github.vincemann.springrapid.core.controller.idFetchingStrategy.UrlParamIdFetchingStrategy;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;

import java.io.Serializable;

/**
 * Fully Functional CrudController.
 * <p>
 * Example-Request-URL's with {@link UrlParamIdFetchingStrategy}:
 * /entityName/httpMethod?id=42
 * <p>
 * /account/get?id=42
 * /account/get?id=44bedc08-8e71-11e9-bc42-526af7764f64
 *
 * @param <E>  Entity Type, of entity, who's crud operations are exposed, via endpoints,  by this Controller
 * @param <ID> Id Type of {@link E}
 */
public abstract class CrudController
        <
        E extends IdentifiableEntity<ID>,
        ID extends Serializable,
        S extends CrudService<E, ID>
        >
        extends GenericCrudController<E, ID, S, CrudEndpointInfo, CrudDtoMappingContextBuilder> {


    @Override
    protected CrudDtoMappingContextBuilder createDtoMappingContextBuilder() {
        return new CrudDtoMappingContextBuilder(this);
    }
}
