package io.github.vincemann.generic.crud.lib.service.jpa;

import io.github.vincemann.generic.crud.lib.advice.log.LogInteraction;
import io.github.vincemann.generic.crud.lib.config.layers.component.ServiceComponent;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.util.DebugTransactionUtil;
import io.github.vincemann.generic.crud.lib.util.NullAwareBeanUtilsBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


@ServiceComponent
@Slf4j
public abstract class JPACrudService
                <
                          E extends IdentifiableEntity<Id>,
                          Id extends Serializable,
                          R extends JpaRepository<E,Id>
                >
        implements CrudService<E,Id,R> {


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

    @LogInteraction
    @Transactional
    @Override
    public Optional<E> findById(Id id) throws NoIdException {
        if(id==null){
            throw new NoIdException("No Id value set for Entity of type: " + entityClass.getSimpleName());
        }
        return jpaRepository.findById(id);
    }

    @LogInteraction
    @Transactional
    @Override
    public E update(E update, Boolean full) throws EntityNotFoundException, NoIdException, BadEntityException {
        try {
            E entityToUpdate = findOldEntity(update.getId());
            if(full){
                return save(update);
            }else {
                //copy non null values from update to entityToUpdate
                BeanUtilsBean notNull = new NullAwareBeanUtilsBean();
                notNull.copyProperties(entityToUpdate, update);
                return save(entityToUpdate);
            }
        }catch (IllegalAccessException | InvocationTargetException e){
            throw new RuntimeException(e);
        }
    }


    private E findOldEntity(Id id) throws NoIdException, EntityNotFoundException {
        if(id==null){
            throw new NoIdException("No Id value set for EntityType: " + entityClass.getSimpleName());
        }
        Optional<E> entityToUpdate = findById(id);
        if(!entityToUpdate.isPresent()){
            throw new EntityNotFoundException(id, entityClass);
        }
        return entityToUpdate.get();
    }

    @LogInteraction
    @Transactional
    @Override
    public E save(E entity) throws BadEntityException {
        DebugTransactionUtil.showTransactionStatus(this.getClass(),"save");
        try {
            return jpaRepository.save(entity);
        }
        catch (NonTransientDataAccessException e){
            throw new BadEntityException(e);
        }
    }

    @LogInteraction
    @Transactional
    @Override
    public Set<E> findAll() {
        return new HashSet<>(jpaRepository.findAll());
    }

    @LogInteraction
    @Transactional
    @Override
    public void delete(E entity) throws EntityNotFoundException, NoIdException {
        if(entity.getId()==null){
            throw new NoIdException("No Id value set for EntityType: " + entityClass.getSimpleName());
        }
        if(!findById(entity.getId()).isPresent()){
            throw new EntityNotFoundException(entity.getId(),entity.getClass());
        }
        jpaRepository.delete(entity);
    }

    @LogInteraction
    @Transactional
    @Override
    public void deleteById(Id id) throws EntityNotFoundException, NoIdException {
        if(id==null){
            throw new NoIdException("No Id value set for EntityType: " + entityClass.getSimpleName());
        }
        Optional<E> entity = findById(id);
        if(!entity.isPresent()){
            throw new EntityNotFoundException(id, entityClass);
        }
        jpaRepository.deleteById(id);
    }

    @Override
    public R getRepository() {
        return jpaRepository;
    }

    public Class<E> getEntityClass() {
        return entityClass;
    }
}
