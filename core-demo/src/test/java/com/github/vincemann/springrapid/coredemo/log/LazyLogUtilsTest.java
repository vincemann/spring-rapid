package com.github.vincemann.springrapid.coredemo.log;

import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.slicing.RapidProfiles;
import com.github.vincemann.springrapid.core.util.LazyLogUtils;
import com.github.vincemann.springrapid.coredemo.model.*;
import com.github.vincemann.springrapid.coretest.slicing.RapidTestProfiles;
import com.github.vincemann.springrapid.coretest.util.TransactionalRapidTestUtil;
import com.google.common.collect.Sets;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;

@ActiveProfiles(value = {RapidTestProfiles.TEST, RapidTestProfiles.SERVICE_TEST, RapidProfiles.SERVICE})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class LazyLogUtilsTest {

//    @Autowired
//    LazyItemService lazyItemService;


//    @Autowired
//    OwnerRepository ownerRepository;

//    @Autowired
//    TransactionalTestTemplate transactionalTestTemplate;


    @Autowired
    LazyLoadedItemRepository loadedItemRepository;

    @Autowired
    LazyExceptionItemRepository loadedExceptionItemRepository;



    LogEntity logEntity;



    @BeforeEach
    void setUp() {
        logEntity = LogEntity.builder()
                .firstName("Olli")
                .lastName("Kahn")
                .address("asljnflksamfslkmf")
                .city("n1 city")
                .telephone("1234567890")
                .build();

        bello = Pet.builder()
                .name("bello")
                .birthDate(LocalDate.now())
                .build();

        clinicCard = ClinicCard.builder()
                .registrationDate(new Date())
                .registrationReason("gilligkeit")
                .build();
    }

    @Test
    void canIgnoreLazy() throws BadEntityException {
        LogParent lazyItem = new LogParent();
//        LazyItem savedLazyItem = getService().save(lazyItem);

        logEntity.getLogChildren1().add(lazyItem);

        Owner savedKahn = ownerService.save(logEntity);


        Owner found = ownerService.findById(savedKahn.getId()).get();
        // would result in lazyinit exception
//        found.getLazyItems().size();

        String s = LazyLogUtils.toString(found,Boolean.FALSE);
        System.err.println(s);

        Assertions.assertTrue(s.contains("LazyInitializationException"));
    }

    @Test
    void canLoadEagerCollection_andIgnoreLazyCollection() throws BadEntityException {
        LogParent lazyItem = new LogParent();

        logEntity.getLogChildren1().add(lazyItem);
        logEntity.getPets().add(bello);

        Owner savedKahn = ownerService.save(logEntity);


        Owner found = ownerService.findById(savedKahn.getId()).get();
        // would result in lazyinit exception
//        found.getLazyItems().size();

        String s = LazyLogUtils.toString(found,Boolean.FALSE);
        System.err.println(s);

        Assertions.assertTrue(s.contains("LazyInitializationException"));
        Assertions.assertTrue(s.contains("bello"));
    }

    @Test
    void canShowLoadedLazyCollection_andIgnoreNotLoadedLazyCollectionsException() throws BadEntityException {
        LogParent lazyItem = new LogParent();
        LogChild logChild = new LogChild("loaded");

        logEntity.getLogChildren1().add(lazyItem);
        logEntity.getLogChildren2().add(logChild);

        Owner savedKahn = ownerService.save(logEntity);


        Owner found = ownerService.lazyLoadFindById(savedKahn.getId());
        // would result in lazyinit exception
//        found.getLazyItems().size();

        String s = LazyLogUtils.toString(found, Boolean.FALSE);
        System.err.println(s);

        Assertions.assertTrue(s.contains("LazyInitializationException"));
        Assertions.assertTrue(s.contains("loaded"));
    }

    @Test
    void canIgnoreCollections() throws BadEntityException {
        LogParent lazyItem = new LogParent();
        LogChild logChild = new LogChild("loaded");

        logEntity.getLogChildren1().add(lazyItem);
        logEntity.getLogChildren2().add(logChild);
        logEntity.getPets().add(bello);

        Owner savedKahn = ownerService.save(logEntity);


        Owner found = ownerService.lazyLoadFindById(savedKahn.getId());
        // would result in lazyinit exception
//        found.getLazyItems().size();

        String s = LazyLogUtils.toString(found);
        System.err.println(s);

        Assertions.assertFalse(s.contains("LazyInitializationException"));
        Assertions.assertFalse(s.contains("loaded"));
        Assertions.assertFalse(s.contains("bello"));
        Assertions.assertTrue(s.contains(logEntity.getFirstName()));
        Assertions.assertTrue(s.contains(logEntity.getLastName()));
        Assertions.assertTrue(s.contains(logEntity.getCity()));
    }

    @Transactional
    @Test
    void canIgnoreSomeEntitiesAndCollections() throws BadEntityException {
        logEntity.setClinicCard(clinicCardRepository.save(clinicCard));
        logEntity.getPets().add(bello);

        Owner savedKahn = ownerService.save(logEntity);

        String s = LazyLogUtils.toString(savedKahn,Boolean.TRUE, Sets.newHashSet("clinicCard"));
        System.err.println(s);

        Assertions.assertFalse(s.contains("bello"));
        Assertions.assertTrue(s.contains(clinicCard.getRegistrationReason()));

        Assertions.assertTrue(s.contains(logEntity.getFirstName()));
        Assertions.assertTrue(s.contains(logEntity.getLastName()));
        Assertions.assertTrue(s.contains(logEntity.getCity()));
    }


    @Test
    void canThrowLazy() throws BadEntityException {
        LogParent lazyItem = new LogParent();
//        LazyItem savedLazyItem = getService().save(lazyItem);

        logEntity.getLogChildren1().add(lazyItem);

        Owner savedKahn = ownerService.save(logEntity);


        Owner found = ownerService.findById(savedKahn.getId()).get();

        Assertions.assertThrows(LazyInitializationException.class,
                () -> LazyLogUtils.toString(found,new HashSet<>(), Boolean.FALSE,Boolean.FALSE,Boolean.FALSE));
    }

    @AfterEach
    void tearDown() {
        TransactionalRapidTestUtil.clear(ownerService);
        clinicCardRepository.deleteAll();
        petRepository.deleteAll();
        loadedItemRepository.deleteAll();
        loadedExceptionItemRepository.deleteAll();

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