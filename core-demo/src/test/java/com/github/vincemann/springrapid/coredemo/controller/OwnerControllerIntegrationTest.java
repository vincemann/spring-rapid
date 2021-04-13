package com.github.vincemann.springrapid.coredemo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import com.github.vincemann.springrapid.core.util.Lists;
import com.github.vincemann.springrapid.coredemo.dtos.owner.CreateOwnerDto;
import com.github.vincemann.springrapid.coredemo.dtos.owner.ReadOwnOwnerDto;
import com.github.vincemann.springrapid.coredemo.model.Owner;
import com.github.vincemann.springrapid.coredemo.model.Pet;
import com.github.vincemann.springrapid.coredemo.model.PetType;
import com.github.vincemann.springrapid.coredemo.repo.OwnerRepository;
import com.github.vincemann.springrapid.coredemo.repo.PetRepository;
import com.github.vincemann.springrapid.coredemo.repo.PetTypeRepository;
import com.github.vincemann.springrapid.coredemo.service.OwnerService;
import com.github.vincemann.springrapid.coredemo.service.PetService;
import com.github.vincemann.springrapid.coredemo.service.PetTypeService;
import com.github.vincemann.springrapid.coredemo.service.plugin.OwnerOfTheYearExtension;
import com.github.vincemann.springrapid.coretest.controller.urlparamid.IntegrationUrlParamIdControllerTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.HashSet;

import static com.github.vincemann.ezcompare.Comparator.compare;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class OwnerControllerIntegrationTest
        extends IntegrationUrlParamIdControllerTest<OwnerController,Long, OwnerService> {

    //Types
    Owner OwnerType = new Owner();

    protected static final String MEIER = "Meier";
    protected static final String KAHN = "Kahn";

    protected static final String BELLO = "Bello";
    protected static final String BELLA = "Bella";
    protected static final String KITTY = "Kitty";

    Owner meier;
    Owner kahn;

    Pet bello;
    Pet kitty;
    Pet bella;

    PetType savedDogPetType;
    PetType savedCatPetType;

    @SpyBean
    OwnerOfTheYearExtension ownerOfTheYearExtension;

    @Autowired
    PetService petService;
    @Autowired
    PetRepository petRepository;

    @Autowired
    PetTypeService petTypeService;
    @Autowired
    PetTypeRepository petTypeRepository;

    @Autowired
    OwnerRepository ownerRepository;

    @Autowired
    RapidSecurityContext<RapidAuthenticatedPrincipal> securityContext;

    @BeforeEach
    public void setupTestData() throws Exception {
        savedDogPetType = petTypeService.save(new PetType("Dog"));
        savedCatPetType = petTypeService.save(new PetType("Cat"));

        bello = Pet.builder()
                .petType(savedDogPetType)
                .name(BELLO)
                .birthDate(LocalDate.now())
                .build();

        bella = Pet.builder()
                .petType(savedDogPetType)
                .name(BELLA)
                .birthDate(LocalDate.now())
                .build();

        kitty = Pet.builder()
                .petType(savedCatPetType)
                .name(KITTY)
                .birthDate(LocalDate.now())
                .build();

        meier = Owner.builder()
                .firstName("Max")
                .lastName(MEIER)
                .address("asljnflksamfslkmf")
                .city("n1 city")
                .telephone("0123456789")
                .build();

        kahn = Owner.builder()
                .firstName("Olli")
                .lastName(KAHN)
                .address("asljnflksamfslkmf")
                .city("n1 city")
                .telephone("1234567890")
//                .pets(new HashSet<>(Lists.newArrayList(bello)))
                .build();
    }

    @Test
    public void canSaveOwnerWithoutPets() throws Exception {
        CreateOwnerDto createKahnDto = new CreateOwnerDto(kahn);


        MvcResult result = getMockMvc().perform(create(createKahnDto))
                .andExpect(status().isOk())
                .andReturn();
        String json = result.getResponse().getContentAsString();
        System.err.println(json);


        ReadOwnOwnerDto responseDto = deserialize(json, ReadOwnOwnerDto.class);
        compare(createKahnDto).with(responseDto)
                .properties()
                .all()
                .ignore(OwnerType::getId)
                .assertEqual();
        Assertions.assertTrue(ownerRepository.findByLastName(KAHN).isPresent());
        Owner dbKahn = ownerRepository.findByLastName(KAHN).get();

        compare(createKahnDto).with(dbKahn)
                .properties()
                .all()
                .ignore(OwnerType::getId)
                .ignore(createKahnDto::getPetIds)
                .assertEqual();

        Assertions.assertNotNull(dbKahn.getId());
    }

    @Test
    public void canSaveOwner_linkToPet() throws Exception {
        Pet savedBello = petRepository.save(bello);


        ReadOwnOwnerDto responseDto = saveOwnerLinkedToPets(kahn,savedBello.getId());
        Assertions.assertTrue(responseDto.getPetIds().contains(savedBello.getId()));

        Owner dbKahn = ownerRepository.findByLastName(KAHN).get();
        Pet dbBello = petRepository.findByName(BELLO).get();
        Assertions.assertEquals(dbBello,dbKahn.getPets().stream().findFirst().get());
        Assertions.assertEquals(dbKahn,dbBello.getOwner());
    }

    private ReadOwnOwnerDto saveOwnerLinkedToPets(Owner owner,Long... petIds) throws Exception {
        CreateOwnerDto createOwnerDto = new CreateOwnerDto(owner);
        createOwnerDto.getPetIds().addAll(Lists.newArrayList(petIds));


       return deserialize(getMockMvc().perform(create(createOwnerDto))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString(),ReadOwnOwnerDto.class);
    }

    @Test
    public void canDeleteOwner_thusUnlinkFromPet() throws Exception {
        Pet savedBello = petRepository.save(bello);

        ReadOwnOwnerDto responseDto = saveOwnerLinkedToPets(kahn,savedBello.getId());

        MvcResult result = getMockMvc().perform(delete(responseDto.getId()))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertFalse(ownerRepository.findByLastName(KAHN).isPresent());
        Pet dbBello = petRepository.findByName(BELLO).get();
        Assertions.assertNull(dbBello.getOwner());
    }

}
