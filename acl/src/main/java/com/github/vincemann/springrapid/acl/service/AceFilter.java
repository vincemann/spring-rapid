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

    public AceFilter(String sid, Permission... permissions) {
        this.permissions = Sets.newHashSet(permissions);
        this.sid = sid;
    }

    @Builder
    public AceFilter(String sid, Set<Permission> permissions) {
        this.permissions = permissions;
        this.sid = sid;
    }



    private AceFilter(){}

    public static AceFilter noFilter(){
        return new AceFilter();
    }

    // https://github.com/spring-projects/spring-security/issues/5401
    public boolean matches(AccessControlEntry ace){
        if (this.sid != null){
            boolean sidMatches = getSidString(ace.getSid()).equals(this.sid);
            if (!sidMatches)
                return false;
        }
        if (!this.permissions.isEmpty())
            return this.permissions.contains(ace.getPermission());
        return true;
    }

}
