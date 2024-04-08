package com.github.vincemann.springrapid.acldemo.controller.suite;

import com.github.vincemann.springrapid.acldemo.other.PetTypeRepository;
import com.github.vincemann.springrapid.acldemo.other.SpecialtyRepository;
import com.github.vincemann.springrapid.acldemo.controller.suite.templates.OwnerControllerTestTemplate;
import com.github.vincemann.springrapid.acldemo.controller.suite.templates.PetControllerTestTemplate;
import com.github.vincemann.springrapid.acldemo.controller.suite.templates.VetControllerTestTemplate;
import com.github.vincemann.springrapid.acldemo.controller.suite.templates.VisitControllerTestTemplate;
import com.github.vincemann.springrapid.acldemo.owner.dto.OwnerReadsOwnOwnerDto;
import com.github.vincemann.springrapid.acldemo.owner.dto.SignupOwnerDto;
import com.github.vincemann.springrapid.acldemo.pet.dto.CreatePetDto;
import com.github.vincemann.springrapid.acldemo.vet.dto.ReadVetDto;
import com.github.vincemann.springrapid.acldemo.vet.dto.SignupVetDto;
import com.github.vincemann.springrapid.acldemo.visit.dto.CreateVisitDto;
import com.github.vincemann.springrapid.acldemo.visit.dto.ReadVisitDto;
import com.github.vincemann.springrapid.acldemo.owner.Owner;
import com.github.vincemann.springrapid.acldemo.pet.Pet;
import com.github.vincemann.springrapid.acldemo.vet.Vet;
import com.github.vincemann.springrapid.acldemo.visit.Visit;
import com.github.vincemann.springrapid.acldemo.owner.OwnerRepository;
import com.github.vincemann.springrapid.acldemo.pet.PetService;
import com.github.vincemann.springrapid.acldemo.vet.VetRepository;
import com.github.vincemann.springrapid.acldemo.visit.VisitRepository;
import com.github.vincemann.springrapid.auth.RapidSecurityContext;
import com.github.vincemann.springrapid.auth.Roles;
import com.github.vincemann.springrapid.auth.msg.AuthMessage;
import com.github.vincemann.springrapid.auth.msg.MessageSender;
import com.github.vincemann.springrapid.auth.util.AopProxyUtils;
import com.github.vincemann.springrapid.authtest.AuthTestUtil;
import com.github.vincemann.springrapid.authtest.RapidUserControllerTestTemplate;
import org.junit.jupiter.api.Assertions;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.mockito.Mockito.atLeast;

@Component
public class IntegrationTestHelper {

    @Autowired // will autowire mock
    private MessageSender messageSenderMock;

    @Autowired
    private TestData testData;

    // services

    @Autowired
    protected PetService petService;

    // repos

    @Autowired
    protected SpecialtyRepository specialtyRepository;
    @Autowired
    protected PetTypeRepository petTypeRepository;
    @Autowired
    private VetRepository vetRepository;
    @Autowired
    private VisitRepository visitRepository;
    @Autowired
    private OwnerRepository ownerRepository;


    // controllers

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



    public void setup(){
        testData.savedDogPetType = petTypeRepository.save(testData.getDogPetType());
        testData.savedCatPetType = petTypeRepository.save(testData.getCatPetType());
        testData.initTestData();
    }


    public Vet signupVetDiCaprioWithHeartAndVerify() throws Exception {
        Vet dicaprio = signupVetDiCaprioWithHeart();
        // verify
        AuthMessage msg = verifyMsgWasSent(dicaprio.getContactInformation());
        userController.perform2xx(userController.verifyUserWithLink(msg.getLink()));

        Vet saved = vetRepository.findByLastName(dicaprio.getLastName()).get();
        Assertions.assertFalse(saved.getRoles().contains(Roles.UNVERIFIED));
        return saved;
    }

    public Vet signupVetMaxWithDentismAndVerify() throws Exception {
        Vet max = signupVetMaxWithDentism();
        // verify
        AuthMessage msg = verifyMsgWasSent(max.getContactInformation());
        userController.perform2xx(userController.verifyUserWithLink(msg.getLink()));

        Vet saved = vetRepository.findByLastName(max.getLastName()).get();
        Assertions.assertFalse(saved.getRoles().contains(Roles.UNVERIFIED));
        return saved;
    }

    public Vet signupVetDiCaprioWithHeart() throws Exception {
        // signup
        Vet diCaprio = testData.getVetDiCaprio();
        diCaprio.getSpecialtys().add(specialtyRepository.save(testData.getHeart()));
        SignupVetDto dto = new SignupVetDto(diCaprio);
        ReadVetDto response = vetController.signup(dto);
        Assertions.assertTrue(response.getRoles().contains(Roles.UNVERIFIED));
        return vetRepository.findById(response.getId()).get();
    }

    public Vet signupVetMaxWithDentism() throws Exception {
        // signup
        Vet max = testData.getVetMax();
        max.getSpecialtys().add(specialtyRepository.save(testData.getDentism()));
        SignupVetDto dto = new SignupVetDto(max);
        ReadVetDto response = vetController.signup(dto);
        Assertions.assertTrue(response.getRoles().contains(Roles.UNVERIFIED));
        return vetRepository.findById(response.getId()).get();
    }

    public Owner signupKahnWithBella() throws Exception {
        Owner owner = signupOwner(testData.getKahn());
        AuthTestUtil.authenticate(owner);
        testData.getBella().setOwner(owner);
        Pet bella = petService.create(new CreatePetDto(testData.getBella()));
        RapidSecurityContext.clear();
        return ownerRepository.findById(owner.getId()).get();
    }

    public Owner signupMeierWithBello() throws Exception {
        Owner owner = signupOwner(testData.getMeier());
        AuthTestUtil.authenticate(owner);
        testData.getBello().setOwner(owner);
        Pet bello = petService.create(new CreatePetDto(testData.getBello()));
        RapidSecurityContext.clear();
        return ownerRepository.findById(owner.getId()).get();
    }

    public Owner signupOwner(Owner owner) throws Exception {
        SignupOwnerDto dto = new SignupOwnerDto(owner);
        OwnerReadsOwnOwnerDto response = ownerController.signup(dto);
        return ownerRepository.findById(response.getId()).get();
    }

    public Visit createVisit(String token, Visit visit, Owner owner, Vet vet, Pet... pets) throws Exception {
        CreateVisitDto dto = new CreateVisitDto(visit);
        dto.setOwnerId(owner.getId());
        dto.setVetId(vet.getId());
        for (Pet pet : pets) {
            dto.getPetIds().add(pet.getId());
        }

        ReadVisitDto response = visitController.create2xx(dto, token);
        return visitRepository.findById(response.getId()).get();
    }

    public AuthMessage verifyMsgWasSent(String recipient) {
        ArgumentCaptor<AuthMessage> msgCaptor = ArgumentCaptor.forClass(AuthMessage.class);

        Mockito.verify(AopProxyUtils.unproxy(messageSenderMock), atLeast(1))
                .send(msgCaptor.capture());
        AuthMessage sentData = msgCaptor.getValue();
        Assertions.assertEquals(sentData.getRecipient(),recipient,"latest msg must be sent to recipient: " +recipient + " but was sent to: " + sentData.getRecipient());
        Mockito.reset(AopProxyUtils.unproxy(messageSenderMock));
        return sentData;
    }
}
