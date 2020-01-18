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
public abstract class ServiceEagerFetchControllerIntegrationTest<E extends IdentifiableEntity<Id>, Id extends Serializable>
        extends ControllerIntegrationTest<E, Id> {

    public ServiceEagerFetchControllerIntegrationTest(String url) {
        super(url);
    }

    public ServiceEagerFetchControllerIntegrationTest() {
    }


    private HibernateForceEagerFetchUtil hibernateForceEagerFetchUtil;


    @Autowired
    public void injectHibernateForceEagerFetchUtil(HibernateForceEagerFetchUtil hibernateForceEagerFetchUtil) {
        this.hibernateForceEagerFetchUtil = hibernateForceEagerFetchUtil;
    }

    protected <E extends IdentifiableEntity<Id>,
            Id extends Serializable,
            R extends CrudRepository<E, Id>
            > CrudService<E, Id, R> wrapWithEagerFetchProxy(CrudService<E, Id, R> crudService) {
        return new CrudServiceHibernateForceEagerFetchProxy<>(crudService, hibernateForceEagerFetchUtil);
    }
}
