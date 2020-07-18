package com.github.vincemann.springrapid.acl.proxy;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.CrudServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

/**
 * Does basic acl permission checks on crud Methods defined in {@link com.github.vincemann.springrapid.core.service.CrudService}.
 * @see com.github.vincemann.springrapid.acl.proxy.DefaultSecurityServiceExtension
 */
public class DefaultSecurityServiceExtensionImpl extends SecurityServiceExtension<CrudService> implements CrudServiceExtension<CrudService> {

    private Class<?> entityClass;

    public DefaultSecurityServiceExtensionImpl() {
        this.entityClass = getLast().getEntityClass();
    }

    @Override
    public Optional findById(Serializable id) throws BadEntityException {
        getSecurityChecker().checkPermission(id,entityClass,getReadPermission());
        return getNext().findById(id);
    }

    @Override
    public IdentifiableEntity update(IdentifiableEntity entity, Boolean full) throws EntityNotFoundException, BadEntityException {
        getSecurityChecker().checkPermission(entity.getId(),entityClass,getWritePermission());
        return getNext().update(entity,full);
    }

    @Override
    public Set findAll() {
        Set<IdentifiableEntity> entities = getNext().findAll();
        return getSecurityChecker().filter(entities,getReadPermission());
    }

    @Override
    public void deleteById(Serializable id) throws EntityNotFoundException, BadEntityException {
        getSecurityChecker().checkPermission(id,entityClass,getDeletePermission());
        getNext().deleteById(id);
    }

}
