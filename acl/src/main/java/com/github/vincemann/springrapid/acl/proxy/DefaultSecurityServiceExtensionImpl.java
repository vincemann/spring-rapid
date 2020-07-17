package com.github.vincemann.springrapid.acl.proxy;

import com.github.vincemann.springrapid.acl.proxy.SecurityServiceExtension;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

/**
 * Does basic acl permission checks on crud Methods defined in {@link com.github.vincemann.springrapid.core.service.CrudService}.
 * @see com.github.vincemann.springrapid.acl.proxy.DefaultSecurityServiceExtension
 */
public class DefaultSecurityServiceExtensionImpl extends SecurityServiceExtension<CrudService> implements CrudService {

    private Class<?> entityClass;

    public DefaultSecurityServiceExtensionImpl() {
        this.entityClass = getChain().getLast().getEntityClass();
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
    public IdentifiableEntity save(IdentifiableEntity entity) throws BadEntityException {
        return getNext().save(entity);
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

    @Override
    public Class getEntityClass() {
        return getNext().getEntityClass();
    }

    @Override
    public CrudRepository getRepository() {
        return getNext().getRepository();
    }

    @Override
    public Class<?> getTargetClass() {
        return getNext().getTargetClass();
    }
}
