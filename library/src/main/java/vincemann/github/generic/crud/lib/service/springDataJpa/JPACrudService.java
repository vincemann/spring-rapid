package vincemann.github.generic.crud.lib.service.springDataJpa;

import vincemann.github.generic.crud.lib.model.IdentifiableEntity;
import vincemann.github.generic.crud.lib.service.CrudService;
import vincemann.github.generic.crud.lib.service.exception.BadEntityException;
import vincemann.github.generic.crud.lib.service.exception.EntityNotFoundException;
import vincemann.github.generic.crud.lib.service.exception.NoIdException;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class JPACrudService<E extends IdentifiableEntity<Id>,Id extends Serializable,R extends JpaRepository<E,Id>> implements CrudService<E,Id> {


    private R jpaRepository;
    private Class<E> entityClazz;

    public JPACrudService(R jpaRepository, Class<E> entityClazz) {
        this.jpaRepository = jpaRepository;
        this.entityClazz=entityClazz;
    }


    @Override
    public E update(E entity) throws  NoIdException, EntityNotFoundException, BadEntityException {
        if(entity.getId()==null){
            throw new NoIdException("No Id value set for EntityType: " + entityClazz.getSimpleName());
        }
        Optional<E> optionalEntity = findById(entity.getId());
        if(!optionalEntity.isPresent()){
            throw new EntityNotFoundException(entity.getId(),entityClazz);
        }
        return save(entity);
    }

    @Override
    public Optional<E> findById(Id id) throws NoIdException {
        if(id==null){
            throw new NoIdException("No Id value set for EntityType: " + entityClazz.getSimpleName());
        }
        return jpaRepository.findById(id);
    }

    @Override
    public E save(E entity) throws  BadEntityException {
        try {
            return jpaRepository.save(entity);
        }
        catch (NonTransientDataAccessException e){
            throw new BadEntityException(e);
        }
    }

    @Override
    public Set<E> findAll() {
       return new HashSet<>(jpaRepository.findAll());
    }

    @Override
    public void delete(E entity) throws EntityNotFoundException, NoIdException {
        if(entity.getId()==null){
            throw new NoIdException("No Id value set for EntityType: " + entityClazz.getSimpleName());
        }
        if(!findById(entity.getId()).isPresent()){
            throw new EntityNotFoundException(entity.getId(),entity.getClass());
        }
        jpaRepository.delete(entity);
    }

    @Override
    public void deleteById(Id id) throws EntityNotFoundException, NoIdException {
        if(id==null){
            throw new NoIdException("No Id value set for EntityType: " + entityClazz.getSimpleName());
        }
        if(!findById(id).isPresent()){
            throw new EntityNotFoundException(id,entityClazz);
        }
        jpaRepository.deleteById(id);
    }

    public R getJpaRepository() {
        return jpaRepository;
    }

    public Class<E> getEntityClazz() {
        return entityClazz;
    }
}
