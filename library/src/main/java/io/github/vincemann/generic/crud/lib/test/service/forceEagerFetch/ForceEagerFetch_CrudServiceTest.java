package io.github.vincemann.generic.crud.lib.test.service.forceEagerFetch;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.test.equalChecker.EqualChecker;
import io.github.vincemann.generic.crud.lib.test.service.CrudServiceTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.data.repository.CrudRepository;

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
        Assertions.assertTrue(proxy instanceof CrudService_Hibernate_ForceEagerFetch_Proxy);
    }


}
