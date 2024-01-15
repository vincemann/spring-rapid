package com.github.vincemann.springrapid.core.repo;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.filter.jpa.EntitySortingStrategy;
import com.github.vincemann.springrapid.core.service.filter.jpa.QueryFilter;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
public class RapidCustomFilterRepository<E extends IdentifiableEntity<?>>
        extends AbstractRapidCustomRepository<E>
        implements CustomFilterRepository<E> {

    private Class<E> entityClass;
    private EntityManager entityManager;

    public RapidCustomFilterRepository(EntityManager entityManager, Class<E> clazz) {
        this.entityManager = entityManager;
//        this.entityClass = (Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.entityClass = clazz;
    }

    @Override
    public List<E> findAll(List<QueryFilter<? super E>> filters, List<EntitySortingStrategy<? super E>> sortingStrategies) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<E> cq = cb.createQuery(entityClass);
        Root<E> root = cq.from(entityClass);

        cq.select(root);
        applyFilters(cq,root,cb,filters);
        applySortingStrategies(cq,root,cb,sortingStrategies);


        TypedQuery<E> query = entityManager.createQuery(cq);
        return query.getResultList();
    }


}
