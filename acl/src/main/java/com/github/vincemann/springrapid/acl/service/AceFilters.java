package com.github.vincemann.springrapid.acl.service;

import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;

public class AceFilters {


    public static Predicate<AccessControlEntry> permissions(Permission... permissions){
        return permissions(Set.of(permissions));
    }

    public static Predicate<AccessControlEntry> permissions(Set<Permission> permissions){
        return ace -> permissions.stream().anyMatch(permission -> ace.getPermission().equals(permission));
    }

    public static Predicate<AccessControlEntry> sids(Sid... sids){
        return ace -> Arrays.stream(sids).anyMatch(sid -> ace.getSid().equals(sid));
    }

    public static Predicate<AccessControlEntry> principals(String... sids){
        return ace -> Arrays.stream(sids).anyMatch(sid -> ace.getSid().equals(new PrincipalSid(sid)));
    }

    public static Predicate<AccessControlEntry> principalsOnly(){
        return ace -> ace.getSid() instanceof PrincipalSid;
    }
}
