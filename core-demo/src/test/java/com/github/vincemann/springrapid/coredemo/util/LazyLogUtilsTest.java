package com.github.vincemann.springrapid.coredemo.util;

import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.util.LazyLogUtils;
import com.github.vincemann.springrapid.coredemo.controller.AbstractControllerIntegrationTest;
import com.github.vincemann.springrapid.coredemo.controller.LazyItemController;
import com.github.vincemann.springrapid.coredemo.model.LazyItem;
import com.github.vincemann.springrapid.coredemo.model.Owner;
import com.github.vincemann.springrapid.coredemo.repo.OwnerRepository;
import com.github.vincemann.springrapid.coredemo.service.LazyItemService;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

class LazyLogUtilsTest extends AbstractControllerIntegrationTest<LazyItemController,LazyItemService> {

//    @Autowired
//    LazyItemService lazyItemService;


    @Autowired
    OwnerRepository ownerRepository;

    Owner kahn;

    @BeforeEach
    void setUp() {
        kahn = Owner.builder()
                .firstName("Olli")
                .lastName("Kahn")
                .address("asljnflksamfslkmf")
                .city("n1 city")
                .telephone("1234567890")
                .lazyItems(new HashSet<>())
                .build();
    }

    @Test
    void testToString_ignoreLazy() throws BadEntityException {
        LazyItem lazyItem = new LazyItem();
//        LazyItem savedLazyItem = getService().save(lazyItem);

        kahn.getLazyItems().add(lazyItem);

        Owner savedKahn = ownerRepository.save(kahn);

        Set<LazyItem> lazyItems = savedKahn.getLazyItems();
        lazyItems.size();


        Owner found = ownerRepository.findById(savedKahn.getId()).get();
        found.getLazyItems().size();

        String s = LazyLogUtils.toString(found,Boolean.FALSE);
        System.err.println(s);

        Assertions.assertTrue(s.contains("LazyInitializationException"));
    }

    @Test
    void testToString_dontIgnoreLazy() throws BadEntityException {
        LazyItem lazyItem = new LazyItem();
//        LazyItem savedLazyItem = getService().save(lazyItem);

        kahn.getLazyItems().add(lazyItem);

        Owner savedKahn = ownerRepository.save(kahn);

        Set<LazyItem> lazyItems = savedKahn.getLazyItems();
        LazyItem i1 = lazyItems.stream().findFirst().get();


        Owner found = ownerRepository.findById(savedKahn.getId()).get();

        Assertions.assertThrows(LazyInitializationException.class,
                () -> LazyLogUtils.toString(found,Boolean.FALSE, Boolean.FALSE));
    }
}