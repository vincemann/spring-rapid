package com.github.vincemann.springrapid.core.repo;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.JPQLEntityFilter;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Set;

@Repository
public class CustomFilterRepositoryImpl<E extends IdentifiableEntity<?>> implements CustomFilterRepository<E> {


    private final Class<E> entityClass;
    private EntityManager entityManager;

    public CustomFilterRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.entityClass = (Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Override
    public List<E> findAll(Set<JPQLEntityFilter<E>> filters) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<E> cq = cb.createQuery(entityClass);
        Root<E> root = cq.from(entityClass);

        cq.select(root);
        Predicate[] predicates = filters.stream().map(JPQLEntityFilter::getPredicates).toArray(Predicate[]::new);
        cq.where(predicates);
        cq.orderBy(cb.desc(root.get("lastModifiedDate")));

        TypedQuery<E> query = entityManager.createQuery(cq);
        return query.getResultList();
    }
}
