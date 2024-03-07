package com.github.vincemann.springrapid.acl.service;

import com.google.common.collect.Sets;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;

import java.util.HashSet;
import java.util.Set;

import static com.github.vincemann.springrapid.acl.util.AclUtils.sidToString;

public class AceFilter {

    private Set<Permission> permissions;
    private Set<String> sids = new HashSet<>();

    private Boolean principalsOnly = Boolean.TRUE;
    private Set<String> ignoredSids = new HashSet<>();

    private AceFilter(){}

    public static AceFilter noFilter(){
        return new AceFilter();
    }

    public static AceFilterBuilder builder(){
        return new AceFilterBuilder();
    }

    public static class AceFilterBuilder {
        private AceFilter aceFilter;

        AceFilterBuilder() {
            this.aceFilter = new AceFilter();
        }

        public AceFilterBuilder permissions(Permission... permission) {
            aceFilter.permissions = Sets.newHashSet(permission);
            return this;
        }

        public AceFilterBuilder sid(String sid) {
            aceFilter.sids.add(sid);
            return this;
        }

        public AceFilterBuilder sids(Set<String> sids) {
            aceFilter.sids.addAll(sids);
            return this;
        }

        public AceFilterBuilder ignoredSid(String ignoredSid) {
            aceFilter.ignoredSids.add(ignoredSid);
            return this;
        }

        public AceFilterBuilder ignoredSids(Set<String> ignoredSids) {
            aceFilter.ignoredSids.addAll(ignoredSids);
            return this;
        }


        public AceFilterBuilder principalsOnly(Boolean principalsOnly) {
            aceFilter.principalsOnly = principalsOnly;
            return this;
        }

        public AceFilter build() {
            return aceFilter;
        }
    }

    // https://github.com/spring-projects/spring-security/issues/5401
    public boolean matches(AccessControlEntry ace){
        Sid aceSid = ace.getSid();
        if (principalsOnly){
            if (aceSid instanceof GrantedAuthoritySid)
                return false;
        }
        String aceSidString = sidToString(aceSid);
        if (!this.ignoredSids.isEmpty()){
            boolean ignored = this.ignoredSids.contains(aceSidString);
            if (ignored)
                return false;
        }
        if (!this.sids.isEmpty()){
            boolean sidMatches = this.sids.contains(aceSidString);
            if (!sidMatches)
                return false;
        }
        if (!this.permissions.isEmpty())
            return this.permissions.contains(ace.getPermission());
        return true;
    }



    public Set<Permission> getPermissions() {
        return permissions;
    }

    public Set<String> getSids() {
        return sids;
    }

    public Boolean getPrincipalsOnly() {
        return principalsOnly;
    }

    public Set<String> getIgnoredSids() {
        return ignoredSids;
    }
}
