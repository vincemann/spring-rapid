package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.coredemo.dtos.VisitDto;
import com.github.vincemann.springrapid.coredemo.model.*;
import com.github.vincemann.springrapid.coredemo.service.VisitService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
        Assertions.assertTrue(responseDto.getPetIds().contains(savedBello.getId()));
        Assertions.assertTrue(responseDto.getPetIds().contains(savedKitty.getId()));
        Assertions.assertEquals(2,responseDto.getPetIds().size());

        Visit dbVisit = visitRepository.findById(responseDto.getId()).get();
        assertVisitHasOwner(dbVisit,KAHN);
        assertVisitHasVet(dbVisit,null);
        assertVisitHasPets(dbVisit,BELLO,KITTY);


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


        return deserialize(getMockMvc().perform(create(visitDto))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse().getContentAsString(), VisitDto.class);
    }
}
