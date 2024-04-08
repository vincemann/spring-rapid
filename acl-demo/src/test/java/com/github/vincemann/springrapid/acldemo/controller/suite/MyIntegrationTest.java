package com.github.vincemann.springrapid.acldemo.controller.suite;

import com.github.vincemann.springrapid.acldemo.other.IllnessRepository;
import com.github.vincemann.springrapid.acldemo.other.Specialty;
import com.github.vincemann.springrapid.acldemo.other.SpecialtyRepository;
import com.github.vincemann.springrapid.acldemo.controller.suite.templates.OwnerControllerTestTemplate;
import com.github.vincemann.springrapid.acldemo.controller.suite.templates.PetControllerTestTemplate;
import com.github.vincemann.springrapid.acldemo.controller.suite.templates.VetControllerTestTemplate;
import com.github.vincemann.springrapid.acldemo.controller.suite.templates.VisitControllerTestTemplate;
import com.github.vincemann.springrapid.acldemo.owner.Owner;
import com.github.vincemann.springrapid.acldemo.owner.OwnerRepository;
import com.github.vincemann.springrapid.acldemo.pet.Pet;
import com.github.vincemann.springrapid.acldemo.pet.PetRepository;
import com.github.vincemann.springrapid.acldemo.vet.Vet;
import com.github.vincemann.springrapid.acldemo.vet.VetRepository;
import com.github.vincemann.springrapid.acldemo.visit.VisitRepository;
import com.github.vincemann.springrapid.auth.msg.AuthMessage;
import com.github.vincemann.springrapid.auth.msg.MessageSender;
import com.github.vincemann.springrapid.auth.util.AopProxyUtils;
import com.github.vincemann.springrapid.authtest.RapidUserControllerTestTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.acls.model.AclCache;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.atLeast;

@Sql(scripts = "classpath:clear-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(scripts = "classpath:/clear-acl-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class MyIntegrationTest
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


    @Autowired
    private AclCache aclCache;

    @Autowired
    protected MockMvc mvc;

    @BeforeEach
    void setUp() {
        helper.setup();
    }

    @AfterEach
    void tearDown() {
        aclCache.clearCache();
    }

    public AuthMessage verifyMsgWasSent(String recipient) {
        ArgumentCaptor<AuthMessage> msgCaptor = ArgumentCaptor.forClass(AuthMessage.class);

        Mockito.verify(AopProxyUtils.unproxy(messageSender), atLeast(1))
                .send(msgCaptor.capture());
        AuthMessage sentData = msgCaptor.getValue();
        Assertions.assertEquals(sentData.getRecipient(),recipient,"latest msg must be sent to recipient: " +recipient + " but was sent to: " + sentData.getRecipient());
        Mockito.reset(AopProxyUtils.unproxy(messageSender));
        return sentData;
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
