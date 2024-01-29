package com.github.vincemann.springrapid.auth.security;

import com.github.vincemann.springrapid.auth.model.AuthAuthenticatedPrincipalImpl;
import com.github.vincemann.springrapid.core.sec.SecurityContextChecker;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import com.github.vincemann.springrapid.core.util.VerifyAccess;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Convenience class wrapping {@link RapidAuthSecurityContext} and offering checking methods, so you dont have to
 * throw the {@link org.springframework.security.access.AccessDeniedException}s yourself all the time.
 */
//no interface needed because all information is in principal and principal creation is interfaced
//no information could be needed gathered after principal/authentication creation, bc of statelessness -> no interface needed
@Getter
public class AuthSecurityContextChecker {

    private RapidSecurityContext<AuthAuthenticatedPrincipalImpl> securityContext;

    public void checkAdmin(){
        SecurityContextChecker.checkAuthenticated();
        AuthAuthenticatedPrincipalImpl principal = securityContext.currentPrincipal();
        VerifyAccess.condition(principal.isAdmin(),"User is not admin");
    }

    public void checkGoodUser(){
        SecurityContextChecker.checkAuthenticated();
        AuthAuthenticatedPrincipalImpl principal = securityContext.currentPrincipal();
        VerifyAccess.condition(principal.isGoodUser(),"User is not good user");
    }

    @Autowired
    public void injectSecurityContext(RapidSecurityContext<AuthAuthenticatedPrincipalImpl> securityContext) {
        this.securityContext = securityContext;
    }
}
