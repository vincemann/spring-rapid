package com.github.vincemann.springrapid.sync.repo;

import com.github.vincemann.springrapid.core.model.AuditingEntity;
import com.github.vincemann.springrapid.core.service.filter.jpa.QueryFilter;
import com.github.vincemann.springrapid.sync.model.EntityUpdateInfo;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import static com.github.vincemann.springrapid.core.util.FilterUtils.*;

public class RapidAuditingRepository<E extends AuditingEntity<Id>,Id extends Serializable>
    extends SimpleJpaRepository<E,Id>
        implements AuditingRepository<E,Id> {

    protected EntityManager entityManager;
    protected Class<E> entityClass;

//    protected SimpleJpaRepository<E,Id> repo;

    public RapidAuditingRepository(EntityManager entityManager, Class<E> entityClass/*, SimpleJpaRepository<E,Id> repo*/) {
        super(entityClass,entityManager);
        this.entityManager = entityManager;
        this.entityClass = entityClass;
//        this.repo = repo;
    }

    @Override
    public EntityUpdateInfo findUpdateInfo(Id id) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<EntityUpdateInfo> query = cb.createQuery(EntityUpdateInfo.class);
        Root<E> root = query.from(entityClass);

        // Construct the EntityLastUpdateInfo with the required fields
        query.select(cb.construct(EntityUpdateInfo.class,
                root.get("id"),
                root.get(AuditingEntity.LAST_MOD_FIELD)));

        return entityManager.createQuery(query).getSingleResult();
    }
    @Override
    public List<E> findEntitiesUpdatedSince(Timestamp since, List<QueryFilter<? super E>> filters) {
        Specification<E> spec = toSpecification(filters);
        spec = spec.and(new UpdatedSince<>(since));

        return super.findAll(spec);
    }

   // faster then the other method
    @Override
    public List<EntityUpdateInfo> findUpdateInfosSince(Timestamp since, List<QueryFilter<? super E>> filters) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<EntityUpdateInfo> cq = cb.createQuery(EntityUpdateInfo.class);
        Root<E> root = cq.from(entityClass);

        Specification<E> spec = Specification.where(new UpdatedSince<>(since));
        spec = spec.and(toSpecification(filters));

        // Construct the EntityLastUpdateInfo with the required fields
        cq.select(cb.construct(EntityUpdateInfo.class,
                root.get("id"),
                root.get(AuditingEntity.LAST_MOD_FIELD)));

        applySpecificationToCriteria(spec,entityClass,cq);
        TypedQuery<EntityUpdateInfo> query = entityManager.createQuery(cq);
        return query.getResultList();
    }



    // add spec in where clause
    protected  <S, U extends E> Root<U> applySpecificationToCriteria(@Nullable Specification<U> spec, Class<U> domainClass,
                                                                  CriteriaQuery<S> query) {

        Assert.notNull(domainClass, "Domain class must not be null!");
        Assert.notNull(query, "CriteriaQuery must not be null!");

        Root<U> root = query.from(domainClass);

        if (spec == null) {
            return root;
        }

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        Predicate predicate = spec.toPredicate(root, query, builder);

        if (predicate != null) {
            query.where(predicate);
        }

        return root;
    }




    protected static final class UpdatedSince<T> implements Specification<T> {

        private Timestamp since;

        public UpdatedSince(Timestamp since) {
            this.since = since;
        }

        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            return cb.greaterThan(root.get(AuditingEntity.LAST_MOD_FIELD), since);
        }
    }



}
