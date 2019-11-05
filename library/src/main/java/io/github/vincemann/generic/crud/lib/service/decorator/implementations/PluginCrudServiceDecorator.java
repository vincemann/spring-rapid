package io.github.vincemann.generic.crud.lib.service.decorator.implementations;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;

/**
 * Adds plugin functionality to {@link CrudService}
 * @param <E>
 * @param <Id>
 */
public class PluginCrudServiceDecorator<E extends IdentifiableEntity<Id>, Id extends Serializable> implements CrudService<E, Id> {
    private CrudService<E, Id> crudService;
    private List<PluginCrudServiceDecorator.Plugin<? super E, ? super Id>> plugins = new ArrayList<>();

    public PluginCrudServiceDecorator(CrudService<E, Id> crudService, Plugin<? super E, ? super Id>... plugins) {
        for (PluginCrudServiceDecorator.Plugin<? super E, ? super Id> plugin : plugins) {
            plugin.setCrudService(this);
        }
        this.plugins.addAll(Arrays.asList(plugins));
        this.crudService = crudService;
    }

    @Override
    public Optional<E> findById(Id id) throws NoIdException {
        plugins.forEach(plugin -> plugin.onBeforeFindById(id));
        Optional<E> foundEntity = crudService.findById(id);
        plugins.forEach(plugin -> plugin.onAfterFindById(foundEntity, id));
        return foundEntity;
    }

    @Override
    public E update(E entity) throws EntityNotFoundException, NoIdException, BadEntityException {
        for (Plugin<? super E, ? super Id> plugin : plugins) {
            plugin.onBeforeUpdate(entity);
        }
        E updatedEntity = crudService.update(entity);
        for (Plugin<? super E, ? super Id> plugin : plugins) {
            plugin.onAfterUpdate(updatedEntity, entity);
        }
        return updatedEntity;
    }

    @Override
    public E save(E entity) throws BadEntityException {
        for (Plugin<? super E, ? super Id> plugin : plugins) {
            plugin.onBeforeSave(entity);
        }
        E savedEntity = crudService.save(entity);

        for (Plugin<? super E, ? super Id> plugin : plugins) {
            plugin.onAfterSave(savedEntity, entity);
        }
        return savedEntity;
    }

    @Override
    public Set<E> findAll() {
        plugins.forEach(Plugin::onBeforeFindAll);
        Set<E> foundEntities = crudService.findAll();
        plugins.forEach(plugin -> plugin.onAfterFindAll(foundEntities));
        return foundEntities;
    }

    @Override
    public void delete(E entity) throws EntityNotFoundException, NoIdException {
        for (Plugin<? super E, ? super Id> plugin : plugins) {
            plugin.onBeforeDelete(entity);
        }
        crudService.delete(entity);

        for (Plugin<? super E, ? super Id> plugin : plugins) {
            plugin.onAfterDelete(entity);
        }
    }

    @Override
    public void deleteById(Id id) throws EntityNotFoundException, NoIdException {
        for (Plugin<? super E, ? super Id> plugin : plugins) {
            plugin.onBeforeDeleteById(id);
        }
        crudService.deleteById(id);

        for (Plugin<? super E, ? super Id> plugin : plugins) {
            plugin.onAfterDeleteById(id);
        }
    }

    @Override
    public Class<E> getEntityClass() {
        return crudService.getEntityClass();
    }

    @Setter
    @Getter
    public static class Plugin<E extends IdentifiableEntity<Id>, Id extends Serializable> {
        private CrudService crudService;


        public void onBeforeFindById(Id id) {
        }

        public void onBeforeUpdate(E entity) throws EntityNotFoundException, NoIdException, BadEntityException {
        }

        public void onBeforeSave(E entity) throws BadEntityException {
        }

        public void onBeforeFindAll() {
        }

        public void onBeforeDelete(E entity) throws EntityNotFoundException, NoIdException {
        }

        public void onBeforeDeleteById(Id id) throws EntityNotFoundException, NoIdException {
        }


        public void onAfterFindById(Optional<? extends E> returnedEntity, Id id) {
        }

        public void onAfterUpdate(E returnedEntity, E requestEntity) throws EntityNotFoundException, NoIdException, BadEntityException {
        }

        public void onAfterSave(E returnedEntity, E requestEntity) throws BadEntityException {
        }

        public void onAfterFindAll(Set<? extends E> returnedEntities) {
        }

        public void onAfterDelete(E requestEntity) throws EntityNotFoundException, NoIdException {
        }

        public void onAfterDeleteById(Id id) throws EntityNotFoundException, NoIdException {
        }


    }
}

