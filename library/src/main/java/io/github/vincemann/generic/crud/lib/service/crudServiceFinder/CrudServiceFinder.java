package io.github.vincemann.generic.crud.lib.service.crudServiceFinder;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;

import java.util.Map;

/**
 * Find all {@link CrudService}s in the Project mapped to the EntityClass handled by the CrudService
 */
public interface CrudServiceFinder {

    public Map<Class<? extends IdentifiableEntity>, CrudService> getCrudServices();
}
