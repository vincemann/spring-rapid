package io.github.vincemann.demo.service;

import io.github.vincemann.demo.model.Owner;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Optional;


public interface OwnerService extends CrudService<Owner,Long> {
    Optional<Owner> findByLastName(String lastName);
}
