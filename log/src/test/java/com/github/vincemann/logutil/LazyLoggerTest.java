package com.github.vincemann.logutil;

import com.github.vincemann.logutil.model.LogChild;
import com.github.vincemann.logutil.model.LogEntity;
import com.github.vincemann.logutil.model.LogParent;
import com.github.vincemann.logutil.model.LazySingleLogChild;
import com.github.vincemann.logutil.repo.LogChildRepository;
import com.github.vincemann.logutil.repo.LogEntityRepository;
import com.github.vincemann.logutil.repo.LogParentRepository;
import com.github.vincemann.logutil.repo.LazySingleLogChildRepository;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.slicing.RapidProfiles;
import com.github.vincemann.springrapid.core.util.LazyLogger;
import com.github.vincemann.springrapid.core.util.TransactionalTemplate;
import com.github.vincemann.springrapid.coretest.slicing.RapidTestProfiles;
import com.google.common.collect.Sets;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

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
    static final String LAZY_PARENT_NAME = "lazy parent col Entity2";
    static final String LAZY_CHILD_NAME = "lazy child";
    static final String EAGER_CHILD_NAME = "eager child";
    static final String LAZY_COL2_ENTITY1_NAME = "lazy Col2 Entity1";
    static final String LAZY_COL2_ENTITY2_NAME = "lazy Col2 Entity2";
    @Autowired
    TransactionalTemplate transactionalTemplate;
    @Autowired
    LogChildRepository logChildRepository;

    @Autowired
    LogParentRepository logParentRepository;

    @Autowired
    LazySingleLogChildRepository lazySingleLogChildRepository;

    @Autowired
    LogEntityRepository logEntityRepository;


    LogEntity logEntity;
    LogChild lazyCol1_child1;
    LogChild lazyCol1_child2;
    LogChild lazyCol2_child1;
    LogChild lazyCol2_child2;
    LogParent lazyParent;
    LazySingleLogChild lazySingleChild;
    LazySingleLogChild eagerSingleChild;

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


        lazyCol2_child1 = LogChild.builder()
                .name(LAZY_COL2_ENTITY1_NAME)
                .build();
        lazyCol2_child2 = LogChild.builder()
                .name(LAZY_COL2_ENTITY2_NAME)
                .build();

        lazySingleChild = new LazySingleLogChild(LAZY_CHILD_NAME);
        eagerSingleChild = new LazySingleLogChild(EAGER_CHILD_NAME);
    }

    @Test
    void canIgnoreLazyInitException() throws BadEntityException {
        lazyLogger = LazyLogger.builder()
                .ignoreLazyException(Boolean.TRUE)
                .ignoreEntities(Boolean.FALSE)
                .onlyLogLoaded(Boolean.FALSE)
                .build();

        final LogEntity[] saved = {null};
        transactionalTemplate.doInTransaction(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                LogChild savedLazyCol1_child1 = logChildRepository.save(lazyCol1_child1);
                logEntity.setLazyChildren1(Sets.newHashSet(savedLazyCol1_child1));
                savedLazyCol1_child1.setLogEntity(logEntity);

                LazySingleLogChild savedEagerSingleChild = lazySingleLogChildRepository.save(eagerSingleChild);
                logEntity.setEagerChild(savedEagerSingleChild);
                savedEagerSingleChild.setLogEntity(logEntity);


                saved[0] = logEntityRepository.save(logEntity);
            }
        });


        String s = lazyLogger.toString(saved[0]);

        System.err.println(s);

        Assertions.assertTrue(s.contains("LazyInitializationException"));
        Assertions.assertTrue(s.contains(EAGER_CHILD_NAME));
        Assertions.assertTrue(s.contains(LOG_ENTITY_NAME));

        Assertions.assertFalse(s.contains(LAZY_COL1_ENTITY1_NAME));
    }

//    @Test
//    void canLoadEagerCollection_andIgnoreLazyCollection() throws BadEntityException {
//        LogParent lazyItem = new LogParent();
//
//        logEntity.getLazyChildren1().add(lazyItem);
//        logEntity.getPets().add(bello);
//
//        Owner savedKahn = ownerService.save(logEntity);
//
//
//        Owner found = ownerService.findById(savedKahn.getId()).get();
//        // would result in lazyinit exception
////        found.getLazyItems().size();
//
//        String s = LazyLogUtils.toString(found,Boolean.FALSE);
//        System.err.println(s);
//
//        Assertions.assertTrue(s.contains("LazyInitializationException"));
//        Assertions.assertTrue(s.contains("bello"));
//    }
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
//        logEntity.setClinicCard(clinicCardRepository.save(clinicCard));
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
//        TransactionalRapidTestUtil.clear(ownerService);
//        clinicCardRepository.deleteAll();
//        petRepository.deleteAll();
        logChildRepository.deleteAll();
        logParentRepository.deleteAll();
        lazySingleLogChildRepository.deleteAll();

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