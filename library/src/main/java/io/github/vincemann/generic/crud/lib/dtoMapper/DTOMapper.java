package io.github.vincemann.generic.crud.lib.dtoMapper;

import io.github.vincemann.generic.crud.lib.controller.exception.EntityMappingException;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;

import java.io.Serializable;

/**
 * maps a DTO Entity to a ServiceEntity, or vice versa
 * @param <Src>     Source EntityType
 * @param <Dest>    Destination EntityType
 * @param <Id>      Id Type
 */
public interface DTOMapper<Src extends IdentifiableEntity<Id>,Dest extends IdentifiableEntity<Id>,Id extends Serializable> {
    public Dest map(Src source) throws EntityMappingException;
}
