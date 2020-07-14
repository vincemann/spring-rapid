package com.github.vincemann.springrapid.core.controller.owner;


import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.LogInteraction;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @see OwnerLocator
 */
public class DelegatingOwnerLocator implements AopLoggable {
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
            return Optional.empty();
        }
        return locator.get().find(entity);
    }

}
