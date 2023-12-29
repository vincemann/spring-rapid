package com.github.vincemann.springrapid.coretest.service;

import com.github.vincemann.springrapid.core.slicing.RapidProfiles;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import com.github.vincemann.springrapid.coretest.InitializingTest;
import com.github.vincemann.springrapid.coretest.slicing.RapidTestProfiles;
import com.github.vincemann.springrapid.coretest.service.request.ServiceRequestBuilder;
import com.github.vincemann.springrapid.coretest.service.resolve.EntityPlaceholder;
import com.github.vincemann.springrapid.coretest.service.resolve.EntityPlaceholderResolver;
import com.github.vincemann.springrapid.coretest.service.result.ServiceResultActions;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;

import static com.github.vincemann.springrapid.coretest.util.TransactionalRapidTestUtil.mustBePresentIn;


@Slf4j
//only include project beans that are relevant for service tests
@ActiveProfiles(value = {RapidTestProfiles.TEST, RapidTestProfiles.SERVICE_TEST, RapidProfiles.SERVICE})
@Transactional
@Rollback
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
//import spring config that is relevant for service tests
//@DataJpaTest
//@ImportRapidCoreServiceConfig
//@ImportRapidCoreTestConfig
public abstract class CrudServiceIntegrationTest
        <
                S extends CrudService<E, Id>,
                E extends IdentifiableEntity<Id>,
                Id extends Serializable
                >
        extends InitializingTest
        implements InitializingBean, ApplicationContextAware {

    @Getter
    @PersistenceContext
    private EntityManager entityManager;
    private ServiceTestTemplate testTemplate;
    @Getter
    private ApplicationContext applicationContext;
    @Getter
    private S testedService;
    private EntityPlaceholderResolver entityPlaceholderResolver;
    private CrudServiceLocator crudServiceLocator;
    private CrudRepository<E, Id> crudRepository;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        testTemplate = ServiceTestTemplate.builder()
                .serviceUnderTest(testedService)
                .applicationContext(applicationContext)
                .entityManager(entityManager)
                .repository(crudRepository)
                .build();
    }

    @Autowired
    public void injectEntityPlaceholderResolver(EntityPlaceholderResolver entityPlaceholderResolver) {
        this.entityPlaceholderResolver = entityPlaceholderResolver;
    }

    @Autowired
    public void injectCrudServiceLocator(CrudServiceLocator crudServiceLocator) {
        this.crudServiceLocator = crudServiceLocator;
    }

    public ServiceResultActions test(ServiceRequestBuilder serviceRequestBuilder) throws Exception {
        return testTemplate.perform(serviceRequestBuilder);
    }

    @AfterEach
    public final void resetTestContext() {
        this.testTemplate.reset();
    }


    public E byId(Id id) {
        return mustBePresentIn(crudRepository, id);
    }

    /**
     * Uses {@link CrudServiceLocator} to find entity of type @param entityClass by @param id.
     */
    public <T extends IdentifiableEntity> T byId(Serializable id, Class<T> entityClass) {
        CrudService service = crudServiceLocator.find(entityClass);
        return mustBePresentIn(service, id);
    }


    /**
     * Only use in combination with {@link ServiceTestTemplate} and after calling {@link ServiceTestTemplate#perform(ServiceRequestBuilder)}.
     *
     * @see ServiceTestTemplate
     */
    public E resolve(EntityPlaceholder entityPlaceholder) {
        return entityPlaceholderResolver.resolve(entityPlaceholder, testTemplate.getTestContext());
    }

    public <R extends CrudRepository<E, Id>> R getRepository() {
        return (R) crudRepository;
    }

    @Lazy
    @Autowired
    public void injectServiceUnderTest(S serviceUnderTest) {
        this.testedService = serviceUnderTest;
    }

    public S getTestedService() {
        return (S) testedService;
    }

    public void setTestedService(S testedService) {
        this.testedService = testedService;
    }

    @Autowired
    public void injectCrudRepository(CrudRepository<E, Id> crudRepository) {
        this.crudRepository = crudRepository;
    }
}