package com.github.vincemann.springrapid.core.util;

import com.github.vincemann.springrapid.core.model.IdAwareEntity;
import com.github.vincemann.springrapid.core.service.filter.EntityFilter;
import com.github.vincemann.springrapid.core.service.filter.jpa.SortingExtension;
import com.github.vincemann.springrapid.core.service.filter.jpa.QueryFilter;
import org.springframework.beans.support.SortDefinition;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.stream.Collectors;

public class FilterUtils {

    public static <E extends IdAwareEntity<?>> Set<E> applyMemoryFilters(Set<E> result, List<EntityFilter<? super E>> filters) {
        Assert.notNull(filters,"filters cant be null");
        if (filters.isEmpty())
            return result;
        java.util.function.Predicate<? super E> filter =
                (java.util.function.Predicate<E>) entity -> !isFilteredOut(filters,entity);
        if (result instanceof LinkedHashSet){
            return result.stream()
                    .filter(filter)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }else{
            return result.stream()
                    .filter(filter)
                    .collect(Collectors.toSet());
        }
    }

    public static <E extends IdAwareEntity<?>> List<E> applyMemoryFilters(List<E> result, List<EntityFilter<? super E>> filters) {
        Assert.notNull(filters,"filters cant be null");
        if (filters.isEmpty())
            return result;
        return result.stream()
                .filter(entity -> !isFilteredOut(filters,entity))
                .collect(Collectors.toList());
    }


    /**
     * @return true if entity is filtered out -> not part of result set
     *         false if entity matches all filters -> is part of result set
     */
    private static <E extends IdAwareEntity<?>> boolean isFilteredOut(List<EntityFilter<? super E>> filters, E entity){
        Assert.notNull(filters,"filters cant be null");
        Assert.notNull(entity,"entity cant be null");
        return filters.stream().anyMatch(filter -> !filter.match(entity));
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

    @Nullable
    public static Sort toSort(List<SortingExtension> sortingExtensions){
        if (sortingExtensions.isEmpty())
            return null;
        List<Sort> sortings = sortingExtensions.stream()
                .map(ext -> convert(ext.getSort()))
                .collect(Collectors.toList());
        Sort sort = sortings.get(0);
        for (Sort sorting : sortings) {
            sort = sort.and(sorting);
        }
        return sort;
    }

    private static Sort convert(SortDefinition sortDefinition){
        if (sortDefinition.isAscending())
            return Sort.by(sortDefinition.getProperty()).ascending();
        else
            return Sort.by(sortDefinition.getProperty()).descending();
    }

    public static <E> Specification<E> toSpec(List<QueryFilter<? super E>> filters) {

        return (Root<E> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
            Predicate combinedPredicate = null;

            for (QueryFilter filter : filters) {
                if (filter != null) {
                    Predicate predicate = filter.toPredicate(root, query, builder);
                    combinedPredicate = combinedPredicate == null ? predicate
                            : builder.and(combinedPredicate, predicate);
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
