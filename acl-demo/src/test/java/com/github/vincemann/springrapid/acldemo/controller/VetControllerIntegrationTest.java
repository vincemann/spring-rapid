package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acldemo.Roles;
import com.github.vincemann.springrapid.acldemo.controller.suite.MyIntegrationTest;
import com.github.vincemann.springrapid.acldemo.dto.pet.VetReadsPetDto;
import com.github.vincemann.springrapid.acldemo.dto.vet.ReadVetDto;
import com.github.vincemann.springrapid.acldemo.dto.vet.SignupVetDto;
import com.github.vincemann.springrapid.acldemo.dto.visit.CreateVisitDto;
import com.github.vincemann.springrapid.acldemo.dto.visit.ReadVisitDto;
import com.github.vincemann.springrapid.acldemo.model.*;
import com.github.vincemann.springrapid.auth.msg.AuthMessage;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import static com.github.vincemann.springrapid.acldemo.controller.suite.TestData.*;
import static com.github.vincemann.springrapid.coretest.util.RapidTestUtil.createUpdateJsonLine;
import static com.github.vincemann.springrapid.coretest.util.RapidTestUtil.createUpdateJsonRequest;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class VetControllerIntegrationTest extends MyIntegrationTest {

    @Test
    public void canSignupVet() throws Exception {
        // when
        Vet diCaprio = testData.getVetDiCaprio();
        diCaprio.getSpecialtys().add(specialtyService.create(testData.getHeart()));
        SignupVetDto dto = new SignupVetDto(diCaprio);
        ReadVetDto response = vetController.signup(dto);

        // then
        Assertions.assertEquals(VET_DICAPRIO,response.getLastName());
        Assertions.assertEquals(VET_DICAPRIO_EMAIL,response.getContactInformation());
        Assertions.assertTrue(response.getRoles().contains(Roles.VET));
        Assertions.assertTrue(response.getRoles().contains(AuthRoles.USER));
        Assertions.assertTrue(response.getRoles().contains(AuthRoles.UNVERIFIED));
        Assertions.assertEquals(3,response.getRoles().size());

        Assertions.assertTrue(vetService.findByLastName(VET_DICAPRIO).isPresent());
        Assertions.assertTrue(vetService.findByContactInformation(VET_DICAPRIO_EMAIL).isPresent());
    }

    @Test
    public void canVerifyVetAfterSignup() throws Exception {
        // given
        Vet dicaprio = helper.signupVetDiCaprioWithHeart();

        // when
        AuthMessage msg = userController.verifyMsgWasSent(dicaprio.getContactInformation());
        userController.perform(userController.verifyContactInformationWithLink(msg.getLink()))
        // then
                .andExpect(status().is2xxSuccessful());

        Vet saved = vetService.findByLastName(VET_DICAPRIO).get();
        Assertions.assertFalse(saved.getRoles().contains(AuthRoles.UNVERIFIED));
    }

    @Test
    public void unverifiedVetCanReadPets() throws Exception {
        // given
        helper.signupVetDiCaprioWithHeart();
        helper.signupKahnWithBella();
        Pet bella = petService.findByName(BELLA).get();

        // when
        String token = userController.login2xx(VET_DICAPRIO_EMAIL, VET_DICAPRIO_PASSWORD);

        mvc.perform(petController.find(bella.getId().toString())
                .header(HttpHeaders.AUTHORIZATION, token))
        // then
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void verifiedVetCanCreateVisit() throws Exception {
        // given
        Vet dicaprio = helper.signupVetDiCaprioWithHeartAndVerify();
        Owner kahn = helper.signupKahnWithBella();
        Pet bella = petService.findByName(BELLA).get();


        // when
        Visit visit = testData.getCheckTeethVisit();
        visit.setVet(dicaprio);
        visit.setOwner(kahn);
        visit.getPets().add(bella);
        CreateVisitDto dto = new CreateVisitDto(visit);
        String token = userController.login2xx(VET_DICAPRIO_EMAIL, VET_DICAPRIO_PASSWORD);
        ReadVisitDto response = visitController.perform2xxAndDeserialize(visitController.create(dto)
                        .header(HttpHeaders.AUTHORIZATION,token)
                , ReadVisitDto.class);
        // then
        Assertions.assertNotNull(response.getId());
        Assertions.assertEquals(visit.getReason(),response.getReason());
    }

    @Test
    public void unverifiedVetCantCreateVisit() throws Exception {
        // given
        Vet dicaprio = helper.signupVetDiCaprioWithHeart();
        Owner kahn = helper.signupKahnWithBella();
        Pet bella = petService.findByName(BELLA).get();

        // when
        Visit visit = testData.getCheckTeethVisit();
        visit.setVet(dicaprio);
        visit.setOwner(kahn);
        visit.getPets().add(bella);
        CreateVisitDto dto = new CreateVisitDto(visit);
        String token = userController.login2xx(VET_DICAPRIO_EMAIL, VET_DICAPRIO_PASSWORD);
        visitController.perform(visitController.create(dto)
                        .header(HttpHeaders.AUTHORIZATION,token))
        // then
                .andExpect(status().isForbidden());
        Assertions.assertTrue(visitService.findAll().isEmpty());
    }

    @Test
    public void givenSelectedPetsDoNotBelongToSelectedOwner_whenCreatingVisit_thenForbidden() throws Exception {
        // given
        Vet dicaprio = helper.signupVetDiCaprioWithHeartAndVerify();
        Owner kahn = helper.signupKahnWithBella();  // kahn is linked to bella not bello
        Owner meier = helper.signupMeierWithBello();
        Pet bello = petService.findByName(BELLO).get();

        // when
        Visit visit = testData.getCheckTeethVisit();
        visit.setVet(dicaprio);
        visit.setOwner(kahn);
        visit.getPets().add(bello); // setting bello here, which is not owned by kahn
        CreateVisitDto dto = new CreateVisitDto(visit);
        String token = userController.login2xx(VET_DICAPRIO_EMAIL, VET_DICAPRIO_PASSWORD);
        visitController.perform(visitController.create(dto)
                .header(HttpHeaders.AUTHORIZATION,token))
        // then
                .andExpect(status().isForbidden());
        Assertions.assertTrue(visitService.findAll().isEmpty());
    }

    @Test
    public void verifiedVetCanUpdatePetsIllnesses() throws Exception {
        // given
        Vet dicaprio = helper.signupVetDiCaprioWithHeartAndVerify();
        Owner kahn = helper.signupKahnWithBella();
        Pet bella = petService.findByName(BELLA).get();
        Illness teethPain = illnessService.create(testData.getTeethPain());

        // when
        String updateJson = createUpdateJsonRequest(
                createUpdateJsonLine("add", "/illnessIds", teethPain.getId().toString())
        );
        String token = userController.login2xx(VET_DICAPRIO_EMAIL, VET_DICAPRIO_PASSWORD);
        VetReadsPetDto responsePetDto = petController.perform2xxAndDeserialize(petController.update(updateJson,bella.getId())
                .header(HttpHeaders.AUTHORIZATION, token), VetReadsPetDto.class);
        Assertions.assertFalse(responsePetDto.getIllnessIds().isEmpty());

        // then
        Pet updatedBella = petService.findByName(BELLA).get();
        Assertions.assertTrue(updatedBella.getIllnesss().stream().anyMatch(i -> i.getName().equals(teethPain.getName())));
    }

}
