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
import com.github.vincemann.springrapid.auth.mail.MailData;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.authtest.UserControllerTestTemplate;
import com.github.vincemann.springrapid.coretest.MvcAware;
import com.github.vincemann.springrapid.coretest.TestMethodInitializable;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.github.vincemann.springrapid.acldemo.controller.suite.TestData.*;

public class TestHelper implements TestMethodInitializable, MvcAware {

    @Autowired
    private TestData testData;

    // services

    @Autowired
    protected SpecialtyService specialtyService;
    @Autowired
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
    protected OwnerService ownerService;


    // controllers

    @Autowired
    protected UserControllerTestTemplate userController;
    @Autowired
    protected OwnerControllerTestTemplate ownerController;
    @Autowired
    protected PetControllerTestTemplate petController;
    @Autowired
    protected VetControllerTestTemplate vetController;
    @Autowired
    protected VisitControllerTestTemplate visitController;



    @Override
    public void before() {
        testData.initTestData();
    }

    @Override
    public void setMvc(MockMvc mvc) {
        userController.setMvc(mvc);
        ownerController.setMvc(mvc);
        petController.setMvc(mvc);
        vetController.setMvc(mvc);
        visitController.setMvc(mvc);
    }

//    public Owner findKahn(){
//        Optional<Owner> kahn = findOptionalKahn();
//        Assertions.assertTrue(kahn.isPresent());
//        return kahn.get();
//    }
//
//    public Optional<Owner> findOptionalKahn(){
//        return ownerService.findByLastName(OWNER_KAHN);
//    }

    /**
     * signup vet dicaprio with specialty heart and verify
     */
    public Vet signupVetDiCaprioWithHeartAndVerify() throws Exception {
        signupVetDiCaprioWithHeart();
        // verify
        MailData mailData = userController.verifyMailWasSend();
        userController.perform2xx(userController.verifyContactInformationWithLink(mailData.getLink()));

        Vet saved = vetService.findByLastName(VET_DICAPRIO).get();
        Assertions.assertFalse(saved.getRoles().contains(AuthRoles.UNVERIFIED));
        return saved;
    }

    /**
     * signup vet dicaprio with specialty heart
     */
    public Vet signupVetDiCaprioWithHeart() throws Exception {
        // signup
        Vet diCaprio = testData.getVetDiCaprio();
        diCaprio.getSpecialtys().add(specialtyService.create(testData.getHeart()));
        SignupVetDto dto = new SignupVetDto(diCaprio);
        ReadVetDto response = vetController.signup(dto);
        Assertions.assertTrue(response.getRoles().contains(AuthRoles.UNVERIFIED));
        return vetService.findById(response.getId()).get();
    }

    /**
     * sign up owner kahn with its pet bella
     */
    public Owner signupKahnWithBella() throws Exception {
        Pet bella = petService.create(testData.getBella());
        testData.getKahn().getPets().add(bella);
        return signupOwner(testData.getKahn());
    }

    public Owner signupMeierWithBello() throws Exception {
        Pet bello = petService.create(testData.getBello());
        testData.getMeier().getPets().add(bello);
        return signupOwner(testData.getMeier());
    }

    public Owner signupOwner(Owner owner) throws Exception {
        SignupOwnerDto dto = new SignupOwnerDto(owner);
        ReadOwnOwnerDto response = ownerController.signup(dto);
        return ownerService.findById(response.getId()).get();
    }



    protected Visit createVisit(String token, Visit visit) throws Exception {
        CreateVisitDto dto = new CreateVisitDto(visit);

        ReadVisitDto response = visitController.performDs2xx(visitController.create(dto)
                        .header(HttpHeaders.AUTHORIZATION,token)
                , ReadVisitDto.class);
        return visitService.findById(response.getId()).get();
    }
}
