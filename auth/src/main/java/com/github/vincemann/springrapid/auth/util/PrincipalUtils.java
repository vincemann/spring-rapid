package com.github.vincemann.springrapid.auth.util;

import com.github.vincemann.springrapid.auth.Roles;
import com.github.vincemann.springrapid.auth.AuthPrincipal;

public class PrincipalUtils {

    public static boolean isAdmin(AuthPrincipal principal){
        return principal.getRoles().contains(AuthRoles.ADMIN);
    }

    public static boolean isAnon(AuthPrincipal principal){
        return principal.getRoles().contains(AuthRoles.ANON);
    }


    public static boolean isVerified(AuthPrincipal principal){
        return !principal.getRoles().contains(AuthRoles.UNVERIFIED);
    }

    public static boolean isBlocked(AuthPrincipal principal){
        return principal.getRoles().contains(AuthRoles.BLOCKED);
    }
}
