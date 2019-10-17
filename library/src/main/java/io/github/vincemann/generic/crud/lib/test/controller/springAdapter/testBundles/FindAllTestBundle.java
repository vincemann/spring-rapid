package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.findAllEntitesTestProvider.FindAllTestEntitiesProvider;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntityModification;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.io.Serializable;

@Getter
public class FindAllTestBundle<ServiceE extends IdentifiableEntity<? extends Serializable>> {

    @Nullable
    private TestRequestEntityModification requestEntityModification;
    private FindAllTestEntitiesProvider<ServiceE> findAllTestEntitiesProvider;

    public FindAllTestBundle(@Nullable TestRequestEntityModification requestEntityModification, FindAllTestEntitiesProvider<ServiceE> findAllTestEntitiesProvider) {
        this.requestEntityModification = requestEntityModification;
        this.findAllTestEntitiesProvider = findAllTestEntitiesProvider;
    }

    public FindAllTestBundle(FindAllTestEntitiesProvider<ServiceE> findAllTestEntitiesProvider) {
        this.findAllTestEntitiesProvider = findAllTestEntitiesProvider;
    }
}
