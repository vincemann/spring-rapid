package io.github.vincemann.springrapid.acl.util;

import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;

public class PermissionUtils {

    public static String toString(Permission permission){
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
            return "UNKNOWN";
        }
    }
}
