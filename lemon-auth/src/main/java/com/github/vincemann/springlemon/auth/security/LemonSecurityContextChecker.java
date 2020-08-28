package com.github.vincemann.springlemon.auth.security;

import com.github.vincemann.springlemon.auth.domain.LemonAuthenticatedPrincipal;
import com.github.vincemann.springrapid.core.security.RapidSecurityContextChecker;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import com.github.vincemann.springrapid.core.util.VerifyAccess;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Convenience class wrapping {@link LemonSecurityContext} and offering checking methods, so you dont have to
 * throw the {@link org.springframework.security.access.AccessDeniedException}s yourself all the time.
 */
//no interface needed because all information is in principal and principal creation is interfaced
//no information could be needed gathered after principal/authentication creation, bc of statelessness -> no interface needed
@Getter
public class LemonSecurityContextChecker {

    private RapidSecurityContext<LemonAuthenticatedPrincipal> securityContext;

    public void checkGoodAdmin(){
        RapidSecurityContextChecker.checkAuthenticated();
        LemonAuthenticatedPrincipal principal = securityContext.currentPrincipal();
        VerifyAccess.isTrue(principal.isGoodAdmin(),"User is not good admin");
    }

    public void checkGoodUser(){
        RapidSecurityContextChecker.checkAuthenticated();
        LemonAuthenticatedPrincipal principal = securityContext.currentPrincipal();
        VerifyAccess.isTrue(principal.isGoodUser(),"User is not good user");
    }

    @Autowired
    public void injectSecurityContext(RapidSecurityContext<LemonAuthenticatedPrincipal> securityContext) {
        this.securityContext = securityContext;
    }
}
