package com.github.vincemann.springrapid.coredemo.util;

import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.slicing.RapidProfiles;
import com.github.vincemann.springrapid.core.util.LazyLogUtils;
import com.github.vincemann.springrapid.coredemo.model.LazyExceptionItem;
import com.github.vincemann.springrapid.coredemo.model.LazyLoadedItem;
import com.github.vincemann.springrapid.coredemo.model.Owner;
import com.github.vincemann.springrapid.coredemo.model.Pet;
import com.github.vincemann.springrapid.coredemo.repo.OwnerRepository;
import com.github.vincemann.springrapid.coredemo.repo.PetTypeRepository;
import com.github.vincemann.springrapid.coredemo.service.OwnerService;
import com.github.vincemann.springrapid.coredemo.service.Root;
import com.github.vincemann.springrapid.coretest.slicing.RapidTestProfiles;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@ActiveProfiles(value = {RapidTestProfiles.TEST, RapidTestProfiles.SERVICE_TEST, RapidProfiles.SERVICE})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class LazyLogUtilsTest {

//    @Autowired
//    LazyItemService lazyItemService;


//    @Autowired
//    OwnerRepository ownerRepository;

    @Autowired
    @Root
    OwnerService ownerService;


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
//        LazyItem savedLazyItem = getService().save(lazyItem);

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
    void canShowLoadedLazyCollection_andIgnoreNotLoadedLazyCollection() throws BadEntityException {
        LazyExceptionItem lazyItem = new LazyExceptionItem();
        LazyLoadedItem lazyLoadedItem = new LazyLoadedItem();
//        LazyItem savedLazyItem = getService().save(lazyItem);

        kahn.getLazyExceptionItems().add(lazyItem);
        kahn.getLazyLoadedItems().add(lazyLoadedItem);

        Owner savedKahn = ownerService.save(kahn);


        Owner found = ownerService.lazyLoadFind(savedKahn.getId());
        // would result in lazyinit exception
//        found.getLazyItems().size();

        String s = LazyLogUtils.toString(found,Boolean.FALSE);
        System.err.println(s);

        Assertions.assertTrue(s.contains("LazyInitializationException"));
        Assertions.assertTrue(s.contains("LazyLoadedItem"));
    }

    @Test
    void canThrowLazy() throws BadEntityException {
        LazyExceptionItem lazyItem = new LazyExceptionItem();
//        LazyItem savedLazyItem = getService().save(lazyItem);

        kahn.getLazyExceptionItems().add(lazyItem);

        Owner savedKahn = ownerService.save(kahn);

        Set<LazyExceptionItem> lazyItems = savedKahn.getLazyExceptionItems();
        lazyItems.size();


        Owner found = ownerService.findById(savedKahn.getId()).get();

        Assertions.assertThrows(LazyInitializationException.class,
                () -> LazyLogUtils.toString(found,Boolean.FALSE, Boolean.FALSE));
    }
}