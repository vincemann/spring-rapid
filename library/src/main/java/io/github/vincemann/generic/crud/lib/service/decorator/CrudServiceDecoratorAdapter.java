package io.github.vincemann.generic.crud.lib.service.decorator;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import lombok.Getter;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;


@Getter
public abstract class CrudServiceDecoratorAdapter<E extends IdentifiableEntity<Id>,Id extends Serializable, S extends CrudService<E,Id>> implements CrudService<E,Id> {
    private CrudService<E,Id> crudServiceDecorator;
    private S undecoratedService;

    public CrudServiceDecoratorAdapter(S undecoratedService, CrudService<E, Id> crudServiceDecorator) {
        this.undecoratedService = undecoratedService;
        this.crudServiceDecorator = crudServiceDecorator;
    }

    @Override
    public Optional<E> findById(Id id) throws NoIdException {
        return crudServiceDecorator.findById(id);
    }

    @Override
    public E update(E entity) throws EntityNotFoundException, NoIdException, BadEntityException {
        return crudServiceDecorator.update(entity);
    }

    @Override
    public E save(E entity) throws BadEntityException {
        return crudServiceDecorator.save(entity);
    }

    @Override
    public Set<E> findAll() {
        return crudServiceDecorator.findAll();
    }

    @Override
    public void delete(E entity) throws EntityNotFoundException, NoIdException {
        crudServiceDecorator.delete(entity);
    }

    @Override
    public void deleteById(Id id) throws EntityNotFoundException, NoIdException {
        crudServiceDecorator.deleteById(id);
    }

    @Override
    public Class<E> getEntityClass() {
        return crudServiceDecorator.getEntityClass();
    }
}
