package com.github.vincemann.springrapid.sync.repo;

import com.github.vincemann.springrapid.core.model.AuditingEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public class RapidCustomAuditingRepository<E extends AuditingEntity<?>>
        implements CustomAuditingRepository<E> {

    @PersistenceContext
    private EntityManager entityManager;

    private Class<E> entityClass;

    public RapidCustomAuditingRepository() {
        this.entityClass = (Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * sorted in order to find latest updates
     * @return
     */
    @Override
    public List<E> findAllSortedByLastModifiedDate() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<E> cq = cb.createQuery(entityClass);
        Root<E> root = cq.from(entityClass);

        cq.select(root);
        cq.orderBy(cb.desc(root.get("lastModifiedDate")));

        TypedQuery<E> query = entityManager.createQuery(cq);
        return query.getResultList();
    }

}
