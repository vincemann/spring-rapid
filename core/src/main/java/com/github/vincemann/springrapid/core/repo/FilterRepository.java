package com.github.vincemann.springrapid.core.repo;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serializable;
import java.util.List;

public interface FilterRepository<T extends IdentifiableEntity<Id>, Id extends Serializable> {

    List<T> findAll(Specification<T> spec);
    List<T> findAll(Specification<T> spec, Sort sort);
}
