package com.github.vincemann.springrapid.sync.repo;

import com.github.vincemann.springrapid.sync.model.entity.AuditingEntity;
import com.github.vincemann.springrapid.sync.model.entity.IAuditingEntity;
import com.github.vincemann.springrapid.core.util.Specs;
import com.github.vincemann.springrapid.sync.model.EntityUpdateInfo;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

public class AuditingRepositoryImpl<E extends IAuditingEntity<Id>,Id extends Serializable>
    extends SimpleJpaRepository<E,Id>
        implements AuditingRepository<E,Id> {

    protected EntityManager entityManager;
    protected Class<E> entityClass;
    protected JpaEntityInformation<E,?> entityInformation;


    public AuditingRepositoryImpl(EntityManager entityManager, Class<E> entityClass) {
        super(entityClass,entityManager);
        this.entityManager = entityManager;
        this.entityClass = entityClass;
        this.entityInformation = JpaEntityInformationSupport.getEntityInformation(entityClass,entityManager);
    }

    @Override
    public EntityUpdateInfo findUpdateInfo(Id id) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<EntityUpdateInfo> query = cb.createQuery(EntityUpdateInfo.class);


        Specification<E> spec = Specification.where(new Specs.ByIdSpecification<>(entityInformation, id));


        Root<E> root = applySpecificationToCriteria(spec,entityClass,query);

        // Construct the EntityLastUpdateInfo with the required fields
        query.select(cb.construct(EntityUpdateInfo.class,
                root.get("id"),
                root.get(AuditingEntity.LAST_MOD_FIELD)));

        return entityManager.createQuery(query).getSingleResult();
    }
    @Override
    public List<E> findEntitiesUpdatedSince(Timestamp since, Specification<E> spec) {
        Specification<E> specification = createSpec(spec, since);
        Sort sort = Sort.by(Sort.Direction.DESC, AuditingEntity.LAST_MOD_FIELD);
        return super.findAll(specification, sort);
    }

    protected Specification<E> createSpec(@Nullable Specification<E> specification, Timestamp timestamp){
        if (specification != null){
            return Specification.where(specification)
                    .and(updatedSince(timestamp));
        }
        else{
            return Specification.where(updatedSince(timestamp));
        }
    }

   // faster then the other method
    @Override
    public List<EntityUpdateInfo> findUpdateInfosSince(Timestamp since,@Nullable Specification<E> spec) {



        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<EntityUpdateInfo> cq = cb.createQuery(EntityUpdateInfo.class);
        Specification<E> specification = createSpec(spec, since);

        Root<E> root = applySpecificationToCriteria(specification,entityClass,cq);

        // Apply sorting
        cq.orderBy(cb.desc(root.get(AuditingEntity.LAST_MOD_FIELD)));

        // Construct the EntityLastUpdateInfo with the required fields
        cq.select(cb.construct(EntityUpdateInfo.class,
                root.get("id"),
                root.get(AuditingEntity.LAST_MOD_FIELD)));

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


    private Specification<E> updatedSince(Timestamp since){
        return new UpdatedSince<>(since);
    }


}
