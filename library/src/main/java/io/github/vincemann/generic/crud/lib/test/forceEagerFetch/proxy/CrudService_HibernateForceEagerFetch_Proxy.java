package io.github.vincemann.generic.crud.lib.test.forceEagerFetch.proxy;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.test.forceEagerFetch.Hibernate_ForceEagerFetch_Helper;
import io.github.vincemann.generic.crud.lib.test.forceEagerFetch.proxy.abs.Hibernate_ForceEagerFetch_Proxy;
import lombok.Getter;
import org.hibernate.Hibernate;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.Set;


/**
 * Proxy for {@link CrudService}, that forces eager fetching from database -> very slow, use only for testing
 * This is done by reflectively finding all {@link javax.persistence.Entity}s and EntityCollections and manually initializing them via {@link Hibernate#initialize(Object)}.
 * @param <E>
 * @param <Id>
 * @param <R>
 */
@Getter
public class CrudService_HibernateForceEagerFetch_Proxy
        <
                E extends IdentifiableEntity<Id>,
                Id extends Serializable,
                R extends CrudRepository<E, Id>,
                S extends CrudService<E,Id,R>
                >
            extends Hibernate_ForceEagerFetch_Proxy
        implements CrudService<E, Id, R> {


    private CrudService<E,Id,R> crudService;

    public CrudService_HibernateForceEagerFetch_Proxy(CrudService<E,Id,R> crudService,
                                                      Hibernate_ForceEagerFetch_Helper helper) {
        super(helper);
        this.crudService = crudService;
    }

    @Override
    public java.util.Optional<E> findById(Id id) throws NoIdException {
        try {
            return getHelper().runInTransactionAndFetchEagerly_OptionalValue(() -> crudService.findById(id));
        } catch (NoIdException|RuntimeException e) {
            throw e;
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public E update(E entity,boolean full) throws EntityNotFoundException, NoIdException, BadEntityException {
        try {
            return getHelper().runInTransactionAndFetchEagerly(() -> {
                return crudService.update(entity,full);
            });
        } catch (EntityNotFoundException|NoIdException|BadEntityException|RuntimeException e) {
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public E save(E entity) throws BadEntityException {
        try {
            return getHelper().runInTransactionAndFetchEagerly(() -> crudService.save(entity));
        } catch (BadEntityException|RuntimeException e) {
            throw e;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<E> findAll() {
        try {
            return getHelper().runInTransactionAndFetchEagerly(() -> crudService.findAll());
        }
        catch (RuntimeException e){
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(E entity) throws EntityNotFoundException, NoIdException {
        crudService.delete(entity);
    }

    @Override
    public void deleteById(Id id) throws EntityNotFoundException, NoIdException {
        crudService.deleteById(id);
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
        return (S) crudService;
    }

}
