package com.github.vincemann.springrapid.authtest;

import com.github.vincemann.springrapid.auth.AbstractUser;
import com.github.vincemann.springrapid.auth.AuthPrincipal;
import com.github.vincemann.springrapid.auth.RapidSecurityContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthTestUtil {

    public static AuthPrincipal createPrincipal(AbstractUser user){
        String[] roles = (String[]) user.getRoles().toArray(new String[0]);
        AuthPrincipal principal = TestPrincipal.create(user.getContactInformation(), roles);
        return principal;
    }

    public static void authenticateAnon(){
        AuthPrincipal anonUser = RapidSecurityContext.getAnonUser();
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                anonUser, anonUser.getPassword(), anonUser.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
    }

    public static void authenticate(AbstractUser user){
        AuthPrincipal principal = createPrincipal(user);
        if (user.getId() != null)
            principal.setId(principal.getId());
        SecurityContext securityContext = createMockSecurityContext(principal);
        SecurityContextHolder.setContext(securityContext);
    }

    public static SecurityContext createMockSecurityContext(AuthPrincipal principal){
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal, principal.getPassword(), principal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }


}
