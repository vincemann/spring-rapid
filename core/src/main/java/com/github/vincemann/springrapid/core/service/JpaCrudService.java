package com.github.vincemann.springrapid.core.service;

import com.github.vincemann.springrapid.core.model.IdAwareEntity;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public abstract class JpaCrudService
        <
                E extends IdAwareEntity<Id>,
                Id extends Serializable,
                Dto,
                R extends JpaRepository<E,Id>
                >
        implements CrudService<E, Id, Dto> {

    private R repository;
    private Class<E> entityClass;

    public JpaCrudService() {
        this.entityClass = (Class<E>) GenericTypeResolver.resolveTypeArguments(this.getClass(), JpaCrudService.class)[0];
    }

    @Override
    public Optional<E> findById(Id id) {
        return repository.findById(id);
    }

    @Override
    public List<E> findAllById(Set<Id> ids) {
        return repository.findAllById(ids);
    }

    @Override
    public void delete(Id id) throws EntityNotFoundException {
        if (!repository.existsById(id))
            throw new EntityNotFoundException(id,entityClass);
        repository.deleteById(id);
    }

    @Autowired
    public void setRepository(R repository) {
        this.repository = repository;
    }

    public R getRepository() {
        return repository;
    }
}
