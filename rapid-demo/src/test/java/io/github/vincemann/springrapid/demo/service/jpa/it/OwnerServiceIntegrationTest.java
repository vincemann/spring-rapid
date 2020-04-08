package io.github.vincemann.springrapid.demo.service.jpa.it;

import com.google.common.collect.Lists;
import io.github.vincemann.springrapid.demo.model.Owner;
import io.github.vincemann.springrapid.demo.model.Pet;
import io.github.vincemann.springrapid.demo.model.PetType;
import io.github.vincemann.springrapid.demo.service.OwnerService;
import io.github.vincemann.springrapid.demo.service.PetService;
import io.github.vincemann.springrapid.demo.service.PetTypeService;
import io.github.vincemann.springrapid.demo.service.plugin.OwnerOfTheYearPlugin;
import io.github.vincemann.springrapid.core.service.exception.BadEntityException;
import io.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import io.github.vincemann.springrapid.core.service.exception.NoIdException;
import io.github.vincemann.springrapid.coretest.compare.FullComparator;
import io.github.vincemann.springrapid.coretest.service.CrudServiceIntegrationTest;
import io.github.vincemann.springrapid.coretest.service.result.ServiceResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

import static io.github.vincemann.springrapid.coretest.service.request.CrudServiceRequestBuilders.*;
import static io.github.vincemann.springrapid.coretest.service.result.matcher.ExceptionMatchers.noException;
import static io.github.vincemann.springrapid.coretest.service.result.matcher.ExistenceMatchers.notPresentInDatabase;
import static io.github.vincemann.springrapid.coretest.service.result.matcher.compare.CompareEntityMatchers.compare;
import static io.github.vincemann.springrapid.coretest.service.result.matcher.compare.CompareEntityMatchers.propertyCompare;
import static io.github.vincemann.springrapid.coretest.service.result.matcher.compare.resolve.CompareEntityPlaceholder.DB_ENTITY;
import static io.github.vincemann.springrapid.coretest.service.result.matcher.compare.resolve.CompareEntityPlaceholder.SERVICE_INPUT_ENTITY;


class OwnerServiceIntegrationTest
        extends CrudServiceIntegrationTest<OwnerService, Owner, Long> {

    @Autowired
    FullComparator<Owner> fullComparator;

    Owner ownerWithoutPets;
    Owner ownerWithOnePet;
    Pet testPet;
    PetType savedDogPetType;

    @SpyBean
    OwnerOfTheYearPlugin ownerOfTheYearPlugin;

    @Autowired
    PetService petService;

    @Autowired
    PetTypeService petTypeService;

    @BeforeEach
    public void setUp() throws Exception {
        super.setup();
        //proxyfy service
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
    public void saveOwnerWithoutPets_ShouldSucceed() {
        ServiceResult entityServiceResult = getTestTemplate()
                .perform(save(ownerWithoutPets))
                .andExpect(compare(ownerWithoutPets)
                        .with(DB_ENTITY)
                        .withServiceReturnedEntity()
                        .partialEqualCheck()
                        .include(ownerWithoutPets::getTelephone)
                        .include(ownerWithoutPets::getAddress)
                        .isEqual())
                .andReturn();
        Assertions.assertEquals(0, ((Owner) entityServiceResult.getResult()).getPets().size());
    }


    @Test
    public void saveOwnerWithPet_ShouldSucceed() throws BadEntityException {
        getTestTemplate()
                .perform(save(ownerWithOnePet))
                .andExpect(compare(SERVICE_INPUT_ENTITY)
                        .withDbEntity()
                        .fullEqualCheck()
                        .ignoreId()
                        .isEqual());
        Assertions.assertTrue(getRepository().existsById(ownerWithOnePet.getId()));
    }

    @Test
    public void saveOwnerWithPersistedPet_shouldSucceed() throws BadEntityException {
        Pet savedPet = petService.save(testPet);

        Owner owner = Owner.builder()
                .firstName("owner with one already persisted pet")
                .lastName("owner with one already persisted pet lastName")
                .address("asljnflksamfslkmf")
                .city("n1 city")
                .telephone("12843723847324")
                .pets(new HashSet<>(Lists.newArrayList(savedPet)))
                .build();

        getTestTemplate()
                .perform(save(owner))
                .andExpect(compare(owner)
                        .withDbEntity()
                        .withServiceReturnedEntity()
                        .fullEqualCheck()
                        .ignoreId()
                        .isEqual()
                );
    }


    @Test
    public void updateOwner_changeTelephoneNumber_shouldSucceed() throws BadEntityException, EntityNotFoundException, NoIdException {
        String newNumber = ownerWithoutPets.getTelephone() + "123";
        Owner diffTelephoneNumberUpdate = Owner.builder()
                .telephone(newNumber)
                .build();
        Owner toUpdate = getRepository().save(ownerWithoutPets);
        diffTelephoneNumberUpdate.setId(toUpdate.getId());

        getTestTemplate()
                .perform(partialUpdate(diffTelephoneNumberUpdate))
                .andExpect(
                        propertyCompare(DB_ENTITY)
                                .shouldMatch(toUpdate::getTelephone, newNumber)
                                .go()
                );
    }

    @Test
    public void updateOwner_addAnotherPet_shouldSucceed() throws BadEntityException, EntityNotFoundException, NoIdException {
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

        getTestTemplate()
                .perform(partialUpdate(ownerUpdateRequest))
                .andExpect(propertyCompare(DB_ENTITY)
                        .shouldMatchSize(ownerUpdateRequest::getPets,2)
                        .go()
                );
    }

    @Test
    public void findOwnerOfTheYear_shouldSucceed_andTriggerPluginCallback() {
        //owner of the years name is 42
        ownerWithOnePet.setFirstName("42");
        Owner savedOwner = getRepository().save(ownerWithOnePet);
        Optional<Owner> ownerOfTheYear = getServiceUnderTest().findOwnerOfTheYear();
        Assertions.assertTrue(ownerOfTheYear.isPresent());
        Mockito.verify(ownerOfTheYearPlugin).onAfterFindOwnerOfTheYear(ownerOfTheYear);
    }

    @Test
    public void findByLastName_shouldSucceed() {
        Owner savedOwner = getRepository().save(ownerWithOnePet);
        Optional<Owner> byLastName = getServiceUnderTest().findByLastName(ownerWithOnePet.getLastName());
        Assertions.assertTrue(byLastName.isPresent());
        Assertions.assertTrue(fullComparator.isEqual(savedOwner, byLastName.get()));
    }

    @Test
    public void deleteOwner_shouldSucceed(){
        Owner savedOwner = getRepository().save(ownerWithOnePet);
        getTestTemplate()
                .perform(deleteById(savedOwner.getId()))
                .andExpect(noException())
                .andExpect(notPresentInDatabase(savedOwner.getId()));
    }


}