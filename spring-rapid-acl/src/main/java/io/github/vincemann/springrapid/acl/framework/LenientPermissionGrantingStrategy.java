package io.github.vincemann.springrapid.acl.framework;

import io.github.vincemann.springrapid.acl.util.PermissionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.domain.AuditLogger;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy;
import org.springframework.security.acls.model.*;

import java.util.List;

/**
 * No exact Permission match is needed.
 * If Entity has create Permission for example (mask = 4), then it implicitly also has read (mask = 1) permission,
 * bc this permission is below create in the Hierarchy.
 * @see BasePermission
 */
@Slf4j
public class LenientPermissionGrantingStrategy extends DefaultPermissionGrantingStrategy {

    public LenientPermissionGrantingStrategy(AuditLogger auditLogger) {
        super(auditLogger);
    }

    @Override
    protected boolean isGranted(AccessControlEntry ace, Permission p) {
        if (ace.isGranting() && p.getMask() != 0) {
            //is granting check erstmal nur den boolean ab aus der db, der eig immer true ist
            //der zweite check zeigt nur, dass hier überhaupt nh valid permission angefragt wird...
            //jetzt gilt es zu checken, ob die permission p auch von ace getragen wird
            int givenPermissionMask = ace.getPermission().getMask();
            int requestedPermissionMask = p.getMask();
//            //Admin does not need to have r & w & c &d&a permission, but only a
//            if(givenPermissionMask== BasePermission.ADMINISTRATION.getMask()){
//                return true;
//            }
            log.debug("checking ace: id:"+ ace.getId() + ", " + PermissionUtils.toString(ace.getPermission()) + ", owner sid of permission: " + ace.getSid());
            log.debug("given permission: " + PermissionUtils.toString(ace.getPermission()) +", mask: " + givenPermissionMask);
            log.debug("requested permission: " + PermissionUtils.toString(p)+", mask: " + requestedPermissionMask);
            return givenPermissionMask >= requestedPermissionMask;
            //return (ace.getPermission().getMask() & p.getMask()) == 0;
        } else {
            //return ace.getPermission().getMask() == p.getMask();
            return false;
        }
    }
}
