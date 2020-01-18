package io.github.vincemann.generic.crud.lib.test.service;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.test.equalChecker.EqualChecker;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.DeleteServiceTest;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.FindServiceTest;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.SaveServiceTest;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.UpdateServiceTest;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.FailedServiceTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.testExecutionListeners.ResetDatabaseTestExecutionListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.TestExecutionListeners;

import java.io.Serializable;

/**
 * Abstract Test Class, offering many convenience methods for crud operation testing.
 * It is expected that Repository-Layer works properly.
 *
 * @param <E>       TestEntityType
 * @param <Id>      Id Type of TestEntityType
 */
@Slf4j
@TestExecutionListeners(
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS,
        listeners = {ResetDatabaseTestExecutionListener.class}
)
@Getter
public abstract class CrudServiceIntegrationTest
                <
                        E extends IdentifiableEntity<Id>,
                        Id extends Serializable
                >
    extends ServiceTest<E,Id>
{
    public static final String PARTIAL_UPDATE_EQUAL_CHECKER_QUALIFIER = "partialUpdateEqualCheckerBean";

    private DeleteServiceTest<E,Id> deleteServiceTest;
    private FindServiceTest<E,Id> findServiceTest;
    private SaveServiceTest<E,Id> saveServiceTest;
    private UpdateServiceTest<E,Id> updateServiceTest;

    public CrudServiceIntegrationTest() {
        this.deleteServiceTest = new DeleteServiceTest<>(this);
        this.findServiceTest = new FindServiceTest<>(this);
        this.saveServiceTest = new SaveServiceTest<>(this);
        this.updateServiceTest = new UpdateServiceTest<>(this);
    }

    @Autowired
    public void injectRepository(CrudRepository<E,Id> repository) {
        setRepository(repository);
    }

    @Autowired
    public void injectDefaultEqualChecker(EqualChecker<E> defaultEqualChecker) {
        setDefaultEqualChecker(defaultEqualChecker);
    }

    @Autowired
    public void injectCrudService(CrudService<E, Id,? extends CrudRepository<E,Id>> crudService) {
        setCrudService(crudService);
    }

    @Autowired
    @Qualifier(PARTIAL_UPDATE_EQUAL_CHECKER_QUALIFIER)
    protected void injectPartialUpdateEqualChecker(EqualChecker<E> partialUpdateEqualChecker){
        setDefaultPartialUpdateEqualChecker(partialUpdateEqualChecker);
    }
}