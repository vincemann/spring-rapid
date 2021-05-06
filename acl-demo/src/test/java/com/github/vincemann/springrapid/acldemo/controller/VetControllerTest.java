package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acldemo.auth.MyRoles;
import com.github.vincemann.springrapid.acldemo.dto.owner.CreateOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.owner.FullOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.pet.FullPetDto;
import com.github.vincemann.springrapid.acldemo.dto.user.FullUserDto;
import com.github.vincemann.springrapid.acldemo.dto.user.UUIDSignupResponseDto;
import com.github.vincemann.springrapid.acldemo.dto.vet.CreateVetDto;
import com.github.vincemann.springrapid.acldemo.dto.vet.FullVetDto;
import com.github.vincemann.springrapid.acldemo.model.*;
import com.github.vincemann.springrapid.acldemo.service.VetService;
import com.github.vincemann.springrapid.auth.domain.AuthRoles;
import com.github.vincemann.springrapid.auth.domain.dto.SignupDto;
import com.github.vincemann.springrapid.coretest.util.RapidTestUtil;
import org.hibernate.usertype.UserType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import java.util.Optional;

import static com.github.vincemann.ezcompare.Comparator.compare;
import static com.github.vincemann.springrapid.coretest.service.PropertyMatchers.propertyAssert;
import static com.github.vincemann.springrapid.coretest.util.RapidTestUtil.createUpdateJsonLine;
import static com.github.vincemann.springrapid.coretest.util.RapidTestUtil.createUpdateJsonRequest;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class VetControllerTest extends AbstractControllerIntegrationTest<VetController, VetService> {


    @Test
    public void canRegisterVet() throws Exception {
        SignupDto signupDto = SignupDto.builder()
                .email(VET_DICAPRIO_EMAIL)
                .password(VET_DICAPRIO_PASSWORD)
                .build();
        UUIDSignupResponseDto signedUpDto = perform2xx(userController.signup(signupDto), UUIDSignupResponseDto.class);
        String uuid = signedUpDto.getUuid();
        Assertions.assertNotNull(uuid);

        Optional<User> byUuid = userService.findByUuid(uuid);
        Assertions.assertTrue(byUuid.isPresent());


        CreateVetDto createVetDto = new CreateVetDto(vetDiCaprio, uuid);
        FullVetDto createdDto = perform2xx(create(createVetDto), FullVetDto.class);

        compare(createVetDto).with(createdDto)
                .properties()
                .all()
                .ignore(createVetDto::getId)
                .ignore(createVetDto::getUuid)
                .assertEqual();


        byUuid = userService.findByUuid(uuid);
        Assertions.assertFalse(byUuid.isPresent());

        Optional<User> vetDiCaprioUserByEmail = userService.findByEmail(VET_DICAPRIO_EMAIL);
        Assertions.assertTrue(vetDiCaprioUserByEmail.isPresent());
        User dbUserDiCaprio = vetDiCaprioUserByEmail.get();

        propertyAssert(dbUserDiCaprio)
                .assertContains(dbUserDiCaprio::getRoles, MyRoles.NEW_VET, AuthRoles.UNVERIFIED, AuthRoles.USER)
                .assertSize(dbUserDiCaprio::getRoles, 3)
                .assertEquals(dbUserDiCaprio::getEmail, VET_DICAPRIO_EMAIL)
                .assertNotNull(dbUserDiCaprio::getPassword)
                .assertNull(dbUserDiCaprio::getUuid);


        Optional<Vet> vetDiCaprioByLastName = getService().findByLastName(VET_DICAPRIO);
        Assertions.assertTrue(vetDiCaprioByLastName.isPresent());
        Vet dbDiCaprio = vetDiCaprioByLastName.get();

        Assertions.assertEquals(dbUserDiCaprio, dbDiCaprio.getUser());
    }

    @Test
    public void newVetCantReadPets() throws Exception {
        registerOwnerWithPets(kahn, OWNER_KAHN_EMAIL, OWNER_KAHN_PASSWORD, bella);
        Pet dbBella = petRepository.findByName(BELLA).get();

        Vet vet = registerVet(vetDiCaprio, VET_DICAPRIO_EMAIL, VET_DICAPRIO_PASSWORD);
        String dicaprioToken = userController.login2xx(VET_DICAPRIO_EMAIL, VET_DICAPRIO_PASSWORD);

        mvc.perform(petController.find(dbBella.getId().toString())
                .header(HttpHeaders.AUTHORIZATION, dicaprioToken))
                .andExpect(status().isForbidden());
    }



    @Test
    public void canRegisterVet_andAdminEnables() throws Exception {
        Vet vet = registerVet(vetDiCaprio, VET_DICAPRIO_EMAIL, VET_DICAPRIO_PASSWORD);
        String adminToken = userController.login2xx(ADMIN_EMAIL, ADMIN_PASSWORD);
        String verifyVetJson = createUpdateJsonRequest(
                createUpdateJsonLine("add", "/roles/-", MyRoles.VET),
                createUpdateJsonLine("remove", "/roles", MyRoles.NEW_VET)
        );

        FullUserDto responseVetUserDto = perform2xx(userController.update(verifyVetJson, vet.getUser().getId().toString())
                .header(HttpHeaders.AUTHORIZATION, adminToken), FullUserDto.class);

        Vet updatedDbVet = vetRepository.findById(vet.getId()).get();
        propertyAssert(responseVetUserDto)
                .assertContains(responseVetUserDto::getRoles, MyRoles.VET, AuthRoles.UNVERIFIED, AuthRoles.USER)
                .assertSize(responseVetUserDto::getRoles, 3);

        propertyAssert(updatedDbVet.getUser())
                .assertContains(UserType::getRoles, MyRoles.VET, AuthRoles.UNVERIFIED, AuthRoles.USER)
                .assertSize(UserType::getRoles, 3);
    }

    @Test
    public void enabledVetCanReadPets() throws Exception {
        registerOwnerWithPets(kahn, OWNER_KAHN_EMAIL, OWNER_KAHN_PASSWORD, bella);
        Pet dbBella = petRepository.findByName(BELLA).get();

        Vet vet = registerEnabledVet(vetDiCaprio, VET_DICAPRIO_EMAIL, VET_DICAPRIO_PASSWORD);
        String dicaprioToken = userController.login2xx(VET_DICAPRIO_EMAIL, VET_DICAPRIO_PASSWORD);

        FullPetDto responsePetDto = perform2xx(petController.find(dbBella.getId().toString())
                .header(HttpHeaders.AUTHORIZATION, dicaprioToken), FullPetDto.class);

        compare(responsePetDto).with(dbBella)
                .properties().all()
                .ignore(RapidTestUtil.dtoIdProperties(FullPetDto.class))
                .assertEqual();
    }

    @Test
    public void enabledVetCanUpdatePetsIllnesses() throws Exception {
        registerOwnerWithPets(kahn, OWNER_KAHN_EMAIL, OWNER_KAHN_PASSWORD, bella);
        Pet dbBella = petRepository.findByName(BELLA).get();
        Illness dbTeethPain = illnessRepository.save(teethPain);
        Vet vet = registerEnabledVet(vetDiCaprio, VET_DICAPRIO_EMAIL, VET_DICAPRIO_PASSWORD);
        String vetToken = userController.login2xx(VET_DICAPRIO_EMAIL, VET_DICAPRIO_PASSWORD);

        String updateJson = createUpdateJsonRequest(
                createUpdateJsonLine("add", "/illnessIds", dbTeethPain.getId().toString())
        );

        FullPetDto responsePetDto = perform2xx(petController.update(updateJson,dbBella.getId().toString())
                .header(HttpHeaders.AUTHORIZATION, vetToken), FullPetDto.class);

        compare(responsePetDto).with(dbBella)
                .properties().all()
                .ignore(RapidTestUtil.dtoIdProperties(FullPetDto.class))
                .assertEqual();

        propertyAssert(responsePetDto)
                .assertContains(responsePetDto::getIllnessIds,dbTeethPain.getId());

        Pet updatedDbBella = petRepository.findByName(BELLA).get();
        Illness dbUpdatedTeethPain = illnessRepository.findById(teethPain.getId()).get();


        propertyAssert(updatedDbBella)
                .assertContains(updatedDbBella::getIllnesss,dbUpdatedTeethPain);


    }

//    @Test
//    public void vetCanUpdatePetsIllness() throws Exception {
//        String token = registerOwnerWithPets(kahn, OWNER_KAHN_EMAIL, OWNER_KAHN_PASSWORD, bella);
//        Pet dbBella = petRepository.findByName(BELLA).get();
//        Illness teethPain = illnessRepository.save(this.teethPain);
//
//
//        String updateJson = createUpdateJsonRequest(
//                createUpdateJsonLine("add", "/illnessIds", savedDogPetType.getId().toString())
//        );
//        mvc.perform(petController.update(updateJson, dbBella.getId().toString())
//                .header(HttpHeaders.AUTHORIZATION, token))
//                .andExpect(status().isForbidden());
//
//        com.github.vincemann.springrapid.acldemo.model.PetType dbDgoType = petTypeRepository.findById(savedDogPetType.getId()).get();
//        Assertions.assertEquals(dbDgoType, dbBella.getPetType());
//    }
}
