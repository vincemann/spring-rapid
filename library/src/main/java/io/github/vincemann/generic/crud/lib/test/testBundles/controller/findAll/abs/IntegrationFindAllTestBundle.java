package io.github.vincemann.generic.crud.lib.test.testBundles.controller.findAll.abs;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.findAllEntitesTestProvider.FindAllTestEntitiesProvider;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntityModification;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.FindAllTestBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.NoArgsTestCallback;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.TestCallback;
import lombok.Builder;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Set;

@Getter
public abstract class IntegrationFindAllTestBundle<ServiceE extends IdentifiableEntity,Dto extends IdentifiableEntity,PostTestCallbackE>
        extends FindAllTestBundle<ServiceE, PostTestCallbackE> {

    @Nullable
    private TestRequestEntityModification requestEntityModification;


    public IntegrationFindAllTestBundle(NoArgsTestCallback preTestCallback, TestCallback<PostTestCallbackE> postTestCallback, Set<ServiceE> entitiesSavedBeforeRequest, FindAllTestEntitiesProvider<ServiceE> findAllTestEntitiesProvider, @Nullable TestRequestEntityModification requestEntityModification) {
        super(preTestCallback, postTestCallback, entitiesSavedBeforeRequest, findAllTestEntitiesProvider);
        this.requestEntityModification = requestEntityModification;
    }
}
