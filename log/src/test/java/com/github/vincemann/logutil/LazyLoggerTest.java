package com.github.vincemann.logutil;

import com.github.vincemann.logutil.model.*;
import com.github.vincemann.logutil.service.*;
import com.github.vincemann.logutil.service.jpa.JpaLogChild2Service;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.slicing.RapidProfiles;
import com.github.vincemann.springrapid.core.util.LazyLogger;
import com.github.vincemann.springrapid.core.util.TransactionalTemplate;
import com.github.vincemann.springrapid.coretest.slicing.RapidTestProfiles;
import com.github.vincemann.springrapid.coretest.util.TransactionalRapidTestUtil;
import com.google.common.collect.Sets;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

//    @Autowired
//    LazyItemService lazyItemService;


//    @Autowired
//    OwnerRepository ownerRepository;

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

    @PersistenceContext
    EntityManager entityManager;

//    @Autowired
//    TransactionalTemplate transactionalTemplate;


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

        lazyLogger.setEntityManager(entityManager);


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

        lazyLogger.setEntityManager(entityManager);


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

        lazyLogger.setEntityManager(entityManager);
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
        lazyLogger.setEntityManager(entityManager);

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
        lazyLogger.setEntityManager(entityManager);

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
        lazyLogger.setEntityManager(entityManager);

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
        lazyLogger.setEntityManager(entityManager);

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
        lazyLogger.setEntityManager(entityManager);

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
        lazyLogger.setEntityManager(entityManager);

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


    private void assertContainsStringOnce(String s, String subString){
        Assertions.assertEquals(1,StringUtils.countMatches(s,subString));
    }

    private void assertContainsString(String s, String subString, Integer times){
        Assertions.assertEquals(times,StringUtils.countMatches(s,subString));
    }

    private boolean isLoaded(Object parent, String childPropertyName) {
        PersistenceUnitUtil persistenceUtil =
                entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
        Boolean loaded = persistenceUtil.isLoaded(parent, childPropertyName);
        return loaded;
    }
//
//    @Test
//    void canShowLoadedLazyCollection_andIgnoreNotLoadedLazyCollectionsException() throws BadEntityException {
//        LogParent lazyItem = new LogParent();
//        LogChild logChild = new LogChild("loaded");
//
//        logEntity.getLazyChildren1().add(lazyItem);
//        logEntity.getLazyChildren2().add(logChild);
//
//        Owner savedKahn = ownerService.save(logEntity);
//
//
//        Owner found = ownerService.lazyLoadFindById(savedKahn.getId());
//        // would result in lazyinit exception
////        found.getLazyItems().size();
//
//        String s = LazyLogUtils.toString(found, Boolean.FALSE);
//        System.err.println(s);
//
//        Assertions.assertTrue(s.contains("LazyInitializationException"));
//        Assertions.assertTrue(s.contains("loaded"));
//    }
//
//    @Test
//    void canIgnoreCollections() throws BadEntityException {
//        LogParent lazyItem = new LogParent();
//        LogChild logChild = new LogChild("loaded");
//
//        logEntity.getLazyChildren1().add(lazyItem);
//        logEntity.getLazyChildren2().add(logChild);
//        logEntity.getPets().add(bello);
//
//        Owner savedKahn = ownerService.save(logEntity);
//
//
//        Owner found = ownerService.lazyLoadFindById(savedKahn.getId());
//        // would result in lazyinit exception
////        found.getLazyItems().size();
//
//        String s = LazyLogUtils.toString(found);
//        System.err.println(s);
//
//        Assertions.assertFalse(s.contains("LazyInitializationException"));
//        Assertions.assertFalse(s.contains("loaded"));
//        Assertions.assertFalse(s.contains("bello"));
//        Assertions.assertTrue(s.contains(logEntity.getFirstName()));
//        Assertions.assertTrue(s.contains(logEntity.getLastName()));
//        Assertions.assertTrue(s.contains(logEntity.getCity()));
//    }
//
//    @Transactional
//    @Test
//    void canIgnoreSomeEntitiesAndCollections() throws BadEntityException {
//        logEntity.setClinicCard(clinicCardService.save(clinicCard));
//        logEntity.getPets().add(bello);
//
//        Owner savedKahn = ownerService.save(logEntity);
//
//        String s = LazyLogUtils.toString(savedKahn,Boolean.TRUE, Sets.newHashSet("clinicCard"));
//        System.err.println(s);
//
//        Assertions.assertFalse(s.contains("bello"));
//        Assertions.assertTrue(s.contains(clinicCard.getRegistrationReason()));
//
//        Assertions.assertTrue(s.contains(logEntity.getFirstName()));
//        Assertions.assertTrue(s.contains(logEntity.getLastName()));
//        Assertions.assertTrue(s.contains(logEntity.getCity()));
//    }
//
//
//    @Test
//    void canThrowLazy() throws BadEntityException {
//        LogParent lazyItem = new LogParent();
////        LazyItem savedLazyItem = getService().save(lazyItem);
//
//        logEntity.getLazyChildren1().add(lazyItem);
//
//        Owner savedKahn = ownerService.save(logEntity);
//
//
//        Owner found = ownerService.findById(savedKahn.getId()).get();
//
//        Assertions.assertThrows(LazyInitializationException.class,
//                () -> LazyLogUtils.toString(found,new HashSet<>(), Boolean.FALSE,Boolean.FALSE,Boolean.FALSE));
//    }

    @AfterEach
    void tearDown() {
        TransactionalRapidTestUtil.clear(logChildService);
        TransactionalRapidTestUtil.clear(logParentService);
        TransactionalRapidTestUtil.clear(lazySingleLogChildService);
        TransactionalRapidTestUtil.clear(eagerSingleLogChildService);
        TransactionalRapidTestUtil.clear(logEntityService);

//        clinicCardService.deleteAll();
//        petService.deleteAll();

//
//        logChildService.deleteAll();
//        logParentService.deleteAll();
//        lazySingleLogChildService.deleteAll();
//        eagerSingleLogChildService.deleteAll();
    }

    //    @Test
//    void doesNotLoadAdditionalEntitiesInTransaction() throws BadEntityException {
//        final Long[] id = {null};
//        transactionalTestTemplate.doInTransaction(new Runnable() {
//            @SneakyThrows
//            @Override
//            public void run() {
//                LazyLoadedItem lazyLoadedItem = new LazyLoadedItem("loaded");
//                LazyLoadedItem lazyLoadedItem2 = new LazyLoadedItem("loaded2");
//
//                LazyExceptionItem notLoadedItem = new LazyExceptionItem("not-loaded");
//
//
//                kahn.getLazyLoadedItems().add(lazyLoadedItem);
//                kahn.getLazyLoadedItems().add(lazyLoadedItem2);
//                kahn.getLazyExceptionItems().add(notLoadedItem);
//
//                Owner savedKahn = ownerService.save(kahn);
//                id[0] = savedKahn.getId();
//            }
//        });
//
//        final Owner[] found = new Owner[1];
//        transactionalTestTemplate.doInTransaction(new Runnable() {
//            @Override
//            public void run() {
//                found[0] = ownerService.lazyLoadFindById(id[0]);
//                String s = LazyLogUtils.toString(found[0], Boolean.FALSE);
//                System.err.println(s);
//
////                Assertions.assertFalse(s.contains("LazyInitializationException"));
//                Assertions.assertTrue(s.contains("loaded"));
//                Assertions.assertTrue(s.contains("loaded2"));
//                Assertions.assertFalse(s.contains("not-loaded"));
//            }
//        });
//        // to string has not loaded more items
//        Assertions.assertThrows(LazyInitializationException.class, () -> found[0].getLazyExceptionItems().size());
//        System.err.println("done");
//
//
//    }
}