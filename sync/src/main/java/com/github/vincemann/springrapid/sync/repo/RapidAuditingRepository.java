package com.github.vincemann.springrapid.sync.repo;

import com.github.vincemann.springrapid.core.model.AuditingEntity;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.filter.jpa.QueryFilter;
import com.github.vincemann.springrapid.sync.model.EntityLastUpdateInfo;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.github.vincemann.springrapid.core.util.FilterUtils.*;

public class RapidAuditingRepository<E extends AuditingEntity<Id>,Id extends Serializable>
        implements AuditingRepository<E,Id> {

    protected EntityManager entityManager;
    protected Class<E> entityClass;

    protected SimpleJpaRepository<E,Id> repo;

    public RapidAuditingRepository(EntityManager entityManager, Class<E> entityClass, SimpleJpaRepository<E,Id> repo) {
//        super(entityClass,entityManager);
        this.entityManager = entityManager;
        this.entityClass = entityClass;
        this.repo = repo;
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

//    static class UpdatedSinceFiler implements QueryFilter<IdentifiableEntity<?>>{
//
//        private Timestamp since;
//
//        @Override
//        public String getName() {
//            return "updatedSince";
//        }
//
//        public UpdatedSinceFiler(Timestamp since) {
//            this.since = since;
//        }
//
//        @Override
//        public void setArgs(String... args) throws BadEntityException {
//            if (args.length != 1)
//                throw new BadEntityException("Need one arg: since-timestamp");
//        }
//
//        @Override
//        public Predicate getPredicate(CriteriaBuilder cb, Root<? extends IdentifiableEntity<?>> root) {
//            return cb.greaterThan(root.get("lastModifiedDate"), since);
//        }
//    }

    @Override
    public List<E> findEntitiesLastUpdatedSince(Timestamp since, List<QueryFilter<? super E>> filters) {
        Specification<E> spec = toSpecification(filters);
        spec.and(new UpdatedSince<>(since));

        return repo.findAll(spec);

//        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//        CriteriaQuery<E> cq = cb.createQuery(entityClass);
//        Root<E> root = cq.from(entityClass);

//        // Create the predicate for filtering
//        Predicate datePredicate = cb.greaterThan(root.get("lastModifiedDate"), since);
//
//        // Construct the EntityLastUpdateInfo with the required fields
//        cq.select(root);
//
//        // Combine the date predicate with custom filters
//        List<Predicate> allPredicates = new ArrayList<>();
//        allPredicates.add(datePredicate);
//        for (QueryFilter<? super E> filter : filters) {
//            Predicate filterPredicate = filter.getPredicate(cb,root);
//            allPredicates.add(filterPredicate);
//        }
//
//        // Apply all predicates to the query
//        cq.where(cb.and(allPredicates.toArray(new Predicate[0])));
//
//        // not needed
////        // Order by lastModifiedDate
////        cq.orderBy(cb.desc(root.get("lastModifiedDate")));
//
//        TypedQuery<E> query = entityManager.createQuery(cq);
//        return query.getResultList();
    }

   // faster then the other method
    @Override
    public List<EntityLastUpdateInfo> findLastUpdateInfosSince(Timestamp since, List<QueryFilter<? super E>> filters) {


        // wont work bc EntityLastUpdateInfo is no SubType of E
//        List<QueryFilter<? super E>> updatedFilters = new ArrayList<>(filters);
//        updatedFilters.add(new UpdatedSinceFiler(since));
//
//        // Create a specification that only fetches id and lastUpdate
//        Specification<E> spec = (root, query, criteriaBuilder) -> {
//            CompoundSelection<EntityLastUpdateInfo> construct = criteriaBuilder.construct(
//                    EntityLastUpdateInfo.class,
//                    root.get("id"),
//                    root.get("lastUpdate"));
//            query.select(construct);
//            return filtered(updatedFilters).toPredicate(root, query, criteriaBuilder);
//        };
//
//        // Assuming that the repo supports custom Specification with projections
//        return repo.findAll(spec);


        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<EntityLastUpdateInfo> cq = cb.createQuery(EntityLastUpdateInfo.class);
        Root<E> root = cq.from(entityClass);

        Specification<E> spec = Specification.where(new UpdatedSince<>(since));
        spec.and(toSpecification(filters));

        // Create the predicate for filtering
//        Predicate datePredicate = cb.greaterThan(root.get("lastModifiedDate"), since);

        // Construct the EntityLastUpdateInfo with the required fields
        cq.select(cb.construct(EntityLastUpdateInfo.class,
                root.get("id"),
                root.get("lastModifiedDate")));

        // Combine the date predicate with custom filters
//        List<Predicate> allPredicates = new ArrayList<>();
//        allPredicates.add(datePredicate);
//        for (QueryFilter<? super E> filter : filters) {
//            Predicate filterPredicate = filter.getPredicate(cb,root);
//            allPredicates.add(filterPredicate);
//        }

        // Apply all predicates to the query
//        cq.where(cb.and(allPredicates.toArray(new Predicate[0])));
        applySpecificationToCriteria(spec,entityClass,cq);


        // not needed
//        // Order by lastModifiedDate
//        cq.orderBy(cb.desc(root.get("lastModifiedDate")));

        TypedQuery<EntityLastUpdateInfo> query = entityManager.createQuery(cq);
        return query.getResultList();
    }



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
            return cb.greaterThan(root.get("lastModifiedDate"), since);
        }
    }



}
