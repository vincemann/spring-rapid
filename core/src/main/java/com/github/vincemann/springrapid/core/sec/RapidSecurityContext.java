package com.github.vincemann.springrapid.core.sec;

import com.google.common.collect.Sets;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogMessage;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

/**
 * simple static wrapper for {@link org.springframework.security.core.context.SecurityContext}, providing typed access to
 * {@link RapidPrincipal}
 */
// keep non static to allow customization
public abstract class RapidSecurityContext {

    private final static Log log = LogFactory.getLog(RapidSecurityContext.class);

    public static void setAuthenticated(RapidPrincipal principal) {
        log.debug(LogMessage.format("authenticated user set to: %s",principal.getUsername()));
        doSetAuthenticated(principal);
    }

    public static void doSetAuthenticated(RapidPrincipal principal){
        SecurityContextHolder.getContext().setAuthentication(createToken(principal));
    }


    public static void setAnonAuthenticated() {
        setAuthenticated(getAnonUser());
    }

    public static List<String> getRoles() {
        List<String> result = new LinkedList<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null){
            return new ArrayList<>();
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if(authorities==null){
            return new ArrayList<>();
        }
        for (GrantedAuthority authority : authorities) {
            result.add(authority.getAuthority());
        }
        return result;
    }

    @Nullable
    public static RapidPrincipal currentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        Assert.isInstanceOf(RapidPrincipal.class, principal, "principal must be of type RapidPrincipal");
        return (RapidPrincipal) authentication.getPrincipal();
    }

    public static void executeAsSystemUser(Runnable runnable) {
        if (systemUserAuthenticated()) {
            runnable.run();
            return;
        }
        Authentication originalAuth = SecurityContextHolder.getContext().getAuthentication();
        try {
            doSetAuthenticated(getSystemUser());
            runnable.run();
        } finally {
            SecurityContextHolder.getContext().setAuthentication(originalAuth);
        }
    }

    public static <T> T executeAsSystemUser(Supplier<T> supplier) {
        if (systemUserAuthenticated())
            return supplier.get();
        Authentication originalAuth = SecurityContextHolder.getContext().getAuthentication();
        try {
            // dont go through authentication manager, bc system only exists in ram
            doSetAuthenticated(getSystemUser());
            return supplier.get();
        } finally {
            SecurityContextHolder.getContext().setAuthentication(originalAuth);
        }
    }

    public static boolean systemUserAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null)
            return false;
        if (authentication.getName() == null)
            return false;
        return authentication.getName().equals(getSystemUser().getName());
    }

    public static RapidPrincipal getAnonUser() {
        RapidPrincipal principal = new RapidPrincipal();
        principal.setRoles(Sets.newHashSet(Roles.ANON));
        return principal;
    }

    public static boolean hasRole(String role) {
        return RapidSecurityContext.getRoles().contains(role);
    }

    public static String getName(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null){
            return null;
        }
        return authentication.getName();
    }

    public static boolean isAuthenticated() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context == null) {
            return false;
        }
        Authentication authentication = context.getAuthentication();
        if (authentication==null){
            return false;
        }else {
            if (AuthorityUtils.authorityListToSet(authentication.getAuthorities()).contains(Roles.ANON)){
                return false;
            }
            return true;
        }
    }
    public static void clear(){
        SecurityContextHolder.clearContext();
    }


    public static RapidPrincipal getSystemUser() {
        RapidPrincipal principal = new RapidPrincipal();
        principal.setName("system");
        principal.setRoles(Sets.newHashSet(Roles.SYSTEM));
        return principal;
    }

    public static Authentication createToken(RapidPrincipal principal) {
        return new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
    }


}
