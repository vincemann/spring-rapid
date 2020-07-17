package com.github.vincemann.springrapid.core.proxy;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import lombok.Getter;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

@Getter
public abstract class AbstractCrudServiceExtension<S extends CrudService<E,Id,?>,E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends ServiceExtension<S>
            implements CrudService<E,Id,CrudRepository<E,Id>>{

    private Class<?> entityClass;

    public AbstractCrudServiceExtension() {
        this.entityClass = getChain().getLast().getEntityClass();
    }

    @Override
    public Optional<E> findById(Id id) throws BadEntityException {
        return getNext().findById(id);
    }

    @Override
    public E update(E entity, Boolean full) throws EntityNotFoundException, BadEntityException {
        return getNext().update(entity,full);
    }

    @Override
    public E save(E entity) throws BadEntityException {
        return getNext().save(entity);
    }

    @Override
    public Set<E> findAll() {
        return getNext().findAll();
    }

    @Override
    public void deleteById(Id id) throws EntityNotFoundException, BadEntityException {
        getNext().deleteById(id);
    }

    @Override
    public Class<E> getEntityClass() {
        return getNext().getEntityClass();
    }

    @Override
    public CrudRepository<E,Id> getRepository() {
        return getNext().getRepository();
    }

    @Override
    public Class<?> getTargetClass() {
        return getNext().getTargetClass();
    }
}
