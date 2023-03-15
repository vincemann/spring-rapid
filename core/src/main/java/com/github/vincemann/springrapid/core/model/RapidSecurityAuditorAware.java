package com.github.vincemann.springrapid.core.model;

import com.github.vincemann.springrapid.core.IdConverter;
import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.Serializable;
import java.util.Optional;

public abstract class RapidSecurityAuditorAware<Id extends Serializable> extends AbstractAuditorAware<Id> {

    private IdConverter<Id> idIdConverter;

    @Override
    protected Id currentId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        RapidAuthenticatedPrincipal principal = (RapidAuthenticatedPrincipal) authentication.getPrincipal();
        if (principal == null){
            return null;
        }

        if (principal.getId() == null){
            return null;
        }
        Id id = idIdConverter.toId(principal.getId());
        return id;
    }


    @Autowired
    public void injectIdIdConverter(IdConverter<Id> idIdConverter) {
        this.idIdConverter = idIdConverter;
    }
}
