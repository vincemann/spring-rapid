package com.github.vincemann.springrapid.acl;

import com.github.vincemann.springrapid.acl.util.AclUtils;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.context.ServiceCallContext;
import com.github.vincemann.springrapid.core.service.context.ServiceCallContextHolder;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.EntityLocator;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.acls.model.Permission;

import java.io.Serializable;

/**
 * Much simpler version of {@link org.springframework.expression.EvaluationContext} only for acl evaluations and adjusted to this library.
 * stores relevant information for one explicit acl checking call.
 * I.E. does authenticated have permission READ over target entity y.
 *
 * Combine with {@link com.github.vincemann.springrapid.core.security.RapidSecurityContext} to also gain information about authenticated.
 *
 * Either {@link this#targetEntity} is set or id and entityClass.
 * You can use {@link this#resolveTargetEntity()} as a convenience method to get target entity from acl context.
 *
 */
@Getter
public class AclEvaluationContext {
    private Permission checkedPermission;
    private IdentifiableEntity<?> targetEntity;
    private Serializable id;
    private Class entityClass;

    public IdentifiableEntity<?> resolveEntity(){
        if (targetEntity != null)
            return targetEntity;
        ServiceCallContext serviceContext = ServiceCallContextHolder.getContext();
        if (serviceContext.getEntityClass().equals(entityClass))
            targetEntity = serviceContext.resolveEntity();
        else {
            targetEntity = forceResolveEntity();
        }
        return targetEntity;
    }

    public IdentifiableEntity<?> forceResolveEntity(){
        try {
            return EntityLocator.findEntity(entityClass,id);
        } catch (EntityNotFoundException e) {
            throw new IllegalArgumentException("Could not find target entity for acl operation");
        }
    }


    @Builder
    public AclEvaluationContext(Permission checkedPermission, IdentifiableEntity<?> targetEntity, Serializable id, Class entityClass) {
        this.checkedPermission = checkedPermission;
        this.targetEntity = targetEntity;
        this.id = id;
        this.entityClass = entityClass;
    }

}
