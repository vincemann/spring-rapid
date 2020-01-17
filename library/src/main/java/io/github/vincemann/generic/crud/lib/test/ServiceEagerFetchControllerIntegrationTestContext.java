package io.github.vincemann.generic.crud.lib.test;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.test.forceEagerFetch.HibernateForceEagerFetchUtil;
import io.github.vincemann.generic.crud.lib.test.forceEagerFetch.proxy.CrudServiceHibernateForceEagerFetchProxy;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;

@Getter
public abstract class ServiceEagerFetchControllerIntegrationTestContext<E extends IdentifiableEntity<Id>, Id extends Serializable>
        extends ControllerIntegrationTestContext<E, Id> {

    public ServiceEagerFetchControllerIntegrationTestContext(String url) {
        super(url);
    }

    public ServiceEagerFetchControllerIntegrationTestContext() {
    }


    private HibernateForceEagerFetchUtil hibernate_forceEagerFetch_util;


    @Autowired
    public void injectHibernate_forceEagerFetch_helper(HibernateForceEagerFetchUtil hibernate_forceEagerFetch_util) {
        this.hibernate_forceEagerFetch_util = hibernate_forceEagerFetch_util;
    }

    protected <E extends IdentifiableEntity<Id>,
            Id extends Serializable,
            R extends CrudRepository<E, Id>
            > CrudService<E, Id, R> wrapWithEagerFetchProxy(CrudService<E, Id, R> crudService) {
        return new CrudServiceHibernateForceEagerFetchProxy<>(crudService, hibernate_forceEagerFetch_util);
    }
}
