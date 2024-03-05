package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.coredemo.controller.suite.MyIntegrationTest;
import com.github.vincemann.springrapid.coredemo.controller.suite.template.PetControllerTestTemplate;
import com.github.vincemann.springrapid.coredemo.dto.owner.ReadOwnOwnerDto;
import com.github.vincemann.springrapid.coredemo.dto.pet.ReadPetDto;
import com.github.vincemann.springrapid.coredemo.model.Owner;
import com.github.vincemann.springrapid.coredemo.model.Pet;
import com.github.vincemann.springrapid.coredemo.model.PetType;
import com.github.vincemann.springrapid.coredemo.model.Toy;
import com.github.vincemann.springrapid.coredemo.service.filter.PetsParentFilter;
import com.github.vincemann.springrapid.coretest.controller.UrlWebExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static com.github.vincemann.springrapid.coredemo.controller.suite.TestData.*;
import static com.github.vincemann.springrapid.coretest.util.RapidTestUtil.createUpdateJsonLine;
import static com.github.vincemann.springrapid.coretest.util.RapidTestUtil.createUpdateJsonRequest;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PetControllerIntegrationTest extends MyIntegrationTest
{

    @Autowired
    PetControllerTestTemplate controller;

    @Test
    public void canCreatePetWithoutOwner() throws Exception {
        // when
        ReadPetDto createPetDto = new ReadPetDto(testData.getBella());
        ReadPetDto responseDto = controller.create2xx(createPetDto, ReadPetDto.class);
        // then
        Optional<Pet> bella = petService.findByName(BELLA);
        Assertions.assertTrue(bella.isPresent());
        Assertions.assertNull(bella.get().getOwner());
        Assertions.assertEquals(responseDto.getId(),bella.get().getId());
    }

    @Test
    public void canFindPetsOfOwner() throws Exception {
        // save kahn and meier
        // kahn has bello and bella
        // meier has kitty
        // find all pets of kahn -> should return bello and bella
        // given
        Pet bello = petService.create(testData.getBello());
        Pet kitty = petService.create(testData.getKitty());
        Pet bella = petService.create(testData.getBella());
        ReadOwnOwnerDto kahn = helper.saveOwnerLinkedToPets(testData.getKahn(),bello.getId(),bella.getId());
        ReadOwnOwnerDto meier = helper.saveOwnerLinkedToPets(testData.getMeier(), kitty.getId());

        assertOwnerHasPets(KAHN,BELLO,BELLA);
        assertOwnerHasPets(MEIER,KITTY);
        assertPetHasOwner(BELLO,KAHN);
        assertPetHasOwner(BELLA,KAHN);
        assertPetHasOwner(KITTY,MEIER);

        // when
        UrlWebExtension parentFilter = new UrlWebExtension(PetsParentFilter.class,kahn.getId().toString());
        List<ReadPetDto> petsOfKahn = controller.findAll2xx(ReadPetDto.class,parentFilter);

        // then
        Assertions.assertEquals(2,petsOfKahn.size());
        ReadPetDto belloDto = petsOfKahn.stream().filter(p -> p.getId().equals(bello.getId())).findFirst().get();
        ReadPetDto bellaDto = petsOfKahn.stream().filter(p -> p.getId().equals(bella.getId())).findFirst().get();
    }


    @Test
    public void canCreatePetWithLinkedOwner() throws Exception {
        // when
        Owner kahn = ownerService.create(testData.getKahn());
        ReadPetDto responseDto = helper.savePetLinkedToOwnerAndToys(testData.getBella(), kahn.getId());
        // then
        Assertions.assertEquals(kahn.getId(),responseDto.getOwnerId());
        assertOwnerHasPets(KAHN, BELLA);
        assertPetHasOwner(BELLA,KAHN);
    }

    @Test
    public void canCreatePetWithLinkedToys() throws Exception {
        // given
        Owner kahn = ownerService.create(testData.getKahn());
        Toy ball = toyService.create(testData.getBall());
        Toy bone = toyService.create(testData.getBone());
        Toy rubberDuck = toyService.create(testData.getRubberDuck());
        // when
        ReadPetDto responseDto = helper.savePetLinkedToOwnerAndToys(testData.getBella(), null,ball,rubberDuck);
        // then
        Assertions.assertNull(responseDto.getOwnerId());
        Assertions.assertTrue(responseDto.getToyIds().contains(ball.getId()));
        Assertions.assertTrue(responseDto.getToyIds().contains(rubberDuck.getId()));
        Assertions.assertEquals(2,responseDto.getToyIds().size());

        assertOwnerHasPets(KAHN);
        assertPetHasOwner(BELLA,null);
        assertPetHasToys(BELLA,BALL,RUBBER_DUCK);
        assertToyHasPet(BALL,BELLA);
        assertToyHasPet(BONE,null);
        assertToyHasPet(RUBBER_DUCK,BELLA);
    }

    @Test
    public void canCreatePetWithLinkedOwnerAndLinkedToys() throws Exception {
        // given
        Owner kahn = ownerService.create(testData.getKahn());
        Toy ball = toyService.create(testData.getBall());
        Toy bone = toyService.create(testData.getBone());
        Toy rubberDuck = toyService.create(testData.getRubberDuck());
        // when
        ReadPetDto responseDto = helper.savePetLinkedToOwnerAndToys(testData.getBella(), kahn.getId(),ball,bone);
        // then
        Assertions.assertEquals(kahn.getId(),responseDto.getOwnerId());
        Assertions.assertTrue(responseDto.getToyIds().contains(ball.getId()));
        Assertions.assertTrue(responseDto.getToyIds().contains(bone.getId()));
        Assertions.assertEquals(2,responseDto.getToyIds().size());

        assertPetHasOwner(BELLA,KAHN);
        assertPetHasToys(BELLA,BALL,BONE);
        assertToyHasPet(BALL,BELLA);
        assertToyHasPet(BONE,BELLA);
    }

    @Test
    public void canUnlinkOwnerFromPetViaUpdatePet() throws Exception {
        // given
        Owner kahn = ownerService.create(testData.getKahn());
        ReadPetDto createdBellaDto = helper.savePetLinkedToOwnerAndToys(testData.getBella(), kahn.getId());
        // when
        String removeOwnerJson = createUpdateJsonRequest(createUpdateJsonLine("remove", "/ownerId"));
        ReadPetDto responseDto = controller.update2xx(removeOwnerJson, createdBellaDto.getId(), ReadPetDto.class);
        // then
        Assertions.assertNull(responseDto.getOwnerId());
        assertOwnerHasPets(KAHN);
        assertPetHasOwner(BELLA,null);
    }

    @Test
    public void canUnlinkSomeToysAndOwnerFromPetViaSingleUpdatePetRequest() throws Exception {
        // bella -> owner=kahn
        //       -> toys=[ball, bone, rubberDuck]

        // unlink owner from bella
        // unlink bone and rubberDuck from bella

        // result = bella -> owner = null
        //                -> toys = [ball]
        // given
        Owner kahn = ownerService.create(testData.getKahn());
        Toy ball = toyService.create(testData.getBall());
        Toy bone = toyService.create(testData.getBone());
        Toy rubberDuck = toyService.create(testData.getRubberDuck());

        // when
        ReadPetDto createdBellaDto = helper.savePetLinkedToOwnerAndToys(testData.getBella(), kahn.getId(),ball,bone,rubberDuck);
        String removeOwnerJson = createUpdateJsonRequest(
                createUpdateJsonLine("remove", "/ownerId"),
                createUpdateJsonLine("remove", "/toyIds",bone.getId().toString()),
                createUpdateJsonLine("remove", "/toyIds",rubberDuck.getId().toString())
        );
        ReadPetDto responseDto = controller.update2xx(removeOwnerJson, createdBellaDto.getId(), ReadPetDto.class);
        // then
        Assertions.assertNull(responseDto.getOwnerId());
        Assertions.assertEquals(1,responseDto.getToyIds().size());
        Assertions.assertTrue(responseDto.getToyIds().contains(ball.getId()));

        assertOwnerHasPets(KAHN);
        assertPetHasOwner(BELLA,null);
        assertPetHasToys(BELLA,BALL);
        assertToyHasPet(BALL,BELLA);
        assertToyHasPet(BONE,null);
        assertToyHasPet(RUBBER_DUCK,null);
    }

    @Test
    public void canUnlinkOwnerAndAddSomeToysToPetViaSingleUpdatePetRequest() throws Exception {
        // given
        Owner kahn = ownerService.create(testData.getKahn());
        Toy ball = toyService.create(testData.getBall());
        Toy bone = toyService.create(testData.getBone());
        Toy rubberDuck = toyService.create(testData.getRubberDuck());

        ReadPetDto bella = helper.savePetLinkedToOwnerAndToys(testData.getBella(), kahn.getId(),rubberDuck);
        // when
        String removeOwnerJson = createUpdateJsonRequest(
                createUpdateJsonLine("remove", "/ownerId"),
                createUpdateJsonLine("add", "/toyIds/-",bone.getId().toString()),
                createUpdateJsonLine("add", "/toyIds/-",ball.getId().toString())
        );
        ReadPetDto responseDto = controller.update2xx(removeOwnerJson, bella.getId(), ReadPetDto.class);
        // then
        Assertions.assertNull(responseDto.getOwnerId());
        Assertions.assertEquals(3,responseDto.getToyIds().size());
        Assertions.assertTrue(responseDto.getToyIds().contains(ball.getId()));
        Assertions.assertTrue(responseDto.getToyIds().contains(bone.getId()));
        Assertions.assertTrue(responseDto.getToyIds().contains(rubberDuck.getId()));

        assertOwnerHasPets(KAHN);
        assertPetHasOwner(BELLA,null);
        assertPetHasToys(BELLA,BALL,BONE,RUBBER_DUCK);
        assertToyHasPet(BALL,BELLA);
        assertToyHasPet(BONE,BELLA);
        assertToyHasPet(RUBBER_DUCK,BELLA);
    }

    @Test
    public void canUnlinkPetsPetTypeViaUpdatePet() throws Exception {
        // given
        ReadPetDto bella = helper.savePetLinkedToOwnerAndToys(testData.getBella(),null);
        // when
        String removePetTypeJson = createUpdateJsonRequest(
                createUpdateJsonLine("remove", "/petTypeId"));
        ReadPetDto responseDto = controller.update2xx(removePetTypeJson, bella.getId(), ReadPetDto.class);
        // then
        Assertions.assertNull(responseDto.getPetTypeId());
        Pet dbBella = petService.findByName(BELLA).get();
        Assertions.assertNull(dbBella.getPetType());
        Assertions.assertTrue(petTypeService.findById(bella.getPetTypeId()).isPresent());
    }

    @Test
    public void canAddOwnerToPetViaUpdatePet() throws Exception {
        // given
        Owner kahn = ownerService.create(testData.getKahn());
        ReadPetDto createdBellaDto = helper.savePetLinkedToOwnerAndToys(testData.getBella(),null);
        // when
        String addOwnerJson = createUpdateJsonRequest(createUpdateJsonLine("add", "/ownerId",kahn.getId().toString()));
        ReadPetDto responseDto = controller.update2xx(addOwnerJson, createdBellaDto.getId(), ReadPetDto.class);
        Assertions.assertEquals(kahn.getId(),responseDto.getOwnerId());

        assertOwnerHasPets(KAHN,BELLA);
        assertPetHasOwner(BELLA,KAHN);
    }

    @Test
    public void canLinkOwnerAndSomeToysToPetViaSingleUpdatePetRequest() throws Exception {
        // given
        Owner kahn = ownerService.create(testData.getKahn());
        Toy ball = toyService.create(testData.getBall());
        Toy bone = toyService.create(testData.getBone());
        ReadPetDto bella = helper.savePetLinkedToOwnerAndToys(testData.getBella(),null);

        // when
        String updateJson = createUpdateJsonRequest(
                createUpdateJsonLine("add", "/ownerId",kahn.getId().toString()),
                createUpdateJsonLine("add", "/toyIds/-",ball.getId().toString()),
                createUpdateJsonLine("add", "/toyIds/-",bone.getId().toString())

        );
        ReadPetDto responseDto = controller.update2xx(updateJson, bella.getId(), ReadPetDto.class);
        // then
        Assertions.assertEquals(kahn.getId(),responseDto.getOwnerId());
        Assertions.assertEquals(2,responseDto.getToyIds().size());
        Assertions.assertTrue(responseDto.getToyIds().contains(ball.getId()));
        Assertions.assertTrue(responseDto.getToyIds().contains(bone.getId()));

        assertOwnerHasPets(KAHN,BELLA);
        assertPetHasOwner(BELLA,KAHN);

        assertPetHasToys(BELLA,BALL,BONE);
        assertToyHasPet(BALL,BELLA);
        assertToyHasPet(BONE,BELLA);
        assertToyHasPet(RUBBER_DUCK,null);

    }

    @Test
    public void canLinkOwnerAndUnlinkToysFromPetViaSingleUpdatePetRequest() throws Exception {
        // given
        Owner kahn = ownerService.create(testData.getKahn());
        Toy ball = toyService.create(testData.getBall());
        Toy bone = toyService.create(testData.getBone());
        Toy rubberDuck = toyService.create(testData.getRubberDuck());
        ReadPetDto bella = helper.savePetLinkedToOwnerAndToys(testData.getBella(),null,ball,rubberDuck,bone);
        // when
        String updateJson = createUpdateJsonRequest(
                createUpdateJsonLine("add", "/ownerId",kahn.getId().toString()),
                createUpdateJsonLine("remove", "/toyIds",ball.getId().toString()),
                createUpdateJsonLine("remove", "/toyIds",rubberDuck.getId().toString())

        );
        ReadPetDto responseDto = controller.update2xx(updateJson, bella.getId(), ReadPetDto.class);
        // then
        Assertions.assertEquals(kahn.getId(),responseDto.getOwnerId());
        Assertions.assertEquals(1,responseDto.getToyIds().size());
        Assertions.assertTrue(responseDto.getToyIds().contains(bone.getId()));

        assertOwnerHasPets(KAHN,BELLA);
        assertPetHasOwner(BELLA,KAHN);

        assertPetHasToys(BELLA,BONE);
        assertToyHasPet(BONE,BELLA);
        assertToyHasPet(BALL,null);
        assertToyHasPet(RUBBER_DUCK,null);

    }

    @Test
    public void canLinkPetTypeToPetViaUpdatePet() throws Exception {
        // given
        testData.getBella().setPetType(null);
        ReadPetDto bella = helper.savePetLinkedToOwnerAndToys(testData.getBella(),null);
        PetType catPetType = testData.getSavedCatPetType();
        // when
        String addPetTypeJson = createUpdateJsonRequest(
                createUpdateJsonLine("add", "/petTypeId",catPetType.getId().toString()));
        ReadPetDto responseDto = controller.update2xx(addPetTypeJson, bella.getId(), ReadPetDto.class);
        // then
        Assertions.assertEquals(catPetType.getId(),responseDto.getPetTypeId());
        Pet dbBella = petService.findByName(BELLA).get();
        com.github.vincemann.springrapid.coredemo.model.PetType dbPetType = petTypeService.findById(catPetType.getId()).get();
        Assertions.assertEquals(dbPetType,dbBella.getPetType());
    }

    @Test
    public void canReplaceOwnerOfPetViaUpdatePet() throws Exception {
        // given
        Owner kahn = ownerService.create(testData.getKahn());
        Owner meier = ownerService.create(testData.getMeier());
        ReadPetDto bella = helper.savePetLinkedToOwnerAndToys(testData.getBella(),kahn.getId());
        // when
        String updateOwnerJson = createUpdateJsonRequest(
                createUpdateJsonLine("replace", "/ownerId",meier.getId().toString())
        );
        ReadPetDto responseDto = controller.update2xx(updateOwnerJson, bella.getId(), ReadPetDto.class);
        // then
        Assertions.assertEquals(meier.getId(),responseDto.getOwnerId());
        assertOwnerHasPets(KAHN);
        assertOwnerHasPets(MEIER,BELLA);
        assertPetHasOwner(BELLA,MEIER);
    }

    @Test
    public void canReplaceOwnerOfPetViaRemoveAndAddUpdateOperationOfPet() throws Exception {
        // bellas owner was kahn
        // remove owner kahn from bella and add new owner meier
        // given
        Owner kahn = ownerService.create(testData.getKahn());
        Owner meier = ownerService.create(testData.getMeier());
        ReadPetDto bella = helper.savePetLinkedToOwnerAndToys(testData.getBella(),kahn.getId());

        // when
        String updateOwnerJson = createUpdateJsonRequest(
                createUpdateJsonLine("remove", "/ownerId"),
                createUpdateJsonLine("add", "/ownerId",meier.getId().toString())

        );
        ReadPetDto responseDto = controller.update2xx(updateOwnerJson, bella.getId(), ReadPetDto.class);
        // then
        Assertions.assertEquals(meier.getId(),responseDto.getOwnerId());
        assertOwnerHasPets(KAHN);
        assertOwnerHasPets(MEIER,BELLA);
        assertPetHasOwner(BELLA,MEIER);
    }

    @Test
    public void givenPetLinkedToOwner_whenRemovingPet_thenUnlinkedFromOwner() throws Exception {
        // given
        Owner kahn = ownerService.create(testData.getKahn());
        ReadPetDto createdBellaDto = helper.savePetLinkedToOwnerAndToys(testData.getBella(),kahn.getId());

        // when
        getMvc().perform(controller.delete(createdBellaDto.getId()))
                .andExpect(status().is2xxSuccessful());
        // then
        Assertions.assertFalse(petService.findByName(BELLA).isPresent());
        assertOwnerHasPets(KAHN);
    }

    @Test
    public void givenPetLinkedToToys_whenRemovePet_thenToysUnlinked() throws Exception {
        // given
        Owner kahn = ownerService.create(testData.getKahn());
        Toy ball = toyService.create(testData.getBall());
        Toy bone = toyService.create(testData.getBone());
        Toy rubberDuck = toyService.create(testData.getRubberDuck());
        ReadPetDto bella = helper.savePetLinkedToOwnerAndToys(testData.getBella(),null,ball,rubberDuck,bone);

        // when
        getMvc().perform(controller.delete(bella.getId()))
                .andExpect(status().is2xxSuccessful());
        // then
        Assertions.assertFalse(petService.findByName(BELLA).isPresent());
        assertOwnerHasPets(KAHN);
        assertToyHasPet(RUBBER_DUCK,null);
        assertToyHasPet(BALL,null);
        assertToyHasPet(BONE,null);
    }


}
