package com.github.vincemann.springrapid.core.sec;

import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents logged in user.
 * Also contains {@link Authentication#getDetails()} information.
 */
@Getter
public class RapidPrincipal implements AuthenticatedPrincipal, CredentialsContainer, UserDetails {
    private String name;
    private Set<String> roles;
    private String password;
    private String id;

    @Builder
    public  RapidPrincipal(String name, String password, Set<String> roles, String id) {
        this.name = name;
        this.password = password;
        this.roles = roles;
        this.id = id;
    }

    public RapidPrincipal(RapidPrincipal principal){
        this(principal.getName(),principal.getPassword(),principal.getRoles(),principal.getId());
    }

    public RapidPrincipal() {
    }

    @Override
    public void eraseCredentials() {
        this.password =null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }

    // UserDetails ...

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getName() {
        return this.name;
    }

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

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
    }

    public String shortToString(){
        return "[ AuthenticatedPrincipal: " + getName() + " ]";
    }
}
