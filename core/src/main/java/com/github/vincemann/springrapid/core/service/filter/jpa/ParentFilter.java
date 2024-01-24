package com.github.vincemann.springrapid.core.service.filter.jpa;

import com.github.vincemann.springrapid.core.IdConverter;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;

public abstract class ParentFilter<Id extends Serializable> implements QueryFilter<IdentifiableEntity<?>> {

    protected String parentName;
//    protected Class<? extends IdentifiableEntity<?>> parentClass;
    protected Id parentId;
    private IdConverter<Id> idConverter;

    public ParentFilter(String parentName/*, Class<? extends IdentifiableEntity<?>> parentClass*/) {
        this.parentName = parentName;
        // only needed when checking acl stuff on parent and I just check the result set for read permission
//        this.parentClass = parentClass;
    }


    @Override
    public String getName() {
        return "parent";
    }

    public Serializable getParentId() {
        return parentId;
    }

//    public Class<? extends IdentifiableEntity<?>> getParentClass() {
//        return parentClass;
//    }

    @Autowired
    public void setIdConverter(IdConverter<Id> idConverter) {
        this.idConverter = idConverter;
    }

    @Override
    public void setArgs(String... args) throws BadEntityException {
        assertAmountArgs(1,args);
        this.parentId = parseId(args[0],idConverter);
    }

    @Override
    public Predicate toPredicate(Root<IdentifiableEntity<?>> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return cb.equal(root.join(parentName).get("id"), parentId);
    }

}
