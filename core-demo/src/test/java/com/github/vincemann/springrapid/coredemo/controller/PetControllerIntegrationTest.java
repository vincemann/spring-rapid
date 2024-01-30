package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.coredemo.controller.template.PetControllerTestTemplate;
import com.github.vincemann.springrapid.coredemo.dto.owner.ReadOwnOwnerDto;
import com.github.vincemann.springrapid.coredemo.dto.pet.PetDto;
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

public class PetControllerIntegrationTest extends MyControllerIntegrationTest
{

    @Autowired
    PetControllerTestTemplate controller;

    @Test
    public void canSavePetWithoutOwner() throws Exception {
        PetDto createPetDto = new PetDto(bella);
        PetDto responseDto = deserialize(getMvc().perform(controller.create(createPetDto))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse().getContentAsString(), PetDto.class);

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

        Set<PetDto> petsOfKahn = deserializeToSet(json, PetDto.class);

        Assertions.assertEquals(2,petsOfKahn.size());
        PetDto belloDto = petsOfKahn.stream().filter(p -> p.getId().equals(savedBello.getId())).findFirst().get();
        PetDto bellaDto = petsOfKahn.stream().filter(p -> p.getId().equals(savedBella.getId())).findFirst().get();

        compare(belloDto).with(savedBello)
                .properties().all()
                .ignore(dtoIdProperties(PetDto.class))
                .assertEqual();

        compare(bellaDto).with(savedBella)
                .properties().all()
                .ignore(dtoIdProperties(PetDto.class))
                .assertEqual();

    }


    @Test
    public void canSavePet_thusGetLinkedToOwner() throws Exception {
        Owner savedKahn = ownerRepository.save(kahn);
        PetDto responseDto = savePetLinkedToOwnerAndToys(bella, savedKahn.getId());
        Assertions.assertEquals(savedKahn.getId(),responseDto.getOwnerId());


        assertOwnerHasPets(KAHN, BELLA);
        assertPetHasOwner(BELLA,KAHN);
    }

    @Test
    public void canSavePet_thusGetLinkedToToys() throws Exception {
        Owner savedKahn = ownerRepository.save(kahn);
        Toy savedBall = toyRepository.save(ball);
        Toy savedBone = toyRepository.save(bone);
        Toy savedRubberDuck = toyRepository.save(rubberDuck);

        PetDto responseDto = savePetLinkedToOwnerAndToys(bella, null,savedBall,savedRubberDuck);
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
    public void canSavePet_thusGetLinkedToOwnerAndToys() throws Exception {
        Owner savedKahn = ownerRepository.save(kahn);
        Toy savedBall = toyRepository.save(ball);
        Toy savedBone = toyRepository.save(bone);
        Toy savedRubberDuck = toyRepository.save(rubberDuck);

        PetDto responseDto = savePetLinkedToOwnerAndToys(bella, savedKahn.getId(),savedBall,savedBone);
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
    public void canUnlinkPetsOwner_viaUpdate() throws Exception {
        Owner savedKahn = ownerRepository.save(kahn);
        PetDto createdBellaDto = savePetLinkedToOwnerAndToys(bella, savedKahn.getId());
        String removeOwnerJson = createUpdateJsonRequest(createUpdateJsonLine("remove", "/ownerId"));

        PetDto responseDto = deserialize(getMvc().perform(controller.update(removeOwnerJson, createdBellaDto.getId()))
                .andReturn().getResponse().getContentAsString(), PetDto.class);
        Assertions.assertNull(responseDto.getOwnerId());

        assertOwnerHasPets(KAHN);
        assertPetHasOwner(BELLA,null);
    }

    @Test
    public void canUnlinkPetsOwnerAndSomeToys_viaUpdate() throws Exception {
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

        PetDto createdBellaDto = savePetLinkedToOwnerAndToys(bella, savedKahn.getId(),savedBall,savedBone,savedRubberDuck);
        String removeOwnerJson = createUpdateJsonRequest(
                createUpdateJsonLine("remove", "/ownerId"),
                createUpdateJsonLine("remove", "/toyIds",savedBone.getId().toString()),
                createUpdateJsonLine("remove", "/toyIds",savedRubberDuck.getId().toString())
        );

        PetDto responseDto = deserialize(getMvc().perform(controller.update(removeOwnerJson, createdBellaDto.getId()))
                .andReturn().getResponse().getContentAsString(), PetDto.class);
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
    public void canUnlinkPetsOwnerAndAddSomeToys_viaUpdate() throws Exception {
        Owner savedKahn = ownerRepository.save(kahn);
        Toy savedBall = toyRepository.save(ball);
        Toy savedBone = toyRepository.save(bone);
        Toy savedRubberDuck = toyRepository.save(rubberDuck);

        PetDto createdBellaDto = savePetLinkedToOwnerAndToys(bella,savedKahn.getId(),savedRubberDuck);
        String removeOwnerJson = createUpdateJsonRequest(
                createUpdateJsonLine("remove", "/ownerId"),
                createUpdateJsonLine("add", "/toyIds/-",savedBone.getId().toString()),
                createUpdateJsonLine("add", "/toyIds/-",savedBall.getId().toString())
        );

        PetDto responseDto = deserialize(getMvc().perform(controller.update(removeOwnerJson, createdBellaDto.getId()))
                .andReturn().getResponse().getContentAsString(), PetDto.class);
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
    public void canUnlinkPetsPetType_viaUpdate() throws Exception {
        PetDto createdBellaDto = savePetLinkedToOwnerAndToys(bella,null);
        String removePetTypeJson = createUpdateJsonRequest(createUpdateJsonLine("remove", "/petTypeId"));

        PetDto responseDto = deserialize(getMvc().perform(controller.update(removePetTypeJson, createdBellaDto.getId()))
                .andReturn().getResponse().getContentAsString(), PetDto.class);
        Assertions.assertNull(responseDto.getPetTypeId());

        Pet dbBella = petRepository.findByName(BELLA).get();
        Assertions.assertNull(dbBella.getPetType());
        Assertions.assertTrue(petTypeRepository.findById(bella.getPetType().getId()).isPresent());
    }

    @Test
    public void canLinkPetsOwner_viaUpdate() throws Exception {
        Owner savedKahn = ownerRepository.save(kahn);
        PetDto createdBellaDto = savePetLinkedToOwnerAndToys(bella,null);
        String addOwnerJson = createUpdateJsonRequest(createUpdateJsonLine("add", "/ownerId",savedKahn.getId().toString()));

        PetDto responseDto = deserialize(getMvc().perform(controller.update(addOwnerJson, createdBellaDto.getId()))
                .andReturn().getResponse().getContentAsString(), PetDto.class);
        Assertions.assertEquals(savedKahn.getId(),responseDto.getOwnerId());

        assertOwnerHasPets(KAHN,BELLA);
        assertPetHasOwner(BELLA,KAHN);
    }

    @Test
    public void canLinkPetsOwner_andSomeToys_viaUpdate() throws Exception {
        Owner savedKahn = ownerRepository.save(kahn);
        Toy savedBall = toyRepository.save(ball);
        Toy savedBone = toyRepository.save(bone);
        Toy savedRubberDuck = toyRepository.save(rubberDuck);

        PetDto createdBellaDto = savePetLinkedToOwnerAndToys(bella,null);
        String updateJson = createUpdateJsonRequest(
                createUpdateJsonLine("add", "/ownerId",savedKahn.getId().toString()),
                createUpdateJsonLine("add", "/toyIds/-",savedBall.getId().toString()),
                createUpdateJsonLine("add", "/toyIds/-",savedBone.getId().toString())

        );

        PetDto responseDto = deserialize(getMvc().perform(controller.update(updateJson, createdBellaDto.getId()))
                .andReturn().getResponse().getContentAsString(), PetDto.class);
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
    public void canLinkPetsOwner_andRemoveSomeToys_viaUpdate() throws Exception {
        Owner savedKahn = ownerRepository.save(kahn);
        Toy savedBall = toyRepository.save(ball);
        Toy savedBone = toyRepository.save(bone);
        Toy savedRubberDuck = toyRepository.save(rubberDuck);

        PetDto createdBellaDto = savePetLinkedToOwnerAndToys(bella,null,savedBall,savedRubberDuck,savedBone);
        String updateJson = createUpdateJsonRequest(
                createUpdateJsonLine("add", "/ownerId",savedKahn.getId().toString()),
                createUpdateJsonLine("remove", "/toyIds",savedBall.getId().toString()),
                createUpdateJsonLine("remove", "/toyIds",savedRubberDuck.getId().toString())

        );

        PetDto responseDto = deserialize(getMvc().perform(controller.update(updateJson, createdBellaDto.getId()))
                .andReturn().getResponse().getContentAsString(), PetDto.class);
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
    public void canLinkPetsPetType_viaUpdate() throws Exception {
        bella.setPetType(null);
        PetDto createdBellaDto = savePetLinkedToOwnerAndToys(bella,null);
        String addPetTypeJson = createUpdateJsonRequest(createUpdateJsonLine("add", "/petTypeId",savedCatPetType.getId().toString()));

        PetDto responseDto = deserialize(getMvc().perform(controller.update(addPetTypeJson, createdBellaDto.getId()))
                .andReturn().getResponse().getContentAsString(), PetDto.class);
        Assertions.assertEquals(savedCatPetType.getId(),responseDto.getPetTypeId());

        Pet dbBella = petRepository.findByName(BELLA).get();
        com.github.vincemann.springrapid.coredemo.model.PetType dbPetType = petTypeRepository.findById(savedCatPetType.getId()).get();
        Assertions.assertEquals(dbPetType,dbBella.getPetType());
    }

    @Test
    public void canChangePetsOwner_viaUpdateReplace() throws Exception {
        Owner savedKahn = ownerRepository.save(kahn);
        Owner savedMeier = ownerRepository.save(meier);

        PetDto createdBellaDto = savePetLinkedToOwnerAndToys(bella,savedKahn.getId());
        String updateOwnerJson = createUpdateJsonRequest(
                createUpdateJsonLine("replace", "/ownerId",savedMeier.getId().toString())
        );

        PetDto responseDto = deserialize(getMvc().perform(controller.update(updateOwnerJson, createdBellaDto.getId()))
                .andReturn().getResponse().getContentAsString(), PetDto.class);
        Assertions.assertEquals(savedMeier.getId(),responseDto.getOwnerId());

        assertOwnerHasPets(KAHN);
        assertOwnerHasPets(MEIER,BELLA);
        assertPetHasOwner(BELLA,MEIER);
    }

    @Test
    public void canChangePetsOwner_viaUpdateRemoveAdd() throws Exception {
        // bellas owner was kahn
        // remove owner kahn from bella and add new owner meier
        Owner savedKahn = ownerRepository.save(kahn);
        Owner savedMeier = ownerRepository.save(meier);

        PetDto createdBellaDto = savePetLinkedToOwnerAndToys(bella,savedKahn.getId());
        String updateOwnerJson = createUpdateJsonRequest(
                createUpdateJsonLine("remove", "/ownerId"),
                createUpdateJsonLine("add", "/ownerId",savedMeier.getId().toString())

        );

        PetDto responseDto = deserialize(getMvc().perform(controller.update(updateOwnerJson, createdBellaDto.getId()))
                .andReturn().getResponse().getContentAsString(), PetDto.class);
        Assertions.assertEquals(savedMeier.getId(),responseDto.getOwnerId());

        assertOwnerHasPets(KAHN);
        assertOwnerHasPets(MEIER,BELLA);
        assertPetHasOwner(BELLA,MEIER);
    }

    @Test
    public void canDeletePet_getUnlinkedFromOwner() throws Exception {
        Owner savedKahn = ownerRepository.save(kahn);
        PetDto createdBellaDto = savePetLinkedToOwnerAndToys(bella,savedKahn.getId());

        getMvc().perform(controller.delete(createdBellaDto.getId()))
                .andExpect(status().is2xxSuccessful());

        Assertions.assertFalse(petRepository.findByName(BELLA).isPresent());
        assertOwnerHasPets(KAHN);
    }

    @Test
    public void canDeletePet_getUnlinkedFromOwnerAndToys() throws Exception {
        Toy savedBall = toyRepository.save(ball);
        Toy savedBone = toyRepository.save(bone);
        Toy savedRubberDuck = toyRepository.save(rubberDuck);

        Owner savedKahn = ownerRepository.save(kahn);
        PetDto createdBellaDto = savePetLinkedToOwnerAndToys(bella,savedKahn.getId(),savedBall,savedBone,savedRubberDuck);

        getMvc().perform(controller.delete(createdBellaDto.getId()))
                .andExpect(status().is2xxSuccessful());

        Assertions.assertFalse(petRepository.findByName(BELLA).isPresent());
        assertOwnerHasPets(KAHN);
        assertToyHasPet(RUBBER_DUCK,null);
        assertToyHasPet(BALL,null);
        assertToyHasPet(BONE,null);
    }



    private PetDto savePetLinkedToOwnerAndToys(Pet pet, Long ownerId, Toy... toys) throws Exception {
        PetDto createPetDto = new PetDto(pet);
        if (ownerId != null)
            createPetDto.setOwnerId(ownerId);
        if (toys.length > 0)
            createPetDto.setToyIds(Arrays.stream(toys).map(Toy::getId).collect(Collectors.toSet()));

        return deserialize(getMvc().perform(controller.create(createPetDto))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse().getContentAsString(),PetDto.class);
    }

}
