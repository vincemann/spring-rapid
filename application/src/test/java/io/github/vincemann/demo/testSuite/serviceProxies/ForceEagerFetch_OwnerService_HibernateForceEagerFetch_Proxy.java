package io.github.vincemann.demo.testSuite.serviceProxies;

import io.github.vincemann.demo.model.Owner;
import io.github.vincemann.demo.repositories.OwnerRepository;
import io.github.vincemann.demo.service.OwnerService;
import io.github.vincemann.generic.crud.lib.test.forceEagerFetch.Hibernate_ForceEagerFetch_Helper;
import io.github.vincemann.generic.crud.lib.test.forceEagerFetch.proxy.CrudService_Hibernate_ForceEagerFetch_Proxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Optional;

@Service
public class ForceEagerFetch_OwnerService_HibernateForceEagerFetch_Proxy
        extends CrudService_Hibernate_ForceEagerFetch_Proxy<Owner, Long, OwnerRepository, OwnerService>
            implements OwnerService {

    @Autowired
    public ForceEagerFetch_OwnerService_HibernateForceEagerFetch_Proxy(OwnerService crudService, Hibernate_ForceEagerFetch_Helper helper) {
        super(crudService, helper);
    }

    @Override
    public Optional<Owner> findByLastName(String lastName) {
        try {
            return getHelper().runInTransactionAndFetchEagerly_OptionalValue(() -> getCrudService().findByLastName(lastName));
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
