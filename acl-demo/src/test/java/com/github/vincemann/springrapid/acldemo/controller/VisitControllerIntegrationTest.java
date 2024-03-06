package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acldemo.controller.suite.MyIntegrationTest;
import com.github.vincemann.springrapid.acldemo.dto.visit.CreateVisitDto;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.acldemo.model.Vet;
import com.github.vincemann.springrapid.acldemo.model.Visit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import static com.github.vincemann.springrapid.acldemo.controller.suite.TestData.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag(value = "demo-projects")
public class VisitControllerIntegrationTest extends MyIntegrationTest {


    @Test
    public void vetCantCreateVisitForOtherVet() throws Exception {
        // given
        Owner kahn = helper.signupKahnWithBella();
        Vet dicaprio = helper.signupVetDiCaprioWithHeartAndVerify();
        Vet max = helper.signupVetMaxWithDentismAndVerify();
        Pet bella = petService.findByName(BELLA).get();

        // when
        String dicaprioToken = userController.login2xx(VET_DICAPRIO_EMAIL, VET_DICAPRIO_PASSWORD);
        CreateVisitDto createVisitDto = new CreateVisitDto(testData.getCheckTeethVisit());
        createVisitDto.setOwnerId(kahn.getId());
        createVisitDto.getPetIds().add(bella.getId());
        createVisitDto.setVetId(max.getId());
        mvc.perform(visitController.create(createVisitDto)
                        .header(HttpHeaders.AUTHORIZATION,dicaprioToken))
        // then
                .andExpect(status().isForbidden());

        Assertions.assertTrue(visitService.findAll().isEmpty());
    }

    @Test
    public void ownerCantCreateVisit() throws Exception {
        // given
        Owner kahn = helper.signupKahnWithBella();
        Vet dicaprio = helper.signupVetDiCaprioWithHeartAndVerify();
        Pet bella = petService.findByName(BELLA).get();

        // when
        String ownerKahnToken = userController.login2xx(OWNER_KAHN_EMAIL, OWNER_KAHN_PASSWORD);
        CreateVisitDto createVisitDto = new CreateVisitDto(testData.getCheckTeethVisit());
        createVisitDto.setOwnerId(kahn.getId());
        createVisitDto.getPetIds().add(bella.getId());
        createVisitDto.setVetId(dicaprio.getId());
        mvc.perform(visitController.create(createVisitDto)
                .header(HttpHeaders.AUTHORIZATION,ownerKahnToken))
        // then
                        .andExpect(status().isForbidden());

        Assertions.assertTrue(visitService.findAll().isEmpty());
    }

    @Test
    public void vetCanReadForeignVisit() throws Exception {
        // given
        Owner kahn = helper.signupKahnWithBella();
        Vet dicaprio = helper.signupVetDiCaprioWithHeartAndVerify();
        Vet max = helper.signupVetMaxWithDentismAndVerify();
        Pet bella = petService.findByName(BELLA).get();
        String dicaprioToken = userController.login2xx(VET_DICAPRIO_EMAIL, VET_DICAPRIO_PASSWORD);
        Visit visit = helper.createVisit(dicaprioToken, testData.getCheckTeethVisit(), kahn, dicaprio, bella);

        // when
        String maxToken = userController.login2xx(VET_MAX_EMAIL, VET_MAX_PASSWORD);
        mvc.perform(visitController.find(visit.getId().toString())
                .header(HttpHeaders.AUTHORIZATION,maxToken))
        // then
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void ownerCanReadOwnVisit() throws Exception {
        // given
        Owner kahn = helper.signupKahnWithBella();
        Vet dicaprio = helper.signupVetDiCaprioWithHeartAndVerify();
        Pet bella = petService.findByName(BELLA).get();
        String dicaprioToken = userController.login2xx(VET_DICAPRIO_EMAIL, VET_DICAPRIO_PASSWORD);
        Visit visit = helper.createVisit(dicaprioToken, testData.getCheckTeethVisit(), kahn, dicaprio, bella);

        // when
        String kahnToken = userController.login2xx(OWNER_KAHN_EMAIL, OWNER_KAHN_PASSWORD);
        visitController.perform(visitController.find(visit.getId().toString())
                        .header(HttpHeaders.AUTHORIZATION, kahnToken))
        // then
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void ownerCantReadForeignVisit() throws Exception {
        // given
        Owner kahn = helper.signupKahnWithBella();
        Owner meier = helper.signupMeierWithBello();
        Vet dicaprio = helper.signupVetDiCaprioWithHeartAndVerify();
        Pet bella = petService.findByName(BELLA).get();
        String dicaprioToken = userController.login2xx(VET_DICAPRIO_EMAIL, VET_DICAPRIO_PASSWORD);
        Visit visit = helper.createVisit(dicaprioToken, testData.getCheckTeethVisit(), kahn, dicaprio, bella);

        // when
        String meierToken = userController.login2xx(OWNER_MEIER_EMAIL, OWNER_MEIER_PASSWORD);
        mvc.perform(visitController.find(visit.getId().toString())
                .header(HttpHeaders.AUTHORIZATION,meierToken))
        // then
                .andExpect(status().isForbidden());
    }

    @Test
    public void vetCanAddSpectatingOwnerToVisit() throws Exception {
        // given
        Owner kahn = helper.signupKahnWithBella();
        Owner meier = helper.signupMeierWithBello();
        Vet dicaprio = helper.signupVetDiCaprioWithHeartAndVerify();
        Pet bella = petService.findByName(BELLA).get();
        String dicaprioToken = userController.login2xx(VET_DICAPRIO_EMAIL, VET_DICAPRIO_PASSWORD);
        Visit visit = helper.createVisit(dicaprioToken, testData.getCheckTeethVisit(), kahn, dicaprio, bella);
        String meierToken = userController.login2xx(meier.getContactInformation(), OWNER_MEIER_PASSWORD);

        // meier cant read visit yet
        mvc.perform(visitController.find(visit.getId())
                        .header(HttpHeaders.AUTHORIZATION,meierToken))
                .andExpect(status().isForbidden());

        // when
        // dicaprio allows meier to spectate visit
        mvc.perform(visitController.addSpectator(visit.getId(),meier.getId())
                        .header(HttpHeaders.AUTHORIZATION,dicaprioToken))
        // then
                .andExpect(status().is2xxSuccessful());

        // now meier should be able to read visit
        mvc.perform(visitController.find(visit.getId())
                .header(HttpHeaders.AUTHORIZATION,meierToken))
                .andExpect(status().is2xxSuccessful());
    }


    @Test
    public void vetCantAddSpectatorToNonHostedVisit() throws Exception {
        // given
        Owner kahn = helper.signupKahnWithBella();
        Vet dicaprio = helper.signupVetDiCaprioWithHeartAndVerify();
        Vet max = helper.signupVetMaxWithDentismAndVerify();
        Pet bella = petService.findByName(BELLA).get();
        Owner meier = helper.signupMeierWithBello();
        String dicaprioToken = userController.login2xx(VET_DICAPRIO_EMAIL, VET_DICAPRIO_PASSWORD);
        Visit visit = helper.createVisit(dicaprioToken, testData.getCheckTeethVisit(), kahn, dicaprio, bella);
        String maxToken = userController.login2xx(max.getContactInformation(), VET_MAX_PASSWORD);
        String meierToken = userController.login2xx(meier.getContactInformation(), OWNER_MEIER_PASSWORD);

        // meier cant read visit
        mvc.perform(visitController.find(visit.getId())
                        .header(HttpHeaders.AUTHORIZATION,meierToken))
                .andExpect(status().isForbidden());

        // when
        mvc.perform(visitController.addSpectator(visit.getId(),meier.getId())
                        .header(HttpHeaders.AUTHORIZATION,maxToken))
        // then
                .andExpect(status().isForbidden());

        // meier still cant read visit
        mvc.perform(visitController.find(visit.getId())
                        .header(HttpHeaders.AUTHORIZATION,meierToken))
                .andExpect(status().isForbidden());
    }

    @Test
    public void vetCanRemoveSpectatorFromVisit() throws Exception {
        // given
        Owner kahn = helper.signupKahnWithBella();
        Owner meier = helper.signupMeierWithBello();
        Vet dicaprio = helper.signupVetDiCaprioWithHeartAndVerify();
        Pet bella = petService.findByName(BELLA).get();
        String dicaprioToken = userController.login2xx(VET_DICAPRIO_EMAIL, VET_DICAPRIO_PASSWORD);
        Visit visit = helper.createVisit(dicaprioToken, testData.getCheckTeethVisit(), kahn, dicaprio, bella);
        String meierToken = userController.login2xx(meier.getContactInformation(), OWNER_MEIER_PASSWORD);
        mvc.perform(visitController.addSpectator(visit.getId(),meier.getId())
                        .header(HttpHeaders.AUTHORIZATION,dicaprioToken))
                .andExpect(status().is2xxSuccessful());

        // now meier is able to read visit
        mvc.perform(visitController.find(visit.getId())
                        .header(HttpHeaders.AUTHORIZATION,meierToken))
                .andExpect(status().is2xxSuccessful());

        // when
        // dicaprio decides meier should not be able to spectate visit anymore
        mvc.perform(visitController.removeSpectator(visit.getId(),meier.getId())
                        .header(HttpHeaders.AUTHORIZATION,dicaprioToken))
                .andExpect(status().is2xxSuccessful());

        // then
        // meier cant read visit anymore
        mvc.perform(visitController.find(visit.getId())
                        .header(HttpHeaders.AUTHORIZATION,meierToken))
                .andExpect(status().isForbidden());
    }


}
