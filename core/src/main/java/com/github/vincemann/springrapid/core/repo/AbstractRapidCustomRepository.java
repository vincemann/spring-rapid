package com.github.vincemann.springrapid.core.repo;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.JPQLEntityFilter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public abstract class AbstractRapidCustomRepository<E extends IdentifiableEntity<?>> {

    protected void applyFilters(CriteriaQuery<E> cq, Root<E> root, CriteriaBuilder cb, List<JPQLEntityFilter<E>> filters) {
        cq.where(filters.stream().map(f -> f.getPredicates(cb,root)).toArray(Predicate[]::new));
    }

}
