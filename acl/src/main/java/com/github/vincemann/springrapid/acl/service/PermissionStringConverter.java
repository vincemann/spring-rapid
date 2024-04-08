package com.github.vincemann.springrapid.acl.service;

import org.springframework.security.acls.model.Permission;

public interface PermissionStringConverter {

    String convert(Permission permission);
    Permission convert(String permission);
}
