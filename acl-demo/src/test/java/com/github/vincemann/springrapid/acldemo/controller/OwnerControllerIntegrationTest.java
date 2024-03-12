package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acldemo.controller.suite.MyIntegrationTest;
import com.github.vincemann.springrapid.acldemo.dto.owner.ReadOwnOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.owner.SignupOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.pet.CreatePetDto;
import com.github.vincemann.springrapid.acldemo.dto.pet.OwnerReadsForeignPetDto;
import com.github.vincemann.springrapid.acldemo.dto.pet.OwnerReadsOwnPetDto;
import com.github.vincemann.springrapid.acldemo.model.Illness;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.model.Pet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import static com.github.vincemann.springrapid.acldemo.controller.suite.TestData.*;
import static com.github.vincemann.springrapid.coretest.util.RapidTestUtil.createUpdateJsonLine;
import static com.github.vincemann.springrapid.coretest.util.RapidTestUtil.createUpdateJsonRequest;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Tag(value = "demo-projects")
public class OwnerControllerIntegrationTest extends MyIntegrationTest {



    @Test
    public void signupOwner() throws Exception {
        // when
        SignupOwnerDto dto = new SignupOwnerDto(testData.getKahn());
        ReadOwnOwnerDto response = ownerController.signup(dto);

        // then
        Assertions.assertEquals(Owner.SECRET, response.getSecret());
        Assertions.assertTrue(ownerService.findByLastName(OWNER_KAHN).isPresent());
        Assertions.assertTrue(ownerService.findByContactInformation(OWNER_KAHN_EMAIL).isPresent());
    }

    @Test
    public void ownerSavesPet() throws Exception {
        // given
        Owner kahn = helper.signupOwner(testData.getKahn());

        // when
        String token = userController.login2xx(OWNER_KAHN_EMAIL, OWNER_KAHN_PASSWORD);
        CreatePetDto createPetDto = new CreatePetDto(testData.getBella());
        createPetDto.setOwnerId(kahn.getId());
        OwnerReadsOwnPetDto responseDto = petController.create2xx(createPetDto,token,OwnerReadsOwnPetDto.class);

        // then
        Assertions.assertEquals(kahn.getId(), responseDto.getOwnerId());
        assertOwnerHasPets(OWNER_KAHN, BELLA);
        assertPetHasOwner(BELLA, OWNER_KAHN);
    }

    @Test
    public void cantSavePetToOtherOwner() throws Exception {
        // given
        Owner kahn = helper.signupOwner(testData.getKahn());
        Owner meier = helper.signupOwner(testData.getMeier());

        // when
        String kahnToken = userController.login2xx(OWNER_KAHN_EMAIL, OWNER_KAHN_PASSWORD);
        CreatePetDto createPetDto = new CreatePetDto(testData.getBella());
        createPetDto.setOwnerId(meier.getId()); // using diff owners id here
        mvc.perform(petController.create(createPetDto)
                .header(HttpHeaders.AUTHORIZATION, kahnToken))
        // then
                .andExpect(status().isForbidden());

        Assertions.assertFalse(petService.findByName(BELLA).isPresent());
    }

    @Test
    public void ownerCantFindForeignOwnerByName() throws Exception {
        // given
        Owner kahn = helper.signupKahnWithBella();
        Owner meier = helper.signupMeierWithBello();

        // when
        String kahnToken = userController.login2xx(kahn.getContactInformation(), OWNER_KAHN_PASSWORD);
        mvc.perform(get(ownerController.getController().getFindByNameUrl())
                .param("name",meier.getLastName())
                        .header(HttpHeaders.AUTHORIZATION,kahnToken))
                // then
                .andExpect(status().isForbidden());
    }

    @Test
    public void canUpdateOwnPetsName() throws Exception {
        // given
        Owner kahn = helper.signupKahnWithBella();
        Pet bella = petService.findByName(BELLA).get();

        // when
        String updateJson = createUpdateJsonRequest(
                createUpdateJsonLine("replace", "/name", "newName")
        );
        String token = userController.login2xx(OWNER_KAHN_EMAIL, OWNER_KAHN_PASSWORD);
        OwnerReadsOwnPetDto updatedPetDto
                = petController.update2xx(updateJson, bella.getId(),token,OwnerReadsOwnPetDto.class);

        // then
        Pet updatedBella = petService.findByName("newName").get();
        Assertions.assertEquals("newName",updatedBella.getName());
        Assertions.assertEquals("newName",updatedPetDto.getName());
    }


    @Test
    public void ownerCantUpdateOwnPetsIllness() throws Exception {
        // given
        Owner kahn = helper.signupKahnWithBella();
        Pet bella = petService.findByName(BELLA).get();
        Illness teethPain = illnessService.create(testData.getTeethPain());

        // when
        String updateJson = createUpdateJsonRequest(
                createUpdateJsonLine("add", "/illnessIds", teethPain.getId().toString())
        );
        String token = userController.login2xx(OWNER_KAHN_EMAIL, OWNER_KAHN_PASSWORD);
        mvc.perform(petController.update(updateJson, bella.getId().toString())
                        .header(HttpHeaders.AUTHORIZATION, token))
        // then
                .andExpect(status().isForbidden());

        bella = petService.findByName(BELLA).get();
        Assertions.assertTrue(bella.getIllnesss().isEmpty());
    }



    @Test
    public void ownerCantUpdateForeignPet() throws Exception {
        // kahn -> bella
        // meier -> bello
        // given
        Owner kahn = helper.signupKahnWithBella();
        Owner meier = helper.signupMeierWithBello();
        Pet bello = petService.findByName(BELLO).get();


        // when
        String updateJson = createUpdateJsonRequest(
                createUpdateJsonLine("replace", "/name", bello.getId().toString())
        );
        String token = userController.login2xx(OWNER_KAHN_EMAIL, OWNER_KAHN_PASSWORD);
        mvc.perform(petController.update(updateJson, bello.getId().toString())
                        .header(HttpHeaders.AUTHORIZATION, token))
        // then
                .andExpect(status().isForbidden());

        Pet dbKitty = petService.findById(bello.getId()).get();
        Assertions.assertEquals(BELLO, dbKitty.getName());
    }

    @Test
    public void ownerCanReadOwnPet() throws Exception {
        // given
        // bella saved with illness teeth pain
        testData.getBella().getIllnesss().add(illnessService.create(testData.getTeethPain()));
        Owner kahn = helper.signupKahnWithBella();
        Pet bella = petService.findByName(BELLA).get();


        // when
        String token = userController.login2xx(OWNER_KAHN_EMAIL, OWNER_KAHN_PASSWORD);
        OwnerReadsOwnPetDto dto = petController.perform2xxAndDeserialize(petController.find(bella.getId())
                .header(HttpHeaders.AUTHORIZATION, token),
                OwnerReadsOwnPetDto.class);

        // then
        Assertions.assertEquals(BELLA,dto.getName());
        Assertions.assertFalse(dto.getIllnessIds().isEmpty());
    }

    @Test
    public void givenOwnerIsNotPetSpectator_ownerCantReadForeignPet() throws Exception {
        // kahn -> bella
        // meier -> bello
        // given
        Owner kahn = helper.signupKahnWithBella();
        Owner meier = helper.signupMeierWithBello();
        Pet bello = petService.findByName(BELLO).get();

        // when
        String ownerToken = userController.login2xx(OWNER_KAHN_EMAIL, OWNER_KAHN_PASSWORD);
        mvc.perform(petController.find(bello.getId().toString())
                        .header(HttpHeaders.AUTHORIZATION, ownerToken))
        // then
                .andExpect(status().isForbidden());
    }

    @Test
    public void givenForeignOwnerWasAddedAsPetSpectator_ownerCanReadForeignPet() throws Exception {
        // kahn -> bella
        // meier -> bello
        // bella saved with illness teeth pain
        testData.getBella().getIllnesss().add(illnessService.create(testData.getTeethPain()));
        Owner kahn = helper.signupKahnWithBella();
        Pet bella = petService.findByName(BELLA).get();

        // bello saved with teeth pain as well for owner meier
        testData.getBello().getIllnesss().add(illnessService.findByName(TEETH_PAIN).get());
        Owner meier = helper.signupMeierWithBello();
        Pet bello = petService.findByName(BELLO).get();

        // kahn gives meier permission to watch its pets bella
        String kahnToken = userController.login2xx(OWNER_KAHN_EMAIL, OWNER_KAHN_PASSWORD);
        ownerController.addPetsSpectator(meier.getId(),kahn.getId(),kahnToken);

        // when
        String meierToken = userController.login2xx(OWNER_MEIER_EMAIL, OWNER_MEIER_PASSWORD);
        String json = petController.perform(petController.find(bella.getId())
                        .header(HttpHeaders.AUTHORIZATION, meierToken))
        // then
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("illnesss").doesNotExist())
                .andReturn().getResponse().getContentAsString();
        OwnerReadsForeignPetDto dto = petController.deserialize(json, OwnerReadsForeignPetDto.class);
        Assertions.assertEquals(BELLA,dto.getName());
    }
}
