package com.github.vincemann.springrapid.acl.service;

import org.springframework.security.acls.model.Permission;

public interface PermissionStringConverter {

    public String convert(Permission permission);
}
