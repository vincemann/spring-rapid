package io.github.vincemann.generic.crud.lib.test.service.testApi.abs;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.test.equalChecker.EqualChecker;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.Optional;


@Getter
public abstract class ServiceTestApi<E extends IdentifiableEntity<Id>, Id extends Serializable,R extends CrudRepository<E,Id>> {

    private CrudService<E,Id,R> crudService;
    private R repository;
    private EqualChecker<E> defaultEqualChecker;


    @Autowired
    public void injectDefaultEqualChecker(EqualChecker<E> defaultEqualChecker) {
        this.defaultEqualChecker = defaultEqualChecker;
    }

    @Autowired
    public void injectCrudService(CrudService<E, Id, R> crudService) {
        setCrudService(crudService);
    }

    public void setCrudService(CrudService<E, Id, R> crudService) {
        this.crudService = crudService;
    }

    @Autowired
    public void injectRepository(R repository) {
        this.repository = repository;
    }

    protected E repoSave(E entityToSave){
        return getRepository().save(entityToSave);
    }

    protected Optional<E> repoFindById(Id id){
        return getRepository().findById(id);
    }


    protected E serviceSave(E entity) throws BadEntityException {
        return getCrudService().save(entity);
    }
    protected Optional<E> serviceFindById(Id id) throws NoIdException {
        return getCrudService().findById(id);
    }

    protected E serviceUpdate(E entity,boolean full) throws EntityNotFoundException, BadEntityException, NoIdException {
        return getCrudService().update(entity,full);
    }

    public <S extends CrudService<E,Id,R>> S getCastedCrudService(){
        return (S) crudService;
    }



}
