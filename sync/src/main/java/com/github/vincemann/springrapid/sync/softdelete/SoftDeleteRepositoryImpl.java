package com.github.vincemann.springrapid.sync.softdelete;

import com.github.vincemann.springrapid.core.util.Specs;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.*;

import static com.github.vincemann.springrapid.sync.softdelete.SoftDeleteSpecs.notDeleted;

/**
 *
 * @author Kristijan Georgiev
 *
 * @param <T>
 *            the class of the entity
 * @param <ID>
 *            the ID class of the entity
 *
 *            Custom implementation for soft deleting
 * @modifiedBy vincemann
 */
public class SoftDeleteRepositoryImpl<T, ID extends Serializable>
        extends SimpleJpaRepository<T, ID>
            implements SoftDeleteRepository<T, ID> {

    private final JpaEntityInformation<T, ?> entityInformation;
    private final EntityManager em;
    private final Class<T> domainClass;


    public SoftDeleteRepositoryImpl(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
        this.em = em;
        this.domainClass = domainClass;
        this.entityInformation = JpaEntityInformationSupport.getEntityInformation(domainClass, em);
    }
    //@Override
    public List<T> findAllActive() {
        return super.findAll(notDeleted());
    }

    //@Override
    public Iterable<T> findAllActive(Sort sort) {
        return super.findAll(notDeleted(), sort);
    }

    @Override
    public List<T> findAllActive(Specification<T> specification) {
        Specification<T> spec = Specification.where(specification)
                .and(notDeleted());
        return findAll(spec);
    }

    @Override
    public List<T> findAllActive(Specification<T> specification, Sort sort) {
        Specification<T> spec = Specification.where(specification)
                .and(notDeleted());
        return super.findAll(spec,sort);
    }

    //@Override
    public Page<T> findAllActive(Pageable pageable) {
        return super.findAll(notDeleted(), pageable);
    }

    //@Override
    public List<T> findAllActive(Collection<ID> ids) {
        if (ids == null || !ids.iterator().hasNext())
            return Collections.emptyList();

        if (entityInformation.hasCompositeId()) {
            List<T> results = new ArrayList<T>();

            for (ID id : ids) {
                findOneActive(id).ifPresent(results::add);
            }

            return results;
        }

        Specs.ByIdsSpecification<T> specification = new Specs.ByIdsSpecification<T>(entityInformation);
        TypedQuery<T> query = getQuery(Specification.where(specification).and(notDeleted()), Sort.unsorted());

        return query.setParameter(specification.getParameter(), ids).getResultList();
    }

    //@Override
    public Optional<T> findOneActive(ID id) {
        return super.findOne(
                Specification.where(new Specs.ByIdSpecification<>(entityInformation, id)).and(notDeleted()));
    }

    //@Override
    @Transactional
    public void softDelete(ID id) {
        Assert.notNull(id, "The given id must not be null!");
        softDelete(id, new Date());
    }

    //@Override
    @Transactional
    public void softDelete(T entity) {
        Assert.notNull(entity, "The entity must not be null!");
        softDelete(entity, new Date());
    }

    //@Override
    @Transactional
    public void softDelete(Iterable<? extends T> entities) {
        Assert.notNull(entities, "The given Iterable of entities not be null!");
        for (T entity : entities)
            softDelete(entity);
    }

    //@Override
    @Transactional
    public void softDeleteAll() {
        for (T entity : findAllActive())
            softDelete(entity);
    }

    //@Override
    @Transactional
    public void scheduleSoftDelete(ID id, Date date) {
        softDelete(id, date);
    }

    //@Override
    @Transactional
    public void scheduleSoftDelete(T entity, Date date) {
        softDelete(entity, date);
    }

    private void softDelete(ID id, Date date) {
        Assert.notNull(id, "The given id must not be null!");

        Optional<T> entity = findOneActive(id);

        if (!entity.isPresent())
            throw new EmptyResultDataAccessException(
                    String.format("No %s entity with id %s exists!", entityInformation.getJavaType(), id), 1);

        softDelete(entity.get(), date);
    }

    private void softDelete(T entity, Date date) {
        Assert.notNull(entity, "The entity must not be null!");

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaUpdate<T> update = cb.createCriteriaUpdate((Class<T>) domainClass);

        Root<T> root = update.from((Class<T>) domainClass);

        update.set(SoftDeleteEntity.DELETED_FIELD, date);

        final List<Predicate> predicates = new ArrayList<Predicate>();

        if (entityInformation.hasCompositeId()) {
            for (String s : entityInformation.getIdAttributeNames())
                predicates.add(cb.equal(root.<ID>get(s),
                        entityInformation.getCompositeIdAttributeValue(entityInformation.getId(entity), s)));
            update.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
        } else
            update.where(cb.equal(root.<ID>get(entityInformation.getIdAttribute().getName()),
                    entityInformation.getId(entity)));

        em.createQuery(update).executeUpdate();
    }

    public long countActive() {
        return super.count(notDeleted());
    }

    //@Override
    public boolean existsActive(ID id) {
        Assert.notNull(id, "The entity must not be null!");
        return findOneActive(id) != null ? true : false;
    }


}
