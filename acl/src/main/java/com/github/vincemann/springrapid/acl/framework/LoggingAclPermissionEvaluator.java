package com.github.vincemann.springrapid.acl.framework;

import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.LogInteraction;
import com.github.vincemann.springrapid.acl.framework.oidresolve.RapidObjectIdentityResolver;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.DefaultPermissionFactory;
import org.springframework.security.acls.domain.ObjectIdentityRetrievalStrategyImpl;
import org.springframework.security.acls.domain.PermissionFactory;
import org.springframework.security.acls.domain.SidRetrievalStrategyImpl;
import org.springframework.security.acls.model.*;
import org.springframework.security.core.Authentication;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Slf4j
public class LoggingAclPermissionEvaluator extends AclPermissionEvaluator implements AopLoggable {

    private final AclService aclService;
    private ObjectIdentityRetrievalStrategy objectIdentityRetrievalStrategy = new ObjectIdentityRetrievalStrategyImpl();
    private ObjectIdentityGenerator objectIdentityGenerator = new ObjectIdentityRetrievalStrategyImpl();
    private SidRetrievalStrategy sidRetrievalStrategy = new SidRetrievalStrategyImpl();
    private PermissionFactory permissionFactory = new DefaultPermissionFactory();
    private RapidObjectIdentityResolver objectIdentityResolver;

    public LoggingAclPermissionEvaluator(AclService aclService) {
        super(aclService);
        this.aclService=aclService;
        log.debug("created");
    }

    /**
     * Called by Spring Security to evaluate the permission
     *
     * @param auth	Spring Security authentication object,
     * 				from which the current-user can be found
     * @param targetDomainObject	Object for which permission is being checked
     * @param permission			What permission is being checked for, e.g. 'WRITE'
     * @see org.springframework.security.acls.domain.BasePermission
     */
    @LogInteraction
    @Override
    public boolean hasPermission(Authentication auth,
                                 Object targetDomainObject, Object permission) {

        // if no domain object is provided,
        // let's pass, allowing the service method
        // to throw a more sensible error message
        if (targetDomainObject == null)
            return true;

        ObjectIdentity objectIdentity = objectIdentityRetrievalStrategy
                .getObjectIdentity(targetDomainObject);

        return checkPermission(auth, objectIdentity, permission);
    }

    @LogInteraction
    @Override
    public boolean hasPermission(Authentication authentication,
                                 Serializable targetId, String targetType, Object permission) {
        ObjectIdentity objectIdentity = objectIdentityGenerator.createObjectIdentity(
                targetId, targetType);
        return checkPermission(authentication, objectIdentity, permission);
    }


    private boolean checkPermission(Authentication authentication, ObjectIdentity oid,
                                    Object permission) {
        // Obtain the SIDs applicable to the principal

        List<Sid> sids = sidRetrievalStrategy.getSids(authentication);
        List<Permission> requiredPermission = resolvePermission(permission);

        if (log.isTraceEnabled()){

            LemonUserDto user = LecwUtils.currentUser();
            log.trace("Does User: " + user.getEmail() + " have permissions: " + requiredPermission +"\n"+
                    "that are required for an operation over: " +
            );
        }

        try {
            // Lookup only ACLs for SIDs we're interested in
            Acl acl = aclService.readAclById(oid, sids);

            if (acl.isGranted(requiredPermission, sids, false)) {
                if (debug) {
                    logger.debug("Access is granted");
                }

                return true;
            }

            if (debug) {
                logger.debug("Returning false - ACLs returned, but insufficient permissions for this principal");
            }

        }
        catch (NotFoundException nfe) {
            if (debug) {
                logger.debug("Returning false - no ACLs apply for this principal");
            }
        }

        return false;

    }

    List<Permission> resolvePermission(Object permission) {
        if (permission instanceof Integer) {
            return Arrays.asList(permissionFactory.buildFromMask((Integer) permission));
        }

        if (permission instanceof Permission) {
            return Arrays.asList((Permission) permission);
        }

        if (permission instanceof Permission[]) {
            return Arrays.asList((Permission[]) permission);
        }

        if (permission instanceof String) {
            String permString = (String) permission;
            Permission p;

            try {
                p = permissionFactory.buildFromName(permString);
            }
            catch (IllegalArgumentException notfound) {
                p = permissionFactory.buildFromName(permString.toUpperCase(Locale.ENGLISH));
            }

            if (p != null) {
                return Arrays.asList(p);
            }

        }
        throw new IllegalArgumentException("Unsupported permission: " + permission);
    }

    protected AclService getAclService() {
        return aclService;
    }

    protected ObjectIdentityRetrievalStrategy getObjectIdentityRetrievalStrategy() {
        return objectIdentityRetrievalStrategy;
    }

    protected ObjectIdentityGenerator getObjectIdentityGenerator() {
        return objectIdentityGenerator;
    }

    protected SidRetrievalStrategy getSidRetrievalStrategy() {
        return sidRetrievalStrategy;
    }

    protected PermissionFactory getPermissionFactory() {
        return permissionFactory;
    }

    protected CrudServiceLocator getCrudServiceLocator() {
        return crudServiceLocator;
    }
}
