package io.github.vincemann.generic.crud.lib.test.testBundles.service.findAll;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.findAllEntitesTestProvider.FindAllTestEntitiesProvider;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.NoArgsTestCallback;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.TestCallback;
import io.github.vincemann.generic.crud.lib.test.testBundles.service.findAll.abs.FindAllServiceTestBundle;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
public class FailedFindAllServiceTestBundle<ServiceE extends IdentifiableEntity<? extends Serializable>> extends FindAllServiceTestBundle<ServiceE> {
    @Nullable
    private Class<? extends Exception> expectedException;

    @Builder
    public FailedFindAllServiceTestBundle(NoArgsTestCallback preTestCallback, TestCallback<Set<ServiceE>> postTestCallback, Set<ServiceE> entitiesSavedBeforeRequest, FindAllTestEntitiesProvider<ServiceE> findAllTestEntitiesProvider, @Nullable Class<? extends Exception> expectedException) {
        super(preTestCallback, postTestCallback, entitiesSavedBeforeRequest, findAllTestEntitiesProvider);
        this.expectedException = expectedException;
    }
}
