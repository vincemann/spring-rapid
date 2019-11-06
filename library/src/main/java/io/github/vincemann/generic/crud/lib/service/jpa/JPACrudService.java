package io.github.vincemann.generic.crud.lib.service.jpa;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.ExtendableCrudService;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public abstract class JPACrudService<E extends IdentifiableEntity<Id>,Id extends Serializable,R extends JpaRepository<E,Id>>
        extends ExtendableCrudService<E,Id> {


    private R jpaRepository;
    @SuppressWarnings("unchecked")
    private Class<E> entityClass = (Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    public JPACrudService(R jpaRepository,Plugin<? super E,? super Id>... plugins) {
        super(plugins);
        this.jpaRepository = jpaRepository;
    }


    @Transactional
    @Override
    public Optional<E> findById(Id id) throws NoIdException {
        return super.findById(id);
    }

    @Transactional
    @Override
    public E update(E entity) throws EntityNotFoundException, NoIdException, BadEntityException {
        return super.update(entity);
    }

    @Transactional
    @Override
    public E save(E entity) throws BadEntityException {
        return super.save(entity);
    }

    @Transactional
    @Override
    public Set<E> findAll() {
        return super.findAll();
    }

    @Transactional
    @Override
    public void delete(E entity) throws EntityNotFoundException, NoIdException {
        super.delete(entity);
    }

    @Transactional
    @Override
    public void deleteById(Id id) throws EntityNotFoundException, NoIdException {
        super.deleteById(id);
    }

    @Override
    public E updateImpl(E entity) throws  NoIdException, EntityNotFoundException, BadEntityException {
        if(entity.getId()==null){
            throw new NoIdException("No Id value set for EntityType: " + entityClass.getSimpleName());
        }
        Optional<E> optionalEntity = findById(entity.getId());
        if(!optionalEntity.isPresent()){
            throw new EntityNotFoundException(entity.getId(), entityClass);
        }
        return save(entity);
    }

    @Override
    public Optional<E> findByIdImpl(Id id) throws NoIdException {
        if(id==null){
            throw new NoIdException("No Id value set for EntityType: " + entityClass.getSimpleName());
        }
        return jpaRepository.findById(id);
    }

    @Override
    public E saveImpl(E entity) throws  BadEntityException {
        try {
            return jpaRepository.save(entity);
        }
        catch (NonTransientDataAccessException e){
            throw new BadEntityException(e);
        }
    }

    @Override
    public Set<E> findAllImpl() {
       return new HashSet<>(jpaRepository.findAll());
    }

    @Override
    public void deleteImpl(E entity) throws EntityNotFoundException, NoIdException {
        if(entity.getId()==null){
            throw new NoIdException("No Id value set for EntityType: " + entityClass.getSimpleName());
        }
        if(!findById(entity.getId()).isPresent()){
            throw new EntityNotFoundException(entity.getId(),entity.getClass());
        }
        jpaRepository.delete(entity);
    }

    @Override
    public void deleteByIdImpl(Id id) throws EntityNotFoundException, NoIdException {
        if(id==null){
            throw new NoIdException("No Id value set for EntityType: " + entityClass.getSimpleName());
        }
        Optional<E> entity = findById(id);
        if(!entity.isPresent()){
            throw new EntityNotFoundException(id, entityClass);
        }
        jpaRepository.deleteById(id);
    }

    public R getJpaRepository() {
        return jpaRepository;
    }

    public Class<E> getEntityClass() {
        return entityClass;
    }
}
