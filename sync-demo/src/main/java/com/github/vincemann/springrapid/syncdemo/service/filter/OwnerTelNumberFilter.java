package com.github.vincemann.springrapid.syncdemo.service.filter;

import com.github.vincemann.springrapid.core.service.filter.jpa.QueryFilter;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.syncdemo.model.Owner;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Component("telprefix")
public class OwnerTelNumberFilter implements QueryFilter<Owner> {


    private String telNrPrefix;

    @Override
    public void setArgs(String... args) throws BadEntityException {
        if (args.length != 1)
            throw new BadEntityException("invalid amount args for filter, need 1");
        this.telNrPrefix = args[0];
    }

    @Override
    public Predicate getPredicate(CriteriaBuilder cb, Root<Owner> root) {
        return cb.like(root.get("telephone"), telNrPrefix + "%");
    }
}
