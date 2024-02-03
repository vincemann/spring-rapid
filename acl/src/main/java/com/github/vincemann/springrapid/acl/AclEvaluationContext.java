package com.github.vincemann.springrapid.acl;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.util.EntityLocator;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.acls.model.Permission;

import java.io.Serializable;
import java.util.Optional;

/**
 * Much simpler version of {@link org.springframework.expression.EvaluationContext} only for acl evaluations and adjusted to this library.
 * Stores relevant information for one explicit acl checking call.
 * I.E. does authenticated user x have permission READ over target entity y.
 * <p>
 * Combine with {@link com.github.vincemann.springrapid.core.sec.RapidSecurityContext} to also use information about authenticated.
 * <p>
 * Either {@link this#targetEntity} is set or id and entityClass.
 * You can use {@link this#resolveEntity(EntityLocator)} to get target entity acl context is created for.
 */
@Getter
public class AclEvaluationContext {
    private Permission checkedPermission;
    private IdentifiableEntity<?> targetEntity;
    private Serializable id;
    private Class entityClass;



    public IdentifiableEntity<?> resolveEntity(EntityLocator entityLocator){
        Optional<IdentifiableEntity> entity = entityLocator.findEntity(entityClass, id);
        if (entity.isEmpty())
            throw new IllegalArgumentException("cannot find entity acl context is created for");
        return entity.get();
    }

    @Builder
    public AclEvaluationContext(Permission checkedPermission, IdentifiableEntity<?> targetEntity, Serializable id, Class entityClass) {
        this.checkedPermission = checkedPermission;
        this.targetEntity = targetEntity;
        this.id = id;
        this.entityClass = entityClass;
    }

}
