package com.github.vincemann.springrapid.core.model.audit;

import com.github.vincemann.springrapid.core.service.id.IdConverter;
import com.github.vincemann.springrapid.core.sec.RapidPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.Serializable;

public class RapidAuditorAware<Id extends Serializable> extends AbstractAuditorAware<Id> {

    private IdConverter<Id> idIdConverter;

    @Override
    protected Id currentId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        RapidPrincipal principal = (RapidPrincipal) authentication.getPrincipal();
        if (principal == null){
            return null;
        }

        if (principal.getId() == null){
            return null;
        }
        return idIdConverter.toId(principal.getId());
    }


    @Autowired
    public void setIdIdConverter(IdConverter<Id> idIdConverter) {
        this.idIdConverter = idIdConverter;
    }
}
