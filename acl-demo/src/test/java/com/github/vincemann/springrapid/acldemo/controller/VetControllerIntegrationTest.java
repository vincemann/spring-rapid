package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acldemo.Roles;
import com.github.vincemann.springrapid.acldemo.controller.suite.MyIntegrationTest;
import com.github.vincemann.springrapid.acldemo.dto.pet.VetReadsPetDto;
import com.github.vincemann.springrapid.acldemo.dto.pet.UpdateIllnessDto;
import com.github.vincemann.springrapid.acldemo.dto.vet.ReadVetDto;
import com.github.vincemann.springrapid.acldemo.dto.vet.SignupVetDto;
import com.github.vincemann.springrapid.acldemo.dto.visit.CreateVisitDto;
import com.github.vincemann.springrapid.acldemo.dto.visit.ReadVisitDto;
import com.github.vincemann.springrapid.acldemo.model.*;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.auth.msg.AuthMessage;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import static com.github.vincemann.springrapid.acldemo.controller.suite.TestData.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag(value = "demo-projects")
public class VetControllerIntegrationTest extends MyIntegrationTest {

    @Test
    public void canSignupVet() throws Exception {
        // when
        Vet diCaprio = testData.getVetDiCaprio();
        diCaprio.getSpecialtys().add(specialtyRepository.save(testData.getHeart()));
        SignupVetDto dto = new SignupVetDto(diCaprio);
        ReadVetDto response = vetController.signup(dto);

        // then
        Assertions.assertEquals(VET_DICAPRIO,response.getLastName());
        Assertions.assertEquals(VET_DICAPRIO_EMAIL,response.getContactInformation());
        Assertions.assertTrue(response.getRoles().contains(Roles.VET));
        Assertions.assertTrue(response.getRoles().contains(AuthRoles.USER));
        Assertions.assertTrue(response.getRoles().contains(AuthRoles.UNVERIFIED));
        Assertions.assertEquals(3,response.getRoles().size());

        Assertions.assertTrue(vetRepository.findByLastName(VET_DICAPRIO).isPresent());
        Assertions.assertTrue(vetRepository.findByContactInformation(VET_DICAPRIO_EMAIL).isPresent());
    }

    @Test
    public void canVerifyVetAfterSignup() throws Exception {
        // given
        Vet dicaprio = helper.signupVetDiCaprioWithHeart();

        // when
        AuthMessage msg = verifyMsgWasSent(dicaprio.getContactInformation());
        userController.perform(userController.verifyUserWithLink(msg.getLink()))
        // then
                .andExpect(status().is2xxSuccessful());

        Vet saved = vetRepository.findByLastName(VET_DICAPRIO).get();
        Assertions.assertFalse(saved.getRoles().contains(AuthRoles.UNVERIFIED));
    }

    @Test
    public void unverifiedVetCanReadPets() throws Exception {
        // given
        helper.signupVetDiCaprioWithHeart();
        helper.signupKahnWithBella();
        Pet bella = petRepository.findByName(BELLA).get();

        // when
        String token = userController.login2xx(VET_DICAPRIO_EMAIL, VET_DICAPRIO_PASSWORD);

        mvc.perform(petController.find(BELLA)
                .header(HttpHeaders.AUTHORIZATION, token))
        // then
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void verifiedVetCanCreateVisit() throws Exception {
        // given
        Vet dicaprio = helper.signupVetDiCaprioWithHeartAndVerify();
        Owner kahn = helper.signupKahnWithBella();
        Pet bella = petRepository.findByName(BELLA).get();


        // when
        Visit visit = testData.getCheckTeethVisit();
        visit.setVet(dicaprio);
        visit.setOwner(kahn);
        visit.getPets().add(bella);
        CreateVisitDto dto = new CreateVisitDto(visit);
        String token = userController.login2xx(VET_DICAPRIO_EMAIL, VET_DICAPRIO_PASSWORD);
        ReadVisitDto response = visitController.create2xx(dto, token);

        // then
        Assertions.assertNotNull(response.getId());
        Assertions.assertEquals(visit.getReason(),response.getReason());
    }

    @Test
    public void unverifiedVetCantCreateVisit() throws Exception {
        // given
        Vet dicaprio = helper.signupVetDiCaprioWithHeart();
        Owner kahn = helper.signupKahnWithBella();
        Pet bella = petRepository.findByName(BELLA).get();

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
        Assertions.assertTrue(visitRepository.findAll().isEmpty());
    }

    @Test
    public void givenSelectedPetsDoNotBelongToSelectedOwner_whenCreatingVisit_thenForbidden() throws Exception {
        // given
        Vet dicaprio = helper.signupVetDiCaprioWithHeartAndVerify();
        Owner kahn = helper.signupKahnWithBella();  // kahn is linked to bella not bello
        Owner meier = helper.signupMeierWithBello();
        Pet bello = petRepository.findByName(BELLO).get();

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
        Assertions.assertTrue(visitRepository.findAll().isEmpty());
    }

    @Test
    public void verifiedVetCanUpdatePetsIllnesses() throws Exception {
        // given
        Vet dicaprio = helper.signupVetDiCaprioWithHeartAndVerify();
        Owner kahn = helper.signupKahnWithBella();
        Pet bella = petRepository.findByName(BELLA).get();
        Illness teethPain = illnessRepository.save(testData.getTeethPain());

        // when
        UpdateIllnessDto dto = UpdateIllnessDto.builder()
                .id(bella.getId())
                .illnessName(TEETH_PAIN)
                .build();
        String token = userController.login2xx(VET_DICAPRIO_EMAIL, VET_DICAPRIO_PASSWORD);
        VetReadsPetDto responsePetDto = petController.perform2xxAndDeserialize(
                petController.addIllness(dto)
                .header(HttpHeaders.AUTHORIZATION, token), VetReadsPetDto.class);
        Assertions.assertFalse(responsePetDto.getIllnessIds().isEmpty());

        // then
        Pet updatedBella = petRepository.findByName(BELLA).get();
        Assertions.assertTrue(updatedBella.getIllnesses().stream()
                .anyMatch(i -> i.getName().equals(teethPain.getName())));
    }

}
