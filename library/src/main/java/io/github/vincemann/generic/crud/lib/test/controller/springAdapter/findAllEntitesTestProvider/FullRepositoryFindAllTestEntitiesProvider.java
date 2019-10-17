package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.findAllEntitesTestProvider;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;

import java.io.Serializable;
import java.util.Set;

public class FullRepositoryFindAllTestEntitiesProvider<ServiceE extends IdentifiableEntity<? extends Serializable>> implements FindAllTestEntitiesProvider<ServiceE> {

    private CrudService<ServiceE,? extends Serializable> crudService;

    public FullRepositoryFindAllTestEntitiesProvider(CrudService<ServiceE, ? extends Serializable> crudService) {
        this.crudService = crudService;
    }

    @Override
    public Set<ServiceE> provideEntitiesShouldBeFound() {
        return crudService.findAll();
    }
}
