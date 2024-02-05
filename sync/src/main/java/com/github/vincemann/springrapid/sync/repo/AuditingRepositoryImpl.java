package com.github.vincemann.springrapid.sync.repo;

import com.github.vincemann.springrapid.core.model.audit.AuditingEntity;
import com.github.vincemann.springrapid.core.model.audit.IAuditingEntity;
import com.github.vincemann.springrapid.core.util.Specs;
import com.github.vincemann.springrapid.sync.model.AuditId;
import com.github.vincemann.springrapid.sync.model.AuditLog;
import com.github.vincemann.springrapid.sync.model.EntityDtoMapping;
import com.github.vincemann.springrapid.sync.model.EntityUpdateInfo;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AuditingRepositoryImpl<E extends IAuditingEntity<Id>,Id extends Serializable>
    extends SimpleJpaRepository<E,Id>
        implements AuditingRepository<E,Id> {

    protected EntityManager entityManager;
    protected Class<E> entityClass;
    protected JpaEntityInformation<E,?> entityInformation;
    private AuditLogRepository auditLogRepository;


    public AuditingRepositoryImpl(EntityManager entityManager, Class<E> entityClass) {
        super(entityClass,entityManager);
        this.entityManager = entityManager;
        this.entityClass = entityClass;
        this.entityInformation = JpaEntityInformationSupport.getEntityInformation(entityClass,entityManager);
    }

    @Override
    public EntityUpdateInfo findUpdateInfo(Id id, Class<?> dtoClass) {
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
    public List<E> findEntitiesUpdatedSince(Timestamp since,Class<?> dtoClass, Specification<E> spec) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<E> cq = cb.createQuery(entityClass);
        Root<E> root = cq.from(entityClass);

        // Join with AuditLog and EntityDtoMapping
        Subquery<AuditLog> subquery = cq.subquery(AuditLog.class);
        Root<AuditLog> auditLogRoot = subquery.correlate(root);
        Join<AuditLog, EntityDtoMapping> mappingJoin = auditLogRoot.join("dtoMappings");

        // Conditions
        Predicate classMatchPredicate = cb.equal(auditLogRoot.get("entityClass"), entityClass.getName());
        Predicate idMatchPredicate = cb.equal(auditLogRoot.get("entityId"), root.get("id").as(String.class));
        Predicate dtoClassMatchPredicate = cb.equal(mappingJoin.get("dtoClass"), dtoClass);
        Predicate updateTimePredicate = cb.greaterThanOrEqualTo(mappingJoin.get("lastUpdateTime"), since);

        // Combine predicates and set the WHERE clause
        cq.where(cb.and(classMatchPredicate, idMatchPredicate, dtoClassMatchPredicate, updateTimePredicate));

        // Execute query
        List<E> result = entityManager.createQuery(cq).getResultList();
        return result;
    }

   // faster then the other method
   // Assume this is part of your service class
   public List<EntityUpdateInfo> findUpdateInfosSince(Timestamp since, Class<?> dtoClass, Specification<E> spec) {
       CriteriaBuilder cb = entityManager.getCriteriaBuilder();
       CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);

       Root<E> root = cq.from(entityClass); // Your entity class
       Join<E, EntityDtoMapping> mappingJoin = root.join("dtoMappings"); // Adjust according to your entity mapping

       // Convert Specification to Predicate
       CriteriaQuery<E> dummyQuery = cb.createQuery(entityClass);
       Predicate specPredicate = spec.toPredicate(root, dummyQuery, cb);

       // Additional predicates based on your method's parameters
       Predicate sincePredicate = cb.greaterThanOrEqualTo(mappingJoin.get("lastUpdateTime"), since);
       Predicate dtoClassPredicate = cb.equal(mappingJoin.get("dtoClass"), dtoClass.getName());

       // Combine all predicates
       cq.where(cb.and(specPredicate, sincePredicate, dtoClassPredicate));

       // Select only necessary fields
       cq.multiselect(root.get("entityId"), mappingJoin.get("lastUpdateTime"));

       TypedQuery<Object[]> query = entityManager.createQuery(cq);
       List<Object[]> results = query.getResultList();

       List<EntityUpdateInfo> entityUpdateInfos = new ArrayList<>();
       for (Object[] result : results) {
           entityUpdateInfos.add(new EntityUpdateInfo((String) result[0], (Date) result[1]));
       }

       return entityUpdateInfos;

//        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//        CriteriaQuery<Serializable> cq = cb.createQuery(Serializable.class);
//        Specification<E> specs = Specification.where(spec)
//                .and(updatedSince(since));
//
//        Root<E> root = applySpecificationToCriteria(specs,entityClass,cq);
//
//        // Construct the EntityLastUpdateInfo with the required fields
//        cq.select(cb.construct(Serializable.class, root.get("id")));
//
//        TypedQuery<Serializable> query = entityManager.createQuery(cq);
//        List<Serializable> updatedIds = query.getResultList();
//
//        List<EntityUpdateInfo> entityUpdateInfos = new ArrayList<>();
//        for (Serializable id : updatedIds) {
//            Optional<AuditLog> auditLog = auditLogRepository.findById(new AuditId(entityClass.getName(), id.toString()));
//            EntityDtoMapping mapping = auditLog.get().findMapping(dtoClass);
//            entityUpdateInfos.add(new EntityUpdateInfo(id.toString(),mapping.getLastUpdateTime()));
//        }
//
//        return entityUpdateInfos;
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
