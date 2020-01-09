package io.github.vincemann.generic.crud.lib.test.service;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.test.equalChecker.EqualChecker;
import io.github.vincemann.generic.crud.lib.test.service.testApi.DeleteServiceTestApi;
import io.github.vincemann.generic.crud.lib.test.service.testApi.FindServiceTestApi;
import io.github.vincemann.generic.crud.lib.test.service.testApi.SaveServiceTestApi;
import io.github.vincemann.generic.crud.lib.test.service.testApi.UpdateServiceTestApi;
import io.github.vincemann.generic.crud.lib.test.testExecutionListeners.ResetDatabaseTestExecutionListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
                        Id extends Serializable,
                        R extends CrudRepository<E,Id>
                >
    extends RootServiceTestContext<E,Id,R>
{

    private DeleteServiceTestApi<E,Id,R> deleteServiceTestApi;
    private FindServiceTestApi<E,Id,R> findServiceTestApi;
    private SaveServiceTestApi<E,Id,R> saveServiceTestApi;
    private UpdateServiceTestApi<E,Id,R> updateServiceTestApi;

    public CrudServiceIntegrationTest() {
        this.deleteServiceTestApi= new DeleteServiceTestApi<>(this);
        this.findServiceTestApi = new FindServiceTestApi<>(this);
        this.saveServiceTestApi= new SaveServiceTestApi<>(this);
        this.updateServiceTestApi= new UpdateServiceTestApi<>(this);
    }

    @Autowired
    public void injectRepository(R repository) {
        setRepository(repository);
    }

    @Autowired
    public void injectDefaultEqualChecker(EqualChecker<E> defaultEqualChecker) {
        setDefaultEqualChecker(defaultEqualChecker);
    }

    @Autowired
    public void injectCrudService(CrudService<E, Id, R> crudService) {
        setCrudService(crudService);
    }
}