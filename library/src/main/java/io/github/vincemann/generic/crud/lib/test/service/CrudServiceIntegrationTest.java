package io.github.vincemann.generic.crud.lib.test.service;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.test.InitializingTest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;

/**
 * Abstract Test Class, offering many convenience methods for crud operation testing.
 * It is expected that Repository-Layer works properly.
 *
 * @param <E>       TestEntityType
 * @param <Id>      Id Type of TestEntityType
 */
@Slf4j
@Getter
public abstract class CrudServiceIntegrationTest
                <
                        S extends CrudService<E,Id,? extends CrudRepository<E,Id>>,
                        E extends IdentifiableEntity<Id>,
                        Id extends Serializable
                >
    extends InitializingTest
    implements InitializingBean
{
    //public static final String PARTIAL_UPDATE_EQUAL_CHECKER_QUALIFIER = "partialUpdateEqualCheckerBean";

    private CrudRepository<E,Id> repository;
    private ServiceTestTemplate testTemplate;
    private S serviceUnderTest;

    @Autowired
    public void injectRepository(CrudRepository<E,Id> repository) {
        this.repository=repository;
    }

    @Autowired
    public void injectServiceTestTemplate(ServiceTestTemplate serviceTestTemplate){
        this.testTemplate =serviceTestTemplate;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        testTemplate.setServiceUnderTest(serviceUnderTest);
    }

    @Autowired
    public void injectServiceUnderTest(S serviceUnderTest) {
        this.serviceUnderTest = serviceUnderTest;
    }
}