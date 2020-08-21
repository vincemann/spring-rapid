package com.github.vincemann.springrapid.core.util;

import com.github.vincemann.springrapid.core.service.security.AbstractAuthenticatedPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

/**
 * Implemented in this static way, bc all over the framework {@link SecurityContextHolder} is accessed in a static way,
 * so there is no way to provide a diff impl of getting role info ect. than statically via {@link SecurityContextHolder}, without getting into trouble with all other spring components.
 * Use this class as a wrapper for {@link SecurityContextHolder}.
 * Rather use {@link #get()} to get the {@link AbstractAuthenticatedPrincipal} instead of using the {@link Authentication} object directly.
 */
public class Authenticated {


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

    public static Object getCredentials(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication==null){
            return null;
        }
        return authentication.getCredentials();
    }



}
