package com.github.vincemann.springrapid.core.repo;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;

public class FilterRepositoryImpl<E extends IdentifiableEntity<Id>, Id extends Serializable>
        extends SimpleJpaRepository<E,Id>
        implements FilterRepository<E,Id> {

    public FilterRepositoryImpl(Class<E> domainClass, EntityManager em) {
        super(domainClass, em);
    }


    @Override
    public List<E> findAll(Specification<E> spec) {
        return super.findAll(spec);
    }

    @Override
    public List<E> findAll(Specification<E> spec, Sort sort) {
        return super.findAll(spec,sort);
    }
}
