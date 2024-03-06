package com.github.vincemann.springrapid.core.service.filter.jpa;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.filter.WebExtension;
import org.springframework.data.jpa.domain.Specification;

/**
 * add custom where clauses to JPQL query.
 */
// jpa is abstract enough for my needs, maybe create more abstract interface later and adapter for jpa and so on
// would need abstraction like QueryContext with default impl JpaQueryContext and this class must not depend on CriteriaBuilder
public interface QueryFilter<E extends IdentifiableEntity<?>> extends WebExtension<E>, Specification<E> {


}
