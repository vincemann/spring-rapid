package com.github.vincemann.springrapid.acl.util;

import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.PrincipalSid;
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
}
