package com.github.vincemann.springrapid.core.repo;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.filter.jpa.EntitySortingStrategy;
import com.github.vincemann.springrapid.core.service.filter.jpa.QueryFilter;

import java.util.List;

public interface CustomFilterRepository<E extends IdentifiableEntity<?>> {

    List<E> findAll(List<QueryFilter<? super E>> filters, List<EntitySortingStrategy<? super E>> sortingStrategies);
}
