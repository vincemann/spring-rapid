package com.github.vincemann.springrapid.coredemo.controller;

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
import com.github.vincemann.springrapid.coretest.util.RapidTestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static com.github.vincemann.ezcompare.Comparator.compare;
import static com.github.vincemann.springrapid.coretest.util.RapidTestUtil.createUpdateJsonLine;
import static com.github.vincemann.springrapid.coretest.util.RapidTestUtil.createUpdateJsonRequest;
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

    @AfterEach
    void tearDown() {
        RapidTestUtil.clear(petService);
        RapidTestUtil.clear(getService());
        RapidTestUtil.clear(petTypeService);
    }



    // SAVE TESTS

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

    @Test
    public void canSaveOwner_linkToPets() throws Exception {
        Pet savedBello = petRepository.save(bello);
        Pet savedKitty = petRepository.save(kitty);


        ReadOwnOwnerDto responseDto = saveOwnerLinkedToPets(kahn,savedBello.getId(),savedKitty.getId());
        Assertions.assertTrue(responseDto.getPetIds().contains(savedBello.getId()));
        Assertions.assertTrue(responseDto.getPetIds().contains(savedKitty.getId()));
        Assertions.assertEquals(2,responseDto.getPetIds().size());

        Owner dbKahn = ownerRepository.findByLastName(KAHN).get();
        Pet dbBello = petRepository.findByName(BELLO).get();
        Pet dbKitty = petRepository.findByName(KITTY).get();
        Assertions.assertEquals(dbBello,dbKahn.getPets().stream().filter(pet -> pet.getName().equals(BELLO)).findFirst().get());
        Assertions.assertEquals(dbKitty,dbKahn.getPets().stream().filter(pet -> pet.getName().equals(KITTY)).findFirst().get());
        Assertions.assertEquals(dbKahn,dbBello.getOwner());
        Assertions.assertEquals(dbKahn,dbKitty.getOwner());
    }

    private ReadOwnOwnerDto saveOwnerLinkedToPets(Owner owner,Long... petIds) throws Exception {
        CreateOwnerDto createOwnerDto = new CreateOwnerDto(owner);
        createOwnerDto.getPetIds().addAll(Lists.newArrayList(petIds));


       return deserialize(getMockMvc().perform(create(createOwnerDto))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse().getContentAsString(),ReadOwnOwnerDto.class);
    }




    // UPDATE TESTS
    @Test
    public void canUpdateOwnersCity() throws Exception {
        ReadOwnOwnerDto createdKahnDto = saveOwnerLinkedToPets(kahn);
        String newCity = kahn.getCity()+"new";

        String updateJson = createUpdateJsonLine("replace", "/city",newCity);
        String jsonResponse = getMockMvc().perform(update(createUpdateJsonRequest(updateJson),createdKahnDto.getId())).andReturn().getResponse().getContentAsString();
        ReadOwnOwnerDto responseDto = deserialize(jsonResponse, ReadOwnOwnerDto.class);
        Assertions.assertEquals(newCity,responseDto.getCity());

        Owner dbKahn = ownerRepository.findByLastName(KAHN).get();
        Assertions.assertEquals(newCity,dbKahn.getCity());
    }

    @Test
    public void canUpdateOwnersCityAndAddressInOneRequest() throws Exception {
        ReadOwnOwnerDto createdKahnDto = saveOwnerLinkedToPets(kahn);
        String newCity = kahn.getCity()+"new";
        String newAdr = kahn.getAddress()+"new";

        String updateCityJson = createUpdateJsonLine("replace", "/city",newCity);
        String updateAdrJson = createUpdateJsonLine("replace", "/address",newAdr);
        String updateJsonRequest = createUpdateJsonRequest(updateCityJson, updateAdrJson);
        System.err.println(updateJsonRequest);
        String jsonResponse = getMockMvc().perform(update(updateJsonRequest,createdKahnDto.getId())).andReturn().getResponse().getContentAsString();
        ReadOwnOwnerDto responseDto = deserialize(jsonResponse, ReadOwnOwnerDto.class);
        Assertions.assertEquals(newCity,responseDto.getCity());
        Assertions.assertEquals(newAdr,responseDto.getAddress());

        Owner dbKahn = ownerRepository.findByLastName(KAHN).get();
        Assertions.assertEquals(newCity,dbKahn.getCity());
        Assertions.assertEquals(newAdr,dbKahn.getAddress());
    }


    @Test
    public void canRemoveOnlyPetFromOwnerExplicitly_viaUpdate() throws Exception {
        Pet savedBello = petRepository.save(bello);
        ReadOwnOwnerDto createdKahnDto = saveOwnerLinkedToPets(kahn,savedBello.getId());

        String updateJson = createUpdateJsonLine("remove", "/petIds", savedBello.getId().toString());
        String jsonResponse = getMockMvc().perform(update(createUpdateJsonRequest(updateJson), createdKahnDto.getId())).andReturn().getResponse().getContentAsString();
        ReadOwnOwnerDto responseDto = deserialize(jsonResponse, ReadOwnOwnerDto.class);
        Assertions.assertTrue(responseDto.getPetIds().isEmpty());

        Owner dbKahn = ownerRepository.findByLastName(KAHN).get();
        Pet dbBello = petRepository.findByName(BELLO).get();
        Assertions.assertTrue(dbKahn.getPets().isEmpty());
        Assertions.assertNull(dbBello.getOwner());
    }

    @Test
    public void canAddPetToOwner_viaUpdate() throws Exception {
        Pet savedBello = petRepository.save(bello);
        ReadOwnOwnerDto createdKahnDto = saveOwnerLinkedToPets(kahn);

        String updateJson = createUpdateJsonLine("add", "/petIds", savedBello.getId().toString());
        String jsonResponse = getMockMvc().perform(update(createUpdateJsonRequest(updateJson),createdKahnDto.getId())).andReturn().getResponse().getContentAsString();
        ReadOwnOwnerDto responseDto = deserialize(jsonResponse, ReadOwnOwnerDto.class);
        Assertions.assertTrue(responseDto.getPetIds().contains(savedBello.getId()));

        Owner dbKahn = ownerRepository.findByLastName(KAHN).get();
        Pet dbBello = petRepository.findByName(BELLO).get();
        Assertions.assertEquals(1,dbKahn.getPets().size());
        Assertions.assertEquals(dbBello,dbKahn.getPets().stream().filter(pet -> pet.getName().equals(BELLO)).findFirst().get());
        Assertions.assertEquals(dbKahn,dbBello.getOwner());

    }



    // DELETE TESTS

    @Test
    public void canDeleteOwner_thusUnlinkFromPet() throws Exception {
        Pet savedBello = petRepository.save(bello);

        ReadOwnOwnerDto responseDto = saveOwnerLinkedToPets(kahn,savedBello.getId());

        MvcResult result = getMockMvc().perform(delete(responseDto.getId()))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Assertions.assertFalse(ownerRepository.findByLastName(KAHN).isPresent());
        Pet dbBello = petRepository.findByName(BELLO).get();
        Assertions.assertNull(dbBello.getOwner());
    }

    @Test
    public void canDeleteOwner_thusUnlinkFromPets() throws Exception {
        Pet savedBello = petRepository.save(bello);
        Pet savedKitty = petRepository.save(kitty);

        ReadOwnOwnerDto responseDto = saveOwnerLinkedToPets(kahn,savedBello.getId(),savedKitty.getId());

        MvcResult result = getMockMvc().perform(delete(responseDto.getId()))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Assertions.assertFalse(ownerRepository.findByLastName(KAHN).isPresent());
        Pet dbBello = petRepository.findByName(BELLO).get();
        Pet dbKitty = petRepository.findByName(KITTY).get();
        Assertions.assertNull(dbBello.getOwner());
        Assertions.assertNull(dbKitty.getOwner());
    }

}
