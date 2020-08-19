package com.github.vincemann.springrapid.core.service;

import lombok.Getter;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.CredentialsContainer;

import java.util.Set;

@Getter
public abstract class RapidAuthenticatedPrincipal implements AuthenticatedPrincipal, CredentialsContainer {
    private String name;
    private Set<String> roles;
    private Object credentials;

    public RapidAuthenticatedPrincipal(String name, Set<String> roles) {
        this.name = name;
        this.roles = roles;
    }

    protected void setName(String name) {
        this.name = name;
    }

    protected void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    @Override
    public void eraseCredentials() {
        this.credentials=null;
    }
}
