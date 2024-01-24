package com.github.vincemann.springrapid.acl.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;

public class RapidPermissionStringConverter implements PermissionStringConverter {

    @Cacheable(value = "permissionStringMapping")
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
        }
        else {
            throw new IllegalArgumentException("Unknown pattern: " + pattern);
        }
    }

    @Cacheable(value = "permissionStringMapping")
    @Override
    public Permission convert(String permission) {
        if (permission.equals("ADMINISTRATION")){
            return BasePermission.ADMINISTRATION;
        }else if(permission.equals("CREATE")){
            return BasePermission.CREATE;
        }else if (permission.equals("DELETE")){
            return BasePermission.DELETE;
        }else if(permission.equals("READ")){
            return BasePermission.READ;
        }else if(permission.equals("WRITE")){
            return BasePermission.WRITE;
        }else {
            throw new IllegalArgumentException("Unknown permission string: " + permission);
        }
    }
}
