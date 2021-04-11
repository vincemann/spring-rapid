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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static com.github.vincemann.ezcompare.Comparator.compare;
import static com.github.vincemann.springrapid.coretest.service.ExceptionMatchers.noException;
import static com.github.vincemann.springrapid.coretest.service.ExistenceMatchers.notPresentInDatabase;
import static com.github.vincemann.springrapid.coretest.service.PropertyMatchers.propertyAssert;
import static com.github.vincemann.springrapid.coretest.service.request.CrudServiceRequestBuilders.*;
import static com.github.vincemann.springrapid.coretest.service.resolve.EntityPlaceholder.DB_ENTITY;

//@EnableProjectComponentScan
//@ImportRapidEntityRelServiceConfig
class PetServiceIntegrationTest
        extends MyCrudServiceIntegrationTest<PetService, Pet, Long> {


    @Autowired
    OwnerService ownerService;
    Pet PetType = new Pet();


    @Test
    public void canSavePet_withSavedPetType() throws BadEntityException {
        test(save(bello))
                .andExpect(() -> compare(bello)
                        .with(resolve(EntityPlaceholder.DB_ENTITY))
                        .properties()
                        .all()
                        .ignore("id")
                        .assertEqual());
    }

    @Test
    public void canSavePet_toSavedOwner() throws BadEntityException {
        Owner savedKahn = ownerService.save(kahn);
        bello.setOwner(savedKahn);

        test(save(bello))
                .andExpect(() -> compare(bello)
                        .with(resolve(EntityPlaceholder.DB_ENTITY))
                        .properties()
                        .all()
                        .ignore("id")
                        .assertEqual());


        // check if bidir relation ships were managed
        Pet dbBello = petRepository.findByName(BELLO).get();
        Owner dbKahn = ownerRepository.findByLastName(KAHN).get();
        Assertions.assertEquals(dbKahn,dbBello.getOwner());
        Assertions.assertEquals(dbBello,dbKahn.getPets().stream().filter(p -> p.getName().equals(BELLO)).findFirst().get());
    }

    @Test
    public void canDeletePet_fromOwner() throws BadEntityException {
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
    }

}