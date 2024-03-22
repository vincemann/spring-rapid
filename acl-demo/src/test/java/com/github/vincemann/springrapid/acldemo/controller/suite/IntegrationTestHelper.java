package com.github.vincemann.springrapid.acldemo.controller.suite;

import com.github.vincemann.springrapid.acldemo.controller.suite.templates.OwnerControllerTestTemplate;
import com.github.vincemann.springrapid.acldemo.controller.suite.templates.PetControllerTestTemplate;
import com.github.vincemann.springrapid.acldemo.controller.suite.templates.VetControllerTestTemplate;
import com.github.vincemann.springrapid.acldemo.controller.suite.templates.VisitControllerTestTemplate;
import com.github.vincemann.springrapid.acldemo.dto.owner.OwnerReadsOwnOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.owner.SignupOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.pet.CreatePetDto;
import com.github.vincemann.springrapid.acldemo.dto.vet.ReadVetDto;
import com.github.vincemann.springrapid.acldemo.dto.vet.SignupVetDto;
import com.github.vincemann.springrapid.acldemo.dto.visit.CreateVisitDto;
import com.github.vincemann.springrapid.acldemo.dto.visit.ReadVisitDto;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.acldemo.model.Vet;
import com.github.vincemann.springrapid.acldemo.model.Visit;
import com.github.vincemann.springrapid.acldemo.repo.*;
import com.github.vincemann.springrapid.acldemo.service.PetService;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.auth.msg.AuthMessage;
import com.github.vincemann.springrapid.authtest.RapidAuthTestUtil;
import com.github.vincemann.springrapid.authtest.RapidUserControllerTestTemplate;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import com.github.vincemann.springrapid.coretest.controller.MvcAware;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;

@Component
public class IntegrationTestHelper implements MvcAware {

    @Autowired
    private TestData testData;

    // services

    @Autowired
    protected PetService petService;

    // repos

    @Autowired
    protected SpecialtyRepository specialtyRepository;
    @Autowired
    protected IllnessRepository illnessRepository;

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

    @Override
    public void setMvc(MockMvc mvc) {
        ownerController.setMvc(mvc);
        petController.setMvc(mvc);
        vetController.setMvc(mvc);
        visitController.setMvc(mvc);
    }


    public Vet signupVetDiCaprioWithHeartAndVerify() throws Exception {
        Vet dicaprio = signupVetDiCaprioWithHeart();
        // verify
        AuthMessage msg = userController.verifyMsgWasSent(dicaprio.getContactInformation());
        userController.perform2xx(userController.verifyUserWithLink(msg.getLink()));

        Vet saved = vetRepository.findByLastName(dicaprio.getLastName()).get();
        Assertions.assertFalse(saved.getRoles().contains(AuthRoles.UNVERIFIED));
        return saved;
    }

    public Vet signupVetMaxWithDentismAndVerify() throws Exception {
        Vet max = signupVetMaxWithDentism();
        // verify
        AuthMessage msg = userController.verifyMsgWasSent(max.getContactInformation());
        userController.perform2xx(userController.verifyUserWithLink(msg.getLink()));

        Vet saved = vetRepository.findByLastName(max.getLastName()).get();
        Assertions.assertFalse(saved.getRoles().contains(AuthRoles.UNVERIFIED));
        return saved;
    }

    public Vet signupVetDiCaprioWithHeart() throws Exception {
        // signup
        Vet diCaprio = testData.getVetDiCaprio();
        diCaprio.getSpecialtys().add(specialtyRepository.save(testData.getHeart()));
        SignupVetDto dto = new SignupVetDto(diCaprio);
        ReadVetDto response = vetController.signup(dto);
        Assertions.assertTrue(response.getRoles().contains(AuthRoles.UNVERIFIED));
        return vetRepository.findById(response.getId()).get();
    }

    public Vet signupVetMaxWithDentism() throws Exception {
        // signup
        Vet max = testData.getVetMax();
        max.getSpecialtys().add(specialtyRepository.save(testData.getDentism()));
        SignupVetDto dto = new SignupVetDto(max);
        ReadVetDto response = vetController.signup(dto);
        Assertions.assertTrue(response.getRoles().contains(AuthRoles.UNVERIFIED));
        return vetRepository.findById(response.getId()).get();
    }

    public Owner signupKahnWithBella() throws Exception {
        Owner owner = signupOwner(testData.getKahn());
        RapidAuthTestUtil.authenticate(owner);
        testData.getBella().setOwner(owner);
        Pet bella = petService.create(new CreatePetDto(testData.getBella()));
        RapidSecurityContext.clear();
        return ownerRepository.findById(owner.getId()).get();
    }

    public Owner signupMeierWithBello() throws Exception {
        Owner owner = signupOwner(testData.getMeier());
        RapidAuthTestUtil.authenticate(owner);
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
}
