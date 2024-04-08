package com.github.vincemann.springrapid.auth;


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
 * Represents authenticated user.
 * Also contains {@link Authentication#getDetails()} information.
 */
public class AuthPrincipal implements AuthenticatedPrincipal, CredentialsContainer, UserDetails {
    private String name;
    private Set<String> roles;
    private String password;
    private String id;

    public AuthPrincipal(String name, String password, Set<String> roles, String id) {
        this.name = name;
        this.password = password;
        this.roles = roles;
        this.id = id;
    }

    public AuthPrincipal(AuthPrincipal principal){
        this(principal.getName(),principal.getPassword(),principal.getRoles(),principal.getId());
    }

    public AuthPrincipal() {
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

    public Set<String> getRoles() {
        return roles;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "RapidPrincipal{" +
                "name='" + name + '\'' +
                ", roles=" + roles +
                ", password='" + password + '\'' +
                ", id='" + id + '\'' +
                ", authorities=" + getAuthorities() +
                ", username='" + getUsername() + '\'' +
                ", accountNonExpired=" + isAccountNonExpired() +
                ", accountNonLocked=" + isAccountNonLocked() +
                ", credentialsNonExpired=" + isCredentialsNonExpired() +
                ", enabled=" + isEnabled() +
                ", shortToString='" + shortToString() + '\'' +
                '}';
    }

    public String shortToString(){
        return "[ AuthenticatedPrincipal: " + getName() + " ]";
    }

    public static final class Builder {
        private String name;
        private Set<String> roles;
        private String password;
        private String id;

        private Builder() {
        }

        public static Builder aRapidPrincipal() {
            return new Builder();
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withRoles(Set<String> roles) {
            this.roles = roles;
            return this;
        }

        public Builder withPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public AuthPrincipal build() {
            AuthPrincipal authPrincipal = new AuthPrincipal();
            authPrincipal.setName(name);
            authPrincipal.setRoles(roles);
            authPrincipal.setPassword(password);
            authPrincipal.setId(id);
            return authPrincipal;
        }
    }
}
