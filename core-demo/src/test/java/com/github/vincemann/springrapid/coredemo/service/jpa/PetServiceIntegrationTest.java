package com.github.vincemann.springrapid.coredemo.service.jpa;

import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.coredemo.model.Owner;
import com.github.vincemann.springrapid.coredemo.model.Pet;
import com.github.vincemann.springrapid.coredemo.model.PetType;
import com.github.vincemann.springrapid.coredemo.service.OwnerService;
import com.github.vincemann.springrapid.coredemo.service.PetService;
import com.github.vincemann.springrapid.coredemo.service.PetTypeService;
import com.github.vincemann.springrapid.coretest.service.AbstractCrudServiceIntegrationTest;
import com.github.vincemann.springrapid.coretest.service.resolve.EntityPlaceholder;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;

import static com.github.vincemann.ezcompare.Comparator.compare;
import static com.github.vincemann.springrapid.coretest.service.ExceptionMatchers.noException;
import static com.github.vincemann.springrapid.coretest.service.ExistenceMatchers.notPresentInDatabase;
import static com.github.vincemann.springrapid.coretest.service.PropertyMatchers.propertyAssert;
import static com.github.vincemann.springrapid.coretest.service.request.CrudServiceRequestBuilders.*;
import static com.github.vincemann.springrapid.coretest.service.resolve.EntityPlaceholder.DB_ENTITY;


class PetServiceIntegrationTest
        extends MyCrudServiceIntegrationTest<PetService, Pet, Long> {


    @Autowired
    OwnerService ownerService;
    Pet PetType = new Pet();


    @Test
    public void canSavePet() throws BadEntityException {
        test(save(bello))
                .andExpect(() -> compare(bello)
                        .with(resolve(EntityPlaceholder.DB_ENTITY))
                        .properties()
                        .all()
                        .ignore("id")
                        .assertEqual());
    }

    @Test
    public void canLinkPetToOwner_viaSave() throws BadEntityException {
        Owner savedKahn = ownerService.save(kahn);
        bello.setOwner(savedKahn);

        test(save(bello));

        // check if bidir relation ships were managed
        Pet dbBello = petRepository.findByName(BELLO).get();
        Owner dbKahn = ownerRepository.findByLastName(KAHN).get();
        Assertions.assertEquals(dbKahn,dbBello.getOwner());
        Assertions.assertEquals(dbBello,dbKahn.getPets().stream().filter(p -> p.getName().equals(BELLO)).findFirst().get());
    }

    @Test
    public void canDeletePet_thusGetUnlinkedFromOwner() throws BadEntityException {
        Owner savedKahn = ownerService.save(kahn);
        bello.setOwner(savedKahn);
        Pet savedBello = getServiceUnderTest().save(bello);

        test(deleteById(savedBello.getId()))
                .andExpect(noException())
                .andExpect(notPresentInDatabase(savedBello.getId()));


        // check if bidir relation ships were managed
        Owner dbKahn = ownerRepository.findByLastName(KAHN).get();
        Assertions.assertTrue(dbKahn.getPets().isEmpty());
    }

    @Test
    public void canUpdatePetsOwner() throws BadEntityException {
        Owner savedKahn = ownerService.save(kahn);
        Owner savedMeier = ownerService.save(meier);

        bello.setOwner(savedKahn);
        Pet savedBello = getServiceUnderTest().save(bello);

        Pet ownerUpdate = Pet.builder()
                .owner(savedMeier)
                .build();
        ownerUpdate.setId(savedBello.getId());

        test(partialUpdate(ownerUpdate))
                .andExpect(() ->
                        propertyAssert(resolve(DB_ENTITY))
                                .assertMatch(PetType::getOwner, savedMeier)
                );



        // check if bidir relation ships were managed
        Owner dbKahn = ownerRepository.findByLastName(KAHN).get();
        Assertions.assertTrue(dbKahn.getPets().isEmpty());
        Owner dbMeier = ownerRepository.findByLastName(MEIER).get();
        Assertions.assertEquals(savedBello,dbMeier.getPets().stream().findFirst().get());

        Pet dbBello = petRepository.findByName(BELLO).get();
        Assertions.assertEquals(dbMeier,dbBello.getOwner());
    }

    @Test
    public void canUnlinkOwnerFromPet_viaFullUpdate() throws BadEntityException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Owner savedKahn = ownerService.save(kahn);

        bello.setOwner(savedKahn);
        Pet savedBello = getServiceUnderTest().save(bello);

        Pet unlinkOwnerUpdate = (Pet) BeanUtilsBean.getInstance().cloneBean(savedBello);
        unlinkOwnerUpdate.setOwner(null);

        test(update(unlinkOwnerUpdate));


        // check if bidir relation ships were managed
        Owner dbKahn = ownerRepository.findByLastName(KAHN).get();
        Assertions.assertTrue(dbKahn.getPets().isEmpty());

        Pet dbBello = petRepository.findByName(BELLO).get();
        Assertions.assertNull(dbBello.getOwner());

    }

    @Test
    public void canLinkOwnerToPet_viaPartialUpdate() throws BadEntityException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Owner savedKahn = ownerService.save(kahn);
        Pet savedBello = getServiceUnderTest().save(bello);

        Pet linkOwnerUpdate = Pet.builder()
                .owner(savedKahn)
                .build();
        linkOwnerUpdate.setId(savedBello.getId());

        test(partialUpdate(linkOwnerUpdate));


        // check if bidir relation ships were managed
        Owner dbKahn = ownerRepository.findByLastName(KAHN).get();
        Assertions.assertEquals(savedBello,dbKahn.getPets().stream().findFirst().get());

        Pet dbBello = petRepository.findByName(BELLO).get();
        Assertions.assertEquals(dbKahn,dbBello.getOwner());

    }

    // todo have to use partial update mode for this case bc i cant get full update to work with bidir relship mangement yet
    @Test
    public void canLinkOwnerToPet_viaFullUpdate() throws BadEntityException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Owner savedKahn = ownerService.save(kahn);
        Pet savedBello = getServiceUnderTest().save(bello);

        Pet updateOwner = (Pet) BeanUtilsBean.getInstance().cloneBean(savedBello);
        updateOwner.setOwner(savedKahn);

        test(update(updateOwner));


        // check if bidir relation ships were managed
        Owner dbKahn = ownerRepository.findByLastName(KAHN).get();
        Pet dbBello = petRepository.findByName(BELLO).get();

        Assertions.assertEquals(1,dbKahn.getPets().size());
        Assertions.assertEquals(dbBello,dbKahn.getPets().stream().findFirst().get());

        Assertions.assertEquals(dbKahn,dbBello.getOwner());

    }

}