package io.github.vincemann.generic.crud.lib.test.service;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.test.equalChecker.EqualChecker;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.Optional;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public abstract class ServiceTestContext<E extends IdentifiableEntity<Id>, Id extends Serializable> {

    private CrudService<E,Id,? extends CrudRepository<E,Id>> crudService;
    private CrudRepository<E,Id> repository;
    private EqualChecker<E> defaultEqualChecker;
    private EqualChecker<E> defaultPartialUpdateEqualChecker;


    public E repoSave(E entityToSave){
        return getRepository().save(entityToSave);
    }
    public Optional<E> repoFindById(Id id){
        return getRepository().findById(id);
    }
    public E serviceSave(E entity) throws BadEntityException {
        return getCrudService().save(entity);
    }
    public Optional<E> serviceFindById(Id id) throws NoIdException {
        return getCrudService().findById(id);
    }
    public E serviceUpdate(E entity,boolean full) throws EntityNotFoundException, BadEntityException, NoIdException {
        return getCrudService().update(entity,full);
    }

    public <R extends CrudRepository<E,Id>> R getCastedCrudRepository(){
        return (R) repository;
    }

    public <S extends CrudService<E,Id,? extends CrudRepository<E,Id>>> S getCastedCrudService(){
        return (S) crudService;
    }
}
