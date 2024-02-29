package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acldemo.controller.suite.MyIntegrationTest;
import com.github.vincemann.springrapid.acldemo.dto.owner.ReadOwnOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.owner.SignupOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.pet.CreatePetDto;
import com.github.vincemann.springrapid.acldemo.dto.pet.ReadPetDto;
import com.github.vincemann.springrapid.acldemo.model.Illness;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.model.Pet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import static com.github.vincemann.springrapid.coretest.util.RapidTestUtil.createUpdateJsonLine;
import static com.github.vincemann.springrapid.coretest.util.RapidTestUtil.createUpdateJsonRequest;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Tag(value = "demo-projects")
public class OwnerControllerTest extends MyIntegrationTest {



    @Test
    public void signupOwner() throws Exception {
        SignupOwnerDto dto = new SignupOwnerDto(kahn);

        ReadOwnOwnerDto response = ownerController.signup(dto);
        Assertions.assertEquals(Owner.DIRTY_SECRET, response.getDirtySecret());

        Assertions.assertTrue(ownerService.findByLastName(OWNER_KAHN).isPresent());
        Assertions.assertTrue(ownerService.findByContactInformation(OWNER_KAHN_EMAIL).isPresent());
    }

    @Test
    public void ownerSavesPet() throws Exception {
        // given
        Owner kahn = signupOwner(this.kahn);

        // when
        String token = userController.login2xx(OWNER_KAHN_EMAIL, OWNER_KAHN_PASSWORD);
        CreatePetDto createPetDto = new CreatePetDto(bella);
        createPetDto.setOwnerId(kahn.getId());
        ReadPetDto pet = performDs2xx(petController.create(createPetDto)
                .header(HttpHeaders.AUTHORIZATION, token), ReadPetDto.class);

        // then
        Assertions.assertEquals(kahn.getId(), pet.getOwnerId());
        assertOwnerHasPets(OWNER_KAHN, BELLA);
        assertPetHasOwner(BELLA, OWNER_KAHN);
    }

    @Test
    public void cantSavePetToOtherOwner() throws Exception {
        // given
        Owner kahn = signupOwner(this.kahn);
        Owner meier = signupOwner(this.meier);

        // when
        String kahnToken = userController.login2xx(OWNER_KAHN_EMAIL, OWNER_KAHN_PASSWORD);
        CreatePetDto createPetDto = new CreatePetDto(bella);
        createPetDto.setOwnerId(meier.getId()); // using diff owners id here
        mvc.perform(petController.create(createPetDto)
                .header(HttpHeaders.AUTHORIZATION, kahnToken))
        // then
                .andExpect(status().isForbidden());

        Assertions.assertFalse(petRepository.findByName(BELLA).isPresent());
    }

    @Test
    public void canUpdateOwnPetsName() throws Exception {
        // given
        kahn.getPets().add(bella);
        Owner kahn = signupOwner(this.kahn);
        Pet dbBella = petRepository.findByName(BELLA).get();

        // when
        String updateJson = createUpdateJsonRequest(
                createUpdateJsonLine("replace", "/name", "newName")
        );
        String token = userController.login2xx(OWNER_KAHN_EMAIL, OWNER_KAHN_PASSWORD);
        ReadPetDto updatedPetDto = performDs2xx(petController.update(updateJson, dbBella.getId().toString())
                        .header(HttpHeaders.AUTHORIZATION, token),
                ReadPetDto.class);

        // then
        Pet updatedBella = petRepository.findByName(BELLA).get();
        Assertions.assertEquals("newName",updatedBella.getName());
        Assertions.assertEquals("newName",updatedPetDto.getName());
    }


    @Test
    public void ownerCantUpdateOwnPetsIllness() throws Exception {
        // given
        kahn.getPets().add(bella);
        Owner kahn = signupOwner(this.kahn);
        Pet dbBella = petRepository.findByName(BELLA).get();
        Illness teethPain = illnessRepository.save(this.teethPain);

        // when
        String updateJson = createUpdateJsonRequest(
                createUpdateJsonLine("add", "/illnessIds", teethPain.getId().toString())
        );
        String token = userController.login2xx(OWNER_KAHN_EMAIL, OWNER_KAHN_PASSWORD);
        mvc.perform(petController.update(updateJson, dbBella.getId().toString())
                        .header(HttpHeaders.AUTHORIZATION, token))
        // then
                .andExpect(status().isForbidden());

        dbBella = petRepository.findByName(BELLA).get();
        Assertions.assertTrue(dbBella.getIllnesss().isEmpty());
    }



    @Test
    public void ownerCantUpdateForeignPet() throws Exception {
        // kahn -> bella
        // meier -> kitty
        // given
        Pet bella = petService.create(this.bella);
        Pet kitty = petService.create(this.kitty);

        kahn.getPets().add(bella);
        Owner kahn = signupOwner(this.kahn);
        meier.getPets().add(kitty);
        Owner meier = signupOwner(this.meier);


        // when
        String updateJson = createUpdateJsonRequest(
                createUpdateJsonLine("replace", "/name", kitty.getId().toString())
        );
        String token = userController.login2xx(OWNER_KAHN_EMAIL, OWNER_KAHN_PASSWORD);
        mvc.perform(petController.update(updateJson, kitty.getId().toString())
                        .header(HttpHeaders.AUTHORIZATION, token))
        // then
                .andExpect(status().isForbidden());

        Pet dbKitty = petService.findById(kitty.getId()).get();
        Assertions.assertEquals(KITTY, dbKitty.getName());
    }

    @Test
    public void ownerCanReadOwnPet() throws Exception {
        // given
        Pet bella = petService.create(this.bella);
        kahn.getPets().add(bella);
        Owner kahn = signupOwner(this.kahn);

        // when
        String token = userController.login2xx(OWNER_KAHN_EMAIL, OWNER_KAHN_PASSWORD);
        ReadPetDto dto = performDs2xx(petController.find(bella.getId())
                .header(HttpHeaders.AUTHORIZATION, token),
                ReadPetDto.class);

        // then
        Assertions.assertEquals(KITTY,dto.getName());
    }

    @Test
    public void ownerCantReadForeignPet() throws Exception {
        // kahn -> bella
        // meier -> kitty
        // given
        Pet bella = petService.create(this.bella);
        Pet kitty = petService.create(this.kitty);

        kahn.getPets().add(bella);
        signupOwner(this.kahn);
        meier.getPets().add(kitty);
        signupOwner(this.meier);

        // when
        String ownerToken = userController.login2xx(OWNER_KAHN_EMAIL, OWNER_KAHN_PASSWORD);
        mvc.perform(petController.find(kitty.getId().toString())
                        .header(HttpHeaders.AUTHORIZATION, ownerToken))
        // then
                .andExpect(status().isForbidden());
    }






}
