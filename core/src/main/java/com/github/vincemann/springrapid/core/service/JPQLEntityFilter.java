package com.github.vincemann.springrapid.core.service;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;

import javax.persistence.criteria.*;

/**
 * add custom where clauses to JPQL query.
 */
public interface JPQLEntityFilter<E extends IdentifiableEntity<?>> extends ArgAwareFilter {

    public Predicate getPredicates(CriteriaBuilder cb, Root<E> root);

//    public Order configureOrdering(CriteriaBuilder cb, Root<E> root);
}
