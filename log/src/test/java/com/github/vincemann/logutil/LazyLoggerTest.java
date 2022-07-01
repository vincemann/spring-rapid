package com.github.vincemann.logutil;

import com.github.vincemann.logutil.model.*;
import com.github.vincemann.logutil.service.*;
import com.github.vincemann.logutil.service.jpa.JpaLogChild2Service;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.slicing.RapidProfiles;
import com.github.vincemann.springrapid.core.util.LazyLogger;
import com.github.vincemann.springrapid.coretest.slicing.RapidTestProfiles;
import com.github.vincemann.springrapid.coretest.slicing.TestConfig;
import com.github.vincemann.springrapid.coretest.util.TransactionalRapidTestUtil;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnitUtil;
import java.util.HashMap;
import java.util.Map;

import static com.github.vincemann.springrapid.core.util.LazyLogger.*;

@ActiveProfiles(value = {RapidTestProfiles.TEST, RapidTestProfiles.SERVICE_TEST, RapidProfiles.SERVICE})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class LazyLoggerTest {


    static final String LOG_ENTITY_NAME = "log entity";
    static final String LAZY_COL1_ENTITY1_NAME = "lazy Col1 Entity1";
    static final String LAZY_COL1_ENTITY2_NAME = "lazy Col1 Entity2";
    static final String EAGER_ENTITY1_NAME = "eager col Entity1";
    static final String EAGER_ENTITY2_NAME = "eager col Entity2";
    static final String LAZY_PARENT_NAME = "lazy parent";
    static final String LAZY_CHILD_NAME = "lazy child";
    static final String EAGER_CHILD_NAME = "eager child";
    static final String LAZY_COL2_ENTITY1_NAME = "lazy Col2 Entity1";
    static final String LAZY_COL2_ENTITY2_NAME = "lazy Col2 Entity2";



    @Autowired
    LogChildService logChildService;

    @Autowired
    JpaLogChild2Service logChild2Service;

    @Autowired
    LogChild3Service logChild3Service;

    @Autowired
    LogParentService logParentService;

    @Autowired
    LazySingleLogChildService lazySingleLogChildService;

    @Autowired
    LogEntityService logEntityService;

    @Autowired
    EagerSingleLogChildService eagerSingleLogChildService;



    LogEntity logEntity;
    LogChild lazyCol1_child1;
    LogChild lazyCol1_child2;
    LogChild2 lazyCol2_child1;
    LogChild2 lazyCol2_child2;

    LogChild3 eager_child1;
    LogChild3 eager_child2;
    LogParent lazyParent;
    LazySingleLogChild lazySingleChild;
    EagerSingleLogChild eagerSingleChild;

    LazyLogger lazyLogger;


    @BeforeEach
    void setUp() {
        logEntity = LogEntity.builder()
                .name(LOG_ENTITY_NAME)
                .build();

        lazyParent = LogParent.builder()
                .name(LAZY_PARENT_NAME)
                .build();

        lazyCol1_child1 = LogChild.builder()
                .name(LAZY_COL1_ENTITY1_NAME)
                .build();
        lazyCol1_child2 = LogChild.builder()
                .name(LAZY_COL1_ENTITY2_NAME)
                .build();

        eager_child1 = LogChild3.builder()
                .name(EAGER_ENTITY1_NAME)
                .build();

        eager_child2 = LogChild3.builder()
                .name(EAGER_ENTITY2_NAME)
                .build();

        lazyCol2_child1 = LogChild2.builder()
                .name(LAZY_COL2_ENTITY1_NAME)
                .build();
        lazyCol2_child2 = LogChild2.builder()
                .name(LAZY_COL2_ENTITY2_NAME)
                .build();

        lazySingleChild = new LazySingleLogChild(LAZY_CHILD_NAME);
        eagerSingleChild = new EagerSingleLogChild(EAGER_CHILD_NAME);
    }

    @Transactional
    @Test
    void canIgnoreLazyInitException() throws BadEntityException {
        lazyLogger = LazyLogger.builder()
                .ignoreLazyException(Boolean.TRUE)
                .ignoreEntities(Boolean.FALSE)
                .onlyLogLoaded(Boolean.FALSE)
                .build();

        // LazyLogger.setEntityManager(entityManager);


        EagerSingleLogChild savedEagerSingleChild = eagerSingleLogChildService.save(eagerSingleChild);
        logEntity.setEagerChild(savedEagerSingleChild);


        LogEntity e = logEntityService.save(logEntity);
        e.getLazyChildren1().add(lazyCol1_child1);


        TestTransaction.flagForCommit();
        TestTransaction.end();

        LogEntity savedLogEntity = logEntityService.findById(e.getId()).get();


        String s = lazyLogger.toString(savedLogEntity);

        System.err.println(s);

        Assertions.assertTrue(s.contains(LazyLogger.LAZY_INIT_EXCEPTION_LIST_STRING));
        Assertions.assertFalse(s.contains(LAZY_COL1_ENTITY1_NAME));

        Assertions.assertTrue(s.contains(EAGER_CHILD_NAME));
        Assertions.assertTrue(s.contains(LOG_ENTITY_NAME));
    }

    @Transactional
    @Test
    void canThrowLazyInitException() throws BadEntityException {
        lazyLogger = LazyLogger.builder()
                .ignoreLazyException(Boolean.FALSE)
                .ignoreEntities(Boolean.FALSE)
                .onlyLogLoaded(Boolean.FALSE)
                .build();

        // LazyLogger.setEntityManager(entityManager);


        EagerSingleLogChild savedEagerSingleChild = eagerSingleLogChildService.save(eagerSingleChild);
        logEntity.setEagerChild(savedEagerSingleChild);

        LogEntity e = logEntityService.save(logEntity);

        e.getLazyChildren1().add(lazyCol1_child1);

        TestTransaction.flagForCommit();
        TestTransaction.end();


        LogEntity savedLogEntity = logEntityService.findById(e.getId()).get();


        Assertions.assertThrows(LazyInitializationException.class, () -> lazyLogger.toString(savedLogEntity));
    }

    @Transactional
    @Test
    void canIgnoreAllEntities() throws BadEntityException {
        lazyLogger = LazyLogger.builder()
                .ignoreLazyException(Boolean.TRUE)
                .ignoreEntities(Boolean.TRUE)
                .onlyLogLoaded(Boolean.FALSE)
                .build();

        // LazyLogger.setEntityManager(entityManager);
        // lazy Child 1
        // eager child set
        // -> nothing gets logged


        EagerSingleLogChild savedEagerSingleChild = eagerSingleLogChildService.save(eagerSingleChild);
        logEntity.setEagerChild(savedEagerSingleChild);



        LogEntity e = logEntityService.save(logEntity);
        e.getLazyChildren1().add(lazyCol1_child1);

        TestTransaction.flagForCommit();
        TestTransaction.end();

        LogEntity savedLogEntity = logEntityService.findById(e.getId()).get();


        String s = lazyLogger.toString(savedLogEntity);

        System.err.println(s);

        Assertions.assertFalse(s.contains(LazyLogger.LAZY_INIT_EXCEPTION_LIST_STRING));
        Assertions.assertFalse(s.contains(LazyLogger.LAZY_INIT_EXCEPTION_STRING));
        Assertions.assertFalse(s.contains(LAZY_COL1_ENTITY1_NAME));
        Assertions.assertFalse(s.contains(EAGER_CHILD_NAME));

        Assertions.assertTrue(s.contains(LOG_ENTITY_NAME));

    }


    @Transactional
    @Test
    void canBlacklistFields() throws BadEntityException {
        lazyLogger = LazyLogger.builder()
                .ignoreLazyException(Boolean.TRUE)
                .ignoreEntities(Boolean.FALSE)
                .onlyLogLoaded(Boolean.FALSE)
                .propertyBlackList(Sets.newHashSet("eagerChild", "lazyChildren1"))
                .build();
        // LazyLogger.setEntityManager(entityManager);

        LogChild child11 = logChildService.save(lazyCol1_child1);
        child11.setLogEntity(logEntity);
        LogChild child12 = logChildService.save(lazyCol1_child2);
        child12.setLogEntity(logEntity);

//        logEntity.setLazyChildren1(Sets.newHashSet(child11,child12));
        logEntity.getLazyChildren1().add(child11);
        logEntity.getLazyChildren1().add(child12);

        LogChild2 child21 = logChild2Service.save(lazyCol2_child1);
        child21.setLogEntity(logEntity);
        LogChild2 child22 = logChild2Service.save(lazyCol2_child2);
        child22.setLogEntity(logEntity);

        logEntity.getLazyChildren2().add(child21);
        logEntity.getLazyChildren2().add(child22);


        // only eagerCollection persists

        LogEntity saved = logEntityService.save(logEntity);
        LogEntity foundLogEntity = logEntityService.findById(saved.getId()).get();

        TestTransaction.flagForCommit();
        TestTransaction.end();

        String logResult = lazyLogger.toString(foundLogEntity);

        System.err.println(logResult);

        Assertions.assertFalse(logResult.contains(LAZY_COL1_ENTITY1_NAME));
        Assertions.assertFalse(logResult.contains(LAZY_COL1_ENTITY2_NAME));
        Assertions.assertFalse(logResult.contains(EAGER_CHILD_NAME));

        Assertions.assertTrue(logResult.contains(LazyLogger.IGNORED_STRING));
        Assertions.assertTrue(logResult.contains(LOG_ENTITY_NAME));
        Assertions.assertTrue(logResult.contains(LAZY_COL2_ENTITY1_NAME));
        Assertions.assertTrue(logResult.contains(LAZY_COL2_ENTITY2_NAME));
    }


    @Transactional
    @Test
    void canIgnoreUnloadedEntities_andLogLoaded_inTransactionalContext() throws BadEntityException {
        lazyLogger = LazyLogger.builder()
                .ignoreLazyException(Boolean.TRUE)
                .ignoreEntities(Boolean.FALSE)
                .onlyLogLoaded(Boolean.TRUE)
                .build();
        // LazyLogger.setEntityManager(entityManager);

        // fill both lazy cols
        // lazyCol1 loaded -> gets Logged
        // lazyCol2 not loaded -> <ignored unloaded>
        // eager child -> gets logged


        EagerSingleLogChild savedEagerSingleChild = eagerSingleLogChildService.save(eagerSingleChild);
        logEntity.setEagerChild(savedEagerSingleChild);

        LogEntity logEntity = logEntityService.save(this.logEntity);

        LogChild child11 = logChildService.save(lazyCol1_child1);
        child11.setLogEntity(logEntity);
        LogChild child12 = logChildService.save(lazyCol1_child2);
        child12.setLogEntity(logEntity);

//        logEntity.setLazyChildren1(Sets.newHashSet(child11,child12));
        logEntity.getLazyChildren1().add(child11);
        logEntity.getLazyChildren1().add(child12);

        LogChild2 child21 = logChild2Service.save(lazyCol2_child1);
        child21.setLogEntity(logEntity);
        LogChild2 child22 = logChild2Service.save(lazyCol2_child2);
        child22.setLogEntity(logEntity);

//        logEntity.setLazyChildren2(Sets.newHashSet(child21,child22));
        logEntity.getLazyChildren2().add(child21);
        logEntity.getLazyChildren2().add(child22);

//        List<LogChild> resultList = entityManager.createQuery("SELECT NEW com.github.vincemann.logutil.model.LogChild(g.id, g.name,g.logEntity) FROM LogChild g").getResultList();
//        logEntity.setLazyChildren1(Sets.newHashSet(resultList));


        Long id = this.logEntity.getId();


        TestTransaction.flagForCommit();
        TestTransaction.end();

        logEntity = logEntityService.findByIdAndLoadCol1(id).get();


//        entityManager.detach(logEntity.getLazyChildren2());
        Assertions.assertTrue(isLoaded(logEntity, "lazyChildren1"));
        Assertions.assertFalse(isLoaded(logEntity, "lazyChildren2"));


        TestTransaction.start();
//        logEntity = entityManager.merge(logEntity);
        String logResult = lazyLogger.toString(logEntity);

        TestTransaction.flagForCommit();
        TestTransaction.end();

        System.err.println(logResult);


        assertContainsStringOnce(logResult,LAZY_COL1_ENTITY1_NAME);
        assertContainsStringOnce(logResult,LAZY_COL1_ENTITY2_NAME);
        assertContainsStringOnce(logResult,EAGER_CHILD_NAME);
        assertContainsStringOnce(logResult,LOG_ENTITY_NAME);
        assertContainsStringOnce(logResult,IGNORED_UNLOADED_STRING);

        Assertions.assertFalse(logResult.contains(LAZY_COL2_ENTITY1_NAME));
        Assertions.assertFalse(logResult.contains(LAZY_COL2_ENTITY2_NAME));
    }


    @Transactional
    @Test
    void canIgnoreUnloadedEntities_andLogLoaded_inNotTransactionalContext() throws BadEntityException {
        lazyLogger = LazyLogger.builder()
                .ignoreLazyException(Boolean.TRUE)
                .ignoreEntities(Boolean.FALSE)
                .onlyLogLoaded(Boolean.TRUE)
                .build();
        // LazyLogger.setEntityManager(entityManager);

        // fill both lazy cols
        // lazyCol1 loaded -> gets Logged
        // lazyCol2 not loaded -> <ignored unloaded>
        // eager child -> gets logged


        EagerSingleLogChild savedEagerSingleChild = eagerSingleLogChildService.save(eagerSingleChild);
        logEntity.setEagerChild(savedEagerSingleChild);

        LogEntity logEntity = logEntityService.save(this.logEntity);

        LogChild child11 = logChildService.save(lazyCol1_child1);
        child11.setLogEntity(logEntity);
        LogChild child12 = logChildService.save(lazyCol1_child2);
        child12.setLogEntity(logEntity);

//        logEntity.setLazyChildren1(Sets.newHashSet(child11,child12));
        logEntity.getLazyChildren1().add(child11);
        logEntity.getLazyChildren1().add(child12);

        LogChild2 child21 = logChild2Service.save(lazyCol2_child1);
        child21.setLogEntity(logEntity);
        LogChild2 child22 = logChild2Service.save(lazyCol2_child2);
        child22.setLogEntity(logEntity);

//        logEntity.setLazyChildren2(Sets.newHashSet(child21,child22));
        logEntity.getLazyChildren2().add(child21);
        logEntity.getLazyChildren2().add(child22);

//        List<LogChild> resultList = entityManager.createQuery("SELECT NEW com.github.vincemann.logutil.model.LogChild(g.id, g.name,g.logEntity) FROM LogChild g").getResultList();
//        logEntity.setLazyChildren1(Sets.newHashSet(resultList));


        Long id = this.logEntity.getId();

        TestTransaction.flagForCommit();
        TestTransaction.end();

        logEntity = logEntityService.findById(id).get();
        logEntity = logEntityService.findByIdAndLoadCol1(id).get();
//        entityManager.detach(logEntity.getLazyChildren2());
        Assertions.assertTrue(isLoaded(logEntity, "lazyChildren1"));
        Assertions.assertFalse(isLoaded(logEntity, "lazyChildren2"));

        String logResult = lazyLogger.toString(logEntity);

        System.err.println(logResult);


        assertContainsStringOnce(logResult,LAZY_COL1_ENTITY1_NAME);
        assertContainsStringOnce(logResult,LAZY_COL1_ENTITY2_NAME);
        assertContainsStringOnce(logResult,EAGER_CHILD_NAME);
        assertContainsStringOnce(logResult,LOG_ENTITY_NAME);
        assertContainsStringOnce(logResult,IGNORED_UNLOADED_STRING);

        Assertions.assertFalse(logResult.contains(LAZY_COL2_ENTITY1_NAME));
        Assertions.assertFalse(logResult.contains(LAZY_COL2_ENTITY2_NAME));
    }

    @Transactional
    @Test
    void canIgnoreUnloadedEntities_andLogLoaded_butNotLogBlacklistedLoaded_inNotTransactionalContext() throws BadEntityException {
        lazyLogger = LazyLogger.builder()
                .ignoreLazyException(Boolean.TRUE)
                .ignoreEntities(Boolean.FALSE)
                .onlyLogLoaded(Boolean.TRUE)
                .logLoadedBlacklist(Sets.newHashSet("lazyChildren2"))
                .build();
        // LazyLogger.setEntityManager(entityManager);

        // fill both lazy cols
        // lazyCol1 loaded -> gets Logged
        // lazyCol2 not loaded -> <ignored unloaded>
        // eager child -> gets logged


        EagerSingleLogChild savedEagerSingleChild = eagerSingleLogChildService.save(eagerSingleChild);
        logEntity.setEagerChild(savedEagerSingleChild);

        LogEntity logEntity = logEntityService.save(this.logEntity);

        LogChild child11 = logChildService.save(lazyCol1_child1);
        child11.setLogEntity(logEntity);
        LogChild child12 = logChildService.save(lazyCol1_child2);
        child12.setLogEntity(logEntity);

//        logEntity.setLazyChildren1(Sets.newHashSet(child11,child12));
        logEntity.getLazyChildren1().add(child11);
        logEntity.getLazyChildren1().add(child12);

        LogChild2 child21 = logChild2Service.save(lazyCol2_child1);
        child21.setLogEntity(logEntity);
        LogChild2 child22 = logChild2Service.save(lazyCol2_child2);
        child22.setLogEntity(logEntity);

//        logEntity.setLazyChildren2(Sets.newHashSet(child21,child22));
        logEntity.getLazyChildren2().add(child21);
        logEntity.getLazyChildren2().add(child22);

//        List<LogChild> resultList = entityManager.createQuery("SELECT NEW com.github.vincemann.logutil.model.LogChild(g.id, g.name,g.logEntity) FROM LogChild g").getResultList();
//        logEntity.setLazyChildren1(Sets.newHashSet(resultList));


        Long id = this.logEntity.getId();

        TestTransaction.flagForCommit();
        TestTransaction.end();

        logEntity = logEntityService.findByIdAndLoadCol1AndCol2(id).get();
//        entityManager.detach(logEntity.getLazyChildren2());
        Assertions.assertTrue(isLoaded(logEntity, "lazyChildren1"));
        Assertions.assertTrue(isLoaded(logEntity, "lazyChildren2"));


        String logResult = lazyLogger.toString(logEntity);

        System.err.println(logResult);


        assertContainsStringOnce(logResult,LAZY_COL1_ENTITY1_NAME);
        assertContainsStringOnce(logResult,LAZY_COL1_ENTITY2_NAME);
        assertContainsStringOnce(logResult,EAGER_CHILD_NAME);
        assertContainsStringOnce(logResult,LOG_ENTITY_NAME);
        assertContainsStringOnce(logResult,IGNORED_LOAD_BLACKLISTED_STRING);

        Assertions.assertFalse(logResult.contains(LAZY_COL2_ENTITY1_NAME));
        Assertions.assertFalse(logResult.contains(LAZY_COL2_ENTITY2_NAME));
    }

    @Transactional
    @Test
    void canLimitAllCollectionsLogSize() throws BadEntityException {
        int maxEntitiesInCollections = 1;

        lazyLogger = LazyLogger.builder()
                .ignoreLazyException(Boolean.TRUE)
                .ignoreEntities(Boolean.FALSE)
                .onlyLogLoaded(Boolean.FALSE)
                .maxEntitiesLoggedInCollections(maxEntitiesInCollections)
                .build();
        // LazyLogger.setEntityManager(entityManager);

        // fill both lazy cols
        // lazyCol1 loaded -> gets Logged
        // lazyCol2 not loaded -> <ignored unloaded>
        // eager child -> gets logged


        EagerSingleLogChild savedEagerSingleChild = eagerSingleLogChildService.save(eagerSingleChild);
        logEntity.setEagerChild(savedEagerSingleChild);

        LogEntity logEntity = logEntityService.save(this.logEntity);

        LogChild child11 = logChildService.save(lazyCol1_child1);
        child11.setLogEntity(logEntity);
        LogChild child12 = logChildService.save(lazyCol1_child2);
        child12.setLogEntity(logEntity);

//        logEntity.setLazyChildren1(Sets.newHashSet(child11,child12));
        logEntity.getLazyChildren1().add(child11);
        logEntity.getLazyChildren1().add(child12);

        LogChild2 child21 = logChild2Service.save(lazyCol2_child1);
        child21.setLogEntity(logEntity);

//        logEntity.setLazyChildren2(Sets.newHashSet(child21,child22));
        logEntity.getLazyChildren2().add(child21);

//        List<LogChild> resultList = entityManager.createQuery("SELECT NEW com.github.vincemann.logutil.model.LogChild(g.id, g.name,g.logEntity) FROM LogChild g").getResultList();
//        logEntity.setLazyChildren1(Sets.newHashSet(resultList));


        Long id = this.logEntity.getId();

        TestTransaction.flagForCommit();
        TestTransaction.end();

        logEntity = logEntityService.findByIdAndLoadCol1AndCol2(id).get();
//        entityManager.detach(logEntity.getLazyChildren2());
        Assertions.assertTrue(isLoaded(logEntity, "lazyChildren1"));
        Assertions.assertTrue(isLoaded(logEntity, "lazyChildren2"));


        String logResult = lazyLogger.toString(logEntity);

        System.err.println(logResult);


        assertContainsStringOnce(logResult,LAZY_COL2_ENTITY1_NAME);
        assertContainsStringOnce(logResult,EAGER_CHILD_NAME);
        assertContainsStringOnce(logResult,LOG_ENTITY_NAME);
        assertContainsStringOnce(logResult,TOO_MANY_ENTRIES_STRING);

        Assertions.assertFalse(logResult.contains(LAZY_COL1_ENTITY1_NAME));
        Assertions.assertFalse(logResult.contains(LAZY_COL1_ENTITY2_NAME));
    }

    @Transactional
    @Test
    void canLimitSpecificCollectionsLogSize() throws BadEntityException {
        Map<String,Integer> maxEntityLimitations = new HashMap<>();
        maxEntityLimitations.put("lazyChildren1",1);
        maxEntityLimitations.put("lazyChildren2",1);
        maxEntityLimitations.put("eagerChildren",3);

        lazyLogger = LazyLogger.builder()
                .ignoreLazyException(Boolean.TRUE)
                .ignoreEntities(Boolean.FALSE)
                .onlyLogLoaded(Boolean.FALSE)
                .maxEntitiesLoggedPropertyMap(maxEntityLimitations)
                .build();
        // LazyLogger.setEntityManager(entityManager);

        // fill both lazy cols
        // lazyCol1 loaded -> gets Logged
        // lazyCol2 not loaded -> <ignored unloaded>
        // eager child -> gets logged


        EagerSingleLogChild savedEagerSingleChild = eagerSingleLogChildService.save(eagerSingleChild);
        logEntity.setEagerChild(savedEagerSingleChild);

        LogEntity logEntity = logEntityService.save(this.logEntity);


        LogChild child11 = logChildService.save(lazyCol1_child1);
        child11.setLogEntity(logEntity);
        LogChild child12 = logChildService.save(lazyCol1_child2);
        child12.setLogEntity(logEntity);

//        logEntity.setLazyChildren1(Sets.newHashSet(child11,child12));
        logEntity.getLazyChildren1().add(child11);
        logEntity.getLazyChildren1().add(child12);

        LogChild2 child21 = logChild2Service.save(lazyCol2_child1);
        child21.setLogEntity(logEntity);
        LogChild2 child22 = logChild2Service.save(lazyCol2_child2);
        child22.setLogEntity(logEntity);


        logEntity.getLazyChildren2().add(child21);
        logEntity.getLazyChildren2().add(child22);

        LogChild3 child31 = logChild3Service.save(eager_child1);
        child31.setLogEntity(logEntity);
        LogChild3 child32 = logChild3Service.save(eager_child2);
        child32.setLogEntity(logEntity);


        logEntity.getEagerChildren().add(child31);
        logEntity.getEagerChildren().add(child32);

//        List<LogChild> resultList = entityManager.createQuery("SELECT NEW com.github.vincemann.logutil.model.LogChild(g.id, g.name,g.logEntity) FROM LogChild g").getResultList();
//        logEntity.setLazyChildren1(Sets.newHashSet(resultList));


        Long id = this.logEntity.getId();

        TestTransaction.flagForCommit();
        TestTransaction.end();

        logEntity = logEntityService.findByIdAndLoadCol1AndCol2(id).get();
//        entityManager.detach(logEntity.getLazyChildren2());
        Assertions.assertTrue(isLoaded(logEntity, "lazyChildren1"));
        Assertions.assertTrue(isLoaded(logEntity, "lazyChildren2"));
        Assertions.assertTrue(isLoaded(logEntity, "eagerChildren"));


        String logResult = lazyLogger.toString(logEntity);

        System.err.println(logResult);


        assertContainsStringOnce(logResult,EAGER_ENTITY1_NAME);
        assertContainsStringOnce(logResult,EAGER_ENTITY2_NAME);
        assertContainsStringOnce(logResult,EAGER_CHILD_NAME);
        assertContainsStringOnce(logResult,LOG_ENTITY_NAME);
        assertContainsString(logResult,TOO_MANY_ENTRIES_STRING,2);

        Assertions.assertFalse(logResult.contains(LAZY_COL1_ENTITY1_NAME));
        Assertions.assertFalse(logResult.contains(LAZY_COL1_ENTITY2_NAME));
        Assertions.assertFalse(logResult.contains(LAZY_COL2_ENTITY2_NAME));
        Assertions.assertFalse(logResult.contains(LAZY_COL2_ENTITY2_NAME));
    }

    @Transactional
    @Test
    void canMapToId() throws BadEntityException {
        lazyLogger = LazyLogger.builder()
                .ignoreLazyException(Boolean.TRUE)
                .ignoreEntities(Boolean.FALSE)
                .idOnly(Boolean.TRUE)
                .onlyLogLoaded(Boolean.FALSE)
                .build();
        // LazyLogger.setEntityManager(entityManager);

        // fill both lazy cols
        // lazyCol1 loaded -> gets Logged
        // lazyCol2 not loaded -> <ignored unloaded>
        // eager child -> gets logged


        EagerSingleLogChild savedEagerSingleChild = eagerSingleLogChildService.save(eagerSingleChild);
        logEntity.setEagerChild(savedEagerSingleChild);

        LogEntity logEntity = logEntityService.save(this.logEntity);


        LogChild child11 = logChildService.save(lazyCol1_child1);
        child11.setLogEntity(logEntity);
        LogChild child12 = logChildService.save(lazyCol1_child2);
        child12.setLogEntity(logEntity);

//        logEntity.setLazyChildren1(Sets.newHashSet(child11,child12));
        logEntity.getLazyChildren1().add(child11);
        logEntity.getLazyChildren1().add(child12);

        LogChild2 child21 = logChild2Service.save(lazyCol2_child1);
        child21.setLogEntity(logEntity);
        LogChild2 child22 = logChild2Service.save(lazyCol2_child2);
        child22.setLogEntity(logEntity);


        logEntity.getLazyChildren2().add(child21);
        logEntity.getLazyChildren2().add(child22);

//        List<LogChild> resultList = entityManager.createQuery("SELECT NEW com.github.vincemann.logutil.model.LogChild(g.id, g.name,g.logEntity) FROM LogChild g").getResultList();
//        logEntity.setLazyChildren1(Sets.newHashSet(resultList));


        Long id = this.logEntity.getId();

        TestTransaction.flagForCommit();
        TestTransaction.end();

        logEntity = logEntityService.findByIdAndLoadCol1AndCol2(id).get();

        String logResult = lazyLogger.toString(logEntity);

        System.err.println(logResult);


        assertContainsIdOnce(logResult,child11.getId());
        assertContainsIdOnce(logResult,child12.getId());
        assertContainsIdOnce(logResult,child21.getId());
        assertContainsIdOnce(logResult,child22.getId());
        assertContainsIdOnce(logResult,savedEagerSingleChild.getId());
        assertContainsStringOnce(logResult,LOG_ENTITY_NAME);

        Assertions.assertFalse(logResult.contains(LAZY_COL1_ENTITY1_NAME));
        Assertions.assertFalse(logResult.contains(LAZY_COL1_ENTITY2_NAME));
        Assertions.assertFalse(logResult.contains(LAZY_COL2_ENTITY2_NAME));
        Assertions.assertFalse(logResult.contains(LAZY_COL2_ENTITY2_NAME));
        Assertions.assertFalse(logResult.contains(EAGER_CHILD_NAME));
    }


    @Transactional
    @Test
    void canMapToId_andLimitResults() throws BadEntityException {

        Map<String,Integer> maxEntityLimitations = new HashMap<>();
        maxEntityLimitations.put("lazyChildren1",1);
        maxEntityLimitations.put("lazyChildren2",1);
        maxEntityLimitations.put("eagerChildren",3);


        lazyLogger = LazyLogger.builder()
                .ignoreLazyException(Boolean.TRUE)
                .ignoreEntities(Boolean.FALSE)
                .idOnly(Boolean.TRUE)
                .onlyLogLoaded(Boolean.FALSE)
                .maxEntitiesLoggedPropertyMap(maxEntityLimitations)
                .build();
        // LazyLogger.setEntityManager(entityManager);

        // fill both lazy cols
        // lazyCol1 loaded -> gets Logged
        // lazyCol2 not loaded -> <ignored unloaded>
        // eager child -> gets logged


        EagerSingleLogChild savedEagerSingleChild = eagerSingleLogChildService.save(eagerSingleChild);
        logEntity.setEagerChild(savedEagerSingleChild);

        LogEntity logEntity = logEntityService.save(this.logEntity);


        LogChild child11 = logChildService.save(lazyCol1_child1);
        child11.setLogEntity(logEntity);
        LogChild child12 = logChildService.save(lazyCol1_child2);
        child12.setLogEntity(logEntity);

//        logEntity.setLazyChildren1(Sets.newHashSet(child11,child12));
        logEntity.getLazyChildren1().add(child11);
        logEntity.getLazyChildren1().add(child12);

        LogChild2 child21 = logChild2Service.save(lazyCol2_child1);
        child21.setLogEntity(logEntity);
        LogChild2 child22 = logChild2Service.save(lazyCol2_child2);
        child22.setLogEntity(logEntity);


        logEntity.getLazyChildren2().add(child21);
        logEntity.getLazyChildren2().add(child22);

//        List<LogChild> resultList = entityManager.createQuery("SELECT NEW com.github.vincemann.logutil.model.LogChild(g.id, g.name,g.logEntity) FROM LogChild g").getResultList();
//        logEntity.setLazyChildren1(Sets.newHashSet(resultList));

        LogChild3 child31 = logChild3Service.save(eager_child1);
        child31.setLogEntity(logEntity);
        LogChild3 child32 = logChild3Service.save(eager_child2);
        child32.setLogEntity(logEntity);


        logEntity.getEagerChildren().add(child31);
        logEntity.getEagerChildren().add(child32);

        Long id = this.logEntity.getId();

        TestTransaction.flagForCommit();
        TestTransaction.end();

        logEntity = logEntityService.findByIdAndLoadCol1AndCol2(id).get();

        String logResult = lazyLogger.toString(logEntity);

        System.err.println(logResult);


        assertContainsIdOnce(logResult,child31.getId());
        assertContainsIdOnce(logResult,child32.getId());
        assertContainsString(logResult,TOO_MANY_ENTRIES_STRING,2);
        assertContainsIdOnce(logResult,savedEagerSingleChild.getId());
        assertContainsStringOnce(logResult,LOG_ENTITY_NAME);
    }


    private void assertContainsStringOnce(String s, String subString){
        Assertions.assertEquals(1,StringUtils.countMatches(s,subString));
    }

    private void assertContainsIdOnce(String s, Long id){
        if (id.equals(1L)){
            assertContainsString(s,id.toString(),2);
        }else if (id.equals(2L)){
            assertContainsString(s,id.toString(),2);
        }else {
            assertContainsStringOnce(s,id.toString());
        }
    }


    private void assertContainsString(String s, String subString, Integer times){
        Assertions.assertEquals(times,StringUtils.countMatches(s,subString));
    }

    private boolean isLoaded(Object parent, String childPropertyName) {
        PersistenceUnitUtil persistenceUtil =
                LazyLogger.getEntityManager().getEntityManagerFactory().getPersistenceUnitUtil();
        Boolean loaded = persistenceUtil.isLoaded(parent, childPropertyName);
        return loaded;
    }


    @AfterEach
    void tearDown() {
        TransactionalRapidTestUtil.clear(logChildService);
        TransactionalRapidTestUtil.clear(logChild2Service);
        TransactionalRapidTestUtil.clear(logChild3Service);
        TransactionalRapidTestUtil.clear(logParentService);
        TransactionalRapidTestUtil.clear(lazySingleLogChildService);
        TransactionalRapidTestUtil.clear(eagerSingleLogChildService);
        TransactionalRapidTestUtil.clear(logEntityService);
    }

}