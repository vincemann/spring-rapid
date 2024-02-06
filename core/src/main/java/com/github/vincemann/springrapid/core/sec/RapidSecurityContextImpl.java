package com.github.vincemann.springrapid.core.sec;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.function.Supplier;

// use springs authentication.getDetails to store more info then already given in RapidAuthenticatedPrincipal
@Slf4j
public class RapidSecurityContextImpl implements RapidSecurityContext
{



    @Override
    public RapidPrincipal setAuthenticated(RapidPrincipal principal) {
        RapidPrincipal old = currentPrincipal();
        if (old != null) {
            if (log.isWarnEnabled())
                log.warn("Principal: " + old + " was already logged in. This login will override authenticated user");
        }
        SecurityContextHolder.getContext().setAuthentication(createToken(principal));
        return old;
    }


    @Override
    public void setAnonAuthenticated() {
        setAuthenticated(getAnonUser());
    }

    @Override
    public RapidPrincipal currentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        try {
            return (RapidPrincipal) authentication.getPrincipal();
        }catch (ClassCastException e){
            throw new IllegalArgumentException("Security Context is malformed. Create with RapidSecurityContext.login(...)");
        }
    }

    @Override
    public void executeAsSystemUser(Runnable runnable) {
        if (systemUserAuthenticated()){
            runnable.run();
            return;
        }
        Authentication originalAuth = SecurityContextHolder.getContext().getAuthentication();
        try {
            setAuthenticated(getSystemUser());
            runnable.run();
        } finally {
            SecurityContextHolder.getContext().setAuthentication(originalAuth);
        }
    }

    @Override
    public <T> T executeAsSystemUser(Supplier<T> supplier) {
        if (systemUserAuthenticated())
            return supplier.get();
        Authentication originalAuth = SecurityContextHolder.getContext().getAuthentication();
        try {
            // dont go through authentication manager, bc system only exists in ram
            setAuthenticated(getSystemUser());
            return supplier.get();
        } finally {
            SecurityContextHolder.getContext().setAuthentication(originalAuth);
        }
    }

    protected boolean systemUserAuthenticated(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null)
            return false;
        if (authentication.getAuthorities() == null)
            return false;
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority(Roles.SYSTEM))){
            return true;
        }
        return false;
    }

    protected RapidPrincipal getAnonUser(){
        RapidPrincipal principal = new RapidPrincipal();
        principal.setName("anon");
        principal.setRoles(Sets.newHashSet(Roles.ANON));
        return principal;
    }



    protected RapidPrincipal getSystemUser() {
        RapidPrincipal principal = new RapidPrincipal();
        principal.setName("system");
        principal.setRoles(Sets.newHashSet(Roles.SYSTEM));
        return principal;
    }

    protected Authentication createToken(RapidPrincipal principal) {
        return new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
    }


    protected GrantedAuthority map(String role) {
        return new SimpleGrantedAuthority(role);
    }

}
