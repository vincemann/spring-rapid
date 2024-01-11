package com.github.vincemann.springrapid.core.service;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;

import javax.persistence.criteria.*;

/**
 * add custom where clauses to JPQL query.
 */
@FunctionalInterface
public interface JPQLEntityFilter<E extends IdentifiableEntity<?>> {

    public Predicate[] getPredicates();

//    public Order configureOrdering(CriteriaBuilder cb, Root<E> root);
}
