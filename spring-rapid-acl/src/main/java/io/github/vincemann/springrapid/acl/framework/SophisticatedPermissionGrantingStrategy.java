package io.github.vincemann.springrapid.acl.framework;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.domain.AuditLogger;
import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy;
import org.springframework.security.acls.model.*;

import java.util.List;

@Slf4j
public class SophisticatedPermissionGrantingStrategy extends DefaultPermissionGrantingStrategy {

    public SophisticatedPermissionGrantingStrategy(AuditLogger auditLogger) {
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
            log.debug("given permission: " + ace.getPermission().toString() +", mask: " + givenPermissionMask);
            log.debug("requested permission: " + p.toString()+", mask: " + requestedPermissionMask);
            return givenPermissionMask >= requestedPermissionMask;
            //return (ace.getPermission().getMask() & p.getMask()) == 0;
        } else {
            //return ace.getPermission().getMask() == p.getMask();
            return false;
        }
    }
}
