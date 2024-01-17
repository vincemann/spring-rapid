package com.github.vincemann.springrapid.core.repo;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;

public class RapidFilterRepository<T extends IdentifiableEntity<Id>, Id extends Serializable>
        extends SimpleJpaRepository<T,Id>
        implements FilterRepository<T,Id> {

    public RapidFilterRepository(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
    }

    @Override
    public List<T> findAll(Specification<T> spec) {
        return super.findAll(spec);
    }

    @Override
    public List<T> findAll(Specification<T> spec, Sort sort) {
        return super.findAll(spec,sort);
    }
}
