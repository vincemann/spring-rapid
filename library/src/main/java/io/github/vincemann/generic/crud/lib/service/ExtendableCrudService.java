package io.github.vincemann.generic.crud.lib.service;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.*;

/**
 * Adds a lot of callbacks for service methods of  {@link CrudService}
 * for {@link Plugin}s that can override these callbacks.
 *
 * ONLY OVERRIDE THE <functionname>Impl methods! Otherwise callbacks wont be called.
 *
 * @param <E>
 * @param <Id>
 * @param <R>
 */
public abstract class ExtendableCrudService
        <
                E extends IdentifiableEntity<Id>,
                Id extends Serializable,
                R extends CrudRepository<E,Id>
        >
        implements CrudService<E, Id,R> {

    private final List<Plugin<? super E,? super Id>> plugins = new ArrayList<>();

    public ExtendableCrudService(Plugin<? super E,? super Id>... plugins) {
        for (Plugin<? super E,? super Id> plugin : plugins) {
            plugin.setCrudService(this);
        }
        this.plugins.addAll(Arrays.asList(plugins));
    }

    public void addPlugin(Plugin<? super E,? super Id> plugin){
        synchronized (plugins){
            this.plugins.add(plugin);
        }
    }

    @Override
    public Optional<E> findById(Id id) throws NoIdException {
        plugins.forEach(plugin -> plugin.onBeforeFindById(id));
        Optional<E> foundEntity = findByIdImpl(id);
        plugins.forEach(plugin -> plugin.onAfterFindById(foundEntity, id));
        return foundEntity;
    }

    public abstract Optional<E> findByIdImpl(Id id) throws NoIdException;


    @Override
    public E update(E entity) throws EntityNotFoundException, NoIdException, BadEntityException {
        for (Plugin<? super E,? super Id> plugin : plugins) {
            plugin.onBeforeUpdate(entity);
        }
        E updatedEntity = updateImpl(entity);
        for (Plugin<? super E,? super Id> plugin : plugins) {
            plugin.onAfterUpdate(updatedEntity, entity);
        }
        return updatedEntity;
    }

    public abstract E updateImpl(E entity) throws EntityNotFoundException, NoIdException, BadEntityException;

    @Override
    public E save(E entity) throws BadEntityException {
        for (Plugin<? super E,? super Id> plugin : plugins) {
            plugin.onBeforeSave(entity);
        }
        E savedEntity = saveImpl(entity);

        for (Plugin<? super E,? super Id> plugin : plugins) {
            plugin.onAfterSave(savedEntity, entity);
        }
        return savedEntity;
    }

    public abstract E saveImpl(E entity) throws BadEntityException;

    @Override
    public Set<E> findAll() {
        plugins.forEach(Plugin::onBeforeFindAll);
        Set<E> foundEntities = findAllImpl();
        plugins.forEach(plugin -> plugin.onAfterFindAll(foundEntities));
        return foundEntities;
    }

    public abstract Set<E> findAllImpl();

    @Override
    public void delete(E entity) throws EntityNotFoundException, NoIdException {
        for (Plugin<? super E,? super Id> plugin : plugins) {
            plugin.onBeforeDelete(entity);
        }
        deleteImpl(entity);

        for (Plugin<? super E,? super Id> plugin : plugins) {
            plugin.onAfterDelete(entity);
        }
    }

    public abstract void deleteImpl(E entity) throws EntityNotFoundException, NoIdException;

    @Override
    public void deleteById(Id id) throws EntityNotFoundException, NoIdException {
        for (Plugin<? super E,? super Id> plugin : plugins) {
            plugin.onBeforeDeleteById(id);
        }
        deleteByIdImpl(id);

        for (Plugin<? super E,? super Id> plugin : plugins) {
            plugin.onAfterDeleteById(id);
        }
    }
    public abstract void deleteByIdImpl(Id id) throws EntityNotFoundException, NoIdException;

    @Setter
    @Getter
    public static class Plugin<E extends IdentifiableEntity<Id>, Id extends Serializable> {
        private ExtendableCrudService crudService;


        public void onBeforeFindById(Id id) {
        }

        public void onBeforeUpdate(E entity) throws EntityNotFoundException, NoIdException, BadEntityException {
        }

        public void onBeforeSave(E entity)throws BadEntityException {
        }

        public void onBeforeFindAll() {
        }

        public void onBeforeDelete(E entity) throws EntityNotFoundException, NoIdException{
        }

        public void onBeforeDeleteById(Id id) throws EntityNotFoundException, NoIdException{
        }


        public void onAfterFindById(Optional<? extends E> returnedEntity, Id id) {
        }

        public void onAfterUpdate(E returnedEntity, E requestEntity) throws EntityNotFoundException, NoIdException, BadEntityException {
        }

        public void onAfterSave(E returnedEntity, E requestEntity) throws BadEntityException{
        }

        public void onAfterFindAll(Set<? extends E> returnedEntities) {
        }

        public void onAfterDelete(E requestEntity) throws EntityNotFoundException, NoIdException{
        }

        public void onAfterDeleteById(Id id) throws EntityNotFoundException, NoIdException{
        }


    }
}