package io.github.vincemann.generic.crud.lib.service.locator;

import io.github.vincemann.generic.crud.lib.config.layers.component.ServiceComponent;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;

import java.util.Map;

/**
 * Find all {@link CrudService}s in the Project mapped to the EntityClass handled by the CrudService
 */
@ServiceComponent
public interface CrudServiceLocator {

    public Map<Class<? extends IdentifiableEntity>, CrudService> find();
}
