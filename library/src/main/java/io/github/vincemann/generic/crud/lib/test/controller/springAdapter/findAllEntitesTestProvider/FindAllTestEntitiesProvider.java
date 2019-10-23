package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.findAllEntitesTestProvider;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import java.util.Set;

public interface FindAllTestEntitiesProvider<ServiceE extends IdentifiableEntity> {

    public Set<ServiceE> provideRepoEntities();
    public Set<ServiceE> provideEntitiesShouldBeFound();
}
