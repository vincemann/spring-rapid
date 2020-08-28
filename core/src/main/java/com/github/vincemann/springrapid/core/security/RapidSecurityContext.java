package com.github.vincemann.springrapid.core.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

/**
 * Wrapper for Springs {@link SecurityContext#getAuthentication()} in a type safe manner for {@link Authentication#getPrincipal()}.
 * @param <P> Principal Type
 */
public interface RapidSecurityContext<P extends RapidAuthenticatedPrincipal> {
    P login(P principal);
    P currentPrincipal();

    //Convenience methods that could all be realized by using login
    void runAs(P principal, Runnable runnable);
    public void runWithRoles(Set<String> roles, Runnable runnable);
    public void runAuthenticated(Runnable runnable);
    public void runAsAdmin(Runnable privRunnable);
    public void runWithName(String name, Runnable runnable);


    // i hardcode these methods as static, because this information is retrieved like that in the whole framework
    // creating them as interfaced methods could result in inconsistent state
    public static boolean hasRole(String role) {
        return RapidSecurityContext.getRoles().contains(role);
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
            return true;
        }
    }
}
