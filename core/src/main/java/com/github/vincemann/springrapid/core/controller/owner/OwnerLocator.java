package com.github.vincemann.springrapid.core.controller.owner;

import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;

import java.util.Optional;

/**
 * Find the {@link Authentication#getName()} of Owner of given Entity.
 */


public interface OwnerLocator<E extends IdentifiableEntity> extends AopLoggable {

    public boolean supports(Class clazz);

    @LogInteraction
    public Optional<String> find(E entity);
}
