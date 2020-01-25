package io.github.vincemann.generic.crud.lib.service.plugin;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;

/**
 * Adds plugin functionality to {@link CrudService}.
 * @param <E>
 * @param <Id>
 * @param <R>
 */
@Getter
public class CrudServicePluginProxy
        <
                E extends IdentifiableEntity<Id>,
                Id extends Serializable,
                R extends CrudRepository<E,Id>,
                S extends CrudService<E, Id,R>
        >
            implements CrudService<E, Id,R> {

    public static final String PLUGIN_PROXY = "pluginProxy";
    private final List<Plugin<? super E,? super Id>> plugins = new ArrayList<>();
    @Setter
    private CrudService<E,Id,R> crudService;

    public CrudServicePluginProxy(CrudService<E,Id,R> crudService, Plugin<? super E, ? super Id>... plugins) {
        this(plugins);
        this.crudService = crudService;

    }

    public CrudServicePluginProxy(Plugin<? super E, ? super Id>... plugins) {
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

    @Transactional
    @Override
    public Optional<E> findById(Id id) throws NoIdException {
        plugins.forEach(plugin -> plugin.onBeforeFindById(id));
        Optional<E> foundEntity = crudService.findById(id);
        plugins.forEach(plugin -> plugin.onAfterFindById(foundEntity, id));
        return foundEntity;
    }

    @Transactional
    @Override
    public E update(E entity,boolean full) throws EntityNotFoundException, NoIdException, BadEntityException {
        for (Plugin<? super E,? super Id> plugin : plugins) {
            plugin.onBeforeUpdate(entity);
        }
        E updatedEntity = crudService.update(entity,full);
        for (Plugin<? super E,? super Id> plugin : plugins) {
            plugin.onAfterUpdate(updatedEntity, entity);
        }
        return updatedEntity;
    }

    @Transactional
    @Override
    public E save(E entity) throws BadEntityException {
        for (Plugin<? super E,? super Id> plugin : plugins) {
            plugin.onBeforeSave(entity);
        }
        E savedEntity = crudService.save(entity);

        for (Plugin<? super E,? super Id> plugin : plugins) {
            plugin.onAfterSave(savedEntity, entity);
        }
        return savedEntity;
    }

    @Transactional
    @Override
    public Set<E> findAll() {
        plugins.forEach(Plugin::onBeforeFindAll);
        Set<E> foundEntities = crudService.findAll();
        plugins.forEach(plugin -> plugin.onAfterFindAll(foundEntities));
        return foundEntities;
    }

    @Transactional
    @Override
    public void delete(E entity) throws EntityNotFoundException, NoIdException {
        for (Plugin<? super E,? super Id> plugin : plugins) {
            plugin.onBeforeDelete(entity);
        }
        crudService.delete(entity);

        for (Plugin<? super E,? super Id> plugin : plugins) {
            plugin.onAfterDelete(entity);
        }
    }

    @Transactional
    @Override
    public void deleteById(Id id) throws EntityNotFoundException, NoIdException {
        for (Plugin<? super E,? super Id> plugin : plugins) {
            plugin.onBeforeDeleteById(id);
        }
        crudService.deleteById(id);

        for (Plugin<? super E,? super Id> plugin : plugins) {
            plugin.onAfterDeleteById(id);
        }
    }

    @Override
    public Class<E> getEntityClass() {
        return crudService.getEntityClass();
    }

    @Override
    public R getRepository() {
        return crudService.getRepository();
    }

    public S getCastedService(){
        return (S) getCrudService();
    }

    @Setter
    @Getter
    public static class Plugin<E extends IdentifiableEntity<Id>, Id extends Serializable> {
        private CrudServicePluginProxy crudService;


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