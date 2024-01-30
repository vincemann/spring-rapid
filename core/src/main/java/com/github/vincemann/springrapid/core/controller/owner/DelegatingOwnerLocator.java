package com.github.vincemann.springrapid.core.controller.owner;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;

import java.util.Optional;

public interface DelegatingOwnerLocator {

    public Optional<String> find(IdentifiableEntity<?> entity);
    public void register(OwnerLocator<?> ownerLocator);
}
