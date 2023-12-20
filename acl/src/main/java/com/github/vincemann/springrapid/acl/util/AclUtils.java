package com.github.vincemann.springrapid.acl.util;

import com.github.vincemann.springrapid.acl.AclEvaluationContext;
import com.github.vincemann.springrapid.acl.service.PermissionStringConverter;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.*;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class AclUtils {

    private static PermissionStringConverter permissionStringConverter;

    public static void setup(PermissionStringConverter permissionStringConverter) {
        AclUtils.permissionStringConverter = permissionStringConverter;
    }


    private AclUtils() {
    }

    public static String sidToString(Sid sid) {
        if (sid instanceof PrincipalSid) {
            return ((PrincipalSid) sid).getPrincipal();
        } else if (sid instanceof GrantedAuthoritySid) {
            return ((GrantedAuthoritySid) sid).getGrantedAuthority();
        } else {
            throw new IllegalArgumentException("unknown sid, overwrite this function");
        }
    }

    public static String aclToString(Acl acl) {
        StringBuilder result = new StringBuilder("__________________________________________\n");
        result.append("acl of: ").append(AclUtils.objectIdentityToString(acl.getObjectIdentity())).append("\n");

        final int[] count = {0};
        acl.getEntries().stream()
                .peek(e -> count[0] += 1)
                .map(AclUtils::aceToString)
                .forEach(e -> result.append(count[0]).append(" ").append(e).append("\n"));

        result.append("__________________________________________");
        return result.toString();
    }

    public static String permissionsToString(Permission... permissions) {
        return Arrays.stream(permissions).map(p -> permissionStringConverter.convert(p)).collect(Collectors.toSet()).toString();
    }

    public static String permissionToString(Permission permission) {
        return permissionStringConverter.convert(permission);
    }

    public static String aceToString(AccessControlEntry ace) {
        return "[ " + AclUtils.sidToString(ace.getSid()) + " -> " + permissionStringConverter.convert(ace.getPermission()) + " ]";
    }

    public static String objectIdentityToString(ObjectIdentity oid) {
        // Split the full class name by the period ('.') character
        String[] parts = oid.getType().split("\\.");

        // Get the last part of the split string, which is the simple class name
        String simpleClassName = parts[parts.length - 1];
        return "[ " + simpleClassName + " : " + oid.getIdentifier() + " ]";
    }

    /**
     * checks if aces sid and aces permission is equal.
     */
    public static boolean isAcePresent(AccessControlEntry ace, Acl acl) {
        // todo mabye add parallel arg here, and for entities with a ton of aces set flag to true and user parallelstream
        return isAcePresent(ace.getPermission(), ace.getSid(), acl);
    }

    public static boolean isAcePresent(Permission permission, Sid sid, Acl acl) {
        // todo mabye add parallel arg here, and for entities with a ton of aces set flag to true and user parallelstream
        return acl.getEntries().stream().filter(entry -> {
            return entry.getPermission().equals(permission) &&
                    entry.getSid().equals(sid);
        }).findAny().isPresent();
    }
}
