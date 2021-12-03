package com.github.vincemann.springrapid.core.model;

import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.Serializable;
import java.util.Optional;

public class RapidSecurityAuditorAware implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        RapidAuthenticatedPrincipal principal = (RapidAuthenticatedPrincipal) authentication.getPrincipal();
        if (principal == null){
            return Optional.empty();
        }

        if (principal.getId() == null){
            return Optional.empty();
        }

        return Optional.of(Long.valueOf(principal.getId()));
    }
}
