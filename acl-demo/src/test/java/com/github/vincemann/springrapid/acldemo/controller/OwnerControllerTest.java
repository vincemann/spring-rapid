package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acldemo.MyRoles;
import com.github.vincemann.springrapid.acldemo.dto.owner.CreateOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.owner.FullOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.pet.FullPetDto;
import com.github.vincemann.springrapid.acldemo.dto.pet.OwnerCreatesPetDto;
import com.github.vincemann.springrapid.acldemo.dto.user.UUIDSignupResponseDto;
import com.github.vincemann.springrapid.acldemo.model.*;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.auth.dto.SignupDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import java.util.Optional;

import static com.github.vincemann.ezcompare.Comparator.compare;
import static com.github.vincemann.ezcompare.PropertyMatchers.propertyAssert;
import static com.github.vincemann.springrapid.coretest.util.RapidTestUtil.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Tag(value = "demo-projects")
public class OwnerControllerTest extends MyControllerIntegrationTest {



    @Test
    public void canRegisterOwner() throws Exception {
        SignupDto signupDto = SignupDto.builder()
                .contactInformation(OWNER_KAHN_CONTACT_INFORMATION)
                .password(OWNER_KAHN_PASSWORD)
                .build();
        UUIDSignupResponseDto signedUpDto = performDs2xx(userController.signup(signupDto), UUIDSignupResponseDto.class);
        String uuid = signedUpDto.getUuid();
        Assertions.assertNotNull(uuid);

        Optional<User> byUuid = userService.findByUuid(uuid);
        Assertions.assertTrue(byUuid.isPresent());


        CreateOwnerDto createOwnerDto = new CreateOwnerDto(kahn, uuid);
        FullOwnerDto createdDto = performDs2xx(ownerController.create(createOwnerDto), FullOwnerDto.class);

        compare(createOwnerDto).with(createdDto)
                .properties()
                .all()
                .ignore(createOwnerDto::getId)
                .ignore(createOwnerDto::getUuid)
                .assertEqual();

        Assertions.assertEquals(FullOwnerDto.DIRTY_SECRET, createdDto.getDirtySecret());

        byUuid = userService.findByUuid(uuid);
        Assertions.assertFalse(byUuid.isPresent());

        Optional<User> kahnUserByContactInformation = userService.findByContactInformation(OWNER_KAHN_CONTACT_INFORMATION);
        Assertions.assertTrue(kahnUserByContactInformation.isPresent());
        User dbUserKahn = kahnUserByContactInformation.get();

        propertyAssert(dbUserKahn)
                .assertContains(dbUserKahn::getRoles, MyRoles.OWNER, AuthRoles.UNVERIFIED, AuthRoles.USER)
                .assertSize(dbUserKahn::getRoles, 3)
                .assertEquals(dbUserKahn::getContactInformation, OWNER_KAHN_CONTACT_INFORMATION)
                .assertNotNull(dbUserKahn::getPassword)
                .assertNull(dbUserKahn::getUuid);


        Optional<Owner> kahnByLastName = ownerService.findByLastName(OWNER_KAHN);
        Assertions.assertTrue(kahnByLastName.isPresent());
        Owner dbKahn = kahnByLastName.get();

        Assertions.assertEquals(dbUserKahn, dbKahn.getUser());

    }

    @Test
    public void canSavePetToOwnAccount() throws Exception {
        Owner dbKahn = registerOwner(kahn, OWNER_KAHN_CONTACT_INFORMATION, OWNER_KAHN_PASSWORD);
        String token = userController.login2xx(dbKahn.getUser().getContactInformation(), OWNER_KAHN_PASSWORD);
        OwnerCreatesPetDto createPetDto = new OwnerCreatesPetDto(bella, dbKahn.getId());
        FullPetDto createdPet = performDs2xx(petController.create(createPetDto)
                .header(HttpHeaders.AUTHORIZATION, token), FullPetDto.class);
        Assertions.assertEquals(dbKahn.getId(), createdPet.getOwnerId());
        assertOwnerHasPets(OWNER_KAHN, BELLA);
        assertPetHasOwner(BELLA, OWNER_KAHN);

    }

    @Test
    public void cantSavePetToOtherOwner() throws Exception {
        Owner dbKahn = registerOwner(kahn, OWNER_KAHN_CONTACT_INFORMATION, OWNER_KAHN_PASSWORD);
        Owner dbMeier = registerOwner(meier, OWNER_MEIER_CONTACT_INFORMATION, OWNER_MEIER_PASSWORD);

        String token = userController.login2xx(OWNER_KAHN_CONTACT_INFORMATION, OWNER_KAHN_PASSWORD);
        OwnerCreatesPetDto createPetDto = new OwnerCreatesPetDto(bella, dbMeier.getId());
        mvc.perform(petController.create(createPetDto)
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isForbidden());

        Assertions.assertFalse(petRepository.findByName(BELLA).isPresent());
    }

    @Test
    public void canUpdateOwnPetsPetType() throws Exception {
        String token = registerOwnerWithPets(kahn, OWNER_KAHN_CONTACT_INFORMATION, OWNER_KAHN_PASSWORD, bella);
        Pet dbBella = petRepository.findByName(BELLA).get();
        Owner dbOwner = ownerRepository.findByLastName(OWNER_KAHN).get();
        String updateJson = createUpdateJsonRequest(
                createUpdateJsonLine("replace", "/petTypeId", savedCatPetType.getId().toString())
        );
        FullPetDto updatedPetDto = performDs2xx(petController.update(updateJson, dbBella.getId().toString())
                        .header(HttpHeaders.AUTHORIZATION, token),
                        FullPetDto.class);

        dbBella = petRepository.findByName(BELLA).get();
        com.github.vincemann.springrapid.acldemo.model.PetType dbCatType = petTypeRepository.findById(savedCatPetType.getId()).get();
        Assertions.assertEquals(dbCatType, dbBella.getPetType());
    }



    @Test
    public void ownerCantUpdateOwnPetsIllness() throws Exception {
        String token = registerOwnerWithPets(kahn, OWNER_KAHN_CONTACT_INFORMATION, OWNER_KAHN_PASSWORD, bella);
        Pet dbBella = petRepository.findByName(BELLA).get();
        Illness teethPain = illnessRepository.save(this.teethPain);

        Owner dbOwner = ownerRepository.findByLastName(OWNER_KAHN).get();
        String updateJson = createUpdateJsonRequest(
                createUpdateJsonLine("add", "/illnessIds", savedDogPetType.getId().toString())
        );
        mvc.perform(petController.update(updateJson, dbBella.getId().toString())
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isForbidden());

        com.github.vincemann.springrapid.acldemo.model.PetType dbDgoType = petTypeRepository.findById(savedDogPetType.getId()).get();
        Assertions.assertEquals(dbDgoType, dbBella.getPetType());
    }



    @Test
    public void ownerCantUpdateForeignPet() throws Exception {
        // kahn -> bella
        // meier -> kitty
        String kahnToken = registerOwnerWithPets(kahn, OWNER_KAHN_CONTACT_INFORMATION, OWNER_KAHN_PASSWORD, bella);
        String meierToken = registerOwnerWithPets(meier, OWNER_MEIER_CONTACT_INFORMATION, OWNER_MEIER_PASSWORD, kitty);

        Pet dbKitty = petRepository.findByName(KITTY).get();
        Owner dbOwner = ownerRepository.findByLastName(OWNER_KAHN).get();


        String updateJson = createUpdateJsonRequest(
                createUpdateJsonLine("replace", "/petTypeId", savedDogPetType.getId().toString())
        );
        mvc.perform(petController.update(updateJson, dbKitty.getId().toString())
                        .header(HttpHeaders.AUTHORIZATION, kahnToken))
                .andExpect(status().isForbidden());

        com.github.vincemann.springrapid.acldemo.model.PetType dbCatType = petTypeRepository.findById(savedCatPetType.getId()).get();
        Assertions.assertEquals(dbCatType, dbKitty.getPetType());
    }

    @Test
    public void ownerCanReadOwnPet() throws Exception {
        String ownerToken = registerOwnerWithPets(kahn, OWNER_KAHN_CONTACT_INFORMATION, OWNER_KAHN_PASSWORD, bella);
        Pet dbBella = petRepository.findByName(BELLA).get();
        FullPetDto fullPetDto = performDs2xx(petController.find(dbBella.getId().toString())
                .header(HttpHeaders.AUTHORIZATION, ownerToken),
                FullPetDto.class);

        compare(fullPetDto).with(dbBella)
                .properties()
                .all()
                .ignore(dtoIdProperties(FullPetDto.class))
                .assertEqual();
    }

    @Test
    public void ownerCantReadForeignPet() throws Exception {
        String ownerToken = registerOwnerWithPets(kahn, OWNER_KAHN_CONTACT_INFORMATION, OWNER_KAHN_PASSWORD, bella);
        String meierToken = registerOwnerWithPets(meier, OWNER_MEIER_CONTACT_INFORMATION, OWNER_MEIER_PASSWORD, kitty);

        Pet dbBella = petRepository.findByName(BELLA).get();
        Pet dbKitty = petRepository.findByName(KITTY).get();
        mvc.perform(petController.find(dbKitty.getId().toString())
                        .header(HttpHeaders.AUTHORIZATION, ownerToken))
                .andExpect(status().isForbidden());
    }






}
