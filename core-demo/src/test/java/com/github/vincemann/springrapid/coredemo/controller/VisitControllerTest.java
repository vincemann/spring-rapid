package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.coredemo.controller.suite.MyIntegrationTest;
import com.github.vincemann.springrapid.coredemo.controller.suite.template.VisitControllerTestTemplate;
import com.github.vincemann.springrapid.coredemo.dto.VisitDto;
import com.github.vincemann.springrapid.coredemo.model.Owner;
import com.github.vincemann.springrapid.coredemo.model.Pet;
import com.github.vincemann.springrapid.coredemo.model.Vet;
import com.github.vincemann.springrapid.coredemo.model.Visit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.github.vincemann.springrapid.coredemo.controller.suite.TestData.*;
import static com.github.vincemann.springrapid.coretest.util.RapidTestUtil.createUpdateJsonLine;
import static com.github.vincemann.springrapid.coretest.util.RapidTestUtil.createUpdateJsonRequest;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag(value = "demo-projects")
public class VisitControllerTest extends MyIntegrationTest {

    @Autowired
    VisitControllerTestTemplate controller;

    @Test
    public void canCreateVisitLinkedToOwnerAndPets() throws Exception {
        // given
        Pet bello = petService.create(testData.getBello());
        Pet bella = petService.create(testData.getBella());
        Pet kitty = petService.create(testData.getKitty());

        Owner kahn = ownerService.create(testData.getKahn());
        Owner meier = ownerService.create(testData.getMeier());

        Vet max = vetService.create(testData.getVetMax());
        Vet poldi = vetService.create(testData.getVetPoldi());

        // when
        VisitDto responseDto = helper.createVisitLinkedTo(testData.getCheckHeartVisit(), null, kahn, bello,kitty);
        // then
        Assertions.assertEquals(2,responseDto.getPetIds().size());
        Assertions.assertNull(responseDto.getVetId());
        Assertions.assertEquals(kahn.getId(),responseDto.getOwnerId());

        Visit dbVisit = visitService.findById(responseDto.getId()).get();
        assertVisitHasOwner(dbVisit,KAHN);
        assertVisitHasVet(dbVisit,null);
        assertVisitHasPets(dbVisit,BELLO,KITTY);


    }

    @Test
    public void canCreateVisitLinkedToVetAndOwner() throws Exception {
        // given
        Pet bello = petService.create(testData.getBello());
        Pet bella = petService.create(testData.getBella());
        Pet kitty = petService.create(testData.getKitty());

        Owner kahn = ownerService.create(testData.getKahn());
        Owner meier = ownerService.create(testData.getMeier());

        Vet max = vetService.create(testData.getVetMax());
        Vet poldi = vetService.create(testData.getVetPoldi());

        // when
        VisitDto responseDto = helper.createVisitLinkedTo(testData.getCheckHeartVisit(), max, kahn);
        // then
        Assertions.assertTrue(responseDto.getPetIds().isEmpty());
        Assertions.assertEquals(max.getId(),responseDto.getVetId());
        Assertions.assertEquals(kahn.getId(),responseDto.getOwnerId());

        Visit dbVisit = visitService.findById(responseDto.getId()).get();
        assertVisitHasOwner(dbVisit,KAHN);
        assertVisitHasVet(dbVisit,VET_MAX);
        assertVisitHasPets(dbVisit);


    }

    @Test
    public void canUnlinkOwnerAndMultiplePetsFromVisitViaSingleUpdateVisitOperation() throws Exception {
        // given
        Pet bello = petService.create(testData.getBello());
        Pet bella = petService.create(testData.getBella());
        Pet kitty = petService.create(testData.getKitty());

        Owner kahn = ownerService.create(testData.getKahn());
        Owner meier = ownerService.create(testData.getMeier());

        Vet max = vetService.create(testData.getVetMax());
        Vet poldi = vetService.create(testData.getVetPoldi());

        VisitDto visitDto = helper.createVisitLinkedTo(testData.getCheckHeartVisit(), max, kahn,bello,bella,kitty);

        // when
        String jsonPatch = createUpdateJsonRequest(
                createUpdateJsonLine("remove", "/ownerId"),
                createUpdateJsonLine("remove", "/petIds",bello.getId().toString()),
                createUpdateJsonLine("remove", "/petIds",bella.getId().toString())

        );
        VisitDto responseDto = controller.update2xx(jsonPatch, visitDto.getId(), VisitDto.class);
        // then
        Assertions.assertEquals(1,responseDto.getPetIds().size());
        Assertions.assertEquals(max.getId(),responseDto.getVetId());
        Assertions.assertNull(responseDto.getOwnerId());

        Visit visit = visitService.findById(visitDto.getId()).get();
        assertVisitHasOwner(visit,null);
        assertVisitHasVet(visit,VET_MAX);
        assertVisitHasPets(visit,KITTY);
    }

    @Test
    public void canLinkOwnerAndSomePetsToVisitViaUpdateVisit() throws Exception {
        // given
        Pet bello = petService.create(testData.getBello());
        Pet bella = petService.create(testData.getBella());
        Pet kitty = petService.create(testData.getKitty());

        Owner kahn = ownerService.create(testData.getKahn());
        Owner meier = ownerService.create(testData.getMeier());

        Vet max = vetService.create(testData.getVetMax());
        Vet poldi = vetService.create(testData.getVetPoldi());


        VisitDto visitDto = helper.createVisitLinkedTo(testData.getCheckHeartVisit(), max,null);
        // when
        String jsonPatch = createUpdateJsonRequest(
                createUpdateJsonLine("add", "/ownerId",kahn.getId().toString()),
                createUpdateJsonLine("add", "/petIds/-",bello.getId().toString()),
                createUpdateJsonLine("add", "/petIds/-",bella.getId().toString())

        );

        VisitDto responseDto = controller.update2xx(jsonPatch, visitDto.getId(), VisitDto.class);
        // then
        Assertions.assertEquals(2,responseDto.getPetIds().size());
        Assertions.assertEquals(max.getId(),responseDto.getVetId());
        Assertions.assertEquals(kahn.getId(),responseDto.getOwnerId());

        Visit visit = visitService.findById(visitDto.getId()).get();
        assertVisitHasOwner(visit,KAHN);
        assertVisitHasVet(visit,VET_MAX);
        assertVisitHasPets(visit,BELLA,BELLO);
    }

    @Test
    public void canLinkNewVetAndSomePetsToVisitViaSingleUpdateVisitOperation() throws Exception {
        // given
        Pet bello = petService.create(testData.getBello());
        Pet bella = petService.create(testData.getBella());
        Pet kitty = petService.create(testData.getKitty());

        Owner kahn = ownerService.create(testData.getKahn());
        Owner meier = ownerService.create(testData.getMeier());

        Vet max = vetService.create(testData.getVetMax());
        Vet poldi = vetService.create(testData.getVetPoldi());


        VisitDto visitDto = helper.createVisitLinkedTo(testData.getCheckHeartVisit(), max, kahn,kitty);

        // when
        String jsonPatch = createUpdateJsonRequest(
                createUpdateJsonLine("replace", "/vetId",poldi.getId().toString()),
                createUpdateJsonLine("add", "/petIds/-",bello.getId().toString()),
                createUpdateJsonLine("add", "/petIds/-",bella.getId().toString())

        );
        VisitDto responseDto = controller.update2xx(jsonPatch, visitDto.getId(), VisitDto.class);
        // then
        Assertions.assertEquals(3,responseDto.getPetIds().size());
        Assertions.assertEquals(poldi.getId(),responseDto.getVetId());
        Assertions.assertEquals(kahn.getId(),responseDto.getOwnerId());

        Visit visit = visitService.findById(visitDto.getId()).get();
        assertVisitHasOwner(visit,KAHN);
        assertVisitHasVet(visit,VET_POLDI);
        assertVisitHasPets(visit,KITTY,BELLO,BELLA);
    }

    @Test
    public void removeVisit() throws Exception {
        // given
        Pet bello = petService.create(testData.getBello());
        Pet bella = petService.create(testData.getBella());
        Pet kitty = petService.create(testData.getKitty());

        Owner kahn = ownerService.create(testData.getKahn());
        Owner meier = ownerService.create(testData.getMeier());

        Vet max = vetService.create(testData.getVetMax());
        Vet poldi = vetService.create(testData.getVetPoldi());

        VisitDto visitDto = helper.createVisitLinkedTo(testData.getCheckHeartVisit(), max, kahn,kitty);

        // when
        getMvc().perform(controller.delete(visitDto.getId()))
                .andExpect(status().is2xxSuccessful());
        // then
        Assertions.assertFalse(visitService.findById(visitDto.getId()).isPresent());
    }


    protected void assertVisitHasOwner(Visit visit, String ownerName) {
        Owner owner = null;
        if (ownerName!=null){
            Optional<Owner> ownerOptional = ownerService.findByLastName(ownerName);
            Assertions.assertTrue(ownerOptional.isPresent());
            owner = ownerOptional.get();
        }
        System.err.println("Checking visit: " + visit);
        Assertions.assertEquals(owner, visit.getOwner());
    }

    protected void assertVisitHasVet(Visit visit, String vetName) {
        Vet vet = null;
        if (vetName!=null){
            Optional<Vet> vetOptional = vetService.findByLastName(vetName);
            Assertions.assertTrue(vetOptional.isPresent());
            vet = vetOptional.get();
        }
        System.err.println("Checking visit: " + visit);
        Assertions.assertEquals(vet, visit.getVet());
    }


    private void assertVisitHasPets(Visit visit, String... petNames){
        Set<Pet> pets = new HashSet<>();

        for (String petName : petNames) {
            Optional<Pet> petOptional = petService.findByName(petName);
            Assertions.assertTrue(petOptional.isPresent());
            pets.add(petOptional.get());
        }
        System.err.println("Checking visit: " + visit);
        Assertions.assertEquals(pets, visit.getPets());
    }
}
