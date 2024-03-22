package com.github.vincemann.springrapid.acldemo.controller.suite;

import com.github.vincemann.acltest.ClearAclCacheTestExecutionListener;
import com.github.vincemann.springrapid.acldemo.controller.suite.templates.OwnerControllerTestTemplate;
import com.github.vincemann.springrapid.acldemo.controller.suite.templates.PetControllerTestTemplate;
import com.github.vincemann.springrapid.acldemo.controller.suite.templates.VetControllerTestTemplate;
import com.github.vincemann.springrapid.acldemo.controller.suite.templates.VisitControllerTestTemplate;
import com.github.vincemann.springrapid.acldemo.model.*;
import com.github.vincemann.springrapid.acldemo.repo.*;
import com.github.vincemann.springrapid.auth.msg.MessageSender;
import com.github.vincemann.springrapid.authtest.RapidUserControllerTestTemplate;
import com.github.vincemann.springrapid.coretest.controller.AbstractMvcTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Sql(scripts = "classpath:clear-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@TestExecutionListeners(
        value = {
                ClearAclCacheTestExecutionListener.class,
        },
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
@Sql(scripts = "classpath:/remove-acl-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class MyIntegrationTest extends AbstractMvcTest
{

    @Autowired
    protected TestData testData;
    @Autowired
    protected IntegrationTestHelper helper;
    @MockBean
    protected MessageSender messageSender;


    // repos

    @Autowired
    protected VetRepository vetRepository;
    @Autowired
    protected PetRepository petRepository;
    @Autowired
    protected VisitRepository visitRepository;
    @Autowired
    protected OwnerRepository ownerRepository;
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
    protected RapidUserControllerTestTemplate userController;


    @BeforeEach
    void setUp() {
        helper.setup();
    }

    @Override
    protected DefaultMockMvcBuilder createMvcBuilder() {
        DefaultMockMvcBuilder mvcBuilder = super.createMvcBuilder();
        mvcBuilder.apply(SecurityMockMvcConfigurers.springSecurity());
        return mvcBuilder;
    }

    protected void assertVetHasSpecialties(String vetName, String... descriptions) {
        Optional<Vet> vetOptional = vetRepository.findByLastName(vetName);
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
            Optional<Vet> optionalVet = vetRepository.findByLastName(vetName);
            Assertions.assertTrue(optionalVet.isPresent());
            vets.add(optionalVet.get());
        }
        System.err.println("Checking Specialty: " + description);
        Assertions.assertEquals(vets, specialty.getVets());
    }

    protected void assertPetHasIllnesses(String petName, String... illnessNames) {
        Optional<Pet> petOptional = petRepository.findByName(petName);
        Assertions.assertTrue(petOptional.isPresent());
        Pet pet = petOptional.get();

        Set<Illness> illnesses = new HashSet<>();
        for (String illness : illnessNames) {
            Optional<Illness> optionalIllness = illnessRepository.findByName(illness);
            Assertions.assertTrue(optionalIllness.isPresent());
            illnesses.add(optionalIllness.get());
        }
        System.err.println("Checking pet: " + petName);
        Assertions.assertEquals(illnesses, pet.getIllnesses());
    }

    protected void assertOwnerHasPets(String ownerName, String... petNames) {
        Optional<Owner> ownerOptional = ownerRepository.findByLastName(ownerName);
        Assertions.assertTrue(ownerOptional.isPresent());
        Owner owner = ownerOptional.get();

        Set<Pet> pets = new HashSet<>();
        for (String petName : petNames) {
            Optional<Pet> optionalPet = petRepository.findByName(petName);
            Assertions.assertTrue(optionalPet.isPresent());
            pets.add(optionalPet.get());
        }
        System.err.println("Checking owner: " + ownerName);
        Assertions.assertEquals(pets, owner.getPets());
    }


    protected void assertPetHasOwner(String petName, String ownerName) {
        Owner owner = null;
        if (ownerName!=null){
            Optional<Owner> ownerOptional = ownerRepository.findByLastName(ownerName);
            Assertions.assertTrue(ownerOptional.isPresent());
            owner = ownerOptional.get();
        }
        Optional<Pet> optionalPet = petRepository.findByName(petName);
        Assertions.assertTrue(optionalPet.isPresent());
        Pet pet = optionalPet.get();
        System.err.println("Checking pet: " + petName);
        Assertions.assertEquals(owner, pet.getOwner());
    }
}
