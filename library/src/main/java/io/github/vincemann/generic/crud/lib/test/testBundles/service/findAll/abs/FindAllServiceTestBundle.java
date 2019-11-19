package io.github.vincemann.generic.crud.lib.test.testBundles.service.findAll.abs;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.findAllEntitesTestProvider.FindAllTestEntitiesProvider;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.FindAllTestBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.NoArgsTestCallback;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.TestCallback;
import lombok.Getter;

import java.io.Serializable;
import java.util.Set;

@Getter
public abstract class FindAllServiceTestBundle<ServiceE extends IdentifiableEntity<? extends Serializable>> extends FindAllTestBundle<ServiceE,Set<ServiceE>> {
    public FindAllServiceTestBundle(NoArgsTestCallback preTestCallback, TestCallback<Set<ServiceE>> postTestCallback, Set<ServiceE> entitiesSavedBeforeRequest, FindAllTestEntitiesProvider<ServiceE> findAllTestEntitiesProvider) {
        super(preTestCallback, postTestCallback, entitiesSavedBeforeRequest, findAllTestEntitiesProvider);
    }
}
