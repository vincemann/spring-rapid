package com.github.vincemann.springrapid.acl.proxy;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

public class AbstractSecurityCrudServiceExtension <T extends CrudService> extends SecurityServiceExtension<T> implements CrudService{

    private Class<?> entityClass;

    public AbstractSecurityCrudServiceExtension() {
        this.entityClass = getChain().getLast().getEntityClass();
    }

    @Override
    public Optional findById(Serializable id) throws BadEntityException {
        return getNext().findById(id);
    }

    @Override
    public IdentifiableEntity update(IdentifiableEntity entity, Boolean full) throws EntityNotFoundException, BadEntityException {
        return getNext().update(entity,full);
    }

    @Override
    public IdentifiableEntity save(IdentifiableEntity entity) throws BadEntityException {
        return getNext().save(entity);
    }

    @Override
    public Set findAll() {
        return getNext().findAll();
    }

    @Override
    public void deleteById(Serializable id) throws EntityNotFoundException, BadEntityException {
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
