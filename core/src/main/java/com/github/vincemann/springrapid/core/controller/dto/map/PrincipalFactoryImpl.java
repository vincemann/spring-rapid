package com.github.vincemann.springrapid.core.controller.dto.map;

import com.github.vincemann.springrapid.core.controller.owner.DelegatingOwnerLocator;
import com.github.vincemann.springrapid.core.controller.owner.OwnerLocator;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class PrincipalFactoryImpl implements PrincipalFactory {

    private DelegatingOwnerLocator ownerLocator;

    public PrincipalFactoryImpl() {
    }

    @Override
    public Principal create(IdentifiableEntity<?> entity) {
        if (entity != null) {
            String authenticated = RapidSecurityContext.getName();
            Optional<String> queried = ownerLocator.find(entity);
            if (queried.isPresent() && authenticated != null) {
                return queried.get().equals(authenticated)
                        ? Principal.OWN
                        : Principal.FOREIGN;
            }
        }
        return Principal.ALL;
    }

    @Autowired
    public void setOwnerLocator(DelegatingOwnerLocator ownerLocator) {
        this.ownerLocator = ownerLocator;
    }
}
