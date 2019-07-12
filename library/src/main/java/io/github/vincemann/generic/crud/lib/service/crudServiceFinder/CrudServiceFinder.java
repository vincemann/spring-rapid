package io.github.vincemann.generic.crud.lib.service.crudServiceFinder;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;

import java.util.Map;

public interface CrudServiceFinder {

    public Map<Class<? extends IdentifiableEntity>, CrudService> getCrudServices();
}
