package com.github.vincemann.springrapid.core.util;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.filter.EntityFilter;
import com.github.vincemann.springrapid.core.service.filter.jpa.EntitySortingStrategy;
import com.github.vincemann.springrapid.core.service.filter.jpa.QueryFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class FilterUtils {

    public static <E extends IdentifiableEntity<?>> Set<E> applyMemoryFilters(Set<E> result, List<EntityFilter<? super E>> filters) {
        if (filters == null)
            return result;
        if (filters.isEmpty())
            return result;
        Set<E> filtered = new HashSet<>();
        for (E entity : result) {
            if (!isFilteredOut(filters,entity)){
                filtered.add(entity);
            }
        }
        return filtered;
    }

    public static <E extends IdentifiableEntity<?>> List<E> applyMemoryFilters(List<E> result, List<EntityFilter<? super E>> filters) {
        if (filters == null)
            return result;
        if (filters.isEmpty())
            return result;
        List<E> filtered = new ArrayList<>();
        for (E entity : result) {
            if (!isFilteredOut(filters,entity)){
                filtered.add(entity);
            }
        }
        return filtered;
    }


    /**
     * @return true if entity is filtered out -> not part of result set
     *         false if entity matches all filters -> is part of result set
     */
    public static <E extends IdentifiableEntity<?>> boolean isFilteredOut(List<EntityFilter<? super E>> filters, E entity){
        for (EntityFilter<? super E> filter : filters) {
            if (log.isDebugEnabled())
                log.debug("applying memory filter: " + filter.getClass().getSimpleName());
            if (!filter.match(entity)) {
                return true;
            }
        }
        return false;
    }


//    default void applyFilters(CriteriaQuery<E> cq, Root<E> root, CriteriaBuilder cb, List<QueryFilter<? super E>> filters) {
//        if (filters == null)
//            return;
//        if (!filters.isEmpty())
//            cq.where(filters.stream().map(f -> f.getPredicate(cb,root)).toArray(Predicate[]::new));
//    }
//
//    default void applySortingStrategies(CriteriaQuery<E> cq, Root<E> root, CriteriaBuilder cb, List<EntitySortingStrategy> sortingStrategies){
//        if (sortingStrategies == null)
//            return;
//        if (!sortingStrategies.isEmpty()){
//            for (EntitySortingStrategy sortingStrategy : sortingStrategies) {
//                cq.orderBy(sortingStrategy.getSort);
//            }
//        }
////            cq.orderBy(sortingStrategies.stream().map(f -> f.getOrders(root,cb)).collect(Collectors.toList()));
//    }

//    static final class FilterSpecification<T> implements Specification<T> {
//
//        private QueryFilter<? super T> filter;
//
//        public FilterSpecification(QueryFilter<? super T> filter) {
//            this.filter = filter;
//        }
//
//        @Override
//        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//            return filter.getPredicate(cb,root);
//        }
//    }

    public static Sort toSort(List<EntitySortingStrategy> sortingStrategies){
        if (sortingStrategies.isEmpty())
            return null;
        Sort sort = sortingStrategies.get(0).getSort();
        for (EntitySortingStrategy sortingStrategy : sortingStrategies) {
            sort = sort.and(sortingStrategy.getSort());
        }
        return sort;
    }


    public static <E> Specification<E> toSpec(List<QueryFilter<? super E>> filters) {
        return (Root<E> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
            Predicate combinedPredicate = null;

            for (QueryFilter filter : filters) {
                if (filter != null) {
                    Predicate predicate = filter.toPredicate(root, query, builder);
                    combinedPredicate = combinedPredicate == null ? predicate : builder.and(combinedPredicate, predicate);
                }
            }

            return combinedPredicate;
        };
    }

    public static <T> Specification<T> mergeSpecifications(List<? extends Specification<T>> specs) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
            Predicate combinedPredicate = null;

            for (Specification<T> spec : specs) {
                if (spec != null) {
                    Predicate predicate = spec.toPredicate(root, query, builder);
                    combinedPredicate = combinedPredicate == null ? predicate : builder.and(combinedPredicate, predicate);
                }
            }

            return combinedPredicate;
        };
    }

//    // returns specification with all filters
//    public static <T> Specification<T> filtered(List<QueryFilter<? super T>> filters) {
//        if (filters.isEmpty())
//            return null;
//        Specification<T> specification = Specification.where(new FilterSpecification<T>(filters.get(0)));
//        for (QueryFilter<? super T> filter : filters) {
//            specification = specification.and(new FilterSpecification<>(filter));
//        }
//        return specification;
//    }
}
