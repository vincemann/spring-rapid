package com.github.vincemann.springrapid.coredemo.util;

import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.slicing.RapidProfiles;
import com.github.vincemann.springrapid.core.util.LazyLogUtils;
import com.github.vincemann.springrapid.coredemo.model.LazyExceptionItem;
import com.github.vincemann.springrapid.coredemo.model.LazyLoadedItem;
import com.github.vincemann.springrapid.coredemo.model.Owner;
import com.github.vincemann.springrapid.coredemo.model.Pet;
import com.github.vincemann.springrapid.coredemo.repo.LazyExceptionItemRepository;
import com.github.vincemann.springrapid.coredemo.repo.LazyLoadedItemRepository;
import com.github.vincemann.springrapid.coredemo.service.OwnerService;
import com.github.vincemann.springrapid.coredemo.service.Root;
import com.github.vincemann.springrapid.coretest.controller.TransactionalTestTemplate;
import com.github.vincemann.springrapid.coretest.slicing.RapidTestProfiles;
import lombok.SneakyThrows;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

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
    @Root
    OwnerService ownerService;

    @Autowired
    LazyLoadedItemRepository loadedItemRepository;
    @Autowired
    LazyExceptionItemRepository loadedExceptionItemRepository;


    Owner kahn;

    Pet bello;

    @BeforeEach
    void setUp() {
        kahn = Owner.builder()
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
    }

    @Test
    void canIgnoreLazy() throws BadEntityException {
        LazyExceptionItem lazyItem = new LazyExceptionItem();
//        LazyItem savedLazyItem = getService().save(lazyItem);

        kahn.getLazyExceptionItems().add(lazyItem);

        Owner savedKahn = ownerService.save(kahn);


        Owner found = ownerService.findById(savedKahn.getId()).get();
        // would result in lazyinit exception
//        found.getLazyItems().size();

        String s = LazyLogUtils.toString(found,Boolean.FALSE);
        System.err.println(s);

        Assertions.assertTrue(s.contains("LazyInitializationException"));
    }

    @Test
    void canLoadEagerCollection_andIgnoreLazyCollection() throws BadEntityException {
        LazyExceptionItem lazyItem = new LazyExceptionItem();

        kahn.getLazyExceptionItems().add(lazyItem);
        kahn.getPets().add(bello);

        Owner savedKahn = ownerService.save(kahn);


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
        LazyExceptionItem lazyItem = new LazyExceptionItem();
        LazyLoadedItem lazyLoadedItem = new LazyLoadedItem("loaded");

        kahn.getLazyExceptionItems().add(lazyItem);
        kahn.getLazyLoadedItems().add(lazyLoadedItem);

        Owner savedKahn = ownerService.save(kahn);


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
        LazyExceptionItem lazyItem = new LazyExceptionItem();
        LazyLoadedItem lazyLoadedItem = new LazyLoadedItem("loaded");

        kahn.getLazyExceptionItems().add(lazyItem);
        kahn.getLazyLoadedItems().add(lazyLoadedItem);
        kahn.getPets().add(bello);

        Owner savedKahn = ownerService.save(kahn);


        Owner found = ownerService.lazyLoadFindById(savedKahn.getId());
        // would result in lazyinit exception
//        found.getLazyItems().size();

        String s = LazyLogUtils.toString(found);
        System.err.println(s);

        Assertions.assertFalse(s.contains("LazyInitializationException"));
        Assertions.assertFalse(s.contains("loaded"));
        Assertions.assertFalse(s.contains("bello"));
        Assertions.assertTrue(s.contains(kahn.getFirstName()));
        Assertions.assertTrue(s.contains(kahn.getLastName()));
        Assertions.assertTrue(s.contains(kahn.getCity()));
    }


    @Test
    void canThrowLazy() throws BadEntityException {
        LazyExceptionItem lazyItem = new LazyExceptionItem();
//        LazyItem savedLazyItem = getService().save(lazyItem);

        kahn.getLazyExceptionItems().add(lazyItem);

        Owner savedKahn = ownerService.save(kahn);


        Owner found = ownerService.findById(savedKahn.getId()).get();

        Assertions.assertThrows(LazyInitializationException.class,
                () -> LazyLogUtils.toString(found, Boolean.FALSE,Boolean.FALSE,Boolean.FALSE));
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