package com.github.vincemann.springrapid.sync.repo;

import com.github.vincemann.springrapid.core.model.AuditingEntity;
import com.github.vincemann.springrapid.core.repo.AbstractRapidCustomRepository;
import com.github.vincemann.springrapid.core.service.JPQLEntityFilter;
import com.github.vincemann.springrapid.sync.model.EntityLastUpdateInfo;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RapidCustomAuditingRepository<E extends AuditingEntity<Id>,Id extends Serializable>
    extends AbstractRapidCustomRepository<E>
        implements CustomAuditingRepository<E,Id> {

    private EntityManager entityManager;
    private Class<E> entityClass;

    public RapidCustomAuditingRepository(EntityManager entityManager, Class<E> entityClass) {
        this.entityManager = entityManager;
//        this.entityClass = (Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.entityClass = entityClass;
    }

    @Override
    public Date findLastModifiedDateById(Id id) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Date> query = cb.createQuery(Date.class);
        Root<E> root = query.from(entityClass);

        query.select(root.get("lastModifiedDate"))
                .where(cb.equal(root.get("id"), id));

        return entityManager.createQuery(query).getSingleResult();
    }

    /**
     * sorted in order to find latest updates
     * @return
     */
    @Override
    public List<EntityLastUpdateInfo> findLastUpdateInfosSince(Timestamp until, List<JPQLEntityFilter<E>> filters) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<EntityLastUpdateInfo> cq = cb.createQuery(EntityLastUpdateInfo.class);
        Root<E> root = cq.from(entityClass);

        // Create the predicate for filtering
        Predicate datePredicate = cb.greaterThan(root.get("lastModifiedDate"), until);

        // Construct the EntityLastUpdateInfo with the required fields
        cq.select(cb.construct(EntityLastUpdateInfo.class,
                root.get("id"),
                root.get("lastModifiedDate")));

        // Combine the date predicate with custom filters
        List<Predicate> allPredicates = new ArrayList<>();
        allPredicates.add(datePredicate);
        for (JPQLEntityFilter<E> filter : filters) {
            Predicate filterPredicate = filter.getPredicates(cb,root);
            allPredicates.add(filterPredicate);
        }

        // Apply all predicates to the query
        cq.where(cb.and(allPredicates.toArray(new Predicate[0])));

        // not needed
//        // Order by lastModifiedDate
//        cq.orderBy(cb.desc(root.get("lastModifiedDate")));

        TypedQuery<EntityLastUpdateInfo> query = entityManager.createQuery(cq);
        return query.getResultList();
    }

}
