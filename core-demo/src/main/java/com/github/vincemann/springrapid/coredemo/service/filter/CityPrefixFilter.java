package com.github.vincemann.springrapid.coredemo.service.filter;

import com.github.vincemann.springrapid.core.service.filter.jpa.QueryFilter;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.coredemo.model.Owner;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Component("city")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CityPrefixFilter implements QueryFilter<Owner> {

    private String cityPrefix;

    @Override
    public void setArgs(String... args) throws BadEntityException {
        if (args.length != 1)
            throw new BadEntityException("invalid amount args for filter, need 1");
        this.cityPrefix = args[0];
    }

    @Override
    public Predicate getPredicate(CriteriaBuilder cb, Root<Owner> root) {
        return cb.like(root.get("city"), cityPrefix + "%");
    }
}