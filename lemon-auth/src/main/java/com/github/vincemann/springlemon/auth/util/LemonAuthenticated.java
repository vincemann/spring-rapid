package com.github.vincemann.springlemon.auth.util;

import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class LemonAuthenticated {

    public static void login(AbstractUser user){
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    public static <U extends AbstractUser> U get(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication==null){
            return null;
        }
        return (U) authentication.getPrincipal();
    }
}
