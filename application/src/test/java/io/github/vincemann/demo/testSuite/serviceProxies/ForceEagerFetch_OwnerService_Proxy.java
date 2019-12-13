package io.github.vincemann.demo.testSuite.serviceProxies;

import io.github.vincemann.demo.model.Owner;
import io.github.vincemann.demo.repositories.OwnerRepository;
import io.github.vincemann.demo.service.OwnerService;
import io.github.vincemann.generic.crud.lib.test.service.Hibernate_ForceEagerFetch_CrudService_Proxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

import java.util.Optional;

@Service
@Qualifier("proxy")
public class ForceEagerFetch_OwnerService_Proxy
        extends Hibernate_ForceEagerFetch_CrudService_Proxy<Owner, Long, OwnerRepository, OwnerService>
            implements OwnerService {

    @Autowired
    public ForceEagerFetch_OwnerService_Proxy(OwnerService crudService, PlatformTransactionManager transactionManager) {
        super(crudService, transactionManager);
    }

    @Override
    public Optional<Owner> findByLastName(String lastName) {
        TransactionStatus status = startNewTransaction();
        Optional<Owner> byLastName = getCrudService().findByLastName(lastName);
        byLastName.ifPresent(this::eagerFetchAllEntities);
        commitTransaction(status);
        return byLastName;
    }
}
