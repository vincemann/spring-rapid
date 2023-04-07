package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.coredemo.dto.VisitDto;
import com.github.vincemann.springrapid.coredemo.model.*;
import com.github.vincemann.springrapid.coredemo.service.VisitService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.vincemann.ezcompare.PropertyMatchers.propertyAssert;
import static com.github.vincemann.springrapid.coretest.util.RapidTestUtil.createUpdateJsonLine;
import static com.github.vincemann.springrapid.coretest.util.RapidTestUtil.createUpdateJsonRequest;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class VisitControllerTest extends AbstractControllerIntegrationTest<VisitController, VisitService> {

    @Test
    public void canSaveVisit_linkToSomePetsAndOwner() throws Exception {
        Pet savedBello = petRepository.save(bello);
        Pet savedBella = petRepository.save(bella);
        Pet savedKitty = petRepository.save(kitty);

        Owner savedKahn = ownerRepository.save(kahn);
        Owner savedMeier = ownerRepository.save(meier);

        Vet savedVetMax = vetRepository.save(vetMax);
        Vet savedVetPoldi = vetRepository.save(vetPoldi);


        VisitDto responseDto = saveVisitLinkedTo(checkHeartVisit, null, savedKahn, savedBello,savedKitty);
        propertyAssert(responseDto)
                .assertSize(responseDto::getPetIds,2)
                .assertContains(responseDto::getPetIds,savedBello.getId(),savedKitty.getId())
                .assertNull(responseDto::getVetId)
                .assertEquals(responseDto::getOwnerId,savedKahn.getId());

        Visit dbVisit = visitRepository.findById(responseDto.getId()).get();
        assertVisitHasOwner(dbVisit,KAHN);
        assertVisitHasVet(dbVisit,null);
        assertVisitHasPets(dbVisit,BELLO,KITTY);


    }

    @Test
    public void canSaveVisit_linkToVetAndOwner() throws Exception {
        Pet savedBello = petRepository.save(bello);
        Pet savedBella = petRepository.save(bella);
        Pet savedKitty = petRepository.save(kitty);

        Owner savedKahn = ownerRepository.save(kahn);
        Owner savedMeier = ownerRepository.save(meier);

        Vet savedVetMax = vetRepository.save(vetMax);
        Vet savedVetPoldi = vetRepository.save(vetPoldi);


        VisitDto responseDto = saveVisitLinkedTo(checkHeartVisit, savedVetMax, savedKahn);
        propertyAssert(responseDto)
                .assertSize(responseDto::getPetIds,0)
                .assertEquals(responseDto::getVetId,savedVetMax.getId())
                .assertEquals(responseDto::getOwnerId,savedKahn.getId());

        Visit dbVisit = visitRepository.findById(responseDto.getId()).get();
        assertVisitHasOwner(dbVisit,KAHN);
        assertVisitHasVet(dbVisit,VET_MAX);
        assertVisitHasPets(dbVisit);


    }

    @Test
    public void canUnlinkOwnerAndSomePetsFromVisit_viaUpdate() throws Exception {
        Pet savedBello = petRepository.save(bello);
        Pet savedBella = petRepository.save(bella);
        Pet savedKitty = petRepository.save(kitty);

        Owner savedKahn = ownerRepository.save(kahn);
        Owner savedMeier = ownerRepository.save(meier);

        Vet savedVetMax = vetRepository.save(vetMax);
        Vet savedVetPoldi = vetRepository.save(vetPoldi);


        VisitDto createdVetDto = saveVisitLinkedTo(checkHeartVisit, savedVetMax, savedKahn,savedBello,savedBella,savedKitty);
        String updateJson = createUpdateJsonRequest(
                createUpdateJsonLine("remove", "/ownerId"),
                createUpdateJsonLine("remove", "/petIds",savedBello.getId().toString()),
                createUpdateJsonLine("remove", "/petIds",savedBella.getId().toString())

        );
        VisitDto responseDto = deserialize(getMvc().perform(update(updateJson, createdVetDto.getId()))
                .andReturn().getResponse().getContentAsString(), VisitDto.class);
        propertyAssert(responseDto)
                .assertSize(responseDto::getPetIds,1)
                .assertContains(responseDto::getPetIds,savedKitty.getId())
                .assertEquals(responseDto::getVetId,savedVetMax.getId())
                .assertNull(responseDto::getOwnerId);


        Visit dbVisit = visitRepository.findById(createdVetDto.getId()).get();
        assertVisitHasOwner(dbVisit,null);
        assertVisitHasVet(dbVisit,VET_MAX);
        assertVisitHasPets(dbVisit,KITTY);


    }

    @Test
    public void canLinkOwnerAndSomePetsToVisit_viaUpdate() throws Exception {
        Pet savedBello = petRepository.save(bello);
        Pet savedBella = petRepository.save(bella);
        Pet savedKitty = petRepository.save(kitty);

        Owner savedKahn = ownerRepository.save(kahn);
        Owner savedMeier = ownerRepository.save(meier);

        Vet savedVetMax = vetRepository.save(vetMax);
        Vet savedVetPoldi = vetRepository.save(vetPoldi);


        VisitDto createdVetDto = saveVisitLinkedTo(checkHeartVisit, savedVetMax,null);
        String updateJson = createUpdateJsonRequest(
                createUpdateJsonLine("add", "/ownerId",savedKahn.getId().toString()),
                createUpdateJsonLine("add", "/petIds/-",savedBello.getId().toString()),
                createUpdateJsonLine("add", "/petIds/-",savedBella.getId().toString())

        );
        VisitDto responseDto = deserialize(getMvc().perform(update(updateJson, createdVetDto.getId()))
                .andReturn().getResponse().getContentAsString(), VisitDto.class);
        propertyAssert(responseDto)
                .assertSize(responseDto::getPetIds,2)
                .assertContains(responseDto::getPetIds,savedBello.getId(),savedBella.getId())
                .assertEquals(responseDto::getOwnerId,savedKahn.getId())
                .assertEquals(responseDto::getVetId,savedVetMax.getId());


        Visit dbVisit = visitRepository.findById(createdVetDto.getId()).get();
        assertVisitHasOwner(dbVisit,KAHN);
        assertVisitHasVet(dbVisit,VET_MAX);
        assertVisitHasPets(dbVisit,BELLA,BELLO);


    }

    @Test
    public void canUpdateVetAndLinkSomePetsToVisit_viaUpdate() throws Exception {
        Pet savedBello = petRepository.save(bello);
        Pet savedBella = petRepository.save(bella);
        Pet savedKitty = petRepository.save(kitty);

        Owner savedKahn = ownerRepository.save(kahn);
        Owner savedMeier = ownerRepository.save(meier);

        Vet savedVetMax = vetRepository.save(vetMax);
        Vet savedVetPoldi = vetRepository.save(vetPoldi);


        VisitDto createdVetDto = saveVisitLinkedTo(checkHeartVisit, savedVetMax, savedKahn,savedKitty);
        String updateJson = createUpdateJsonRequest(
                createUpdateJsonLine("replace", "/vetId",savedVetPoldi.getId().toString()),
                createUpdateJsonLine("add", "/petIds/-",savedBello.getId().toString()),
                createUpdateJsonLine("add", "/petIds/-",savedBella.getId().toString())

        );
        VisitDto responseDto = deserialize(getMvc().perform(update(updateJson, createdVetDto.getId()))
                .andReturn().getResponse().getContentAsString(), VisitDto.class);
        propertyAssert(responseDto)
                .assertSize(responseDto::getPetIds,3)
                .assertContains(responseDto::getPetIds,savedKitty.getId(),savedBella.getId(),savedBello.getId())
                .assertEquals(responseDto::getVetId,savedVetPoldi.getId())
                .assertEquals(responseDto::getOwnerId,savedKahn.getId());

        Visit dbVisit = visitRepository.findById(createdVetDto.getId()).get();
        assertVisitHasOwner(dbVisit,KAHN);
        assertVisitHasVet(dbVisit,VET_POLDI);
        assertVisitHasPets(dbVisit,KITTY,BELLO,BELLA);


    }

    @Test
    public void canRemoveVisit() throws Exception {
        Pet savedBello = petRepository.save(bello);
        Pet savedBella = petRepository.save(bella);
        Pet savedKitty = petRepository.save(kitty);

        Owner savedKahn = ownerRepository.save(kahn);
        Owner savedMeier = ownerRepository.save(meier);

        Vet savedVetMax = vetRepository.save(vetMax);
        Vet savedVetPoldi = vetRepository.save(vetPoldi);


        VisitDto createdVetDto = saveVisitLinkedTo(checkHeartVisit, savedVetMax, savedKahn,savedKitty);
        getMvc().perform(delete(createdVetDto.getId()))
                .andExpect(status().is2xxSuccessful());

        Assertions.assertFalse(visitRepository.findById(createdVetDto.getId()).isPresent());
    }


    protected void assertVisitHasOwner(Visit visit, String ownerName) {
        Owner owner = null;
        if (ownerName!=null){
            Optional<Owner> ownerOptional = ownerRepository.findByLastName(ownerName);
            Assertions.assertTrue(ownerOptional.isPresent());
            owner = ownerOptional.get();
        }
        System.err.println("Checking visit: " + visit);
        Assertions.assertEquals(owner, visit.getOwner());
    }

    protected void assertVisitHasVet(Visit visit, String vetName) {
        Vet vet = null;
        if (vetName!=null){
            Optional<Vet> vetOptional = vetRepository.findByLastName(vetName);
            Assertions.assertTrue(vetOptional.isPresent());
            vet = vetOptional.get();
        }
        System.err.println("Checking visit: " + visit);
        Assertions.assertEquals(vet, visit.getVet());
    }


    private void assertVisitHasPets(Visit visit, String... petNames){
        Set<Pet> pets = new HashSet<>();

        for (String petName : petNames) {
            Optional<Pet> petOptional = petRepository.findByName(petName);
            Assertions.assertTrue(petOptional.isPresent());
            pets.add(petOptional.get());
        }
        System.err.println("Checking visit: " + visit);
        Assertions.assertEquals(pets, visit.getPets());
    }


    private VisitDto saveVisitLinkedTo(Visit visit, Vet vet, Owner owner, Pet... pets) throws Exception {
        VisitDto visitDto = new VisitDto(visit);
        if (owner != null)
            visitDto.setOwnerId(owner.getId());
        if (vet != null)
            visitDto.setVetId(vet.getId());
        if (pets.length > 0)
            visitDto.setPetIds(Arrays.stream(pets).map(Pet::getId).collect(Collectors.toSet()));


        return deserialize(getMvc().perform(create(visitDto))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse().getContentAsString(), VisitDto.class);
    }
}
