package io.github.vincemann.generic.crud.lib.test.service;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.service.sessionReattach.EntityGraph_SessionReattachment_Helper;
import io.github.vincemann.generic.crud.lib.test.forceEagerFetch.Hibernate_ForceEagerFetch_Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Optional;

/**
 * Wraps all findById and save repo calls, used in {@link CrudServiceIntegrationTest}, in transactions and forces eager fetching on Result with {@link Hibernate_ForceEagerFetch_Helper}.
 * Also makes sure to attach all detached entities to current session, when using save and findById Repo calls.
 *
 * This makes the use {@link io.github.vincemann.generic.crud.lib.test.forceEagerFetch.proxy.abs.Hibernate_ForceEagerFetch_Proxy} not necessary in most cases.
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
                > extends CrudServiceIntegrationTest<S, R, E, Id> {

    private Hibernate_ForceEagerFetch_Helper hibernate_forceEagerFetch_helper;
    private EntityGraph_SessionReattachment_Helper entityGraph_sessionReattachment_helper;



    @Autowired
    public void setHibernate_forceEagerFetch_helper(Hibernate_ForceEagerFetch_Helper hibernate_forceEagerFetch_helper) {
        this.hibernate_forceEagerFetch_helper = hibernate_forceEagerFetch_helper;
    }

    @Autowired
    public void setEntityGraph_sessionReattachment_helper(EntityGraph_SessionReattachment_Helper entityGraph_sessionReattachment_helper) {
        this.entityGraph_sessionReattachment_helper = entityGraph_sessionReattachment_helper;
    }

    @Override
    protected Optional<E> repoFindById(Id id) {
        return hibernate_forceEagerFetch_helper.runInTransactionAndFetchEagerly_OptionalValue_NoException(() -> {
            return super.repoFindById(id);
        });
    }

    @Transactional
    @Override
    protected E serviceSave(E entity) throws BadEntityException {
        try {
            return hibernate_forceEagerFetch_helper.runInTransactionAndFetchEagerly(() -> {
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
            return hibernate_forceEagerFetch_helper.runInTransactionAndFetchEagerly(() -> {
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
            return hibernate_forceEagerFetch_helper.runInTransactionAndFetchEagerly(() -> {
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

    @Override
    @Transactional
    protected E repoSave(E entity) {
        //make sure that there are no entities, not attached to the current session (created via @Transactional)
        return hibernate_forceEagerFetch_helper.runInTransactionAndFetchEagerly_NoException(() -> {
            entityGraph_sessionReattachment_helper.attachEntityGraphToCurrentSession(entity);
            return super.repoSave(entity);
        });
    }

}
