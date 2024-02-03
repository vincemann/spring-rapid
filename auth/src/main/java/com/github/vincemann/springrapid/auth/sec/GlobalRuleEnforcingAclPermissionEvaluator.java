package com.github.vincemann.springrapid.auth.sec;

import com.github.vincemann.springrapid.acl.AclEvaluationContext;
import com.github.vincemann.springrapid.acl.RapidAclSecurityContext;
import com.github.vincemann.springrapid.acl.framework.VerboseAclPermissionEvaluator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * call {@link GlobalSecurityRule#checkAccess(AclEvaluationContext)} on each permission evaluation.
 */
@Slf4j
public class GlobalRuleEnforcingAclPermissionEvaluator extends VerboseAclPermissionEvaluator {

    private List<GlobalSecurityRule> globalSecurityRules = new ArrayList<>();
    private RapidAclSecurityContext securityContext;




    public GlobalRuleEnforcingAclPermissionEvaluator(AclService aclService, List<GlobalSecurityRule> globalSecurityRules, RapidAclSecurityContext securityContext) {
        super(aclService);
        this.globalSecurityRules = globalSecurityRules;
        this.securityContext = securityContext;
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
        Boolean allowAccess = performGlobalSecurityChecks();
        if (allowAccess != null)
            return allowAccess;
        else
            return super.hasPermission(auth,targetDomainObject,permission);
    }

    @Transactional
    @Override
    public boolean hasPermission(Authentication authentication,
                                 Serializable targetId, String targetType, Object permission) {
        Boolean allowAccess = performGlobalSecurityChecks();
        if (allowAccess != null)
            return allowAccess;
        else
            return super.hasPermission(authentication,targetId,targetType,permission);
    }



    public Boolean performGlobalSecurityChecks(){
        for (GlobalSecurityRule globalSecurityRule : globalSecurityRules) {
            Boolean allowAccess = globalSecurityRule.checkAccess(securityContext.getAclContext());
            if (allowAccess != null)
                return allowAccess;
        }

        return null;
    }
}
