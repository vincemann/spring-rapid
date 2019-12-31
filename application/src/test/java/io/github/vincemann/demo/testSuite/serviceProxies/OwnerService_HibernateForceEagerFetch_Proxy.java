package io.github.vincemann.demo.testSuite.serviceProxies;

import io.github.vincemann.demo.model.Owner;
import io.github.vincemann.demo.repositories.OwnerRepository;
import io.github.vincemann.demo.service.OwnerService;
import io.github.vincemann.generic.crud.lib.test.forceEagerFetch.Hibernate_ForceEagerFetch_Helper;
import io.github.vincemann.generic.crud.lib.test.forceEagerFetch.proxy.CrudService_HibernateForceEagerFetch_Proxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class OwnerService_HibernateForceEagerFetch_Proxy
        extends CrudService_HibernateForceEagerFetch_Proxy<Owner, Long, OwnerRepository, OwnerService>
            implements OwnerService {

    @Autowired
    public OwnerService_HibernateForceEagerFetch_Proxy(OwnerService crudService, Hibernate_ForceEagerFetch_Helper helper) {
        super(crudService, helper);
    }

    @Transactional
    @Override
    public Optional<Owner> findByLastName(String lastName) {
        try {
            return getHelper().runInTransactionAndFetchEagerly_OptionalValue(() -> getCastedService().findByLastName(lastName));
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
