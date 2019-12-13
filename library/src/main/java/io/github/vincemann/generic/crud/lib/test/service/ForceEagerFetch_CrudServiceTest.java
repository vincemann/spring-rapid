package io.github.vincemann.generic.crud.lib.test.service;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.test.equalChecker.EqualChecker;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
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


    public ForceEagerFetch_CrudServiceTest(S proxy,  EqualChecker<E> equalChecker, R repository) {
        super(proxy, equalChecker, repository);
        Assertions.assertTrue(proxy instanceof Hibernate_ForceEagerFetch_CrudService_Proxy);
    }


}
