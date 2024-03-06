package com.github.vincemann.springrapid.coredemo.controller.suite;

import com.github.vincemann.springrapid.coredemo.controller.suite.template.OwnerControllerTestTemplate;
import com.github.vincemann.springrapid.coredemo.controller.suite.template.PetControllerTestTemplate;
import com.github.vincemann.springrapid.coredemo.controller.suite.template.VetControllerTestTemplate;
import com.github.vincemann.springrapid.coredemo.controller.suite.template.VisitControllerTestTemplate;
import com.github.vincemann.springrapid.coredemo.model.*;
import com.github.vincemann.springrapid.coredemo.service.*;
import com.github.vincemann.springrapid.coretest.controller.AbstractMvcTest;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Sql(scripts = "classpath:clear-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class MyIntegrationTest extends AbstractMvcTest
{

    @Autowired
    protected TestData testData;
    @Autowired
    protected IntegrationTestHelper helper;

    @Autowired
    protected SpecialtyService specialtyService;
    @Autowired
    protected VetService vetService;
    @Autowired
    protected ToyService toyService;
    @Autowired
    protected PetService petService;
    @Autowired
    protected ClinicCardService clinicCardService;
    @Autowired
    protected PetTypeService petTypeService;
    @Autowired
    protected VisitService visitService;
    @Autowired
    protected OwnerService ownerService;

    @Autowired
    protected TransactionTemplate transactionTemplate;


    // controller
    @Autowired
    protected OwnerControllerTestTemplate ownerController;
    @Autowired
    protected PetControllerTestTemplate petController;
    @Autowired
    protected VetControllerTestTemplate vetController;
    @Autowired
    protected VisitControllerTestTemplate visitController;


    protected void assertVetHasSpecialties(String vetName, String... descriptions) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            Optional<Vet> vetOptional = vetService.findByLastName(vetName);
            Assertions.assertTrue(vetOptional.isPresent());
            Vet vet = vetOptional.get();

            Set<Specialty> specialtys = new HashSet<>();
            for (String description : descriptions) {
                Optional<Specialty> optionalSpecialty = specialtyService.findByDescription(description);
                Assertions.assertTrue(optionalSpecialty.isPresent());
                specialtys.add(optionalSpecialty.get());
            }
            System.err.println("Checking vet: " + vetName);
            Assertions.assertEquals(specialtys, vet.getSpecialtys());
        });

    }

    protected void assertSpecialtyHasVets(String description, String... vetNames) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            Optional<Specialty> optionalSpecialty = specialtyService.findByDescription(description);
            Assertions.assertTrue(optionalSpecialty.isPresent());
            Specialty specialty = optionalSpecialty.get();

            Set<Vet> vets = new HashSet<>();
            for (String vetName : vetNames) {
                Optional<Vet> optionalVet = vetService.findByLastName(vetName);
                Assertions.assertTrue(optionalVet.isPresent());
                vets.add(optionalVet.get());
            }
            System.err.println("Checking Specialty: " + description);
            Assertions.assertEquals(vets, specialty.getVets());
        });

    }

    public void assertPetHasToys(String petName, String... toyNames) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            Optional<Pet> petOptional = petService.findByName(petName);
            Assertions.assertTrue(petOptional.isPresent());
            Pet pet = petOptional.get();

            Set<Toy> toys = new HashSet<>();
            for (String toyName : toyNames) {
                Optional<Toy> optionalToy = toyService.findByName(toyName);
                Assertions.assertTrue(optionalToy.isPresent());
                toys.add(optionalToy.get());
            }
            System.err.println("Checking pet: " + petName);
            Assertions.assertEquals(toys, pet.getToys());
        });
    }

    protected void assertOwnerHasPets(String ownerName, String... petNames) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            Optional<Owner> ownerOptional = ownerService.findByLastName(ownerName);
            Assertions.assertTrue(ownerOptional.isPresent());
            Owner owner = ownerOptional.get();

            Set<Pet> pets = new HashSet<>();
            for (String petName : petNames) {
                Optional<Pet> optionalPet = petService.findByName(petName);
                Assertions.assertTrue(optionalPet.isPresent());
                pets.add(optionalPet.get());
            }
            System.err.println("Checking owner: " + ownerName);
            Assertions.assertEquals(pets, owner.getPets());
        });
    }

    protected void assertOwnerHasClinicCard(String ownerName, Long clinicCardId) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            Optional<Owner> ownerOptional = ownerService.findByLastName(ownerName);
            Assertions.assertTrue(ownerOptional.isPresent());
            Owner owner = ownerOptional.get();
            System.err.println("Checking owner: " + ownerName);
            if (clinicCardId == null) {
                Assertions.assertNull(owner.getClinicCard());
            } else {
                ClinicCard clinicCard = clinicCardService.findById(clinicCardId).get();
                Assertions.assertEquals(clinicCard, owner.getClinicCard());
            }
        });
    }

    protected void assertClinicCardHasOwner(Long clinicCardId, String ownerName) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            if (ownerName == null) {
                ClinicCard clinicCard = clinicCardService.findById(clinicCardId).get();
                Assertions.assertNull(clinicCard.getOwner());
            } else {
                Optional<Owner> ownerOptional = ownerService.findByLastName(ownerName);
                Assertions.assertTrue(ownerOptional.isPresent());
                Owner owner = ownerOptional.get();
                ClinicCard clinicCard = clinicCardService.findById(clinicCardId).get();
                System.err.println("Checking owner: " + ownerName);
                Assertions.assertEquals(owner, clinicCard.getOwner());
            }
        });

    }

    protected void assertToyHasPet(String toyName, String petName) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            Pet pet = null;
            if (petName != null) {
                Optional<Pet> petOptional = petService.findByName(petName);
                Assertions.assertTrue(petOptional.isPresent());
                pet = petOptional.get();
            }
            Optional<Toy> optionalToy = toyService.findByName(toyName);
            Assertions.assertTrue(optionalToy.isPresent());
            Toy toy = optionalToy.get();
            System.err.println("Checking toy: " + toyName);
            Assertions.assertEquals(pet, toy.getPet());
        });
    }

    protected void assertPetHasOwner(String petName, String ownerName) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            Owner owner = null;
            if (ownerName != null) {
                Optional<Owner> ownerOptional = ownerService.findByLastName(ownerName);
                Assertions.assertTrue(ownerOptional.isPresent());
                owner = ownerOptional.get();
            }
            Optional<Pet> optionalPet = petService.findByName(petName);
            Assertions.assertTrue(optionalPet.isPresent());
            Pet pet = optionalPet.get();
            System.err.println("Checking pet: " + petName);
            Assertions.assertEquals(owner, pet.getOwner());
        });
    }
}
