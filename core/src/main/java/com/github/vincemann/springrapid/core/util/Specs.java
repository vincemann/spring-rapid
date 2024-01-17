package com.github.vincemann.springrapid.core.util;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;

import javax.persistence.criteria.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Specs {

    public static final class ByIdSpecification<T, ID extends Serializable> implements Specification<T> {

        private final JpaEntityInformation<T, ?> entityInformation;
        private final ID id;

        public ByIdSpecification(JpaEntityInformation<T, ?> entityInformation, ID id) {
            this.entityInformation = entityInformation;
            this.id = id;
        }

        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            final List<Predicate> predicates = new ArrayList<Predicate>();
            if (entityInformation.hasCompositeId()) {
                for (String s : entityInformation.getIdAttributeNames())
                    predicates.add(cb.equal(root.<ID>get(s), entityInformation.getCompositeIdAttributeValue(id, s)));

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
            return cb.equal(root.<ID>get(entityInformation.getIdAttribute().getName()), id);
        }
    }

    @SuppressWarnings("rawtypes")
    public static final class ByIdsSpecification<T> implements Specification<T> {

        private final JpaEntityInformation<T, ?> entityInformation;

        ParameterExpression<Iterable> parameter;

        public ByIdsSpecification(JpaEntityInformation<T, ?> entityInformation) {
            this.entityInformation = entityInformation;
        }

        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            Path<?> path = root.get(entityInformation.getIdAttribute());
            parameter = cb.parameter(Iterable.class);
            return path.in(parameter);
        }

        public ParameterExpression<Iterable> getParameter() {
            return parameter;
        }
    }
}
