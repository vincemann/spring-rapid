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

    protected String parentName;
//    protected Class<? extends IdentifiableEntity<?>> parentClass;
    protected String parentId;
    private IdConverter idConverter;

    public ParentFilter(String parentName/*, Class<? extends IdentifiableEntity<?>> parentClass*/) {
        this.parentName = parentName;
        // only needed when checking acl stuff on parent and I just check the result set for read permission
//        this.parentClass = parentClass;
    }

    @Override
    public String getName() {
        return "parent";
    }

    public String getParentId() {
        return parentId;
    }

//    public Class<? extends IdentifiableEntity<?>> getParentClass() {
//        return parentClass;
//    }

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
    public Predicate getPredicate(CriteriaBuilder cb, Root<? extends IdentifiableEntity<?>> root) {
        return cb.equal(root.join(parentName).get("id"), idConverter.toId(parentId));
    }
}
