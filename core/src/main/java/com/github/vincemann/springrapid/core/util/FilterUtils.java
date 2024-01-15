package com.github.vincemann.springrapid.core.util;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.filter.EntityFilter;
import lombok.extern.slf4j.Slf4j;

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
}
