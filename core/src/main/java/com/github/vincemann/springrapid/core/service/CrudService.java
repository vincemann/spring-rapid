package com.github.vincemann.springrapid.core.service;

import com.github.vincemann.springrapid.core.model.IdAwareEntity;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CrudService<E extends IdAwareEntity<Id>,Id extends Serializable,Dto>
{

    public E create(Dto dto);
    public Optional<E> findById(Id id);
    public List<E> findAllById(Set<Id> ids);

    public void delete(Id id);
}
