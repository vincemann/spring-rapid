package com.github.vincemann.springrapid.acl.service;

import com.google.common.collect.Sets;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;

import java.util.Set;

import static com.github.vincemann.springrapid.acl.util.AclUtils.getSidString;

@Getter
public class AceFilter {

    private Set<Permission> permissions;
    private String sid;
    private Boolean principalsOnly = Boolean.TRUE;

    public AceFilter(String sid, Boolean principalsOnly, Permission... permissions) {
        this.principalsOnly = principalsOnly;
        this.permissions = Sets.newHashSet(permissions);
        this.sid = sid;
    }

    @Builder
    public AceFilter(String sid, Boolean principalsOnly, Set<Permission> permissions) {
        if (principalsOnly != null)
            this.principalsOnly = principalsOnly;
        this.permissions = permissions;
        this.sid = sid;
    }



    private AceFilter(){}

    public static AceFilter noFilter(){
        return new AceFilter();
    }

    // https://github.com/spring-projects/spring-security/issues/5401
    public boolean matches(AccessControlEntry ace){
        Sid aceSid = ace.getSid();
        if (principalsOnly){
            if (aceSid instanceof GrantedAuthoritySid)
                return false;
        }
        if (this.sid != null){
            boolean sidMatches = getSidString(aceSid).equals(this.sid);
            if (!sidMatches)
                return false;
        }
        if (!this.permissions.isEmpty())
            return this.permissions.contains(ace.getPermission());
        return true;
    }

}
