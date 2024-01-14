package com.github.vincemann.springrapid.core.service.filter.jpa;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.filter.ArgAware;

import javax.persistence.criteria.*;

/**
 * add custom where clauses to JPQL query.
 */
// jpa is abstract enough for my needs, maybe create more abstract interface later and adapter for jpa and so on
// would need abstraction like QueryContext with default impl JpaQueryContext and this class must not depend on CriteriaBuilder
public interface QueryFilter<E extends IdentifiableEntity<?>> extends ArgAware {

    public Predicate getPredicate(CriteriaBuilder cb, Root<E> root);
}