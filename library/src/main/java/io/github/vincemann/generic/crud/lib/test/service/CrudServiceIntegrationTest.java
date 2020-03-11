package io.github.vincemann.generic.crud.lib.test.service;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.test.InitializingTest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;


@Slf4j
@Getter
@ActiveProfiles(value = {"test","service"})
@Transactional
@Rollback
@DataJpaTest
public abstract class CrudServiceIntegrationTest
                <
                        S extends CrudService<E,Id,? extends CrudRepository<E,Id>>,
                        E extends IdentifiableEntity<Id>,
                        Id extends Serializable
                >
    extends InitializingTest
    implements InitializingBean
{

    private CrudRepository<E,Id> repository;

    @PersistenceContext
    private EntityManager entityManager;
    private ServiceTestTemplate testTemplate;
    private CrudService<E,Id,? extends CrudRepository<E,Id>> serviceUnderTest;

    @Autowired
    public void injectRepository(CrudRepository<E,Id> repository) {
        this.repository=repository;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        testTemplate = new ServiceTestTemplate();
        testTemplate.setEntityManager(entityManager);
        testTemplate.setServiceUnderTest(serviceUnderTest);
        testTemplate.setRepository(repository);
    }

    @Autowired
    public void injectServiceUnderTest(CrudService<E,Id,? extends CrudRepository<E,Id>> serviceUnderTest) {
        this.serviceUnderTest = serviceUnderTest;
    }

    public void setServiceUnderTest(CrudService<E,Id,? extends CrudRepository<E,Id>> serviceUnderTest) {
        this.serviceUnderTest = serviceUnderTest;
    }

    public S getServiceUnderTest() {
        return (S) serviceUnderTest;
    }
}