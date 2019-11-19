package io.github.vincemann.generic.crud.lib.test.testBundles.controller.findAll;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.findAllEntitesTestProvider.FindAllTestEntitiesProvider;
import io.github.
        vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntityModification;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.NoArgsTestCallback;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.TestCallback;
import io.github.vincemann.generic.crud.lib.test.testBundles.controller.findAll.abs.IntegrationFindAllTestBundle;
import lombok.Builder;

import java.util.Set;

public class SuccessfulFindAllIntegrationTestBundle<ServiceE extends IdentifiableEntity,Dto extends IdentifiableEntity>
        extends IntegrationFindAllTestBundle<ServiceE, Dto, Set<Dto>> {
    @Builder
    public SuccessfulFindAllIntegrationTestBundle(NoArgsTestCallback preTestCallback, TestCallback<Set<Dto>> postTestCallback, Set<ServiceE> entitiesSavedBeforeRequest, FindAllTestEntitiesProvider<ServiceE> findAllTestEntitiesProvider, TestRequestEntityModification requestEntityModification) {
        super(preTestCallback, postTestCallback, entitiesSavedBeforeRequest, findAllTestEntitiesProvider, requestEntityModification);
    }
}
