package com.github.vincemann.springrapid.core.controller.owner;


import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @see OwnerLocator
 */
public class DelegatingOwnerLocatorImpl implements DelegatingOwnerLocator {

    private static final Log log = LogFactory.getLog(DelegatingOwnerLocatorImpl.class);

    private List<OwnerLocator> ownerLocators = new ArrayList<>();

    public void register(OwnerLocator ownerLocator){
        ownerLocators.add(ownerLocator);
    }

    public Optional<String> find(IdentifiableEntity<?> entity){
        Optional<OwnerLocator> locator = ownerLocators.stream()
                .filter(l -> l.supports(entity.getClass()))
                .findFirst();
        if (locator.isEmpty()){
            if (log.isWarnEnabled())
                log.warn(LogMessage.format("could not find matching owner locator for entity class: '%s'", entity.getClass().getSimpleName()));
            return Optional.empty();
        }
        Optional<String> owner = locator.get().find(entity);
        logOwner(entity,owner);
        return owner;
    }

    private void logOwner(IdentifiableEntity<?> entity, Optional<String> owner){
        if (owner.isPresent())
            log.debug(LogMessage.format("found owner %s for entity %s - %s",owner.get(),entity.getClass().getSimpleName(),entity.getId().toString()));
        else
            log.debug(LogMessage.format("Did not find any owner for entity %s - %s",entity.getClass().getSimpleName(),entity.getId().toString()));
    }

}
