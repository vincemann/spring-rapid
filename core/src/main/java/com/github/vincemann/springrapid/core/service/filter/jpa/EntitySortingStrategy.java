package com.github.vincemann.springrapid.core.service.filter.jpa;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.filter.UrlExtension;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.util.List;

public interface EntitySortingStrategy<E extends IdentifiableEntity<?>> extends UrlExtension
{
    List<Order> getOrders(Root<E> root, CriteriaBuilder cb);


}
