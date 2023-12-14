package com.github.vincemann.springrapid.acl.util;

import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.Sid;

public class AclUtils {


    private AclUtils(){}
    public static String getSidString(Sid sid){
        if (sid instanceof PrincipalSid) {
            return ((PrincipalSid) sid).getPrincipal();
        }else if (sid instanceof GrantedAuthoritySid){
            return ((GrantedAuthoritySid) sid).getGrantedAuthority();
        }else {
            throw new IllegalArgumentException("unknown sid, overwrite this function");
        }
    }

    /**
     * checks if aces sid and aces permission is equal.
     */
    public static boolean isAcePresent(AccessControlEntry ace, Acl acl){
        // todo mabye add parallel arg here, and for entities with a ton of aces set flag to true and user parallelstream
        return acl.getEntries().stream().filter(entry -> {
            return entry.getPermission().equals(ace.getPermission()) &&
                    entry.getSid().equals(ace.getSid());
        }).findAny().isPresent();
    }
}
