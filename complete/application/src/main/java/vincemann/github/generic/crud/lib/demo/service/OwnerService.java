package vincemann.github.generic.crud.lib.demo.service;

import vincemann.github.generic.crud.lib.demo.model.Owner;
import vincemann.github.generic.crud.lib.service.CrudService;

import java.util.Optional;

public interface OwnerService extends CrudService<Owner,Long> {
    Optional<Owner> findByLastName(String lastName);
}
