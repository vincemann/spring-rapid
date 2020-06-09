package com.github.vincemann.springrapid.acl.proxy.rules;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.CalledByProxy;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;

/**
 * Does basic acl permission checks on crud Methods defined in {@link com.github.vincemann.springrapid.core.service.CrudService}.
 * @see DefaultServiceSecurityRule
 */
public class DefaultCrudSecurityRule extends ServiceSecurityRule {


    @CalledByProxy
    public void preAuthorizeUpdate(IdentifiableEntity<? extends Serializable> entity, boolean full) throws BadEntityException {
        getSecurityChecker().checkPermission(entity.getId(),entity.getClass(),getWritePermission());
    }

    @CalledByProxy
    public void preAuthorizeFindById(Serializable id, Class entityClass){
        getSecurityChecker().checkPermission(id,entityClass,getReadPermission());
    }

    @CalledByProxy
    public void preAuthorizeDelete(IdentifiableEntity<? extends Serializable> entity){
        getSecurityChecker().checkPermission(entity.getId(),entity.getClass(),getDeletePermission());
    }

    @CalledByProxy
    public void preAuthorizeDeleteById(Serializable id,Class entityClass){
        getSecurityChecker().checkPermission(id,entityClass,getDeletePermission());
    }

    @CalledByProxy
    public Collection<? extends IdentifiableEntity<? extends Serializable>>  postAuthorizeFindAll(Collection<? extends IdentifiableEntity<? extends Serializable>> entities){
        return getSecurityChecker().filter(entities,getReadPermission());
    }


}
