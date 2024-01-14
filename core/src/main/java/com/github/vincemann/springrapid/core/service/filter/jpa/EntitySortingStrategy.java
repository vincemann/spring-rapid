package com.github.vincemann.springrapid.core.service.filter.jpa;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.filter.ArgAware;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public interface EntitySortingStrategy<E extends IdentifiableEntity<?>> extends ArgAware
{
    List<Order> getOrders(Root<E> root, CriteriaBuilder cb);


}
