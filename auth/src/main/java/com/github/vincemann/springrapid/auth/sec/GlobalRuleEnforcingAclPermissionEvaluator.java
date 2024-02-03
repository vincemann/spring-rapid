package com.github.vincemann.springrapid.auth.sec;

import com.github.vincemann.springrapid.acl.framework.VerboseAclPermissionEvaluator;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import com.github.vincemann.springrapid.core.util.EntityLocator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * call {@link GlobalSecurityRule#checkAccess(IdentifiableEntity, Object, RapidSecurityContext)} on each permission evaluation.
 */
@Slf4j
public class GlobalRuleEnforcingAclPermissionEvaluator extends VerboseAclPermissionEvaluator {

    private List<GlobalSecurityRule> globalSecurityRules = new ArrayList<>();
    private RapidSecurityContext securityContext;

    private EntityLocator entityLocator;




    public GlobalRuleEnforcingAclPermissionEvaluator(AclService aclService, List<GlobalSecurityRule> globalSecurityRules, RapidSecurityContext securityContext) {
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
        Boolean allowAccess = performGlobalSecurityChecks((IdentifiableEntity<?>) targetDomainObject,permission);
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
            Optional<IdentifiableEntity> entity = entityLocator.findEntity(Class.forName(targetType), targetId);
            if (entity.isEmpty())
                throw new IllegalArgumentException("entity permission gets checked for does not exist, check before checking acl info");
            Boolean allowAccess = performGlobalSecurityChecks(entity.get(),permission);
            if (allowAccess != null)
                return allowAccess;
            else
                return super.hasPermission(authentication,targetId,targetType,permission);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }



    public Boolean performGlobalSecurityChecks(IdentifiableEntity<?> entity, Object permission){
        for (GlobalSecurityRule globalSecurityRule : globalSecurityRules) {
            Boolean allowAccess = globalSecurityRule.checkAccess(entity,permission,securityContext);
            if (allowAccess != null)
                return allowAccess;
        }

        return null;
    }

    @Autowired
    public void setEntityLocator(EntityLocator entityLocator) {
        this.entityLocator = entityLocator;
    }
}
