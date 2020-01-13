package io.github.vincemann.generic.crud.lib.test;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.test.forceEagerFetch.Hibernate_ForceEagerFetch_Helper;
import io.github.vincemann.generic.crud.lib.test.forceEagerFetch.proxy.CrudService_HibernateForceEagerFetch_Proxy;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;

@Getter
public class ServiceEagerFetch_ControllerIntegrationTestContext<E extends IdentifiableEntity<Id>, Id extends Serializable> extends ControllerIntegrationTestContext<E, Id> {
    public ServiceEagerFetch_ControllerIntegrationTestContext(String url) {
        super(url);
    }

    public ServiceEagerFetch_ControllerIntegrationTestContext() {
    }


    private Hibernate_ForceEagerFetch_Helper hibernate_forceEagerFetch_helper;


    @Autowired
    public void injectHibernate_forceEagerFetch_helper(Hibernate_ForceEagerFetch_Helper hibernate_forceEagerFetch_helper) {
        this.hibernate_forceEagerFetch_helper = hibernate_forceEagerFetch_helper;
    }

    protected <E extends IdentifiableEntity<Id>,
            Id extends Serializable,
            R extends CrudRepository<E, Id>
            > CrudService<E, Id, R> wrapWithEagerFetchProxy(CrudService<E, Id, R> crudService) {
        return new CrudService_HibernateForceEagerFetch_Proxy<>(crudService, hibernate_forceEagerFetch_helper);
    }
}
