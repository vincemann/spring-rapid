package io.github.vincemann.generic.crud.lib.test.service;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.service.sessionReattach.EntityGraph_SessionReattachment_Helper;
import io.github.vincemann.generic.crud.lib.test.forceEagerFetch.Hibernate_ForceEagerFetch_Helper;
import io.github.vincemann.generic.crud.lib.test.forceEagerFetch.proxy.CrudService_HibernateForceEagerFetch_Proxy;
import lombok.Getter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Optional;

/**
 * Provides method: {@link #wrapWithEagerFetchProxy(CrudService)} for wrapping {@link CrudService} and {@link CrudRepository} with {@link io.github.vincemann.generic.crud.lib.test.forceEagerFetch.proxy.abs.Hibernate_ForceEagerFetch_Proxy}s.
 * -> no Lazy-initialize Exceptions can occur
 * -> Tests can be non {@link Transactional}, while Service Layer can use Lazy fetching.
 *
 *
 * Also makes sure to attach all detached entities to current session, when using Repo calls and fetches results eagerly.
 * For Service Layer this behavior is usually realized with {@link io.github.vincemann.generic.crud.lib.service.plugin.SessionReattachmentPlugin}.
 *
 *
 * @param <S>
 * @param <R>
 * @param <E>
 * @param <Id>
 */
public abstract class ForceEagerFetch_CrudServiceIntegrationTest<
                        S extends CrudService<E,Id,R>,
                        R extends CrudRepository<E,Id>,
                        E extends IdentifiableEntity<Id>,
                        Id extends Serializable
                > extends CrudServiceIntegrationTest<S, R, E, Id>
{

    @Getter
    private Hibernate_ForceEagerFetch_Helper forceEagerFetchHelper;
    private EntityGraph_SessionReattachment_Helper sessionReattachmentHelper;


    @Autowired
    public void injectHibernate_forceEagerFetch_helper(Hibernate_ForceEagerFetch_Helper hibernate_forceEagerFetch_helper) {
        this.forceEagerFetchHelper = hibernate_forceEagerFetch_helper;
    }

    @Autowired
    public void injectEntityGraph_sessionReattachment_helper(EntityGraph_SessionReattachment_Helper entityGraph_sessionReattachment_helper) {
        this.sessionReattachmentHelper = entityGraph_sessionReattachment_helper;
    }

    @Override
    protected Optional<E> repoFindById(Id id) {
        return forceEagerFetchHelper.runInTransactionAndFetchEagerly_OptionalValue_NoException(() -> {
            return super.repoFindById(id);
        });
    }

    @Transactional
    @Override
    protected E serviceSave(E entity) throws BadEntityException {
        try {
            return forceEagerFetchHelper.runInTransactionAndFetchEagerly(() -> {
                //it is expected for service to handle reattachment (for example with reattachment plugin) so this is not necessary
                //entityGraph_sessionReattachment_helper.attachEntityGraphToCurrentSession(entity);
                return super.serviceSave(entity);
            });
        }catch (BadEntityException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    protected Optional<E> serviceFindById(Id id) throws NoIdException {
        try {
            return forceEagerFetchHelper.runInTransactionAndFetchEagerly_OptionalValue(() -> {
                return super.serviceFindById(id);
            });
        }catch (NoIdException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }

    @Override
    @Transactional
    protected E serviceUpdate(E entity) throws EntityNotFoundException, BadEntityException, NoIdException {
        try {
            return forceEagerFetchHelper.runInTransactionAndFetchEagerly(() -> {
                //it is expected for service to handle reattachment (for example with reattachment plugin) so this is not necessary
                //entityGraph_sessionReattachment_helper.attachEntityGraphToCurrentSession(entity);
                return super.serviceUpdate(entity);
            });
        }catch (NoIdException|EntityNotFoundException|BadEntityException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }

    protected  <E extends IdentifiableEntity<Id>,Id extends Serializable,R extends CrudRepository<E,Id>> CrudService<E,Id,R> wrapWithEagerFetchProxy(CrudService<E,Id,R> crudService){
        return new CrudService_HibernateForceEagerFetch_Proxy<>(crudService, forceEagerFetchHelper);
    }

    @Override
    @Transactional
    protected E repoSave(E entity) {
        //make sure that there are no entities, not attached to the current session (created via @Transactional)
        return forceEagerFetchHelper.runInTransactionAndFetchEagerly_NoException(() -> {
            sessionReattachmentHelper.attachEntityGraphToCurrentSession(entity);
            return super.repoSave(entity);
        });
    }

}
