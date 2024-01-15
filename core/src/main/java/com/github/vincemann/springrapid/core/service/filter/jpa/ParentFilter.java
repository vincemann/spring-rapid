package com.github.vincemann.springrapid.core.service.filter.jpa;

import com.github.vincemann.springrapid.core.IdConverter;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

// alternative to ParentAwareController
public abstract class ParentFilter implements QueryFilter<IdentifiableEntity<?>> {

    private String parentName;
    private String parentId;
    private IdConverter idConverter;

    public ParentFilter(String parentName) {
        this.parentName = parentName;
    }

    @Override
    public String getName() {
        return "parent";
    }

    @Autowired
    public void setIdConverter(IdConverter idConverter) {
        this.idConverter = idConverter;
    }

    @Override
    public void setArgs(String... args) throws BadEntityException {
        if (args.length != 1)
            throw new BadEntityException("invalid amount args for filter, need 1: parentId");
        try {
            this.parentId = idConverter.toId(args[0]).toString();
        }catch (ClassCastException e){
            throw new BadEntityException("Invalid id type: ", e);
        }
    }

    @Override
    public Predicate getPredicate(CriteriaBuilder cb, Root<IdentifiableEntity<?>> root) {
        return cb.equal(root.join(parentName).get("id"), idConverter.toId(parentId));
    }
}
