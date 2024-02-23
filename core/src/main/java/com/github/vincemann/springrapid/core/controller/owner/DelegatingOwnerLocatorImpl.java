package com.github.vincemann.springrapid.core.controller.owner;


import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @see OwnerLocator
 */
@Slf4j
public class DelegatingOwnerLocatorImpl implements DelegatingOwnerLocator, AopLoggable {
    private List<OwnerLocator> ownerLocators = new ArrayList<>();

    public void register(OwnerLocator ownerLocator){
        ownerLocators.add(ownerLocator);
    }

    @LogInteraction
    public Optional<String> find(IdentifiableEntity<?> entity){
        Optional<OwnerLocator> locator = ownerLocators.stream()
                .filter(l -> l.supports(entity.getClass()))
                .findFirst();
        if (locator.isEmpty()){
            if (log.isWarnEnabled())
                log.warn("could not find matching owner locator for entity class: " + entity.getClass().getSimpleName());
            return Optional.empty();
        }
        return locator.get().find(entity);
    }

}
