package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.coredemo.dtos.pet.PetDto;
import com.github.vincemann.springrapid.coredemo.dtos.pet.UpdatePetDto;
import com.github.vincemann.springrapid.coredemo.model.Owner;
import com.github.vincemann.springrapid.coredemo.model.Pet;
import com.github.vincemann.springrapid.coredemo.service.PetService;
import com.github.vincemann.springrapid.coretest.util.RapidTestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static com.github.vincemann.ezcompare.Comparator.compare;
import static com.github.vincemann.springrapid.coretest.util.RapidTestUtil.createUpdateJsonLine;
import static com.github.vincemann.springrapid.coretest.util.RapidTestUtil.createUpdateJsonRequest;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    public void canRemovePetsOwner_viaUpdate() throws Exception {
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

    // todo does not work yet bc i cant get full update to work with bidir relship mangement yet
    @Test
    public void canAddPetsOwner_viaUpdate() throws Exception {
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
