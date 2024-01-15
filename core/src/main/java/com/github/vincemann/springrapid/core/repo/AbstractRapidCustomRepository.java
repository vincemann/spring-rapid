package com.github.vincemann.springrapid.core.repo;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.filter.jpa.EntitySortingStrategy;
import com.github.vincemann.springrapid.core.service.filter.jpa.QueryFilter;

import javax.persistence.criteria.*;
import java.util.List;

public abstract class AbstractRapidCustomRepository<E extends IdentifiableEntity<?>> {

    protected void applyFilters(CriteriaQuery<E> cq, Root<E> root, CriteriaBuilder cb, List<QueryFilter<? super E>> filters) {
        if (filters == null)
            return;
        if (!filters.isEmpty())
            cq.where(filters.stream().map(f -> f.getPredicate(cb,root)).toArray(Predicate[]::new));
    }

    protected void applySortingStrategies(CriteriaQuery<E> cq, Root<E> root, CriteriaBuilder cb, List<EntitySortingStrategy<? super E>> sortingStrategies){
        if (sortingStrategies == null)
            return;
        if (!sortingStrategies.isEmpty()){
            for (EntitySortingStrategy<? super E> sortingStrategy : sortingStrategies) {
                cq.orderBy(sortingStrategy.getOrders(root,cb));
            }
        }
//            cq.orderBy(sortingStrategies.stream().map(f -> f.getOrders(root,cb)).collect(Collectors.toList()));
    }

}
