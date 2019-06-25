package io.github.vincemann.generic.crud.lib.dtoMapper;

import io.github.vincemann.generic.crud.lib.controller.exception.EntityMappingException;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;

import java.io.Serializable;

public interface DTOMapper<Src extends IdentifiableEntity<Id>,Dest extends IdentifiableEntity<Id>,Id extends Serializable> {
    public Dest map(Src source) throws EntityMappingException;
}
