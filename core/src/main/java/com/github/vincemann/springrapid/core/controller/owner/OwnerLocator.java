package com.github.vincemann.springrapid.core.controller.owner;


import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;

import java.util.Optional;

/**
 * Find the {@link Authentication#getName()} of Owner of given Entity.
 */


public interface OwnerLocator<E extends IdentifiableEntity> {

    public boolean supports(Class clazz);
    public Optional<String> find(E entity);
}
