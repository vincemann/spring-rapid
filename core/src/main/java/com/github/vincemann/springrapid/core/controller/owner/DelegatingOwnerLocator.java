package com.github.vincemann.springrapid.core.controller.owner;


import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @see OwnerLocator
 */
public class DelegatingOwnerLocator implements OwnerLocator<IdentifiableEntity<?>>, AopLoggable {
    private List<OwnerLocator> ownerLocators = new ArrayList<>();

    public void register(OwnerLocator ownerLocator){
        ownerLocators.add(ownerLocator);
    }

    @Override
    public boolean supports(Class clazz) {
        return ownerLocators.stream().anyMatch(l -> l.supports(clazz));
    }

    @LogInteraction
    public Optional<String> find(IdentifiableEntity<?> entity){
        Optional<OwnerLocator> locator = ownerLocators.stream()
                .filter(l -> l.supports(entity.getClass()))
                .findFirst();
        if (locator.isEmpty()){
            return Optional.empty();
        }
        return locator.get().find(entity);
    }

}
