package com.github.vincemann.springrapid.sync.softdelete;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.Date;

public class SoftDeleteSpecs {

    private static final class DeletedIsNull<T> implements Specification<T> {
        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            return cb.isNull(root.<Date>get(SoftDeleteEntity.DELETED_FIELD));
        }
    }

    private static final class DeletedTimeGreaterThanNow<T> implements Specification<T> {
        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            return cb.greaterThan(root.<Date>get(SoftDeleteEntity.DELETED_FIELD), new Date());
        }
    }

    public static final <T> Specification<T> notDeleted() {
        return Specification.where(new DeletedIsNull<T>()).or(new DeletedTimeGreaterThanNow<T>());
    }
}
