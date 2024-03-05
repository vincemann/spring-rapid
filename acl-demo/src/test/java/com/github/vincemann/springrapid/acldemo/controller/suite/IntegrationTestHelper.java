package com.github.vincemann.springrapid.acldemo.controller.suite;

import com.github.vincemann.springrapid.acldemo.controller.suite.templates.OwnerControllerTestTemplate;
import com.github.vincemann.springrapid.acldemo.controller.suite.templates.PetControllerTestTemplate;
import com.github.vincemann.springrapid.acldemo.controller.suite.templates.VetControllerTestTemplate;
import com.github.vincemann.springrapid.acldemo.controller.suite.templates.VisitControllerTestTemplate;
import com.github.vincemann.springrapid.acldemo.dto.owner.ReadOwnOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.owner.SignupOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.vet.ReadVetDto;
import com.github.vincemann.springrapid.acldemo.dto.vet.SignupVetDto;
import com.github.vincemann.springrapid.acldemo.dto.visit.CreateVisitDto;
import com.github.vincemann.springrapid.acldemo.dto.visit.ReadVisitDto;
import com.github.vincemann.springrapid.acldemo.model.*;
import com.github.vincemann.springrapid.acldemo.service.*;
import com.github.vincemann.springrapid.auth.AuthMessage;
import com.github.vincemann.springrapid.authtest.UserControllerTestTemplate;
import com.github.vincemann.springrapid.core.Root;
import com.github.vincemann.springrapid.auth.mail.MailData;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.coretest.MvcAware;
import com.github.vincemann.springrapid.coretest.TestMethodInitializable;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;

@Component
public class IntegrationTestHelper implements TestMethodInitializable, MvcAware {

    @Autowired
    private TestData testData;

    // services

    @Autowired
    protected SpecialtyService specialtyService;
    @Autowired
    @Root
    protected VetService vetService;
    @Autowired
    protected IllnessService illnessService;
    @Autowired
    protected PetService petService;
    @Autowired
    protected PetTypeService petTypeService;
    @Autowired
    protected VisitService visitService;
    @Autowired
    @Root
    protected OwnerService ownerService;


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
    protected UserControllerTestTemplate userController;



    @Override
    public void beforeTestMethod() throws BadEntityException {
        testData.initTestData();
        testData.savedDogPetType = petTypeService.create(testData.getSavedDogPetType());
        testData.savedCatPetType = petTypeService.create(testData.getSavedCatPetType());
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
        userController.perform2xx(userController.verifyContactInformationWithLink(msg.getLink()));

        Vet saved = vetService.findByLastName(dicaprio.getLastName()).get();
        Assertions.assertFalse(saved.getRoles().contains(AuthRoles.UNVERIFIED));
        return saved;
    }

    public Vet signupVetMaxWithDentismAndVerify() throws Exception {
        Vet max = signupVetMaxWithDentism();
        // verify
        AuthMessage msg = userController.verifyMsgWasSent(max.getContactInformation());
        userController.perform2xx(userController.verifyContactInformationWithLink(msg.getLink()));

        Vet saved = vetService.findByLastName(max.getLastName()).get();
        Assertions.assertFalse(saved.getRoles().contains(AuthRoles.UNVERIFIED));
        return saved;
    }

    public Vet signupVetDiCaprioWithHeart() throws Exception {
        // signup
        Vet diCaprio = testData.getVetDiCaprio();
        diCaprio.getSpecialtys().add(specialtyService.create(testData.getHeart()));
        SignupVetDto dto = new SignupVetDto(diCaprio);
        ReadVetDto response = vetController.signup(dto);
        Assertions.assertTrue(response.getRoles().contains(AuthRoles.UNVERIFIED));
        return vetService.findById(response.getId()).get();
    }

    public Vet signupVetMaxWithDentism() throws Exception {
        // signup
        Vet max = testData.getVetMax();
        max.getSpecialtys().add(specialtyService.create(testData.getDentism()));
        SignupVetDto dto = new SignupVetDto(max);
        ReadVetDto response = vetController.signup(dto);
        Assertions.assertTrue(response.getRoles().contains(AuthRoles.UNVERIFIED));
        return vetService.findById(response.getId()).get();
    }

    public Owner signupKahnWithBella() throws Exception {
        Owner owner = signupOwner(testData.getKahn());
        testData.getBella().setOwner(owner);
        Pet bella = petService.create(testData.getBella());
        return ownerService.findPresentById(owner.getId());
    }

    public Owner signupMeierWithBello() throws Exception {
        Owner owner = signupOwner(testData.getMeier());
        testData.getBello().setOwner(owner);
        Pet bello = petService.create(testData.getBello());
        return ownerService.findPresentById(owner.getId());
    }

    public Owner signupOwner(Owner owner) throws Exception {
        SignupOwnerDto dto = new SignupOwnerDto(owner);
        ReadOwnOwnerDto response = ownerController.signup(dto);
        return ownerService.findById(response.getId()).get();
    }

    public Visit createVisit(String token, Visit visit, Owner owner, Vet vet, Pet... pets) throws Exception {
        CreateVisitDto dto = new CreateVisitDto(visit);
        dto.setOwnerId(owner.getId());
        dto.setVetId(vet.getId());
        for (Pet pet : pets) {
            dto.getPetIds().add(pet.getId());
        }

        ReadVisitDto response = visitController.perform2xxAndDeserialize(visitController.create(dto)
                        .header(HttpHeaders.AUTHORIZATION,token)
                , ReadVisitDto.class);
        return visitService.findById(response.getId()).get();
    }
}
