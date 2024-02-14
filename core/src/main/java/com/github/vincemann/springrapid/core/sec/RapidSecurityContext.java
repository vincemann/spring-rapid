package com.github.vincemann.springrapid.core.sec;

import com.github.vincemann.aoplog.Severity;
import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.annotation.CustomToString;
import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;
import java.util.function.Supplier;

@LogInteraction(Severity.TRACE)
public interface RapidSecurityContext
        extends AopLoggable {

    @LogInteraction(Severity.DEBUG)
    @CustomToString(key = "arg1", toStringMethod = "shortToString")
    void setAuthenticated(RapidPrincipal principal);

    @LogInteraction(Severity.DEBUG)
    @CustomToString(key = "arg1", toStringMethod = "shortToString")
    void setAnonAuthenticated();

    @LogInteraction(Severity.DEBUG)
    @CustomToString(key = "ret", toStringMethod = "shortToString")
    RapidPrincipal currentPrincipal();

    public static void unsetAuthenticated(){
        SecurityContextHolder.clearContext();
    }


    public void executeAsSystemUser(Runnable aclOperation);
    public <T> T executeAsSystemUser(Supplier<T> supplier);


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
            if (AuthorityUtils.authorityListToSet(authentication.getAuthorities()).contains(Roles.ANON)){
                return false;
            }
            return true;
        }
    }
}
