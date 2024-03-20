package com.github.vincemann.springrapid.core.service;

import com.github.vincemann.springrapid.core.model.IdAwareEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public abstract class AbstractCrudService
        <
                E extends IdAwareEntity<Id>,
                Id extends Serializable,
                Dto
                >
        implements CrudService<E, Id, Dto> {

    private JpaRepository<E,Id> repository;

    @Override
    public Optional<E> findById(Id id) {
        return repository.findById(id);
    }

    @Override
    public List<E> findAllById(Set<Id> ids) {
        return repository.findAllById(ids);
    }

    @Override
    public void delete(Id id) {
        repository.deleteById(id);
    }

    @Autowired
    public void setRepository(JpaRepository<E, Id> repository) {
        this.repository = repository;
    }
}
