package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.coredemo.dtos.pet.PetDto;
import com.github.vincemann.springrapid.coredemo.model.Owner;
import com.github.vincemann.springrapid.coredemo.model.Pet;
import com.github.vincemann.springrapid.coredemo.model.PetType;
import com.github.vincemann.springrapid.coredemo.service.PetService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.github.vincemann.ezcompare.Comparator.compare;
import static com.github.vincemann.springrapid.coretest.util.RapidTestUtil.createUpdateJsonLine;
import static com.github.vincemann.springrapid.coretest.util.RapidTestUtil.createUpdateJsonRequest;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PetControllerIntegrationTest extends MyControllerIntegrationTest<PetController, PetService>
{

    @Test
    public void canSavePetWithoutOwner() throws Exception {
        PetDto createPetDto = new PetDto(bella);
        PetDto responseDto = deserialize(getMockMvc().perform(create(createPetDto))
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
                .all()
                .ignore(createPetDto::getId)
                .ignore(createPetDto::getPetTypeId)
                .ignore(createPetDto::getOwnerId)
                .assertEqual();

        Assertions.assertEquals(responseDto.getId(),dbBella.getId());
    }

    @Test
    public void canSavePet_thusGetLinkedToOwner() throws Exception {
        Owner savedKahn = ownerRepository.save(kahn);
        PetDto responseDto = savePetLinkedToOwner(bella, savedKahn.getId());
        Assertions.assertEquals(savedKahn.getId(),responseDto.getOwnerId());

        Pet dbBella = petRepository.findByName(BELLA).get();
        Owner dbKahn = ownerRepository.findByLastName(KAHN).get();

        Assertions.assertEquals(dbKahn,dbBella.getOwner());
        Assertions.assertEquals(dbBella,dbKahn.getPets().stream().filter(pet -> pet.getName().equals(BELLA)).findFirst().get());
        Assertions.assertEquals(1,dbKahn.getPets().size());
    }

    @Test
    public void canUnlinkPetsOwner_viaUpdate() throws Exception {
        Owner savedKahn = ownerRepository.save(kahn);
        PetDto createdBellaDto = savePetLinkedToOwner(bella, savedKahn.getId());
        String removeOwnerJson = createUpdateJsonRequest(createUpdateJsonLine("remove", "/ownerId"));

        PetDto responseDto = deserialize(getMockMvc().perform(update(removeOwnerJson, createdBellaDto.getId()))
                .andReturn().getResponse().getContentAsString(), PetDto.class);
        Assertions.assertNull(responseDto.getOwnerId());

        Pet dbBella = petRepository.findByName(BELLA).get();
        Owner dbKahn = ownerRepository.findByLastName(KAHN).get();

        Assertions.assertNull(dbBella.getOwner());
        Assertions.assertTrue(dbKahn.getPets().isEmpty());
    }

    @Test
    public void canUnlinkPetsPetType_viaUpdate() throws Exception {
        PetDto createdBellaDto = savePetLinkedToOwner(bella);
        String removePetTypeJson = createUpdateJsonRequest(createUpdateJsonLine("remove", "/petTypeId"));

        PetDto responseDto = deserialize(getMockMvc().perform(update(removePetTypeJson, createdBellaDto.getId()))
                .andReturn().getResponse().getContentAsString(), PetDto.class);
        Assertions.assertNull(responseDto.getPetTypeId());

        Pet dbBella = petRepository.findByName(BELLA).get();
        Assertions.assertNull(dbBella.getPetType());
        Assertions.assertTrue(petTypeRepository.findById(bella.getPetType().getId()).isPresent());
    }

    @Test
    public void canLinkPetsOwner_viaUpdate() throws Exception {
        Owner savedKahn = ownerRepository.save(kahn);
        PetDto createdBellaDto = savePetLinkedToOwner(bella);
        String addOwnerJson = createUpdateJsonRequest(createUpdateJsonLine("add", "/ownerId",savedKahn.getId().toString()));

        PetDto responseDto = deserialize(getMockMvc().perform(update(addOwnerJson, createdBellaDto.getId()))
                .andReturn().getResponse().getContentAsString(), PetDto.class);
        Assertions.assertEquals(savedKahn.getId(),responseDto.getOwnerId());

        Pet dbBella = petRepository.findByName(BELLA).get();
        Owner dbKahn = ownerRepository.findByLastName(KAHN).get();

        Assertions.assertEquals(dbKahn,dbBella.getOwner());
        Assertions.assertEquals(1,dbKahn.getPets().size());
        Assertions.assertEquals(dbBella,dbKahn.getPets().stream().filter(pet -> pet.getName().equals(BELLA)).findFirst().get());
    }

    @Test
    public void canLinkPetsPetType_viaUpdate() throws Exception {
        bella.setPetType(null);
        PetDto createdBellaDto = savePetLinkedToOwner(bella);
        String addPetTypeJson = createUpdateJsonRequest(createUpdateJsonLine("add", "/petTypeId",savedCatPetType.getId().toString()));

        PetDto responseDto = deserialize(getMockMvc().perform(update(addPetTypeJson, createdBellaDto.getId()))
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

        PetDto createdBellaDto = savePetLinkedToOwner(bella,savedKahn.getId());
        String updateOwnerJson = createUpdateJsonRequest(createUpdateJsonLine("replace", "/ownerId",savedMeier.getId().toString()));

        PetDto responseDto = deserialize(getMockMvc().perform(update(updateOwnerJson, createdBellaDto.getId()))
                .andReturn().getResponse().getContentAsString(), PetDto.class);
        Assertions.assertEquals(savedMeier.getId(),responseDto.getOwnerId());

        Pet dbBella = petRepository.findByName(BELLA).get();
        Owner dbKahn = ownerRepository.findByLastName(KAHN).get();
        Owner dbMeier = ownerRepository.findByLastName(MEIER).get();


        Assertions.assertEquals(dbMeier,dbBella.getOwner());
        Assertions.assertEquals(1,dbMeier.getPets().size());
        Assertions.assertEquals(dbBella,dbMeier.getPets().stream().filter(pet -> pet.getName().equals(BELLA)).findFirst().get());

        Assertions.assertTrue(dbKahn.getPets().isEmpty());
    }

    @Test
    public void canChangePetsOwner_viaUpdateRemoveAdd() throws Exception {
        Owner savedKahn = ownerRepository.save(kahn);
        Owner savedMeier = ownerRepository.save(meier);

        PetDto createdBellaDto = savePetLinkedToOwner(bella,savedKahn.getId());
        String updateOwnerJson = createUpdateJsonRequest(
                createUpdateJsonLine("remove", "/ownerId"),
                createUpdateJsonLine("add", "/ownerId",savedMeier.getId().toString())

        );

        PetDto responseDto = deserialize(getMockMvc().perform(update(updateOwnerJson, createdBellaDto.getId()))
                .andReturn().getResponse().getContentAsString(), PetDto.class);
        Assertions.assertEquals(savedMeier.getId(),responseDto.getOwnerId());

        Pet dbBella = petRepository.findByName(BELLA).get();
        Owner dbKahn = ownerRepository.findByLastName(KAHN).get();
        Owner dbMeier = ownerRepository.findByLastName(MEIER).get();


        Assertions.assertEquals(dbMeier,dbBella.getOwner());
        Assertions.assertEquals(1,dbMeier.getPets().size());
        Assertions.assertEquals(dbBella,dbMeier.getPets().stream().filter(pet -> pet.getName().equals(BELLA)).findFirst().get());

        Assertions.assertTrue(dbKahn.getPets().isEmpty());
    }

    @Test
    public void canDeletePet_getUnlinkedFromOwner() throws Exception {
        Owner savedKahn = ownerRepository.save(kahn);
        PetDto createdBellaDto = savePetLinkedToOwner(bella,savedKahn.getId());

        getMockMvc().perform(delete(createdBellaDto.getId()))
                .andExpect(status().is2xxSuccessful());

        Assertions.assertFalse(petRepository.findByName(BELLA).isPresent());
        Owner dbKahn = ownerRepository.findByLastName(KAHN).get();
        Assertions.assertTrue(dbKahn.getPets().isEmpty());
    }



    private PetDto savePetLinkedToOwner(Pet pet, Long... ownerId) throws Exception {
        PetDto createPetDto = new PetDto(pet);
        if (ownerId.length==1)
            createPetDto.setOwnerId(ownerId[0]);
        else if(ownerId.length>1){
            throw new IllegalArgumentException();
        }

        return deserialize(getMockMvc().perform(create(createPetDto))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse().getContentAsString(),PetDto.class);
    }

}
