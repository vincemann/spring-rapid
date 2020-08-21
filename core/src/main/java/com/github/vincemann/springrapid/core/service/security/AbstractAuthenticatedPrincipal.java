package com.github.vincemann.springrapid.core.service.security;

import lombok.Getter;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public abstract class AbstractAuthenticatedPrincipal implements AuthenticatedPrincipal, CredentialsContainer, UserDetails {
    private String name;
    private Set<String> roles;
    private Collection<? extends GrantedAuthority> authorities;
    private Object credentials;

    public AbstractAuthenticatedPrincipal(String name, Object credentials, Set<String> roles) {
        this.name = name;
        this.credentials = credentials;
        this.roles = roles;
        this.authorities = createAuthorities();
    }

    protected Collection<? extends GrantedAuthority> createAuthorities(){
        return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }

    protected void setName(String name) {
        this.name = name;
    }

    protected void setCredentials(Object credentials) {
        this.credentials = credentials;
    }

    protected void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    @Override
    public void eraseCredentials() {
        this.credentials=null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    // UserDetails ...

    @Override
    public String getPassword() {
        return getPassword();
    }

    @Override
    public String getName() {
        return getName();
    }

    //username is always email in spring lemon
    @Override
    public String getUsername() {
        return getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
