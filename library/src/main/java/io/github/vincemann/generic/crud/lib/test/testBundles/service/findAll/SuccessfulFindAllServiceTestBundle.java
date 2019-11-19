package io.github.vincemann.generic.crud.lib.test.testBundles.service.findAll;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.findAllEntitesTestProvider.FindAllTestEntitiesProvider;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.NoArgsTestCallback;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.TestCallback;
import io.github.vincemann.generic.crud.lib.test.testBundles.service.findAll.abs.FindAllServiceTestBundle;
import lombok.Builder;

import java.io.Serializable;
import java.util.Set;

public class SuccessfulFindAllServiceTestBundle<ServiceE extends IdentifiableEntity<? extends Serializable>> extends FindAllServiceTestBundle<ServiceE> {

    @Builder
    public SuccessfulFindAllServiceTestBundle(NoArgsTestCallback preTestCallback, TestCallback<Set<ServiceE>> postTestCallback, Set<ServiceE> entitiesSavedBeforeRequest, FindAllTestEntitiesProvider<ServiceE> findAllTestEntitiesProvider) {
        super(preTestCallback, postTestCallback, entitiesSavedBeforeRequest, findAllTestEntitiesProvider);
    }
}
