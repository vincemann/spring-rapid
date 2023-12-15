package com.github.vincemann.springrapid.acl.framework;

import com.github.vincemann.springrapid.acl.util.AclUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.domain.AuditLogger;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy;
import org.springframework.security.acls.model.*;

import java.util.List;

/**
 * No exact Permission match is needed, if ADMIN permission is given.
 * Otherwise exactly like {@link DefaultPermissionGrantingStrategy}.
 */
@Slf4j
public class AdminDefaultPermissionGrantingStrategy extends DefaultPermissionGrantingStrategy {

    private final transient AuditLogger auditLogger;
//    private PermissionStringConverter permissionStringConverter;

    public AdminDefaultPermissionGrantingStrategy(AuditLogger auditLogger) {
        super(auditLogger);
        this.auditLogger = auditLogger;
    }


    @Override
    public boolean isGranted(Acl acl, List<Permission> permission, List<Sid> sids,
                             boolean administrativeMode) throws NotFoundException {

        final List<AccessControlEntry> aces = acl.getEntries();

        AccessControlEntry firstRejection = null;

        for (Permission p : permission) {
            for (Sid sid : sids) {
                // Attempt to find exact match for this permission mask and SID
                boolean scanNextSid = true;

                for (AccessControlEntry ace : aces) {

                    if (ace.getSid().equals(sid)) {
                        if (log.isDebugEnabled()){
                           log.debug("checking ace: " + AclUtils.aceToString(ace));
                        }
                        if (isGranted(ace, p)) {

                            // Found a matching ACE, so its authorization decision will
                            // prevail
                            if (ace.isGranting()) {
                                // Success
                                if (!administrativeMode) {
                                    auditLogger.logIfNeeded(true, ace);
                                }
                                log.debug("Match! Sid: " + sid + " has sufficient permissions for operation");
                                return true;
                            } else {
                                log.warn("Sid: " + sid + " is not granting");
                            }

                            // Failure for this permission, so stop search
                            // We will see if they have a different permission
                            // (this permission is 100% rejected for this SID)
                            if (firstRejection == null) {
                                // Store first rejection for auditing reasons
                                firstRejection = ace;
                            }

                            scanNextSid = false; // helps break the loop

                            break; // exit aces loop
                        }else {
                            log.debug("Sid: " + sid + " does not have sufficient permissions for operation...");
                        }
                    }
                }

                if (!scanNextSid) {
                    break; // exit SID for loop (now try next permission)
                }
            }
        }

        if (firstRejection != null) {
            // We found an ACE to reject the request at this point, as no
            // other ACEs were found that granted a different permission
            if (!administrativeMode) {
                auditLogger.logIfNeeded(false, firstRejection);
            }

            return false;
        }

        // No matches have been found so far
        if (acl.isEntriesInheriting() && (acl.getParentAcl() != null)) {
            // We have a parent, so let them try to find a matching ACE
            return acl.getParentAcl().isGranted(permission, sids, false);
        }
        else {
            // We either have no parent, or we're the uppermost parent
            throw new NotFoundException(
                    "Unable to locate a matching ACE for passed permissions and SIDs");
        }
    }

    @Override
    protected boolean isGranted(AccessControlEntry ace, Permission p) {
        if (ace.isGranting() && p.getMask() != 0) {
            //is granting check erstmal nur den boolean ab aus der db, der eig immer true ist
            //der zweite check zeigt nur, dass hier überhaupt nh valid permission angefragt wird...
            //jetzt gilt es zu checken, ob die permission p auch von ace getragen wird
            int givenPermissionMask = ace.getPermission().getMask();
            int requestedPermissionMask = p.getMask();

            // for admin treat in hierarchy mode -> request READ and only one ace with ADMIN will be sufficient
            if (givenPermissionMask == BasePermission.ADMINISTRATION.getMask() && givenPermissionMask >= requestedPermissionMask){
                return true;
            }
            return super.isGranted(ace,p);
//            //Admin does not need to have r & w & c &d&a permission, but only a
//            if(givenPermissionMask== BasePermission.ADMINISTRATION.getMask()){
//                return true;
//            }
//            log.debug("Requested permission: " + PermissionUtils.toString(p)/*+", mask: " + requestedPermissionMask*/);
//            log.debug("Checking ace with id:"+ ace.getId() /*", " + PermissionUtils.toString(ace.getPermission()) +*/);
//            log.trace("Content of that ace: " + ace);
//            log.debug("Sid of ace: " + ace.getSid()+ " has permission: " + PermissionUtils.toString(ace.getPermission()) /*+", mask: " + givenPermissionMask*/);

//            return givenPermissionMask >= requestedPermissionMask;
//            //return (ace.getPermission().getMask() & p.getMask()) == 0;
//        } else {
//            //return ace.getPermission().getMask() == p.getMask();
//            return false;
        }else {
            return false;
        }
    }

//    @Autowired
//    public void injectPermissionStringConverter(PermissionStringConverter permissionStringConverter) {
//        this.permissionStringConverter = permissionStringConverter;
//    }
}
