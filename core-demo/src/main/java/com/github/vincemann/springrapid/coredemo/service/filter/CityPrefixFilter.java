package com.github.vincemann.springrapid.coredemo.service.filter;

import com.github.vincemann.springrapid.core.service.filter.jpa.QueryFilter;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.stereotype.Component;
import com.github.vincemann.springrapid.coredemo.model.Owner;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CityPrefixFilter implements QueryFilter<Owner> {

    private String cityPrefix;

    @Override
    public String getName() {
        return "city";
    }

    @Override
    public void setArgs(String... args) throws BadEntityException {
        assertAmountArgs(1,args);
        this.cityPrefix = args[0];
    }

    @Override
    public Predicate toPredicate(Root<Owner> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return cb.like(root.get("city"), cityPrefix + "%");
    }

}
