package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acldemo.auth.MyRoles;
import com.github.vincemann.springrapid.acldemo.dto.owner.CreateOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.owner.FullOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.user.UUIDSignupResponseDto;
import com.github.vincemann.springrapid.acldemo.dto.vet.CreateVetDto;
import com.github.vincemann.springrapid.acldemo.dto.vet.FullVetDto;
import com.github.vincemann.springrapid.acldemo.model.*;
import com.github.vincemann.springrapid.acldemo.service.VetService;
import com.github.vincemann.springrapid.auth.domain.AuthRoles;
import com.github.vincemann.springrapid.auth.domain.dto.SignupDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import java.util.Optional;

import static com.github.vincemann.ezcompare.Comparator.compare;
import static com.github.vincemann.springrapid.coretest.service.PropertyMatchers.propertyAssert;
import static com.github.vincemann.springrapid.coretest.util.RapidTestUtil.createUpdateJsonLine;
import static com.github.vincemann.springrapid.coretest.util.RapidTestUtil.createUpdateJsonRequest;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class VetControllerTest extends AbstractControllerIntegrationTest<VetController, VetService>
{


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
