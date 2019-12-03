package io.github.vincemann.generic.crud.lib.test.service;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.test.equalChecker.EqualChecker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.Serializable;


@Slf4j
public abstract class ForceEagerFetch_CrudServiceTest
        <
                        S extends CrudService<E,Id,R>,
                        R extends CrudRepository<E,Id>,
                        E extends IdentifiableEntity<Id>,
                        Id extends Serializable
        >
        extends CrudServiceTest<S, R, E, Id> {


    public ForceEagerFetch_CrudServiceTest(S crudService, EqualChecker<E> equalChecker, R repository, PlatformTransactionManager transactionManager) {
        super(crudService, equalChecker, repository);
        log.debug("initiliazing proxy for crudservice: " + crudService.getClass().getSimpleName()+ " all entities will be loaded eagerly");
        setCrudService(new Hibernate_ForceEagerFetch_CrudService_Proxy<>(crudService, transactionManager));
    }
}
