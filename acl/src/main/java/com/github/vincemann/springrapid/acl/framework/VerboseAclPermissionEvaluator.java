package com.github.vincemann.springrapid.acl.framework;

import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.springrapid.acl.framework.oidresolve.ObjectIdentityResolver;
import com.github.vincemann.springrapid.acl.framework.oidresolve.RapidObjectIdentityResolver;
import com.github.vincemann.springrapid.acl.framework.oidresolve.UnresolvableOidException;
import com.github.vincemann.springrapid.acl.service.PermissionStringConverter;
import com.github.vincemann.springrapid.acl.util.AclUtils;
import com.github.vincemann.springrapid.core.IdConverter;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.stream.Collectors;

/**
 * Adds more verbose logging
 */
@Slf4j
public class VerboseAclPermissionEvaluator extends AclPermissionEvaluator implements AopLoggable {

    private final AclService aclService;
    private ObjectIdentityRetrievalStrategy objectIdentityRetrievalStrategy = new ObjectIdentityRetrievalStrategyImpl();
    private ObjectIdentityGenerator objectIdentityGenerator = new ObjectIdentityRetrievalStrategyImpl();
    private SidRetrievalStrategy sidRetrievalStrategy = new SidRetrievalStrategyImpl();
    private PermissionFactory permissionFactory = new DefaultPermissionFactory();
    private ObjectIdentityResolver objectIdentityResolver;
    private PermissionStringConverter permissionStringConverter;

    public VerboseAclPermissionEvaluator(AclService aclService) {
        super(aclService);
        this.aclService = aclService;
    }

    /**
     * Called by Spring Security to evaluate the permission
     *
     * @param auth               Spring Security authentication object,
     *                           from which the current-user can be found
     * @param targetDomainObject Object for which permission is being checked
     * @param permission         What permission is being checked for, e.g. 'WRITE'
     * @see org.springframework.security.acls.domain.BasePermission
     */
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

    @Override
    public boolean hasPermission(Authentication authentication,
                                 Serializable targetId, String targetType, Object permission) {
        ObjectIdentity objectIdentity = objectIdentityGenerator.createObjectIdentity(
                targetId, targetType);
        return checkPermission(authentication, objectIdentity, permission);
    }


    protected boolean checkPermission(Authentication authentication, ObjectIdentity oid,
                                    Object permission) {
        // Obtain the SIDs applicable to the principal
        List<Sid> sids = sidRetrievalStrategy.getSids(authentication);
        List<Permission> requiredPermissions = resolvePermission(permission);
        List<String> stringPermissions = requiredPermissions.parallelStream().map(p -> permissionStringConverter.convert(p)).collect(Collectors.toList());


        IdentifiableEntity resolvedOid = null;
        if (log.isTraceEnabled()) {
            try {
                resolvedOid = objectIdentityResolver.resolve(oid);
            } catch (UnresolvableOidException e) {
                log.warn("Could not resolve Oid for trace logging: " + e.getMessage());
            }
        }
        String name = RapidSecurityContext.getName();
        //expensive trace logging

        if (log.isTraceEnabled()) {
            if (resolvedOid!=null) {
                log.trace("Checking if User: " + name + " has permissions: " + stringPermissions + "\n" +
                        "that are required for an operation over: " +resolvedOid +" ?"
                );
            }else {
                log.trace("Checking if User: " + name + " has permissions: " + stringPermissions + "\n" +
                        "that are required for an operation over: " + oid + " ?"
                );
            }
        } else {
            log.debug("Checking if User: " + name + " has permissions: " + stringPermissions + "\n" +
                    "that are required for an operation over: " + oid + " ?"
            );
        }

        log.debug("User has sid's: " + sids);

        try {
            // Lookup only ACL for SIDs we're interested in
            Acl acl = aclService.readAclById(oid, sids);
            if (log.isDebugEnabled()){
                log.debug("acl in question:");
                log.debug(AclUtils.aclToString(acl));
            }

            if (acl.isGranted(requiredPermissions, sids, false)) {
                log.debug("Access granted");
                return true;
            }

            log.debug("No Sid has sufficient permissions for operation");
            log.debug("Access not granted");

        } catch (NotFoundException nfe) {
            log.debug("No ACL found for sids of user.");
            log.debug("Access not granted");
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
            } catch (IllegalArgumentException notfound) {
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

    @Autowired
    public void injectObjectIdentityResolver(ObjectIdentityResolver objectIdentityResolver) {
        this.objectIdentityResolver = objectIdentityResolver;
    }

    @Autowired
    public void injectPermissionStringConverter(PermissionStringConverter permissionStringConverter) {
        this.permissionStringConverter = permissionStringConverter;
    }
}
