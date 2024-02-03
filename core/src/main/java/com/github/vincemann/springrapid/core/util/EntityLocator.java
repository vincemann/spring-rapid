package com.github.vincemann.springrapid.core.util;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.CrudServiceLocator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Optional;

// dont make transactional - if code needs transaction, it should create it himself, then this call will also be wrapped in its own transaction
//@Transactional
public interface EntityLocator {

    public <E extends IdentifiableEntity> Optional<E> findEntity(E entity);
    public <E extends IdentifiableEntity> Optional<E> findEntity(Class clazz, Serializable id);

}
