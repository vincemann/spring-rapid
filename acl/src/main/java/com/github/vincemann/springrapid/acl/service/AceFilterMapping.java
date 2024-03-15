package com.github.vincemann.springrapid.acl.service;

import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;

import java.util.function.Predicate;

public class AceFilterMapping {
    private Predicate<AccessControlEntry> filter;
    private Permission permission;
    private Sid sid;

    public AceFilterMapping(Predicate<AccessControlEntry> filter, Permission permission, Sid sid) {
        this.filter = filter;
        this.permission = permission;
        this.sid = sid;
    }



    public Sid getSid() {
        return sid;
    }

    public Predicate<AccessControlEntry> getFilter() {
        return filter;
    }

    public Permission getPermission() {
        return permission;
    }

    public static Builder builder(){
        return new Builder();
    }

    public static final class Builder {
        private Predicate<AccessControlEntry> filter;
        private Permission permission;
        private Sid sid;

        private Builder() {
        }

        public Builder filter(Predicate<AccessControlEntry> filter) {
            this.filter = filter;
            return this;
        }

        public Builder permission(Permission permission) {
            this.permission = permission;
            return this;
        }

        public Builder sid(Sid sid) {
            this.sid = sid;
            return this;
        }

        public AceFilterMapping build() {
            return new AceFilterMapping(filter, permission, sid);
        }
    }
}
