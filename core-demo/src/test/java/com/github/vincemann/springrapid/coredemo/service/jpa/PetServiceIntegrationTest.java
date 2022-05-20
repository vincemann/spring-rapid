package com.github.vincemann.springrapid.coredemo.service.jpa;

import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.coredemo.model.Owner;
import com.github.vincemann.springrapid.coredemo.model.Pet;
import com.github.vincemann.springrapid.coredemo.service.OwnerService;
import com.github.vincemann.springrapid.coredemo.service.PetService;
import com.github.vincemann.springrapid.core.util.BeanUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;

import static com.github.vincemann.ezcompare.Comparator.compare;

import static com.github.vincemann.springrapid.coretest.service.ExistenceMatchers.notPresentInDatabase;
import static com.github.vincemann.springrapid.coretest.service.PropertyMatchers.propertyAssert;
import static com.github.vincemann.springrapid.coretest.service.request.CrudServiceRequestBuilders.*;
import static com.github.vincemann.springrapid.coretest.service.resolve.EntityPlaceholder.DB_ENTITY;


/**
 * Test to showcase that auto management of one-many bidir-relationships work for child side
 */
class PetServiceIntegrationTest
        extends OneToManyServiceIntegrationTest<PetService, Pet, Long> {


    @Autowired
    OwnerService ownerService;
    Pet PetType = new Pet();


    @Test
    public void canSavePet() throws Exception {
        test(save(bello))
                .andExpect(() -> compare(bello)
                        .with(resolve(DB_ENTITY))
                        .properties()
                        .all()
                        .ignore(PetType::getId)
                        .assertEqual());
    }

    @Test
    public void canLinkPetToOwner_viaSave() throws Exception {
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
    public void canSavePet_unlinkPetType_viaUpdate() throws Exception, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, EntityNotFoundException {
        Pet savedBello = getTestedService().save(bello);

        Pet update = BeanUtils.clone(savedBello);
        update.setPetType(null);

        test(update(update))
                .andExpect(() -> propertyAssert(resolve(DB_ENTITY))
                .assertEquals(PetType::getPetType,null));
    }

    @Test
    public void canSavePet_unlinkFromPetType_removePetTypes() throws Exception, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, EntityNotFoundException {
        Pet savedBello = getTestedService().save(bello);

        Pet update = BeanUtils.clone(savedBello);
        update.setPetType(null);

        getTestedService().fullUpdate(update);

        petTypeRepository.deleteAll();

        Pet dbBello = petRepository.findByName(BELLO).get();
        Assertions.assertNull(dbBello.getPetType());
    }

    @Test
    public void canDeletePet_thusGetUnlinkedFromOwner() throws Exception {
        Owner savedKahn = ownerService.save(kahn);
        bello.setOwner(savedKahn);
        Pet savedBello = getTestedService().save(bello);

        test(deleteById(savedBello.getId()))
                
                .andExpect(notPresentInDatabase(savedBello.getId()));


        // check if bidir relation ships were managed
        Owner dbKahn = ownerRepository.findByLastName(KAHN).get();
        Assertions.assertTrue(dbKahn.getPets().isEmpty());
    }

    @Test
    public void canUpdatePetsOwner() throws Exception {
        Owner savedKahn = ownerService.save(kahn);
        Owner savedMeier = ownerService.save(meier);

        bello.setOwner(savedKahn);
        Pet savedBello = getTestedService().save(bello);

        Pet ownerUpdate = Pet.builder()
                .owner(savedMeier)
                .build();
        ownerUpdate.setId(savedBello.getId());

        test(partialUpdate(ownerUpdate))
                .andExpect(() ->
                        propertyAssert(resolve(DB_ENTITY))
                                .assertEquals(PetType::getOwner, savedMeier)
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
    public void canUnlinkOwnerFromPet_viaFullUpdate() throws Exception, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Owner savedKahn = ownerService.save(kahn);

        bello.setOwner(savedKahn);
        Pet savedBello = getTestedService().save(bello);

        Pet unlinkOwnerUpdate = BeanUtils.clone(savedBello);
        unlinkOwnerUpdate.setOwner(null);

        test(update(unlinkOwnerUpdate));


        // check if bidir relation ships were managed
        Owner dbKahn = ownerRepository.findByLastName(KAHN).get();
        Assertions.assertTrue(dbKahn.getPets().isEmpty());

        Pet dbBello = petRepository.findByName(BELLO).get();
        Assertions.assertNull(dbBello.getOwner());

    }

    @Test
    public void canLinkOwnerToPet_viaPartialUpdate() throws Exception, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Owner savedKahn = ownerService.save(kahn);
        Pet savedBello = getTestedService().save(bello);

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
    public void canLinkOwnerToPet_viaFullUpdate() throws Exception, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Owner savedKahn = ownerService.save(kahn);
        Pet savedBello = getTestedService().save(bello);

        Pet updatePetsOwner = Pet.builder()
                .name(BELLO)
                .petType(savedBello.getPetType())
                .owner(savedKahn)
                .build();
        updatePetsOwner.setId(savedBello.getId());
//        Pet updatePetsOwner = BeanUtils.clone(savedBello);
//        updatePetsOwner.setOwner(savedKahn);

        test(update(updatePetsOwner));


        // check if bidir relation ships were managed
        Owner dbKahn = ownerRepository.findByLastName(KAHN).get();
        Pet dbBello = petRepository.findByName(BELLO).get();

        Assertions.assertEquals(1,dbKahn.getPets().size());
        Assertions.assertEquals(dbBello,dbKahn.getPets().stream().findFirst().get());

        Assertions.assertEquals(dbKahn,dbBello.getOwner());

    }

}