package com.github.vincemann.springrapid.core.service;

import com.github.vincemann.springrapid.commons.NullAwareBeanUtilsBean;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import com.github.vincemann.springrapid.core.util.EntityUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


/**
 * Implementation of {@link CrudService} that utilizes Jpa's {@link JpaRepository}.
 *
 * @param <E>  Type of Entity which's crud operations are exposed by this Service
 * @param <Id> Id type of E
 * @param <R>  {@link JpaRepository} Type
 */
@ServiceComponent
public abstract class JPACrudService
        <
                E extends IdentifiableEntity<Id>,
                Id extends Serializable,
                R extends JpaRepository<E, Id>
                >
        implements CrudService<E, Id, R> {


    private R jpaRepository;
    @SuppressWarnings("unchecked")
    private Class<E> entityClass = (Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    public JPACrudService() {
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public void injectJpaRepository(R jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Transactional
    @Override
    public Optional<E> findById(Id id) throws BadEntityException {
        EntityUtils.checkNotNull(id, "Id");
        try {
            return jpaRepository.findById(id);
        } catch (NonTransientDataAccessException e) {
            throw new BadEntityException(e);
        }
    }

    @Transactional
    @Override
    public E update(E update, Boolean full) throws EntityNotFoundException, BadEntityException {
        try {
            EntityUtils.checkPresent(update.getId(), "No Id set for update");
            if (full) {
                return save(update);
            } else {
                E entityToUpdate = findOldEntity(update.getId());
                //copy non null values from update to entityToUpdate
                BeanUtilsBean notNull = new NullAwareBeanUtilsBean();
                notNull.copyProperties(entityToUpdate, update);
                return save(entityToUpdate);
            }
        } catch (NonTransientDataAccessException e) {
            throw new BadEntityException(e);
        } catch (IllegalAccessException|InvocationTargetException e) {
           throw new RuntimeException(e);
        }
    }


    private E findOldEntity(Id id) throws BadEntityException, EntityNotFoundException {
        EntityUtils.checkNotNull(id, "id");
        Optional<E> entityToUpdate = findById(id);
        EntityUtils.checkPresent(entityToUpdate, id, getEntityClass());
        return entityToUpdate.get();
    }

    @Transactional
    @Override
    public E save(E entity) throws BadEntityException {
        try {
            return jpaRepository.save(entity);
        } catch (NonTransientDataAccessException e) {
            throw new BadEntityException(e);
        }
    }

    @Transactional
    @Override
    public Set<E> findAll() {
        return new HashSet<>(jpaRepository.findAll());
    }


    @Transactional
    @Override
    public void deleteById(Id id) throws EntityNotFoundException, BadEntityException {
        try {
            EntityUtils.checkNotNull(id, "Id");
            Optional<E> entity = findById(id);
            EntityUtils.checkPresent(entity, id, entityClass);
            jpaRepository.deleteById(id);
        } catch (NonTransientDataAccessException e) {
            throw new BadEntityException(e);
        }

    }

    @Override
    public R getRepository() {
        return jpaRepository;
    }

    public Class<E> getEntityClass() {
        return entityClass;
    }
}
