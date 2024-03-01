package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acldemo.MyRoles;
import com.github.vincemann.springrapid.acldemo.controller.suite.MyIntegrationTest;
import com.github.vincemann.springrapid.acldemo.dto.VisitDto;
import com.github.vincemann.springrapid.acldemo.dto.pet.FullPetDto;
import com.github.vincemann.springrapid.acldemo.dto.user.MyFullUserDto;
import com.github.vincemann.springrapid.acldemo.dto.user.UUIDSignupResponseDto;
import com.github.vincemann.springrapid.acldemo.dto.vet.CreateVetDto;
import com.github.vincemann.springrapid.acldemo.dto.vet.ReadVetDto;
import com.github.vincemann.springrapid.acldemo.dto.vet.SignupVetDto;
import com.github.vincemann.springrapid.acldemo.dto.visit.CreateVisitDto;
import com.github.vincemann.springrapid.acldemo.dto.visit.ReadVisitDto;
import com.github.vincemann.springrapid.acldemo.model.*;
import com.github.vincemann.springrapid.acldemo.service.VetService;
import com.github.vincemann.springrapid.auth.mail.MailData;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.coretest.util.RapidTestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import static com.github.vincemann.ezcompare.Comparator.compare;
import static com.github.vincemann.ezcompare.PropertyMatchers.propertyAssert;
import static com.github.vincemann.springrapid.coretest.util.RapidTestUtil.createUpdateJsonLine;
import static com.github.vincemann.springrapid.coretest.util.RapidTestUtil.createUpdateJsonRequest;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class VetControllerTest extends MyIntegrationTest {

    @Autowired
    VetService vetService;

    @Test
    public void canSignupVet() throws Exception {
        // when
        vetDiCaprio.getSpecialtys().add(specialtyService.create(heart));
        SignupVetDto dto = new SignupVetDto(vetDiCaprio);
        ReadVetDto response = vetController.signup(dto);

        // then
        Assertions.assertEquals(VET_DICAPRIO,response.getLastName());
        Assertions.assertEquals(VET_DICAPRIO,response.getContactInformation());
        Assertions.assertTrue(response.getRoles().contains(MyRoles.VET));
        Assertions.assertTrue(response.getRoles().contains(AuthRoles.USER));
        Assertions.assertTrue(response.getRoles().contains(AuthRoles.UNVERIFIED));
        Assertions.assertEquals(2,response.getRoles().size());

        Assertions.assertTrue(vetService.findByLastName(VET_DICAPRIO).isPresent());
        Assertions.assertTrue(vetService.findByContactInformation(VET_DICAPRIO_EMAIL).isPresent());
    }

    @Test
    public void givenVetSignedUp_canVerify() throws Exception {
        // given
        signupVetDiCaprio();

        // when
        MailData mailData = userController.verifyMailWasSend();
        perform(userController.verifyContactInformationWithLink(mailData.getLink()))
        // then
                .andExpect(status().is2xxSuccessful());

        Vet saved = vetService.findByLastName(VET_DICAPRIO).get();
        Assertions.assertFalse(saved.getRoles().contains(AuthRoles.UNVERIFIED));
    }

    @Test
    public void unverifiedVetCanReadPets() throws Exception {
        // given
        signupVetDiCaprio();
        Pet bella = petService.create(this.bella);

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
        Vet dicaprio = signupVetDiCaprioAndVerify();
        Owner kahn = signupKahn();
        Pet bella = petRepository.findByName(BELLA).get();


        // when
        checkTeethVisit.setVet(dicaprio);
        checkTeethVisit.setOwner(kahn);
        checkTeethVisit.getPets().add(bella);
        CreateVisitDto dto = new CreateVisitDto(checkTeethVisit);
        String token = userController.login2xx(VET_DICAPRIO_EMAIL, VET_DICAPRIO_PASSWORD);
        ReadVisitDto response = performDs2xx(visitController.create(dto)
                        .header(HttpHeaders.AUTHORIZATION,token)
                , ReadVisitDto.class);
        // then
        Assertions.assertNotNull(response.getId());
        Assertions.assertEquals(checkHeartVisit.getReason(),response.getReason());
    }

    @Test
    public void unverifiedVetCantCreateVisit() throws Exception {
        // given
        Vet dicaprio = signupVetDiCaprio();
        Owner kahn = signupKahn();
        Pet bella = petRepository.findByName(BELLA).get();

        // when
        checkTeethVisit.setVet(dicaprio);
        checkTeethVisit.setOwner(kahn);
        checkTeethVisit.getPets().add(bella);
        CreateVisitDto dto = new CreateVisitDto(checkTeethVisit);
        String token = userController.login2xx(VET_DICAPRIO_EMAIL, VET_DICAPRIO_PASSWORD);
        perform(visitController.create(dto)
                        .header(HttpHeaders.AUTHORIZATION,token))
        // then
                .andExpect(status().isForbidden());
        Assertions.assertTrue(visitService.findAll().isEmpty());
    }

    @Test
    public void cantCreateVisitForOwnerAndPetsNotOwnedByOwner() throws Exception {
        // given
        Vet dicaprio = signupVetDiCaprioAndVerify();
        Owner kahn = signupKahn(); // kahn is linked to bella not bello
        Pet bello = petRepository.findByName(BELLO).get();

        // when
        checkTeethVisit.setVet(dicaprio);
        checkTeethVisit.setOwner(kahn);
        checkTeethVisit.getPets().add(bello); // setting bello here, which is not owner by kahn
        CreateVisitDto dto = new CreateVisitDto(checkTeethVisit);
        String token = userController.login2xx(VET_DICAPRIO_EMAIL, VET_DICAPRIO_PASSWORD);
        perform(visitController.create(dto)
                .header(HttpHeaders.AUTHORIZATION,token))
                // then
                .andExpect(status().isForbidden());
        Assertions.assertTrue(visitService.findAll().isEmpty());
    }

    @Test
    public void verifiedVetCanUpdatePetsIllnesses() throws Exception {
        registerOwnerWithPets(kahn, OWNER_KAHN_EMAIL, OWNER_KAHN_PASSWORD, bella);
        Pet dbBella = petRepository.findByName(BELLA).get();
        Illness dbTeethPain = illnessRepository.save(teethPain);
        Vet vet = registerEnabledVet(vetDiCaprio, VET_DICAPRIO_EMAIL, VET_DICAPRIO_PASSWORD);
        String vetToken = userController.login2xx(VET_DICAPRIO_EMAIL, VET_DICAPRIO_PASSWORD);

        String updateJson = createUpdateJsonRequest(
                createUpdateJsonLine("add", "/illnessIds", dbTeethPain.getId().toString())
        );

        FullPetDto responsePetDto = performDs2xx(petController.update(updateJson,dbBella.getId().toString())
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
//        String token = registerOwnerWithPets(kahn, OWNER_KAHN_CONTACT_INFORMATION, OWNER_KAHN_PASSWORD, bella);
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
