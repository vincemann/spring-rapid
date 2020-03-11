package io.github.vincemann.generic.crud.lib.service.plugin;

import io.github.vincemann.generic.crud.lib.config.ServiceLayer;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;

@Getter
@Setter
@ServiceLayer
public abstract class CrudServicePlugin<E extends IdentifiableEntity<Id>, Id extends Serializable> {
    private CrudService<E,Id, CrudRepository<E,Id>> service;
}
