package io.github.vincemann.generic.crud.lib.test;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.proxy.invocationHandler.ForceEagerFetchCrudServiceDynamicInvocationHandler;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.proxy.factory.CrudServiceEagerFetchProxyFactory;
import io.github.vincemann.generic.crud.lib.test.forceEagerFetch.HibernateForceEagerFetchUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;

/**
 * Provides tools for forcing eagerly fetching and auto-proxies {@link #getTestService()} with {@link ForceEagerFetchCrudServiceDynamicInvocationHandler}.
 * @param <E>
 * @param <Id>
 */
@Getter
public abstract class EagerFetchControllerIntegrationTest<E extends IdentifiableEntity<Id>, Id extends Serializable>
        extends ControllerIntegrationTest<E, Id> {

    private CrudServiceEagerFetchProxyFactory eagerFetchProxyFactory;
    private HibernateForceEagerFetchUtil eagerFetchUtil;


    public EagerFetchControllerIntegrationTest(String url) {
        super(url);
    }

    public EagerFetchControllerIntegrationTest() {
    }



    protected <E extends IdentifiableEntity<Id>, Id extends Serializable, R extends CrudRepository<E, Id>> CrudService<E, Id, R>
    wrapWithEagerFetchProxy(CrudService<E, Id, R> crudService,String... omittedMethods) {
        return eagerFetchProxyFactory.create(crudService,eagerFetchUtil, omittedMethods);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        //todo cant make generics work here for some reason
        setTestService(wrapWithEagerFetchProxy(getTestService()));
    }

    @Autowired
    public void injectEagerFetchProxyFactory(CrudServiceEagerFetchProxyFactory eagerFetchProxyFactory) {
        this.eagerFetchProxyFactory = eagerFetchProxyFactory;
    }

    @Autowired
    public void injectHibernateForceEagerFetchUtil(HibernateForceEagerFetchUtil hibernateForceEagerFetchUtil) {
        this.eagerFetchUtil = hibernateForceEagerFetchUtil;
    }



}
