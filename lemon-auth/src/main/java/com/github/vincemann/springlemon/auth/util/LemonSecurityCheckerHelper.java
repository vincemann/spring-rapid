package com.github.vincemann.springlemon.auth.util;

import com.github.vincemann.springlemon.auth.domain.LemonRoles;
import com.github.vincemann.springrapid.core.security.RapidRoles;
import com.github.vincemann.springrapid.core.security.SecurityChecker;

public class LemonSecurityCheckerHelper {

    public static void checkGoodAdmin(SecurityChecker securityChecker){
        securityChecker.checkHasRoles(RapidRoles.ADMIN);
        securityChecker.checkHasNotRoles(LemonRoles.UNVERIFIED);
    }

    public static void checkGoodUser(SecurityChecker securityChecker){
        securityChecker.checkHasRoles(RapidRoles.USER);
        securityChecker.checkHasNotRoles(LemonRoles.UNVERIFIED);
    }
}
