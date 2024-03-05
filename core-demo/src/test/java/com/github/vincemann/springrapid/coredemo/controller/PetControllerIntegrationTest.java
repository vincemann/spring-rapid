package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.coredemo.controller.suite.MyIntegrationTest;
import com.github.vincemann.springrapid.coredemo.controller.suite.template.PetControllerTestTemplate;
import com.github.vincemann.springrapid.coredemo.dto.owner.ReadOwnOwnerDto;
import com.github.vincemann.springrapid.coredemo.dto.pet.ReadPetDto;
import com.github.vincemann.springrapid.coredemo.model.Owner;
import com.github.vincemann.springrapid.coredemo.model.Pet;
import com.github.vincemann.springrapid.coredemo.model.Toy;
import com.github.vincemann.springrapid.coredemo.service.filter.PetsParentFilter;
import com.github.vincemann.springrapid.coretest.controller.UrlWebExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.vincemann.ezcompare.Comparator.compare;
import static com.github.vincemann.springrapid.coretest.util.RapidTestUtil.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PetControllerIntegrationTest extends MyIntegrationTest
{

    @Autowired
    PetControllerTestTemplate controller;

    @Test
    public void canSavePetWithoutOwner() throws Exception {
        ReadPetDto createPetDto = new ReadPetDto(bella);
        ReadPetDto responseDto = deserialize(getMvc().perform(controller.create(createPetDto))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse().getContentAsString(), ReadPetDto.class);

        compare(createPetDto).with(responseDto)
                .properties()
                .all()
                .ignore(createPetDto::getId)
                .assertEqual();

        Assertions.assertTrue(petRepository.findByName(BELLA).isPresent());
        Pet dbBella = petRepository.findByName(BELLA).get();
        Assertions.assertNull(dbBella.getOwner());

        compare(createPetDto).with(dbBella)
                .properties()
                .include(createPetDto::getName)
                .include(createPetDto::getBirthDate)
                .assertEqual();

        Assertions.assertEquals(responseDto.getId(),dbBella.getId());
    }

    @Test
    public void canFindPetsOfOwner() throws Exception {
        // save kahn and meier
        // kahn has bello and bella
        // meier has kitty
        // find all pets of kahn -> should return bello and bella
        Pet savedBello = petRepository.save(bello);
        Pet savedBella = petRepository.save(bella);
        Pet savedKitty = petRepository.save(kitty);
        ReadOwnOwnerDto savedKahn = saveOwnerLinkedToPets(kahn,savedBello.getId(),savedBella.getId());
        ReadOwnOwnerDto savedMeier = saveOwnerLinkedToPets(meier,savedKitty.getId());

        assertOwnerHasPets(KAHN,BELLO,BELLA);
        assertOwnerHasPets(MEIER,KITTY);
        assertPetHasOwner(BELLO,KAHN);
        assertPetHasOwner(BELLA,KAHN);
        assertPetHasOwner(KITTY,MEIER);

        UrlWebExtension parentFilter = new UrlWebExtension(PetsParentFilter.class,savedKahn.getId().toString());
        String json = perform(controller.findAll(parentFilter))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();

        Set<ReadPetDto> petsOfKahn = deserializeToSet(json, ReadPetDto.class);

        Assertions.assertEquals(2,petsOfKahn.size());
        ReadPetDto belloDto = petsOfKahn.stream().filter(p -> p.getId().equals(savedBello.getId())).findFirst().get();
        ReadPetDto bellaDto = petsOfKahn.stream().filter(p -> p.getId().equals(savedBella.getId())).findFirst().get();

        compare(belloDto).with(savedBello)
                .properties().all()
                .ignore(dtoIdProperties(ReadPetDto.class))
                .assertEqual();

        compare(bellaDto).with(savedBella)
                .properties().all()
                .ignore(dtoIdProperties(ReadPetDto.class))
                .assertEqual();

    }


    @Test
    public void whenSavePetWithSetOwner_thenGetBiDirLinkedToOwner() throws Exception {
        Owner savedKahn = ownerRepository.save(kahn);
        ReadPetDto responseDto = savePetLinkedToOwnerAndToys(bella, savedKahn.getId());
        Assertions.assertEquals(savedKahn.getId(),responseDto.getOwnerId());


        assertOwnerHasPets(KAHN, BELLA);
        assertPetHasOwner(BELLA,KAHN);
    }

    @Test
    public void whenSavePetWithSetToys_thenToysGetBiDirLinked() throws Exception {
        Owner savedKahn = ownerRepository.save(kahn);
        Toy savedBall = toyRepository.save(ball);
        Toy savedBone = toyRepository.save(bone);
        Toy savedRubberDuck = toyRepository.save(rubberDuck);

        ReadPetDto responseDto = savePetLinkedToOwnerAndToys(bella, null,savedBall,savedRubberDuck);
        Assertions.assertNull(responseDto.getOwnerId());
        Assertions.assertTrue(responseDto.getToyIds().contains(savedBall.getId()));
        Assertions.assertTrue(responseDto.getToyIds().contains(savedRubberDuck.getId()));
        Assertions.assertEquals(2,responseDto.getToyIds().size());

        assertOwnerHasPets(KAHN);
        assertPetHasOwner(BELLA,null);
        assertPetHasToys(BELLA,BALL,RUBBER_DUCK);
        assertToyHasPet(BALL,BELLA);
        assertToyHasPet(BONE,null);
        assertToyHasPet(RUBBER_DUCK,BELLA);
    }

    @Test
    public void whenSavePetWithSetOwnerAndToys_thenBiDirLinkedToBoth() throws Exception {
        Owner savedKahn = ownerRepository.save(kahn);
        Toy savedBall = toyRepository.save(ball);
        Toy savedBone = toyRepository.save(bone);
        Toy savedRubberDuck = toyRepository.save(rubberDuck);

        ReadPetDto responseDto = savePetLinkedToOwnerAndToys(bella, savedKahn.getId(),savedBall,savedBone);
        Assertions.assertEquals(savedKahn.getId(),responseDto.getOwnerId());
        Assertions.assertTrue(responseDto.getToyIds().contains(savedBall.getId()));
        Assertions.assertTrue(responseDto.getToyIds().contains(savedBone.getId()));
        Assertions.assertEquals(2,responseDto.getToyIds().size());

        assertPetHasOwner(BELLA,KAHN);
        assertPetHasToys(BELLA,BALL,BONE);
        assertToyHasPet(BALL,BELLA);
        assertToyHasPet(BONE,BELLA);
    }

    @Test
    public void whenUnlinkOwnerOfPetViaUpdate_thenBiDirUnlinked() throws Exception {
        Owner savedKahn = ownerRepository.save(kahn);
        ReadPetDto createdBellaDto = savePetLinkedToOwnerAndToys(bella, savedKahn.getId());
        String removeOwnerJson = createUpdateJsonRequest(createUpdateJsonLine("remove", "/ownerId"));

        ReadPetDto responseDto = deserialize(getMvc().perform(controller.update(removeOwnerJson, createdBellaDto.getId()))
                .andReturn().getResponse().getContentAsString(), ReadPetDto.class);
        Assertions.assertNull(responseDto.getOwnerId());

        assertOwnerHasPets(KAHN);
        assertPetHasOwner(BELLA,null);
    }

    @Test
    public void whenPetsOwnerAndSomeToysUnlinkedViaUpdate_thenBiDirUnlinked() throws Exception {
        // bella -> owner=kahn
        //       -> toys=[ball, bone, rubberDuck]

        // unlink owner from bella
        // unlink bone and rubberDuck from bella

        // result = bella -> owner = null
        //                -> toys = [ball]
        Owner savedKahn = ownerRepository.save(kahn);
        Toy savedBall = toyRepository.save(ball);
        Toy savedBone = toyRepository.save(bone);
        Toy savedRubberDuck = toyRepository.save(rubberDuck);

        ReadPetDto createdBellaDto = savePetLinkedToOwnerAndToys(bella, savedKahn.getId(),savedBall,savedBone,savedRubberDuck);
        String removeOwnerJson = createUpdateJsonRequest(
                createUpdateJsonLine("remove", "/ownerId"),
                createUpdateJsonLine("remove", "/toyIds",savedBone.getId().toString()),
                createUpdateJsonLine("remove", "/toyIds",savedRubberDuck.getId().toString())
        );

        ReadPetDto responseDto = deserialize(getMvc().perform(controller.update(removeOwnerJson, createdBellaDto.getId()))
                .andReturn().getResponse().getContentAsString(), ReadPetDto.class);
        Assertions.assertNull(responseDto.getOwnerId());
        Assertions.assertEquals(1,responseDto.getToyIds().size());
        Assertions.assertTrue(responseDto.getToyIds().contains(savedBall.getId()));

        assertOwnerHasPets(KAHN);
        assertPetHasOwner(BELLA,null);
        assertPetHasToys(BELLA,BALL);
        assertToyHasPet(BALL,BELLA);
        assertToyHasPet(BONE,null);
        assertToyHasPet(RUBBER_DUCK,null);
    }


//    public void canUnlinkOwnerAndAddLazyIllness_viaUpdate(){
//
//    }

    @Test
    public void whenUnlinkingSomePetsAndOwnerViaUpdate_thenAllBiDirUnlinked() throws Exception {
        Owner savedKahn = ownerRepository.save(kahn);
        Toy savedBall = toyRepository.save(ball);
        Toy savedBone = toyRepository.save(bone);
        Toy savedRubberDuck = toyRepository.save(rubberDuck);

        ReadPetDto createdBellaDto = savePetLinkedToOwnerAndToys(bella,savedKahn.getId(),savedRubberDuck);
        String removeOwnerJson = createUpdateJsonRequest(
                createUpdateJsonLine("remove", "/ownerId"),
                createUpdateJsonLine("add", "/toyIds/-",savedBone.getId().toString()),
                createUpdateJsonLine("add", "/toyIds/-",savedBall.getId().toString())
        );

        ReadPetDto responseDto = deserialize(getMvc().perform(controller.update(removeOwnerJson, createdBellaDto.getId()))
                .andReturn().getResponse().getContentAsString(), ReadPetDto.class);
        Assertions.assertNull(responseDto.getOwnerId());
        Assertions.assertEquals(3,responseDto.getToyIds().size());
        Assertions.assertTrue(responseDto.getToyIds().contains(savedBall.getId()));
        Assertions.assertTrue(responseDto.getToyIds().contains(savedBone.getId()));
        Assertions.assertTrue(responseDto.getToyIds().contains(savedRubberDuck.getId()));

        assertOwnerHasPets(KAHN);
        assertPetHasOwner(BELLA,null);
        assertPetHasToys(BELLA,BALL,BONE,RUBBER_DUCK);
        assertToyHasPet(BALL,BELLA);
        assertToyHasPet(BONE,BELLA);
        assertToyHasPet(RUBBER_DUCK,BELLA);
    }

    @Test
    public void whenUnlinkPetsPetTypeViaUpdate_thenBiDirUnlinked() throws Exception {
        ReadPetDto createdBellaDto = savePetLinkedToOwnerAndToys(bella,null);
        String removePetTypeJson = createUpdateJsonRequest(createUpdateJsonLine("remove", "/petTypeId"));

        ReadPetDto responseDto = deserialize(getMvc().perform(controller.update(removePetTypeJson, createdBellaDto.getId()))
                .andReturn().getResponse().getContentAsString(), ReadPetDto.class);
        Assertions.assertNull(responseDto.getPetTypeId());

        Pet dbBella = petRepository.findByName(BELLA).get();
        Assertions.assertNull(dbBella.getPetType());
        Assertions.assertTrue(petTypeRepository.findById(bella.getPetType().getId()).isPresent());
    }

    @Test
    public void whenAddOwnerToPetViaPetUpdate_thenBiDirLinked() throws Exception {
        Owner savedKahn = ownerRepository.save(kahn);
        ReadPetDto createdBellaDto = savePetLinkedToOwnerAndToys(bella,null);
        String addOwnerJson = createUpdateJsonRequest(createUpdateJsonLine("add", "/ownerId",savedKahn.getId().toString()));

        ReadPetDto responseDto = deserialize(getMvc().perform(controller.update(addOwnerJson, createdBellaDto.getId()))
                .andReturn().getResponse().getContentAsString(), ReadPetDto.class);
        Assertions.assertEquals(savedKahn.getId(),responseDto.getOwnerId());

        assertOwnerHasPets(KAHN,BELLA);
        assertPetHasOwner(BELLA,KAHN);
    }

    @Test
    public void whenAddOwnerAndToysToPetViaUpdate_thenAllBiDirLinked() throws Exception {
        Owner savedKahn = ownerRepository.save(kahn);
        Toy savedBall = toyRepository.save(ball);
        Toy savedBone = toyRepository.save(bone);
        Toy savedRubberDuck = toyRepository.save(rubberDuck);

        ReadPetDto createdBellaDto = savePetLinkedToOwnerAndToys(bella,null);
        String updateJson = createUpdateJsonRequest(
                createUpdateJsonLine("add", "/ownerId",savedKahn.getId().toString()),
                createUpdateJsonLine("add", "/toyIds/-",savedBall.getId().toString()),
                createUpdateJsonLine("add", "/toyIds/-",savedBone.getId().toString())

        );

        ReadPetDto responseDto = deserialize(getMvc().perform(controller.update(updateJson, createdBellaDto.getId()))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString(), ReadPetDto.class);
        Assertions.assertEquals(savedKahn.getId(),responseDto.getOwnerId());
        Assertions.assertEquals(2,responseDto.getToyIds().size());
        Assertions.assertTrue(responseDto.getToyIds().contains(savedBall.getId()));
        Assertions.assertTrue(responseDto.getToyIds().contains(savedBone.getId()));

        assertOwnerHasPets(KAHN,BELLA);
        assertPetHasOwner(BELLA,KAHN);

        assertPetHasToys(BELLA,BALL,BONE);
        assertToyHasPet(BALL,BELLA);
        assertToyHasPet(BONE,BELLA);
        assertToyHasPet(RUBBER_DUCK,null);

    }

    @Test
    public void whenAddOwnerAndRemovedToysFromPetViaUpdate_thenOwnerBiDirLinkedAndToysBiDirUnlinked() throws Exception {
        Owner savedKahn = ownerRepository.save(kahn);
        Toy savedBall = toyRepository.save(ball);
        Toy savedBone = toyRepository.save(bone);
        Toy savedRubberDuck = toyRepository.save(rubberDuck);

        ReadPetDto createdBellaDto = savePetLinkedToOwnerAndToys(bella,null,savedBall,savedRubberDuck,savedBone);
        String updateJson = createUpdateJsonRequest(
                createUpdateJsonLine("add", "/ownerId",savedKahn.getId().toString()),
                createUpdateJsonLine("remove", "/toyIds",savedBall.getId().toString()),
                createUpdateJsonLine("remove", "/toyIds",savedRubberDuck.getId().toString())

        );

        ReadPetDto responseDto = deserialize(getMvc().perform(controller.update(updateJson, createdBellaDto.getId()))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString(), ReadPetDto.class);
        Assertions.assertEquals(savedKahn.getId(),responseDto.getOwnerId());
        Assertions.assertEquals(1,responseDto.getToyIds().size());
        Assertions.assertTrue(responseDto.getToyIds().contains(savedBone.getId()));

        assertOwnerHasPets(KAHN,BELLA);
        assertPetHasOwner(BELLA,KAHN);

        assertPetHasToys(BELLA,BONE);
        assertToyHasPet(BONE,BELLA);
        assertToyHasPet(BALL,null);
        assertToyHasPet(RUBBER_DUCK,null);

    }

    @Test
    public void whenAddPetTypeToPetViaUpdate_thenBiDirLinked() throws Exception {
        bella.setPetType(null);
        ReadPetDto createdBellaDto = savePetLinkedToOwnerAndToys(bella,null);
        String addPetTypeJson = createUpdateJsonRequest(createUpdateJsonLine("add", "/petTypeId",savedCatPetType.getId().toString()));

        ReadPetDto responseDto = deserialize(getMvc().perform(controller.update(addPetTypeJson, createdBellaDto.getId()))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString(), ReadPetDto.class);
        Assertions.assertEquals(savedCatPetType.getId(),responseDto.getPetTypeId());

        Pet dbBella = petRepository.findByName(BELLA).get();
        com.github.vincemann.springrapid.coredemo.model.PetType dbPetType = petTypeRepository.findById(savedCatPetType.getId()).get();
        Assertions.assertEquals(dbPetType,dbBella.getPetType());
    }

    @Test
    public void whenPetsOwnerReplacedViaReplaceOpUpdate_thenOldOwnerBiDirUnlinkedAndNewOwnerBiDirLinked() throws Exception {
        Owner savedKahn = ownerRepository.save(kahn);
        Owner savedMeier = ownerRepository.save(meier);

        ReadPetDto createdBellaDto = savePetLinkedToOwnerAndToys(bella,savedKahn.getId());
        String updateOwnerJson = createUpdateJsonRequest(
                createUpdateJsonLine("replace", "/ownerId",savedMeier.getId().toString())
        );

        ReadPetDto responseDto = deserialize(getMvc().perform(controller.update(updateOwnerJson, createdBellaDto.getId()))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString(), ReadPetDto.class);
        Assertions.assertEquals(savedMeier.getId(),responseDto.getOwnerId());

        assertOwnerHasPets(KAHN);
        assertOwnerHasPets(MEIER,BELLA);
        assertPetHasOwner(BELLA,MEIER);
    }

    @Test
    public void whenPetsOwnerReplacedViaRemoveAndAddOpUpdate_thenOldOwnerBiDirUnlinkedAndNewOwnerBiDirLinked() throws Exception {
        // bellas owner was kahn
        // remove owner kahn from bella and add new owner meier
        Owner savedKahn = ownerRepository.save(kahn);
        Owner savedMeier = ownerRepository.save(meier);

        ReadPetDto createdBellaDto = savePetLinkedToOwnerAndToys(bella,savedKahn.getId());
        String updateOwnerJson = createUpdateJsonRequest(
                createUpdateJsonLine("remove", "/ownerId"),
                createUpdateJsonLine("add", "/ownerId",savedMeier.getId().toString())

        );

        ReadPetDto responseDto = deserialize(getMvc().perform(controller.update(updateOwnerJson, createdBellaDto.getId()))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString(), ReadPetDto.class);
        Assertions.assertEquals(savedMeier.getId(),responseDto.getOwnerId());

        assertOwnerHasPets(KAHN);
        assertOwnerHasPets(MEIER,BELLA);
        assertPetHasOwner(BELLA,MEIER);
    }

    @Test
    public void givenPetLinkedBiDirToOwner_whenRemovingPetViaRemove_thenBiDirUnlinked() throws Exception {
        Owner savedKahn = ownerRepository.save(kahn);
        ReadPetDto createdBellaDto = savePetLinkedToOwnerAndToys(bella,savedKahn.getId());

        getMvc().perform(controller.delete(createdBellaDto.getId()))
                .andExpect(status().is2xxSuccessful());

        Assertions.assertFalse(petRepository.findByName(BELLA).isPresent());
        assertOwnerHasPets(KAHN);
    }

    @Test
    public void givenPetBiDirLinkedToToysAndOwner_whenRemovePetViaRemove_thenAllBiDirUnlinked() throws Exception {
        Toy savedBall = toyRepository.save(ball);
        Toy savedBone = toyRepository.save(bone);
        Toy savedRubberDuck = toyRepository.save(rubberDuck);

        Owner savedKahn = ownerRepository.save(kahn);
        ReadPetDto createdBellaDto = savePetLinkedToOwnerAndToys(bella,savedKahn.getId(),savedBall,savedBone,savedRubberDuck);

        getMvc().perform(controller.delete(createdBellaDto.getId()))
                .andExpect(status().is2xxSuccessful());

        Assertions.assertFalse(petRepository.findByName(BELLA).isPresent());
        assertOwnerHasPets(KAHN);
        assertToyHasPet(RUBBER_DUCK,null);
        assertToyHasPet(BALL,null);
        assertToyHasPet(BONE,null);
    }



    private ReadPetDto savePetLinkedToOwnerAndToys(Pet pet, Long ownerId, Toy... toys) throws Exception {
        ReadPetDto createPetDto = new ReadPetDto(pet);
        if (ownerId != null)
            createPetDto.setOwnerId(ownerId);
        if (toys.length > 0)
            createPetDto.setToyIds(Arrays.stream(toys).map(Toy::getId).collect(Collectors.toSet()));

        return deserialize(getMvc().perform(controller.create(createPetDto))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse().getContentAsString(), ReadPetDto.class);
    }

}
