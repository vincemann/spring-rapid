package com.github.vincemann.springrapid.core.service;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;

import java.io.Serializable;
import java.util.Optional;

// dont make transactional - if code needs transaction, it should create it itself, then this call will also be wrapped in its transaction
//@Transactional
public interface EntityLocator {

    public <E extends IdentifiableEntity> Optional<E> findEntity(E entity);
    public <E extends IdentifiableEntity> Optional<E> findEntity(Class clazz, Serializable id);

}
