package com.github.vincemann.springrapid.core.repo;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.filter.jpa.EntitySortingStrategy;
import com.github.vincemann.springrapid.core.service.filter.jpa.QueryFilter;
import com.sun.xml.bind.v2.model.core.ID;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.util.List;

//@Repository
//public class RapidFilterRepository<E extends IdentifiableEntity<Id>, Id extends Serializable>
//        implements FilterRepository<E>, FilterAwareRepository<E> {
//
////    private Class<E> entityClass;
////    private EntityManager entityManager;
//
//    private SimpleJpaRepository<E,ID> repo;
//
//    public RapidFilterRepository(/*Class<E> domainClass, EntityManager em,*/ SimpleJpaRepository<E, ID> repo) {
////        super(domainClass, em);
////        this.entityClass = domainClass;
////        this.entityManager = em;
//        this.repo = repo;
////        this.entityInformation = JpaEntityInformationSupport.getEntityInformation(domainClass, em);
//    }
//
//    @Override
//    public List<E> findAll(List<QueryFilter<? super E>> filters, List<EntitySortingStrategy> sortingStrategies) {
//        return repo.findAll(filtered(filters), sorted(sortingStrategies));
//
////        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
////        CriteriaQuery<E> cq = cb.createQuery(entityClass);
////        Root<E> root = cq.from(entityClass);
////
////        cq.select(root);
////        applyFilters(cq,root,cb,filters);
////        applySortingStrategies(cq,root,cb,sortingStrategies);
////
////
////        TypedQuery<E> query = entityManager.createQuery(cq);
////        return query.getResultList();
//    }
//
//
//
//}
