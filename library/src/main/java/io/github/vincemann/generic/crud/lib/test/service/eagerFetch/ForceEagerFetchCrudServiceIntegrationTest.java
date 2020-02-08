package io.github.vincemann.generic.crud.lib.test.service.eagerFetch;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.sessionReattach.EntityGraphSessionReattacher;
import io.github.vincemann.generic.crud.lib.proxy.factory.CrudServiceEagerFetchProxyFactory;
import io.github.vincemann.generic.crud.lib.test.forceEagerFetch.HibernateForceEagerFetchUtil;
import io.github.vincemann.generic.crud.lib.test.service.CrudServiceIntegrationTest;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Optional;

public class ForceEagerFetchCrudServiceIntegrationTest<
        E extends IdentifiableEntity<Id>,
        Id extends Serializable
        > extends CrudServiceIntegrationTest<E, Id> {


    @Autowired
    @Getter
    private CrudServiceEagerFetchProxyFactory eagerFetchProxyFactory;
    @Autowired
    private EntityGraphSessionReattacher sessionReattacher;

    @Autowired
    private HibernateForceEagerFetchUtil eagerFetchUtil;


    @Override
    @Transactional
    public E repoSave(E entity) {
        //make sure that there are no entities, that are not attached to the current session (created via @Transactional)
        return eagerFetchUtil.runInTransactionAndFetchEagerly_NoException(() -> {
            sessionReattacher.attachEntityGraphToCurrentSession(entity);
            return super.repoSave(entity);
        });
    }

    @Override
    public Optional<E> repoFindById(Id id) {
        return eagerFetchUtil.runInTransactionAndFetchEagerly_OptionalValue_NoException(() -> {
            return super.repoFindById(id);
        });
    }

    protected <E extends IdentifiableEntity<Id>, Id extends Serializable, R extends CrudRepository<E, Id>,S extends CrudService<E,Id,R>> S
    wrapWithEagerFetchProxy(S crudService,String... omittedMethods) {
        return eagerFetchProxyFactory.create(crudService,omittedMethods);
    }

    @Autowired
    @Override
    public void injectCrudService(CrudService<E, Id, ? extends CrudRepository<E, Id>> crudService) throws Exception {
        super.injectCrudService(eagerFetchProxyFactory.create(crudService));
    }
}
