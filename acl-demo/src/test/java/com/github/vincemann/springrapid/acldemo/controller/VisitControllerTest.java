package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acldemo.controller.suite.MyIntegrationTest;
import com.github.vincemann.springrapid.acldemo.dto.VisitDto;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.acldemo.model.Vet;
import com.github.vincemann.springrapid.acldemo.model.Visit;
import com.github.vincemann.springrapid.core.util.Lists;
import com.github.vincemann.springrapid.coretest.util.RapidTestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import java.util.HashSet;

import static com.github.vincemann.ezcompare.Comparator.compare;
import static com.github.vincemann.ezcompare.PropertyMatchers.propertyAssert;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class VisitControllerTest extends MyIntegrationTest {





    @Test
    public void vetCantCreateVisitForOtherVet() throws Exception {
        registerOwnerWithPets(kahn, OWNER_KAHN_EMAIL, OWNER_KAHN_PASSWORD, bella);
        Pet savedBella = petRepository.findByName(BELLA).get();
        Owner savedKahn = ownerRepository.findByLastName(OWNER_KAHN).get();
        Vet savedDicaprio = registerEnabledVet(vetDiCaprio, VET_DICAPRIO_EMAIL, VET_DICAPRIO_PASSWORD);
        Vet savedVetMax = registerEnabledVet(vetMax, VET_MAX_EMAIL, VET_MAX_PASSWORD);
        String dicaprioToken = userController.login2xx(VET_DICAPRIO_EMAIL, VET_DICAPRIO_PASSWORD);

        VisitDto createVisitDto = new VisitDto(checkTeethVisit);
        createVisitDto.setOwnerId(savedKahn.getId());
        createVisitDto.setPetIds(new HashSet<>(Lists.newArrayList(savedBella.getId())));
        createVisitDto.setVetId(savedVetMax.getId());

        mvc.perform(visitController.create(createVisitDto)
                        .header(HttpHeaders.AUTHORIZATION,dicaprioToken))
                .andExpect(status().isForbidden());

        Assertions.assertTrue(visitRepository.findAll().isEmpty());
    }

    @Test
    public void ownerCantCreateVisit() throws Exception {
        registerOwnerWithPets(kahn, OWNER_KAHN_EMAIL, OWNER_KAHN_PASSWORD, bella);
        Pet savedBella = petRepository.findByName(BELLA).get();
        Owner savedKahn = ownerRepository.findByLastName(OWNER_KAHN).get();
        Vet savedVetMax = registerEnabledVet(vetMax, VET_MAX_EMAIL, VET_MAX_PASSWORD);
        String ownerKahnToken = userController.login2xx(OWNER_KAHN_EMAIL, OWNER_KAHN_PASSWORD);

        VisitDto createVisitDto = new VisitDto(checkTeethVisit);
        createVisitDto.setOwnerId(savedKahn.getId());
        createVisitDto.setPetIds(new HashSet<>(Lists.newArrayList(savedBella.getId())));
        createVisitDto.setVetId(savedVetMax.getId());

        mvc.perform(visitController.create(createVisitDto)
                .header(HttpHeaders.AUTHORIZATION,ownerKahnToken))
                .andExpect(status().isForbidden());

        Assertions.assertTrue(visitRepository.findAll().isEmpty());
    }

    @Test
    public void vetCanReadForeignVisit() throws Exception {
        registerOwnerWithPets(kahn, OWNER_KAHN_EMAIL, OWNER_KAHN_PASSWORD, bella);
        Pet savedBella = petRepository.findByName(BELLA).get();
        Owner savedKahn = ownerRepository.findByLastName(OWNER_KAHN).get();
        Vet savedDicaprio = registerEnabledVet(vetDiCaprio, VET_DICAPRIO_EMAIL, VET_DICAPRIO_PASSWORD);
        Vet savedVetMax = registerEnabledVet(vetMax, VET_MAX_EMAIL, VET_MAX_PASSWORD);
        String dicaprioToken = userController.login2xx(VET_DICAPRIO_EMAIL, VET_DICAPRIO_PASSWORD);
        String maxToken = userController.login2xx(VET_MAX_EMAIL, VET_MAX_PASSWORD);

        Visit visit = createVisit(dicaprioToken, savedKahn, savedDicaprio, checkTeethVisit, savedBella);
        mvc.perform(visitController.find(visit.getId().toString())
                .header(HttpHeaders.AUTHORIZATION,maxToken))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void ownerCanReadOwnVisit() throws Exception {
        registerOwnerWithPets(kahn, OWNER_KAHN_EMAIL, OWNER_KAHN_PASSWORD, bella);

        Pet savedBella = petRepository.findByName(BELLA).get();
        Owner savedKahn = ownerRepository.findByLastName(OWNER_KAHN).get();
        Vet savedDicaprio = registerEnabledVet(vetDiCaprio, VET_DICAPRIO_EMAIL, VET_DICAPRIO_PASSWORD);
        String vetDiCaprioToken = userController.login2xx(VET_DICAPRIO_EMAIL, VET_DICAPRIO_PASSWORD);
        String kahnToken = userController.login2xx(OWNER_KAHN_EMAIL, OWNER_KAHN_PASSWORD);

        Visit visit = createVisit(vetDiCaprioToken, savedKahn, savedDicaprio, checkTeethVisit, savedBella);

        performDs2xx(visitController.find(visit.getId().toString())
                        .header(HttpHeaders.AUTHORIZATION, kahnToken),
                VisitDto.class);
    }

    @Test
    public void ownerCantReadForeignVisit() throws Exception {
        registerOwnerWithPets(kahn, OWNER_KAHN_EMAIL, OWNER_KAHN_PASSWORD, bella);
        registerOwnerWithPets(meier, OWNER_MEIER_CONTACT_INFORMATION, OWNER_MEIER_PASSWORD, bello);

        Pet savedBella = petRepository.findByName(BELLA).get();
        Owner savedKahn = ownerRepository.findByLastName(OWNER_KAHN).get();
        Vet savedDicaprio = registerEnabledVet(vetDiCaprio, VET_DICAPRIO_EMAIL, VET_DICAPRIO_PASSWORD);
        String vetDiCaprioToken = userController.login2xx(VET_DICAPRIO_EMAIL, VET_DICAPRIO_PASSWORD);
        String meierToken = userController.login2xx(OWNER_MEIER_CONTACT_INFORMATION, OWNER_MEIER_PASSWORD);

        Visit visit = createVisit(vetDiCaprioToken, savedKahn, savedDicaprio, checkTeethVisit, savedBella);

        mvc.perform(visitController.find(visit.getId().toString())
                .header(HttpHeaders.AUTHORIZATION,meierToken))
                .andExpect(status().isForbidden());
    }

    @Test
    public void vetCanSubscribeForeignOwnerToVisit() throws Exception {
        registerOwnerWithPets(kahn, OWNER_KAHN_EMAIL, OWNER_KAHN_PASSWORD, bella);
        registerOwnerWithPets(meier, OWNER_MEIER_CONTACT_INFORMATION, OWNER_MEIER_PASSWORD, bello);
        Pet savedBella = petRepository.findByName(BELLA).get();
        Owner savedKahn = ownerRepository.findByLastName(OWNER_KAHN).get();
        Owner savedMeier = ownerRepository.findByLastName(OWNER_MEIER).get();
        Vet savedDicaprio = registerEnabledVet(vetDiCaprio, VET_DICAPRIO_EMAIL, VET_DICAPRIO_PASSWORD);
        String vetDiCaprioToken = userController.login2xx(VET_DICAPRIO_EMAIL, VET_DICAPRIO_PASSWORD);
        String kahnToken = userController.login2xx(OWNER_KAHN_EMAIL, OWNER_KAHN_PASSWORD);
        String meierToken = userController.login2xx(OWNER_MEIER_CONTACT_INFORMATION, OWNER_MEIER_PASSWORD);
        Visit visit = createVisit(vetDiCaprioToken, savedKahn, savedDicaprio, checkTeethVisit, savedBella);

        // meier cant subscribe visit
        mvc.perform(visitController.find(visit.getId())
                .header(HttpHeaders.AUTHORIZATION,meierToken))
                .andExpect(status().isForbidden());

        mvc.perform(visitController.subscribe(vetDiCaprioToken,savedMeier.getId(),visit.getId(),true))
                .andExpect(status().is2xxSuccessful());
        // now meier should be able to subscribe visit

        mvc.perform(visitController.find(visit.getId())
                .header(HttpHeaders.AUTHORIZATION,meierToken))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void vetCanRevokeSubscriptionFromForeignOwnerFromVisit() throws Exception {
        registerOwnerWithPets(kahn, OWNER_KAHN_EMAIL, OWNER_KAHN_PASSWORD, bella);
        registerOwnerWithPets(meier, OWNER_MEIER_CONTACT_INFORMATION, OWNER_MEIER_PASSWORD, bello);

        Pet savedBella = petRepository.findByName(BELLA).get();
        Owner savedKahn = ownerRepository.findByLastName(OWNER_KAHN).get();
        Owner savedMeier = ownerRepository.findByLastName(OWNER_MEIER).get();

        Vet savedDicaprio = registerEnabledVet(vetDiCaprio, VET_DICAPRIO_EMAIL, VET_DICAPRIO_PASSWORD);

        String vetDiCaprioToken = userController.login2xx(VET_DICAPRIO_EMAIL, VET_DICAPRIO_PASSWORD);
        String kahnToken = userController.login2xx(OWNER_KAHN_EMAIL, OWNER_KAHN_PASSWORD);
        String meierToken = userController.login2xx(OWNER_MEIER_CONTACT_INFORMATION, OWNER_MEIER_PASSWORD);

        Visit visit = createVisit(vetDiCaprioToken, savedKahn, savedDicaprio, checkTeethVisit, savedBella);

        // meier cant subscribe to visit
        mvc.perform(visitController.find(visit.getId())
                .header(HttpHeaders.AUTHORIZATION,meierToken))
                .andExpect(status().isForbidden());

        mvc.perform(visitController.subscribe(vetDiCaprioToken,savedMeier.getId(),visit.getId(),true))
                .andExpect(status().is2xxSuccessful());
        // now meier should be able to subscribe to visit

        mvc.perform(visitController.find(visit.getId())
                .header(HttpHeaders.AUTHORIZATION,meierToken))
                .andExpect(status().is2xxSuccessful());

        // revoke
        mvc.perform(visitController.subscribe(vetDiCaprioToken,savedMeier.getId(),visit.getId(),false))
                        .andExpect(status().is2xxSuccessful());

        mvc.perform(visitController.find(visit.getId())
                .header(HttpHeaders.AUTHORIZATION,meierToken))
                .andExpect(status().isForbidden());
    }


}
