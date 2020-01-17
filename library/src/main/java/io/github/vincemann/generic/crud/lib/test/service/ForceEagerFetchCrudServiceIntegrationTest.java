package io.github.vincemann.generic.crud.lib.test.service;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.service.sessionReattach.EntityGraphSessionReattacher;
import io.github.vincemann.generic.crud.lib.test.forceEagerFetch.HibernateForceEagerFetchUtil;
import io.github.vincemann.generic.crud.lib.test.forceEagerFetch.proxy.HibernateForceEagerFetchProxyCrudService;
import io.github.vincemann.generic.crud.lib.test.forceEagerFetch.proxy.abs.HibernateForceEagerFetchProxy;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Optional;

/**
 * Provides method: {@link #wrapWithEagerFetchProxy(CrudService)} for wrapping {@link CrudService} and {@link CrudRepository} with {@link HibernateForceEagerFetchProxy}s.
 * -> no Lazy-initialize Exceptions can occur
 * -> Tests can be non {@link Transactional}, while Service Layer can use Lazy fetching.
 *
 *
 * Also makes sure to attach all detached entities to current session, when using Repo calls and fetches results eagerly.
 * For Service Layer this behavior is usually realized with {@link io.github.vincemann.generic.crud.lib.service.plugin.SessionReattachmentPlugin}.
 *
 *
 * @param <E>
 * @param <Id>
 */
public abstract class ForceEagerFetchCrudServiceIntegrationTest<E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends CrudServiceIntegrationTest<E,Id>
{

    @Getter
    private HibernateForceEagerFetchUtil forceEagerFetchHelper;
    private EntityGraphSessionReattacher sessionReattacher;


    @Autowired
    public void injectHibernate_forceEagerFetch_helper(HibernateForceEagerFetchUtil hibernate_forceEagerFetch_util) {
        this.forceEagerFetchHelper = hibernate_forceEagerFetch_util;
    }

    @Autowired
    public void injectEntityGraph_sessionReattachment_helper(EntityGraphSessionReattacher entityGraph_sessionReattacher) {
        this.sessionReattacher = entityGraph_sessionReattacher;
    }

    @Override
    public Optional<E> repoFindById(Id id) {
        return forceEagerFetchHelper.runInTransactionAndFetchEagerly_OptionalValue_NoException(() -> {
            return super.repoFindById(id);
        });
    }

    @Transactional
    @Override
    public E serviceSave(E entity) throws BadEntityException {
        try {
            return forceEagerFetchHelper.runInTransactionAndFetchEagerly(() -> {
                //it is expected for service to handle reattachment (for example with reattachment plugin) so this is not necessary
                //entityGraph_sessionReattachment_helper.attachEntityGraphToCurrentSession(entity);
                return super.serviceSave(entity);
            });
        }catch (BadEntityException|RuntimeException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public Optional<E> serviceFindById(Id id) throws NoIdException {
        try {
            return forceEagerFetchHelper.runInTransactionAndFetchEagerly_OptionalValue(() -> {
                return super.serviceFindById(id);
            });
        }catch (NoIdException|RuntimeException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }

    @Override
    @Transactional
    public E serviceUpdate(E entity,boolean full) throws EntityNotFoundException, BadEntityException, NoIdException {
        try {
            return forceEagerFetchHelper.runInTransactionAndFetchEagerly(() -> {
                //it is expected for service to handle reattachment (for example with reattachment plugin) so this is not necessary
                //entityGraph_sessionReattachment_helper.attachEntityGraphToCurrentSession(entity);
                return super.serviceUpdate(entity,full);
            });
        }catch (NoIdException|EntityNotFoundException|BadEntityException|RuntimeException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }

    protected  <E extends IdentifiableEntity<Id>,Id extends Serializable,R extends CrudRepository<E,Id>> CrudService<E,Id,R> wrapWithEagerFetchProxy(CrudService<E,Id,R> crudService){
        return new HibernateForceEagerFetchProxyCrudService<>(crudService, forceEagerFetchHelper);
    }

    @Override
    @Transactional
    public E repoSave(E entity) {
        //make sure that there are no entities, not attached to the current session (created via @Transactional)
        return forceEagerFetchHelper.runInTransactionAndFetchEagerly_NoException(() -> {
            sessionReattacher.attachEntityGraphToCurrentSession(entity);
            return super.repoSave(entity);
        });
    }

}
