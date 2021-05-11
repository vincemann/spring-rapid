package com.github.vincemann.springrapid.acl.service;

import org.springframework.security.acls.model.Permission;

public class RapidPermissionStringConverter implements PermissionStringConverter {

    @Override
    public String convert(Permission permission) {
        String pattern = permission.getPattern().toLowerCase();
        if (pattern.contains(".a")){
            return "ADMINISTRATION";
        }else if(pattern.contains(".c")){
            return "CREATE";
        }else if (pattern.contains(".d")){
            return "DELETE";
        }else if(pattern.contains(".r")){
            return "READ";
        }else if(pattern.contains(".w")){
            return "WRITE";
        }else {
            throw new IllegalArgumentException("Unknown pattern: " + pattern);
        }
    }
}
