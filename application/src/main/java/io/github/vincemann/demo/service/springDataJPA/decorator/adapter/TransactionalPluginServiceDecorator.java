package io.github.vincemann.demo.service.springDataJPA.decorator.adapter;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.decorator.implementations.PluginCrudServiceDecorator;
import io.github.vincemann.generic.crud.lib.service.decorator.implementations.TransactionalCrudServiceDecorator;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

/**
 * Convenience Class combining {@link TransactionalPluginServiceDecorator} and {@link PluginCrudServiceDecorator}
 * @param <E>
 * @param <Id>
 */
public class TransactionalPluginServiceDecorator<E extends IdentifiableEntity<Id>,Id extends Serializable> implements CrudService<E,Id> {

    private CrudService<E,Id> decorator;

    public TransactionalPluginServiceDecorator(CrudService<E, Id> decorator, PluginCrudServiceDecorator.Plugin<? super E,? super Id>... plugins) {
        this.decorator = new TransactionalCrudServiceDecorator<>(
                new PluginCrudServiceDecorator<>(
                        decorator,
                        plugins
                )
        );
    }

    @Override
    public Optional<E> findById(Id id) throws NoIdException {
        return decorator.findById(id);
    }

    @Override
    public E update(E entity) throws EntityNotFoundException, NoIdException, BadEntityException {
        return decorator.update(entity);
    }

    @Override
    public E save(E entity) throws BadEntityException {
        return decorator.save(entity);
    }

    @Override
    public Set<E> findAll() {
        return decorator.findAll();
    }

    @Override
    public void delete(E entity) throws EntityNotFoundException, NoIdException {
        decorator.delete(entity);
    }

    @Override
    public void deleteById(Id id) throws EntityNotFoundException, NoIdException {
        decorator.deleteById(id);
    }

    @Override
    public Class<E> getEntityClass() {
        return decorator.getEntityClass();
    }
}
