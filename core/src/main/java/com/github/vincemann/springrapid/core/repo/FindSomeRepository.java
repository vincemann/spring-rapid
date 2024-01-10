package com.github.vincemann.springrapid.core.repo;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;

import java.io.Serializable;
import java.util.Set;

public interface FindSomeRepository<E extends IdentifiableEntity<Id>,Id extends Serializable> {
    Set<E> findAllByIdIn(Set<Id> ids);
}
