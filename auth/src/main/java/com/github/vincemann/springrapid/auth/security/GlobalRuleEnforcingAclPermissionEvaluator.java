package com.github.vincemann.springrapid.auth.security;

import com.github.vincemann.springrapid.acl.framework.VerboseAclPermissionEvaluator;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * call {@link GlobalSecurityRule#check(IdentifiableEntity, BasePermission)} on each permission evaluation.
 */
@Slf4j
public class GlobalRuleEnforcingAclPermissionEvaluator extends VerboseAclPermissionEvaluator {

    private List<GlobalSecurityRule> globalSecurityRules = new ArrayList<>();
    private CrudServiceLocator crudServiceLocator;



    public GlobalRuleEnforcingAclPermissionEvaluator(AclService aclService, List<GlobalSecurityRule> globalSecurityRules, CrudServiceLocator crudServiceLocator) {
        super(aclService);
        this.globalSecurityRules = globalSecurityRules;
        this.crudServiceLocator = crudServiceLocator;
    }

    /**
     * Called by Spring Security to evaluate the permission
     *
     * @param auth    Spring Security authentication object,
     * 				from which the current-user can be found
     * @param targetDomainObject    Object for which permission is being checked
     * @param permission            What permission is being checked for, e.g. 'WRITE'
     * @see org.springframework.security.acls.domain.BasePermission
     */
    @Transactional
    @Override
    public boolean hasPermission(Authentication auth,
                                 Object targetDomainObject, Object permission) {


        if (targetDomainObject == null)    // if no domain object is provided,
            return true;                // let's pass, allowing the service method
        // to throw a more sensible error message
        Boolean allowAccess = performGlobalSecurityChecks((IdentifiableEntity<?>) targetDomainObject, (BasePermission) permission);
        if (allowAccess != null)
            return allowAccess;
        else
            return super.hasPermission(auth,targetDomainObject,permission);
    }

    @Transactional
    @Override
    public boolean hasPermission(Authentication authentication,
                                 Serializable targetId, String targetType, Object permission) {
        try {
            Class<?> clazz = Class.forName(targetType);
            // Now 'clazz' contains the Class object for the specified class.
            Optional byId = crudServiceLocator.find((Class<? extends IdentifiableEntity>) clazz).findById(targetId);
            VerifyEntity.isPresent(byId,targetId,clazz);
            Boolean allowAccess = performGlobalSecurityChecks((IdentifiableEntity<?>) byId.get(), (BasePermission) permission);
            if (allowAccess != null)
                return allowAccess;
            else
                return super.hasPermission(authentication,targetId,targetType,permission);
        } catch (ClassNotFoundException e) {
            // Handle the exception if the class is not found.
            throw new IllegalArgumentException("Cannot find class object of type string: " + targetType,e);
        } catch (BadEntityException e) {
            throw new IllegalArgumentException("target id is null",e);
        } catch (EntityNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }



    //todo is this really the right place for those kind of checks?
    public Boolean performGlobalSecurityChecks(IdentifiableEntity<?> target, BasePermission permission){
        globalSecurityRules.forEach(rule -> rule.check(target,permission));
        for (GlobalSecurityRule globalSecurityRule : globalSecurityRules) {
            Boolean allowAccess = globalSecurityRule.check(target, permission);
            if (allowAccess != null)
                return allowAccess;
        }

        return null;
//        //check if blocked or unverified
//        LemonAuthenticatedPrincipal principal = securityContext.currentPrincipal();
//        if(principal ==null){
//            return;
//        }
        //todo pack das auch woanders hin, evtl gibt es für blocked user trotzdem endpunkte wo die report einreichen könnne zb
//        String name = RapidSecurityContext.getName();
//        boolean blocked = RapidSecurityContext.hasRole(AuthRoles.BLOCKED);
//        log.debug("Checking if current User: " + name + " is blocked.");
//
//        if(blocked){
//            throw new AccessDeniedException("User is Blocked");
//        }
        //todo check das lieber in LemonSecurityCheckerUtil. Es kann doch auch aktionen geben, die ein unverified admin darf evlt..
//        if(principal.isAdmin() && principal.isUnverified()){
//            throw new AccessDeniedException("Admin is Unverified");
//        }
//        log.debug("Current User is NOT blocked or an unverified admin.");

    }

//    @Autowired
//    public void injectSecurityContext(RapidSecurityContext<LemonAuthenticatedPrincipal> securityContext) {
//        this.securityContext = securityContext;
//    }
}
