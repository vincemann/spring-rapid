package com.github.vincemann.springrapid.core.repo;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serializable;
import java.util.List;

public interface FilterRepository<E extends IdentifiableEntity<Id>, Id extends Serializable> {

    List<E> findAll(Specification<E> spec);
    List<E> findAll(Specification<E> spec, Sort sort);
}
