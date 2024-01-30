package com.github.vincemann.springrapid.auth.util;

import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.core.sec.RapidPrincipal;

public class PrincipalUtils {

    public static boolean isAdmin(RapidPrincipal principal){
        return principal.getRoles().contains(AuthRoles.ADMIN);
    }

    public static boolean isAnon(RapidPrincipal principal){
        return principal.getRoles().contains(AuthRoles.ANON);
    }


    public static boolean isVerified(RapidPrincipal principal){
        return !principal.getRoles().contains(AuthRoles.UNVERIFIED);
    }

    public static boolean isBlocked(RapidPrincipal principal){
        return principal.getRoles().contains(AuthRoles.BLOCKED);
    }
}
