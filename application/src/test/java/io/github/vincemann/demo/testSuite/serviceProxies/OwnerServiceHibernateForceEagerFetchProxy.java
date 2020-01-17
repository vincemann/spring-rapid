package io.github.vincemann.demo.testSuite.serviceProxies;

import io.github.vincemann.demo.model.Owner;
import io.github.vincemann.demo.repositories.OwnerRepository;
import io.github.vincemann.demo.service.OwnerService;
import io.github.vincemann.generic.crud.lib.test.forceEagerFetch.HibernateForceEagerFetchUtil;
import io.github.vincemann.generic.crud.lib.test.forceEagerFetch.proxy.CrudServiceHibernateForceEagerFetchProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class OwnerServiceHibernateForceEagerFetchProxy
        extends CrudServiceHibernateForceEagerFetchProxy<Owner, Long, OwnerRepository, OwnerService>
            implements OwnerService {

    @Autowired
    public OwnerServiceHibernateForceEagerFetchProxy(OwnerService crudService, HibernateForceEagerFetchUtil helper) {
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
