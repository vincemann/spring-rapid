package com.github.vincemann.springrapid.core.util;

import com.github.vincemann.springrapid.core.service.RapidAuthenticatedPrincipal;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;
import java.util.stream.Collectors;

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


    public static <T extends RapidAuthenticatedPrincipal> T get(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication==null){
            return null;
        }
        return (T) authentication.getPrincipal();
    }
}
