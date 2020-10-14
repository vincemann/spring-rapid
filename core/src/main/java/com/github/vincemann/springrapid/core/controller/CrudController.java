package com.github.vincemann.springrapid.core.controller;

import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContextBuilder;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;

import java.io.Serializable;

public abstract class CrudController
        <
        E extends IdentifiableEntity<Id>,
        Id extends Serializable,
        S extends CrudService<E, Id>
        >
        extends GenericCrudController<E, Id, S, CrudEndpointInfo, DtoMappingContextBuilder> {
}
