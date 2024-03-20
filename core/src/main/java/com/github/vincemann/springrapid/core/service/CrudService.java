package com.github.vincemann.springrapid.core.service;

import com.github.vincemann.springrapid.core.model.IdAwareEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Validated
public interface CrudService<E extends IdAwareEntity<Id>,Id extends Serializable,Dto>
{

    public E create(@Valid Dto dto) throws EntityNotFoundException, BadEntityException;
    public Optional<E> findById(Id id);
    public List<E> findAllById(Set<Id> ids);

    public void delete(Id id) throws EntityNotFoundException;
}
