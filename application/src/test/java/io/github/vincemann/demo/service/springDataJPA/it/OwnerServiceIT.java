package io.github.vincemann.demo.service.springDataJPA.it;

import io.github.vincemann.demo.model.Owner;
import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.demo.model.PetType;
import io.github.vincemann.demo.service.OwnerService;
import io.github.vincemann.demo.service.PetService;
import io.github.vincemann.demo.service.PetTypeService;
import io.github.vincemann.demo.service.plugin.OwnerOfTheYearPlugin;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.test.equalChecker.ReflectionComparator;
import io.github.vincemann.generic.crud.lib.test.exception.InvalidConfigurationModificationException;
import io.github.vincemann.generic.crud.lib.test.service.CrudServiceIntegrationTest;
import io.github.vincemann.generic.crud.lib.test.service.result.EntityServiceResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static io.github.vincemann.generic.crud.lib.test.service.CopyNonNullValuesEntityMerger.merge;
import static io.github.vincemann.generic.crud.lib.test.service.request.CrudServiceRequestBuilders.*;
import static io.github.vincemann.generic.crud.lib.test.service.result.matcher.compare.ReflectionCompareResultMatchers.deepCompare;
import static io.github.vincemann.generic.crud.lib.test.service.result.matcher.compare.PropertyCompareResultMatchers.compare;

//@DataJpaTest cant be used because i need autowired components from generic-crud-lib, edit just use @Import
@ActiveProfiles(value = {"test", "springdatajpa"})
@Transactional
@DataJpaTest
class OwnerServiceIT
        extends CrudServiceIntegrationTest<OwnerService,Owner, Long> {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    ReflectionComparator<Owner> reflectionComparator;

    private Owner ownerWithoutPets;
    private Owner ownerWithOnePet;
    private Pet testPet;
    private PetType savedDogPetType;

    @SpyBean
    private OwnerOfTheYearPlugin ownerOfTheYearPlugin;

    @Autowired
    private PetService petService;
    @Autowired
    private PetTypeService petTypeService;

    @BeforeEach
    public void setUp() throws Exception {
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
                .pets(new HashSet<>(Arrays.asList(testPet)))
                .build();
    }

    @Test
    public void saveOwnerWithoutPets_ShouldSucceed() {
        EntityServiceResult entityServiceResult = getTestTemplate().perform(save(ownerWithoutPets))
                .andExpect(compare(ownerWithoutPets).withReturnedAndDbEntity()
                        .property(ownerWithoutPets::getTelephone)
                        .property(ownerWithoutPets::getAddress)
                        .isEqual())
                .andReturn();
        Assertions.assertEquals(0,((Owner) entityServiceResult.getResult()).getPets().size());
    }


    @Test
    public void saveOwnerWithPet_ShouldSucceed() throws BadEntityException {
        getTestTemplate().perform(save(ownerWithOnePet))
                .andExpect(deepCompare(ownerWithOnePet).withReturnedEntity().isEqual());
    }

    @Test
    public void saveOwnerWithPersistedPet_ShouldSucceed() throws BadEntityException {
        Pet savedPet = petService.save(testPet);

        Owner owner = Owner.builder()
                .firstName("owner with one already persisted pet")
                .lastName("owner with one already persisted pet lastName")
                .address("asljnflksamfslkmf")
                .city("n1 city")
                .telephone("12843723847324")
                .pets(new HashSet<>(Arrays.asList(savedPet)))
                .build();

        getTestTemplate().perform(save(owner))
                .andExpect(deepCompare(owner).withReturnedAndDbEntity().isEqual());
    }


    @Test
    public void updateOwner_ChangeTelephoneNumber_ShouldSucceed() throws BadEntityException, EntityNotFoundException, NoIdException, InvalidConfigurationModificationException {
        Owner diffTelephoneNumberUpdate = Owner.builder()
                .telephone(ownerWithoutPets.getTelephone() + "123")
                .build();
        Owner toUpdate = getRepository().save(ownerWithoutPets);
        diffTelephoneNumberUpdate.setId(toUpdate.getId());

        getTestTemplate().perform(partialUpdate(diffTelephoneNumberUpdate))
                .andExpect(
                        deepCompare(merge(diffTelephoneNumberUpdate,toUpdate))
                        .withReturnedAndDbEntity()
                                .isEqual()
                );
    }

    @Test
    public void updateOwner_addAnotherPet_shouldSucceed() throws BadEntityException, EntityNotFoundException, InvalidConfigurationModificationException, NoIdException {
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
                .pets(new HashSet<>(Arrays.asList(savedPet)))
                .build();

        Owner ownerUpdateRequest = new Owner();
        ownerUpdateRequest.getPets().addAll(owner.getPets());
        //here comes the new pet
        ownerUpdateRequest.getPets().add(savedPetToAdd);

        //when
        Owner saved = getRepository().save(owner);
        ownerUpdateRequest.setId(saved.getId());

        getTestTemplate().perform(partialUpdate(ownerUpdateRequest))
                .andExpect(
                        compare(merge(ownerUpdateRequest,owner))
                        .property(ownerUpdateRequest::getPets)
                        .sizeIs(2)
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
        Assertions.assertTrue(reflectionComparator.isEqual(savedOwner, byLastName.get()));
    }


}