package vincemann.github.generic.crud.lib.dtoMapper;

import vincemann.github.generic.crud.lib.controller.exception.EntityMappingException;
import vincemann.github.generic.crud.lib.model.IdentifiableEntity;

import java.io.Serializable;

public interface DTOMapper<Src extends IdentifiableEntity<Id>,Dest extends IdentifiableEntity<Id>,Id extends Serializable> {
    public Dest map(Src source) throws EntityMappingException;
}
