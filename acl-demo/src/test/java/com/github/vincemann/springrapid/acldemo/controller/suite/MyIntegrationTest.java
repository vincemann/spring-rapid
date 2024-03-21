package com.github.vincemann.springrapid.acldemo.controller.suite;

import com.github.vincemann.acltest.AclMvcTest;
import com.github.vincemann.springrapid.acldemo.controller.suite.templates.OwnerControllerTestTemplate;
import com.github.vincemann.springrapid.acldemo.controller.suite.templates.PetControllerTestTemplate;
import com.github.vincemann.springrapid.acldemo.controller.suite.templates.VetControllerTestTemplate;
import com.github.vincemann.springrapid.acldemo.controller.suite.templates.VisitControllerTestTemplate;
import com.github.vincemann.springrapid.acldemo.model.*;
import com.github.vincemann.springrapid.acldemo.repo.IllnessRepository;
import com.github.vincemann.springrapid.acldemo.repo.PetRepository;
import com.github.vincemann.springrapid.acldemo.repo.SpecialtyRepository;
import com.github.vincemann.springrapid.acldemo.service.*;
import com.github.vincemann.springrapid.auth.msg.MessageSender;
import com.github.vincemann.springrapid.authtest.UserControllerTestTemplate;
import com.github.vincemann.springrapid.core.Root;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Sql(scripts = "classpath:clear-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class MyIntegrationTest extends AclMvcTest
{

    @Autowired
    protected TestData testData;
    @Autowired
    protected IntegrationTestHelper helper;

    @MockBean
    protected MessageSender messageSender;


    // services

    @Autowired
    @Root
    protected VetService vetService;
    @Autowired
    protected PetService petService;
    @Autowired
    protected VisitService visitService;
    @Autowired
    @Root
    protected OwnerService ownerService;

    @Autowired
    protected PetRepository petRepository;
    @Autowired
    protected IllnessRepository illnessRepository;

    @Autowired
    protected SpecialtyRepository specialtyRepository;


    // controller
    @Autowired
    protected OwnerControllerTestTemplate ownerController;
    @Autowired
    protected PetControllerTestTemplate petController;
    @Autowired
    protected VetControllerTestTemplate vetController;
    @Autowired
    protected VisitControllerTestTemplate visitController;
    @Autowired
    protected UserControllerTestTemplate userController;


    @Override
    protected DefaultMockMvcBuilder createMvcBuilder() {
        DefaultMockMvcBuilder mvcBuilder = super.createMvcBuilder();
        mvcBuilder.apply(SecurityMockMvcConfigurers.springSecurity());
        return mvcBuilder;
    }

    protected void assertVetHasSpecialties(String vetName, String... descriptions) {
        Optional<Vet> vetOptional = vetService.findByLastName(vetName);
        Assertions.assertTrue(vetOptional.isPresent());
        Vet vet = vetOptional.get();

        Set<Specialty> specialtys = new HashSet<>();
        for (String description : descriptions) {
            Optional<Specialty> optionalSpecialty = specialtyRepository.findByDescription(description);
            Assertions.assertTrue(optionalSpecialty.isPresent());
            specialtys.add(optionalSpecialty.get());
        }
        System.err.println("Checking vet: " + vetName);
        Assertions.assertEquals(specialtys, vet.getSpecialtys());
    }

    protected void assertSpecialtyHasVets(String description, String... vetNames) {
        Optional<Specialty> optionalSpecialty = specialtyRepository.findByDescription(description);
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
    }

    protected void assertPetHasIllnesses(String petName, String... illnessNames) {
        Optional<Pet> petOptional = petService.findByName(petName);
        Assertions.assertTrue(petOptional.isPresent());
        Pet pet = petOptional.get();

        Set<Illness> illnesses = new HashSet<>();
        for (String illness : illnessNames) {
            Optional<Illness> optionalIllness = illnessRepository.findByName(illness);
            Assertions.assertTrue(optionalIllness.isPresent());
            illnesses.add(optionalIllness.get());
        }
        System.err.println("Checking pet: " + petName);
        Assertions.assertEquals(illnesses, pet.getIllnesss());
    }

    protected void assertOwnerHasPets(String ownerName, String... petNames) {
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
    }


    protected void assertPetHasOwner(String petName, String ownerName) {
        Owner owner = null;
        if (ownerName!=null){
            Optional<Owner> ownerOptional = ownerService.findByLastName(ownerName);
            Assertions.assertTrue(ownerOptional.isPresent());
            owner = ownerOptional.get();
        }
        Optional<Pet> optionalPet = petService.findByName(petName);
        Assertions.assertTrue(optionalPet.isPresent());
        Pet pet = optionalPet.get();
        System.err.println("Checking pet: " + petName);
        Assertions.assertEquals(owner, pet.getOwner());
    }
}
