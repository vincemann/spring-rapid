package com.github.vincemann.springrapid.coretest.service;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.slicing.test.ImportRapidCoreServiceConfig;
import com.github.vincemann.springrapid.coretest.InitializingTest;
import com.github.vincemann.springrapid.coretest.slicing.test.ImportRapidCoreTestConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;


@Slf4j
@Getter
//only include project beans that are relevant for service tests
@ActiveProfiles(value = {"test","service","serviceTest"})
@Transactional
@Rollback
//import spring config that is relevant for service tests
@DataJpaTest
@ImportRapidCoreServiceConfig
@ImportRapidCoreTestConfig
public abstract class CrudServiceIntegrationTest
                <
                        S extends CrudService<E,Id,? extends CrudRepository<E,Id>>,
                        E extends IdentifiableEntity<Id>,
                        Id extends Serializable
                >
    extends InitializingTest
    implements InitializingBean, ApplicationContextAware
{

    private CrudRepository<E,Id> repository;

    @PersistenceContext
    private EntityManager entityManager;
    private ServiceTestTemplate testTemplate;
    private ApplicationContext applicationContext;
    private S serviceUnderTest;

    @Autowired
    public void injectRepository(CrudRepository<E,Id> repository) {
        this.repository=repository;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        testTemplate = new ServiceTestTemplate();
        testTemplate.setEntityManager(entityManager);
        testTemplate.setServiceUnderTest(serviceUnderTest);
        testTemplate.setRepository(repository);
        testTemplate.setApplicationContext(applicationContext);
    }

    @Autowired
    public void injectServiceUnderTest(S serviceUnderTest) {
        this.serviceUnderTest = serviceUnderTest;
    }

    public void setServiceUnderTest(S serviceUnderTest) {
        this.serviceUnderTest = serviceUnderTest;
    }

    public S getServiceUnderTest() {
        return (S) serviceUnderTest;
    }
}