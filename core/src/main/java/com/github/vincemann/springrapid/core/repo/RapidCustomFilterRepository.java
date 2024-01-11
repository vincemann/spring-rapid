package com.github.vincemann.springrapid.core.repo;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.JPQLEntityFilter;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.lang.reflect.ParameterizedType;
import java.util.List;

@Repository
public class RapidCustomFilterRepository<E extends IdentifiableEntity<?>>
        extends AbstractRapidCustomRepository<E>
        implements CustomFilterRepository<E> {


    private final Class<E> entityClass;
    private EntityManager entityManager;

    public RapidCustomFilterRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.entityClass = (Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Override
    public List<E> findAll(List<JPQLEntityFilter<E>> filters) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<E> cq = cb.createQuery(entityClass);
        Root<E> root = cq.from(entityClass);

        cq.select(root);
        applyFilters(cq,root,cb,filters);
        cq.orderBy(cb.desc(root.get("lastModifiedDate")));

        TypedQuery<E> query = entityManager.createQuery(cq);
        return query.getResultList();
    }


}
