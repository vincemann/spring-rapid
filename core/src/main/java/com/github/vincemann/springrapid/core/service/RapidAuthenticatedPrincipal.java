package com.github.vincemann.springrapid.core.service;

import org.springframework.security.core.AuthenticatedPrincipal;

import java.util.Set;

public abstract class RapidAuthenticatedPrincipal implements AuthenticatedPrincipal {
    private String name;
    private Set<String> roles;

    public RapidAuthenticatedPrincipal(String name, Set<String> roles) {
        this.name = name;
        this.roles = roles;
    }
}
