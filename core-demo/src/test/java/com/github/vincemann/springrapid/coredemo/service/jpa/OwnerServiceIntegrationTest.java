package com.github.vincemann.springrapid.coredemo.service.jpa;

import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.Lists;
import com.github.vincemann.springrapid.coredemo.model.Owner;
import com.github.vincemann.springrapid.coredemo.model.Pet;
import com.github.vincemann.springrapid.coredemo.model.PetType;
import com.github.vincemann.springrapid.coredemo.service.OwnerService;
import com.github.vincemann.springrapid.coredemo.service.PetService;
import com.github.vincemann.springrapid.coredemo.service.PetTypeService;
import com.github.vincemann.springrapid.coredemo.service.plugin.OwnerOfTheYearExtension;
import com.github.vincemann.springrapid.coretest.service.AbstractCrudServiceIntegrationTest;
import com.github.vincemann.springrapid.coretest.service.result.ServiceResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

import static com.github.vincemann.ezcompare.Comparator.compare;
import static com.github.vincemann.springrapid.coretest.service.ExceptionMatchers.noException;
import static com.github.vincemann.springrapid.coretest.service.ExistenceMatchers.notPresentInDatabase;
import static com.github.vincemann.springrapid.coretest.service.PropertyMatchers.propertyAssert;
import static com.github.vincemann.springrapid.coretest.service.request.CrudServiceRequestBuilders.*;
import static com.github.vincemann.springrapid.coretest.service.resolve.EntityPlaceholder.*;


//@EnableProjectComponentScan
//@ImportRapidEntityRelServiceConfig
class OwnerServiceIntegrationTest
        extends AbstractCrudServiceIntegrationTest<OwnerService, Owner, Long> {
    //Types
    Owner OwnerType = new Owner();

    Owner ownerWithoutPets;
    Owner ownerWithOnePet;
    Pet testPet;
    PetType savedDogPetType;

    @SpyBean
    OwnerOfTheYearExtension ownerOfTheYearExtension;

    @Autowired
    PetService petService;

    @Autowired
    PetTypeService petTypeService;

    @BeforeEach
    public void setupTestData() throws Exception {
//        super.setup();
        savedDogPetType = petTypeService.save(new PetType("Dog"));

        testPet = Pet.builder()
                .petType(savedDogPetType)
                .name("Bello")
                .birthDate(LocalDate.now())
                .build();

        ownerWithoutPets = Owner.builder()
                .firstName("owner without pets")
                .lastName("owner without pets lastName")
                .address("asljnflksamfslkmf")
                .city("n1 city")
                .telephone("12843723847324")
                .build();

        ownerWithOnePet = Owner.builder()
                .firstName("owner with one pet")
                .lastName("owner with one pet lastName")
                .address("asljnflksamfslkmf")
                .city("n1 city")
                .telephone("12843723847324")
                .pets(new HashSet<>(Lists.newArrayList(testPet)))
                .build();
    }

    @Test
    public void canSaveOwnerWithoutPets() {
        ServiceResult serviceResult =
                test(save(ownerWithoutPets))
                        .andExpect(() -> compare(ownerWithoutPets)
                                .with(resolve(DB_ENTITY))
                                .properties()
                                //per instance or Type is both possible Owner::getAnything wont work
                                .include(ownerWithoutPets::getTelephone)
                                .include(OwnerType::getAddress)
                                .assertEqual())
                        .andExpect(()-> propertyAssert(resolve(SERVICE_RETURNED_ENTITY))
                                .shouldBeEmpty(OwnerType::getPets)
                        )
                        .andReturn();
        Assertions.assertEquals(0, ((Owner) serviceResult.getResult()).getPets().size());
    }



    @Test
    public void canSaveOwnerWithPet() throws BadEntityException {
        test(save(ownerWithOnePet))
                .andDo(() -> compare(resolve(SERVICE_INPUT_ENTITY))
                        .with(resolve(DB_ENTITY))
                        .properties()
                        .all()
                        //ignoring/adding properties via their name as a string is also possible but not recommended
                        .ignore("id")
                        .assertEqual());
        Assertions.assertTrue(getRepository().existsById(ownerWithOnePet.getId()));
    }

    @Test
    public void givenAlreadyPersistedPet_canLinkOwner_viaSave() throws BadEntityException {
        Pet savedPet = petService.save(testPet);

        Owner owner = Owner.builder()
                .firstName("owner with one already persisted pet")
                .lastName("owner with one already persisted pet lastName")
                .address("asljnflksamfslkmf")
                .city("n1 city")
                .telephone("12843723847324")
                .pets(new HashSet<>(Lists.newArrayList(savedPet)))
                .build();

        test(save(owner))
                .andExpect(() -> compare(owner)
                        .with(resolve(SERVICE_RETURNED_ENTITY))
                        .properties()
                        .all()
                        .ignore(OwnerType::getId)
                        .assertEqual()
                );
    }


    @Test
    public void canUpdateOwnersTelephoneNumber() throws BadEntityException, EntityNotFoundException, BadEntityException {
        String newNumber = ownerWithoutPets.getTelephone() + "123";
        Owner diffTelephoneNumberUpdate = Owner.builder()
                .telephone(newNumber)
                .build();
        Owner toUpdate = getRepository().save(ownerWithoutPets);
        diffTelephoneNumberUpdate.setId(toUpdate.getId());

        test(partialUpdate(diffTelephoneNumberUpdate))
                .andExpect(() ->
                        propertyAssert(resolve(DB_ENTITY))
                                .shouldMatch(OwnerType::getTelephone, newNumber)
                );
    }

    @Test
    public void canAddAnotherPetToOwner_viaUpdate() throws BadEntityException, EntityNotFoundException, BadEntityException {
        //given
        Pet savedPet = petService.save(testPet);
        String newPetName = "petToAdd";
        Pet newPet = Pet.builder()
                .name(newPetName)
                .petType(savedDogPetType)
                .birthDate(LocalDate.now())
                .build();
        Pet savedPetToAdd = petService.save(newPet);

        Owner owner = Owner.builder()
                .firstName("owner with one already persisted pet")
                .lastName("owner with one already persisted pet lastName")
                .address("asljnflksamfslkmf")
                .city("n1 city")
                .telephone("12843723847324")
                .pets(new HashSet<>(Lists.newArrayList(savedPet)))
                .build();

        Owner ownerUpdateRequest = new Owner();
        ownerUpdateRequest.getPets().addAll(owner.getPets());
        //here comes the new pet
        ownerUpdateRequest.getPets().add(savedPetToAdd);

        //when
        Owner saved = getRepository().save(owner);
        ownerUpdateRequest.setId(saved.getId());

        test(partialUpdate(ownerUpdateRequest))
                .andExpect(() -> propertyAssert(resolve(DB_ENTITY))
                        .shouldMatchSize(OwnerType::getPets, 2)
                );
    }

    @Test
    public void canFindOwnerOfTheYear_triggersExtension() {
        //owner of the years name is 42
        ownerWithOnePet.setFirstName("42");
        Owner savedOwner = getRepository().save(ownerWithOnePet);
        Optional<Owner> ownerOfTheYear = getServiceUnderTest().findOwnerOfTheYear();
        Assertions.assertTrue(ownerOfTheYear.isPresent());
        Mockito.verify(ownerOfTheYearExtension).findOwnerOfTheYear();
    }

    @Test
    public void canFindOwnerByLastName() {
        Owner savedOwner = getRepository().save(ownerWithOnePet);
        Optional<Owner> byLastName = getServiceUnderTest().findByLastName(ownerWithOnePet.getLastName());
        Assertions.assertTrue(byLastName.isPresent());
        compare(savedOwner)
                .with(byLastName.get())
                .properties()
                .all()
                .assertEqual();
    }

    @Test
    public void canDeleteOwner() {
        Owner savedOwner = getRepository().save(ownerWithOnePet);
        test(deleteById(savedOwner.getId()))
                .andExpect(noException())
                .andExpect(notPresentInDatabase(savedOwner.getId()));
    }


}