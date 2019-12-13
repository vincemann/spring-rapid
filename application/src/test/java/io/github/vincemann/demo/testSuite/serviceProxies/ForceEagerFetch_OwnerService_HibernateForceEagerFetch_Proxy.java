package io.github.vincemann.demo.testSuite.serviceProxies;

import io.github.vincemann.demo.model.Owner;
import io.github.vincemann.demo.repositories.OwnerRepository;
import io.github.vincemann.demo.service.OwnerService;
import io.github.vincemann.generic.crud.lib.test.service.forceEagerFetch.CrudService_Hibernate_ForceEagerFetch_Proxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

import java.util.Optional;

@Service
public class ForceEagerFetch_OwnerService_HibernateForceEagerFetch_Proxy
        extends CrudService_Hibernate_ForceEagerFetch_Proxy<Owner, Long, OwnerRepository, OwnerService>
            implements OwnerService {

    @Autowired
    public ForceEagerFetch_OwnerService_HibernateForceEagerFetch_Proxy(OwnerService crudService, PlatformTransactionManager transactionManager) {
        super(crudService, transactionManager);
    }

    @Override
    public Optional<Owner> findByLastName(String lastName) {
        try {
            return runInTransactionAndFetchEagerly_OptionalValue(() -> getCrudService().findByLastName(lastName));
        }catch (Exception e){
            throw new RuntimeException(e);
        }

        /*
        TransactionStatus status = startNewTransaction();
        Optional<Owner> byLastName =
        byLastName.ifPresent(this::eagerFetchAllEntities);
        commitTransaction(status);
        return byLastName;*/
    }
}
