package com.github.vincemann.springrapid.sync.softdelete;

import com.github.vincemann.springrapid.core.model.IdAwareEntity;
import com.github.vincemann.springrapid.core.util.MethodNameUtil;
import com.github.vincemann.springrapid.sync.model.entity.AuditingEntity;
import com.github.vincemann.springrapid.core.util.Specs;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.lang.Nullable;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

public class SoftDeleteAuditingRepositoryImpl<E extends ISoftDeleteEntity<Id>, Id extends Serializable>
        extends SimpleJpaRepository<E, Id>
        implements SoftDeleteAuditingRepository<E, Id> {

    protected EntityManager entityManager;
    protected Class<E> entityClass;
    protected JpaEntityInformation<E,?> entityInformation;


    public SoftDeleteAuditingRepositoryImpl(EntityManager entityManager, Class<E> entityClass) {
        super(entityClass, entityManager);
        this.entityManager = entityManager;
        this.entityClass = entityClass;
        this.entityInformation = JpaEntityInformationSupport.getEntityInformation(entityClass,entityManager);
    }

    @Override
    public SoftDeleteEntityUpdateInfo findUpdateInfo(Id id) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<SoftDeleteEntityUpdateInfo> query = cb.createQuery(SoftDeleteEntityUpdateInfo.class);

        Specification<E> spec = Specification.where(new Specs.ByIdSpecification<>(entityInformation, id));
        Root<E> root = applySpecificationToCriteria(spec,entityClass,query);

        // Construct the EntityLastUpdateInfo with the required fields
        query.select(cb.construct(SoftDeleteEntityUpdateInfo.class,
                root.get("id"),
                root.get(AuditingEntity.LAST_MOD_FIELD),
                root.get(SoftDeleteEntity.DELETED_FIELD)
        ));

        return entityManager.createQuery(query).getSingleResult();
    }

    @Override
    public List<E> findEntitiesUpdatedSince(Timestamp since, Specification<E> specification) {
        Specification<E> spec = createSpec(specification,since);
        Sort sort = Sort.by(Sort.Direction.DESC, AuditingEntity.LAST_MOD_FIELD);
        return super.findAll(spec,sort);
    }

    // faster then the other method
    @Override
    public List<SoftDeleteEntityUpdateInfo> findUpdateInfosSince(Timestamp since, Specification<E> specification) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<SoftDeleteEntityUpdateInfo> cq = cb.createQuery(SoftDeleteEntityUpdateInfo.class);

        Specification<E> spec = createSpec(specification,since);

        Root<E> root = applySpecificationToCriteria(spec,entityClass,cq);

        // Apply sorting
        cq.orderBy(cb.desc(root.get(AuditingEntity.LAST_MOD_FIELD)));

        // Construct the EntityLastUpdateInfo with the required fields
        cq.select(cb.construct(SoftDeleteEntityUpdateInfo.class,
                root.get("id"),
                root.get(AuditingEntity.LAST_MOD_FIELD),
                root.get(SoftDeleteEntity.DELETED_FIELD)

        ));

        TypedQuery<SoftDeleteEntityUpdateInfo> query = entityManager.createQuery(cq);
        return query.getResultList();
    }

    protected Specification<E> createSpec(@Nullable Specification<E> specification, Timestamp timestamp){
        if (specification != null){
            return Specification.where(specification)
                    .and(updatedOrRemoved(timestamp));
        }
        else{
            return Specification.where(updatedOrRemoved(timestamp));
        }
    }


    // add spec in where clause
    protected <S, U extends E> Root<U> applySpecificationToCriteria(@Nullable Specification<U> spec, Class<U> domainClass,
                                                                    CriteriaQuery<S> query) {


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

        private final Timestamp since;

        public UpdatedSince(Timestamp since) {
            this.since = since;
        }

        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            return cb.greaterThan(root.get(AuditingEntity.LAST_MOD_FIELD), since);
        }
    }

    protected static final class RemovedSince<T> implements Specification<T> {

        private final Timestamp since;

        public RemovedSince(Timestamp since) {
            this.since = since;
        }

        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            return cb.greaterThan(root.get(SoftDeleteEntity.DELETED_FIELD), since);
        }
    }

    private Specification<E> updatedOrRemoved(Timestamp since){
        return Specification.where(new UpdatedSince<E>(since)).or(new RemovedSince<>(since));
    }
}