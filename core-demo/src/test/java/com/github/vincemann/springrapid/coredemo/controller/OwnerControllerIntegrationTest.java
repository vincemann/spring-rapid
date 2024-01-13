package com.github.vincemann.springrapid.coredemo.controller;

import com.fasterxml.jackson.databind.type.CollectionType;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import com.github.vincemann.springrapid.core.util.Lists;
import com.github.vincemann.springrapid.coredemo.dto.owner.CreateOwnerDto;
import com.github.vincemann.springrapid.coredemo.dto.owner.ReadForeignOwnerDto;
import com.github.vincemann.springrapid.coredemo.dto.owner.ReadOwnOwnerDto;
import com.github.vincemann.springrapid.coredemo.model.ClinicCard;
import com.github.vincemann.springrapid.coredemo.model.Owner;
import com.github.vincemann.springrapid.coredemo.model.Pet;
import com.github.vincemann.springrapid.coredemo.service.OwnerService;
import com.github.vincemann.springrapid.coredemo.service.filter.CityPrefixFilter;
import com.github.vincemann.springrapid.coredemo.service.filter.HasPetsFilter;
import com.github.vincemann.springrapid.coredemo.service.filter.OwnerTelNumberFilter;
import com.github.vincemann.springrapid.coredemo.service.filter.PetNameEndsWithFilter;
import com.github.vincemann.springrapid.coretest.TestPrincipal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.github.vincemann.ezcompare.Comparator.compare;
import static com.github.vincemann.springrapid.coretest.util.RapidTestUtil.createUpdateJsonLine;
import static com.github.vincemann.springrapid.coretest.util.RapidTestUtil.createUpdateJsonRequest;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class OwnerControllerIntegrationTest
        extends AbstractControllerIntegrationTest<OwnerController, OwnerService> {



    // SAVE TESTS

    @Test
    public void canSaveOwnerWithoutPets() throws Exception {
        CreateOwnerDto createKahnDto = new CreateOwnerDto(kahn);


        MvcResult result = getMvc().perform(create(createKahnDto))
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
                .ignore(createKahnDto::getClinicCardId)
                .assertEqual();

        Assertions.assertEquals(responseDto.getId(),dbKahn.getId());
    }

    @Test
    public void canSaveOwnerWithManyHobbies() throws Exception {

        String bodybuilding = "bodybuilding";
        Set<String> hobbies = new HashSet<>(Arrays.asList("swimming","biking",bodybuilding,"jogging","eating"));
        kahn.setHobbies(hobbies);

        ReadOwnOwnerDto responseDto = saveOwnerLinkedToPets(kahn);
        Assertions.assertEquals(hobbies,responseDto.getHobbies());

        Owner dbKahn = ownerRepository.findByLastName(KAHN).get();
        Assertions.assertEquals(hobbies,dbKahn.getHobbies());
    }

    @Test
    public void canSaveOwner_linkToPet() throws Exception {
        Pet savedBello = petRepository.save(bello);


        ReadOwnOwnerDto responseDto = saveOwnerLinkedToPets(kahn,savedBello.getId());
        Assertions.assertTrue(responseDto.getPetIds().contains(savedBello.getId()));


        assertOwnerHasPets(KAHN,BELLO);
        assertPetHasOwner(BELLO,KAHN);
    }

    @Test
    public void canSaveOwner_linkToClinicCard() throws Exception {
        ClinicCard savedClinicCard = clinicCardRepository.save(clinicCard);

        ReadOwnOwnerDto responseDto = saveOwnerLinkedToClinicCard(kahn,savedClinicCard);
        Assertions.assertEquals(responseDto.getClinicCardId(),savedClinicCard.getId());

        assertOwnerHasClinicCard(KAHN,savedClinicCard.getId());
        assertClinicCardHasOwner(savedClinicCard.getId(),KAHN);
    }

    @Test
    public void canSaveOwner_linkToPets() throws Exception {
        Pet savedBello = petRepository.save(bello);
        Pet savedKitty = petRepository.save(kitty);


        ReadOwnOwnerDto responseDto = saveOwnerLinkedToPets(kahn,savedBello.getId(),savedKitty.getId());
        Assertions.assertTrue(responseDto.getPetIds().contains(savedBello.getId()));
        Assertions.assertTrue(responseDto.getPetIds().contains(savedKitty.getId()));
        Assertions.assertEquals(2,responseDto.getPetIds().size());


        assertOwnerHasPets(KAHN,BELLO,KITTY);
        assertPetHasOwner(BELLO,KAHN);
        assertPetHasOwner(KITTY,KAHN);
    }

    // UPDATE TESTS
    @Test
    public void canUpdateOwnersCity() throws Exception {
        ReadOwnOwnerDto createdKahnDto = saveOwnerLinkedToPets(kahn);
        String newCity = kahn.getCity()+"new";

        String updateJson = createUpdateJsonLine("replace", "/city",newCity);
        String jsonResponse = getMvc().perform(update(createUpdateJsonRequest(updateJson),createdKahnDto.getId())).andReturn().getResponse().getContentAsString();
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
        String jsonResponse = getMvc().perform(update(updateJsonRequest,createdKahnDto.getId())).andReturn().getResponse().getContentAsString();
        ReadOwnOwnerDto responseDto = deserialize(jsonResponse, ReadOwnOwnerDto.class);
        Assertions.assertEquals(newCity,responseDto.getCity());
        Assertions.assertEquals(newAdr,responseDto.getAddress());

        Owner dbKahn = ownerRepository.findByLastName(KAHN).get();
        Assertions.assertEquals(newCity,dbKahn.getCity());
        Assertions.assertEquals(newAdr,dbKahn.getAddress());
    }


    @Test
    public void canRemoveOnlyPetFromOwner_viaRemoveAllPetsUpdate() throws Exception {
        Pet savedBello = petRepository.save(bello);
        ReadOwnOwnerDto createdKahnDto = saveOwnerLinkedToPets(kahn,savedBello.getId());

        String updateJson = createUpdateJsonLine("remove", "/petIds");
        String jsonResponse = getMvc().perform(update(createUpdateJsonRequest(updateJson), createdKahnDto.getId())).andReturn().getResponse().getContentAsString();
        ReadOwnOwnerDto responseDto = deserialize(jsonResponse, ReadOwnOwnerDto.class);
        Assertions.assertTrue(responseDto.getPetIds().isEmpty());

        assertOwnerHasPets(KAHN);
        assertPetHasOwner(BELLO,null);
    }

    @Test
    public void canRemoveClinicCardFromOwner_viaUpdate() throws Exception {
        ClinicCard savedClinicCard = clinicCardRepository.save(clinicCard);

        ReadOwnOwnerDto createdKahnDto = saveOwnerLinkedToClinicCard(kahn,savedClinicCard);
        String updateJson = createUpdateJsonLine("remove", "/clinicCardId");
        ReadOwnOwnerDto responseDto = performDs2xx(update(createUpdateJsonRequest(updateJson), createdKahnDto.getId()),ReadOwnOwnerDto.class);
        Assertions.assertNull(responseDto.getClinicCardId());

        assertOwnerHasClinicCard(KAHN,null);
        assertClinicCardHasOwner(savedClinicCard.getId(),null);
    }

    @Test
    public void canAddClinicCardToOwner_viaUpdate() throws Exception {
        ClinicCard savedClinicCard = clinicCardRepository.save(clinicCard);

        ReadOwnOwnerDto createdKahnDto = saveOwner(kahn);
        String updateJson = createUpdateJsonLine("add", "/clinicCardId",savedClinicCard.getId().toString());
        ReadOwnOwnerDto responseDto = performDs2xx(update(createUpdateJsonRequest(updateJson), createdKahnDto.getId()),ReadOwnOwnerDto.class);
        Assertions.assertEquals(savedClinicCard.getId(),responseDto.getClinicCardId());

        assertOwnerHasClinicCard(KAHN,savedClinicCard.getId());
        assertClinicCardHasOwner(savedClinicCard.getId(),KAHN);
    }

    @Test
    public void canRelinkDiffClinicCardToOwner_viaUpdate() throws Exception {
        ClinicCard savedClinicCard = clinicCardRepository.save(clinicCard);
        ClinicCard savedSecondClinicCard = clinicCardRepository.save(secondClinicCard);

        ReadOwnOwnerDto createdKahnDto = saveOwnerLinkedToClinicCard(kahn,savedClinicCard);
        String updateJson = createUpdateJsonLine("replace", "/clinicCardId",savedSecondClinicCard.getId().toString());
        ReadOwnOwnerDto responseDto = performDs2xx(update(createUpdateJsonRequest(updateJson), createdKahnDto.getId()),ReadOwnOwnerDto.class);
        Assertions.assertEquals(savedSecondClinicCard.getId(),responseDto.getClinicCardId());

        assertOwnerHasClinicCard(KAHN,savedSecondClinicCard.getId());
        assertClinicCardHasOwner(savedClinicCard.getId(),null);
        assertClinicCardHasOwner(savedSecondClinicCard.getId(),KAHN);
    }

    @Test
    public void canRemoveOnlyPetFromOwner_viaRemoveSpecificUpdate() throws Exception {
        Pet savedBello = petRepository.save(bello);
        ReadOwnOwnerDto createdKahnDto = saveOwnerLinkedToPets(kahn,savedBello.getId());

        String updateJson = createUpdateJsonLine("remove", "/petIds",savedBello.getId().toString());
        String jsonResponse = getMvc().perform(update(createUpdateJsonRequest(updateJson), createdKahnDto.getId())).andReturn().getResponse().getContentAsString();
        ReadOwnOwnerDto responseDto = deserialize(jsonResponse, ReadOwnOwnerDto.class);
        Assertions.assertTrue(responseDto.getPetIds().isEmpty());

        assertOwnerHasPets(KAHN);
        assertPetHasOwner(BELLO,null);
    }

    @Test
    public void canRemoveMultiplePetsFromOwner_viaRemoveAllPetsUpdate() throws Exception {
        Pet savedBello = petRepository.save(bello);
        Pet savedKitty = petRepository.save(kitty);
        ReadOwnOwnerDto createdKahnDto = saveOwnerLinkedToPets(kahn,savedBello.getId(),savedKitty.getId());

        String updateJson = createUpdateJsonLine("remove", "/petIds");
        String jsonResponse = getMvc().perform(update(createUpdateJsonRequest(updateJson), createdKahnDto.getId())).andReturn().getResponse().getContentAsString();
        ReadOwnOwnerDto responseDto = deserialize(jsonResponse, ReadOwnOwnerDto.class);
        Assertions.assertTrue(responseDto.getPetIds().isEmpty());

        assertOwnerHasPets(KAHN);
        assertPetHasOwner(BELLO,null);
        assertPetHasOwner(KITTY,null);
    }

    @Test
    public void canRemoveOneOfManyPetsFromOwner_viaUpdate() throws Exception {
        Pet savedBello = petRepository.save(bello);
        Pet savedKitty = petRepository.save(kitty);
        ReadOwnOwnerDto createdKahnDto = saveOwnerLinkedToPets(kahn,savedBello.getId(),savedKitty.getId());

        String updateJson = createUpdateJsonLine("remove", "/petIds",savedBello.getId().toString());
        String jsonResponse = getMvc().perform(update(createUpdateJsonRequest(updateJson), createdKahnDto.getId())).andReturn().getResponse().getContentAsString();
        ReadOwnOwnerDto responseDto = deserialize(jsonResponse, ReadOwnOwnerDto.class);
        Assertions.assertTrue(responseDto.getPetIds().contains(savedKitty.getId()));
        Assertions.assertEquals(1,responseDto.getPetIds().size());

        assertOwnerHasPets(KAHN,KITTY);
        assertPetHasOwner(BELLO,null);
        assertPetHasOwner(KITTY,KAHN);
    }

    @Test
    public void canRemoveSomeOfManyPetsFromOwner_viaUpdate() throws Exception {
        Pet savedBello = petRepository.save(bello);
        Pet savedKitty = petRepository.save(kitty);
        Pet savedBella = petRepository.save(bella);
        ReadOwnOwnerDto createdKahnDto = saveOwnerLinkedToPets(kahn,savedBello.getId(),savedKitty.getId(),savedBella.getId());

        String removeBelloJson = createUpdateJsonLine("remove", "/petIds",savedBello.getId().toString());
        String removeKittyJson = createUpdateJsonLine("remove", "/petIds",savedKitty.getId().toString());
        String removePetsJson = createUpdateJsonRequest(removeBelloJson, removeKittyJson);

        String jsonResponse = getMvc().perform(update(removePetsJson, createdKahnDto.getId())).andReturn().getResponse().getContentAsString();
        ReadOwnOwnerDto responseDto = deserialize(jsonResponse, ReadOwnOwnerDto.class);
        Assertions.assertTrue(responseDto.getPetIds().contains(savedBella.getId()));
        Assertions.assertEquals(1,responseDto.getPetIds().size());

        assertOwnerHasPets(KAHN,BELLA);
        assertPetHasOwner(BELLO,null);
        assertPetHasOwner(KITTY,null);
        assertPetHasOwner(BELLA,KAHN);
    }

    @Test
    public void canRemoveSomeOfManyPetsFromOwner_viaUpdate_diffOrder() throws Exception {
        Pet savedBello = petRepository.save(bello);
        Pet savedKitty = petRepository.save(kitty);
        Pet savedBella = petRepository.save(bella);
        ReadOwnOwnerDto createdKahnDto = saveOwnerLinkedToPets(kahn,savedBello.getId(),savedKitty.getId(),savedBella.getId());

        String removeBelloJson = createUpdateJsonLine("remove", "/petIds",savedBello.getId().toString());
        String removeKittyJson = createUpdateJsonLine("remove", "/petIds",savedBella.getId().toString());
        String removePetsJson = createUpdateJsonRequest(removeBelloJson, removeKittyJson);

        String jsonResponse = getMvc().perform(update(removePetsJson, createdKahnDto.getId())).andReturn().getResponse().getContentAsString();
        ReadOwnOwnerDto responseDto = deserialize(jsonResponse, ReadOwnOwnerDto.class);
        Assertions.assertTrue(responseDto.getPetIds().contains(savedKitty.getId()));
        Assertions.assertEquals(1,responseDto.getPetIds().size());

        assertOwnerHasPets(KAHN,KITTY);
        assertPetHasOwner(BELLO,null);
        assertPetHasOwner(KITTY,KAHN);
        assertPetHasOwner(BELLA,null);
    }

    @Test
    public void canRemoveOneOfManyHobbiesFromOwner_viaUpdate() throws Exception {
        String hobbyToRemove = "bodybuilding";
        Set<String> hobbies = new HashSet<>(Arrays.asList("swimming","biking",hobbyToRemove,"jogging","eating"));
        kahn.setHobbies(hobbies);
        ReadOwnOwnerDto createdKahnDto = saveOwnerLinkedToPets(kahn);

        String updateJson = createUpdateJsonLine("remove", "/hobbies", hobbyToRemove);
        String jsonResponse = getMvc().perform(update(createUpdateJsonRequest(updateJson), createdKahnDto.getId())).andReturn().getResponse().getContentAsString();
        ReadOwnOwnerDto responseDto = deserialize(jsonResponse, ReadOwnOwnerDto.class);
        Assertions.assertFalse(responseDto.getHobbies().contains(hobbyToRemove));
        Assertions.assertEquals(hobbies.size()-1,responseDto.getHobbies().size());


        Owner dbKahn = ownerRepository.findByLastName(KAHN).get();
        Assertions.assertFalse(dbKahn.getHobbies().contains(hobbyToRemove));
        Assertions.assertEquals(hobbies.size()-1,dbKahn.getHobbies().size());
    }

    @Test
    public void canAddPetToOwner_viaUpdate() throws Exception {
        Pet savedBello = petRepository.save(bello);
        ReadOwnOwnerDto createdKahnDto = saveOwnerLinkedToPets(kahn);

        String updateJson = createUpdateJsonLine("add", "/petIds", savedBello.getId().toString());
        String jsonResponse = getMvc().perform(update(createUpdateJsonRequest(updateJson),createdKahnDto.getId())).andReturn().getResponse().getContentAsString();
        ReadOwnOwnerDto responseDto = deserialize(jsonResponse, ReadOwnOwnerDto.class);
        Assertions.assertTrue(responseDto.getPetIds().contains(savedBello.getId()));

        assertOwnerHasPets(KAHN,BELLO);
        assertPetHasOwner(BELLO,KAHN);
    }

    // FIND TESTS
    @Test
    public void canFindOwnOwner() throws Exception {
        securityContext.login(TestPrincipal.withName(KAHN));
        ReadOwnOwnerDto savedKahnDto = saveOwnerLinkedToPets(kahn);
        ReadOwnOwnerDto responseDto = deserialize(getMvc().perform(find(savedKahnDto.getId()))
                .andExpect(jsonPath("$.lastName").value(KAHN))
                .andReturn().getResponse().getContentAsString(), ReadOwnOwnerDto.class);

        compare(savedKahnDto).with(responseDto)
                .properties().all()
                .assertEqual();
        Assertions.assertEquals(Owner.DIRTY_SECRET,responseDto.getDirtySecret());

        RapidSecurityContext.logout();
    }

    @Test
    public void canFindAllOwnersWithPetsFilter() throws Exception {

        // save kahn -> bello
        // meier -> kitty
        // gil -> []
        // find all owners with hasPets filter (in memory filter), authenticated as kahn
        // should find kahn and meier
        // kahn should be FindOwnOwnerDto and meier FindForeignOwnerDto

        Pet savedBello = petRepository.save(bello);
        Pet savedKitty = petRepository.save(kitty);

        ReadOwnOwnerDto savedKahn = saveOwnerLinkedToPets(kahn,savedBello.getId());
        ReadOwnOwnerDto savedMeier = saveOwnerLinkedToPets(meier, savedKitty.getId());
        ReadOwnOwnerDto savedGil = saveOwnerLinkedToPets(gil);

        Assertions.assertEquals(3,ownerRepository.findAll().size());


        securityContext.login(TestPrincipal.withName(KAHN));
        // memory filter
        String filter = createFilterString(new Filter(HasPetsFilter.class));
        Set<ReadOwnOwnerDto> responseDtos = deserializeToSet(getMvc().perform(findAll(null,filter))
                .andReturn().getResponse().getContentAsString(), ReadOwnOwnerDto.class);
        RapidSecurityContext.logout();

        // one dto findOwnDto and one findForeign
        Assertions.assertEquals(2,responseDtos.size());
        ReadOwnOwnerDto kahnDto = findInCollection(responseDtos, savedKahn);
        Assertions.assertEquals(Owner.DIRTY_SECRET,kahnDto.getDirtySecret());
        ReadOwnOwnerDto meierDto = findInCollection(responseDtos, savedMeier);
        Assertions.assertNull(meierDto.getDirtySecret());
    }

    // can combine jpql filter with memory filter
    @Test
    public void canFindAllOwnersWithPetsFilter_andTelNrPrefix() throws Exception {
        // save kahn -> bello
        // meier -> kitty
        // gil -> []
        // find all owners with hasPets filter (in memory filter), authenticated as kahn
        // and combine with jpql filter checking telnr prefix for 0176
        // should find kahn only
        // kahn should be FindOwnOwnerDto
        Pet savedBello = petRepository.save(bello);
        Pet savedKitty = petRepository.save(kitty);

        ReadOwnOwnerDto savedKahn = saveOwnerLinkedToPets(kahn,savedBello.getId());
        ReadOwnOwnerDto savedMeier = saveOwnerLinkedToPets(meier, savedKitty.getId());
        ReadOwnOwnerDto savedGil = saveOwnerLinkedToPets(gil);

        Assertions.assertEquals(3,ownerRepository.findAll().size());

        String telnrPrefix = "0176";
        Assertions.assertTrue(savedKahn.getTelephone().startsWith(telnrPrefix));
        Assertions.assertTrue(savedGil.getTelephone().startsWith(telnrPrefix));
        Assertions.assertFalse(savedMeier.getTelephone().startsWith(telnrPrefix));


        securityContext.login(TestPrincipal.withName(KAHN));
        // memory filter
        String hasPetsFilter = createFilterString(new Filter(HasPetsFilter.class));
        String telNrPrefixFilter = createFilterString(new Filter(OwnerTelNumberFilter.class,telnrPrefix));
        Set<ReadOwnOwnerDto> responseDtos = deserializeToSet(getMvc().perform(findAll(telNrPrefixFilter,hasPetsFilter))
                .andReturn().getResponse().getContentAsString(), ReadOwnOwnerDto.class);
        RapidSecurityContext.logout();

        // one dto findOwnDto and one findForeign
        Assertions.assertEquals(1,responseDtos.size());
        ReadOwnOwnerDto kahnDto = findInCollection(responseDtos, savedKahn);
        Assertions.assertEquals(Owner.DIRTY_SECRET,kahnDto.getDirtySecret());
    }

    // combine jpql filter with two memory filters
    @Test
    public void canFindAllOwnersWithPetsFilter_andTelNrPrefix_andPetNameEndsWithA() throws Exception {
        // save kahn -> bello
        // meier -> kitty
        // gil -> bella
        // find all owners with hasPets filter (in memory filter), authenticated as kahn -> all
        // and combine with jpql filter checking telnr prefix for 0176 -> kahn, gil
        // and combine with petNameEnds with a filter -> gil
        // should find gil only
        // gil should be FindForeignOwnerDto
        Pet savedBello = petRepository.save(bello);
        Pet savedKitty = petRepository.save(kitty);
        Pet savedBella = petRepository.save(bella);

        ReadOwnOwnerDto savedKahn = saveOwnerLinkedToPets(kahn,savedBello.getId());
        ReadOwnOwnerDto savedMeier = saveOwnerLinkedToPets(meier, savedKitty.getId());
        ReadOwnOwnerDto savedGil = saveOwnerLinkedToPets(gil,savedBella.getId());

        Assertions.assertEquals(3,ownerRepository.findAll().size());

        String telnrPrefix = "0176";
        Assertions.assertTrue(savedKahn.getTelephone().startsWith(telnrPrefix));
        Assertions.assertTrue(savedGil.getTelephone().startsWith(telnrPrefix));
        Assertions.assertFalse(savedMeier.getTelephone().startsWith(telnrPrefix));


        securityContext.login(TestPrincipal.withName(KAHN));
        // memory filter
        String memoryFilters = createFilterString(new Filter(HasPetsFilter.class),new Filter(PetNameEndsWithFilter.class,"a"));
        // jpql filters (always come first)
        String telNrPrefixFilter = createFilterString(new Filter(OwnerTelNumberFilter.class,telnrPrefix));
        Set<ReadForeignOwnerDto> responseDtos = deserializeToSet(getMvc().perform(findAll(telNrPrefixFilter,memoryFilters))
                .andReturn().getResponse().getContentAsString(), ReadForeignOwnerDto.class);
        RapidSecurityContext.logout();

        // one dto findOwnDto and one findForeign
        Assertions.assertEquals(1,responseDtos.size());
        ReadForeignOwnerDto gilDto = findInCollection(responseDtos, savedGil);

        compare(gilDto).with(savedGil)
                .properties()
                .all()
                .assertEqual();
    }

    // combine multiple jpql filters with memory filter
    @Test
    public void canFindAllOwnersWithPetsFilter_andTelNrPrefix_andCityIsN1() throws Exception {
        // save kahn -> bello
        // meier -> kitty
        // gil -> bella
        // find all owners with hasPets filter (in memory filter), authenticated as kahn -> all
        // and combine with jpql filter checking telnr prefix for 0176 -> kahn, gil
        // and combine with city is n1 -> kahn
        // should find kahn only
        // kahn should be FindOwnOwnerDto
        Pet savedBello = petRepository.save(bello);
        Pet savedKitty = petRepository.save(kitty);
        Pet savedBella = petRepository.save(bella);

        ReadOwnOwnerDto savedKahn = saveOwnerLinkedToPets(kahn,savedBello.getId());
        ReadOwnOwnerDto savedMeier = saveOwnerLinkedToPets(meier, savedKitty.getId());
        ReadOwnOwnerDto savedGil = saveOwnerLinkedToPets(gil,savedBella.getId());

        Assertions.assertEquals(3,ownerRepository.findAll().size());

        String telnrPrefix = "0176";
        Assertions.assertTrue(savedKahn.getTelephone().startsWith(telnrPrefix));
        Assertions.assertTrue(savedGil.getTelephone().startsWith(telnrPrefix));
        Assertions.assertFalse(savedMeier.getTelephone().startsWith(telnrPrefix));

        String niceCityPrefix = "n1";
        Assertions.assertTrue(savedKahn.getCity().startsWith(niceCityPrefix));
        Assertions.assertFalse(savedGil.getCity().startsWith(niceCityPrefix));

        securityContext.login(TestPrincipal.withName(KAHN));
        // memory filter
        String memoryFilters = createFilterString(new Filter(HasPetsFilter.class));
        // jpql filters (always come first)
        String telNrPrefixFilter = createFilterString(new Filter(OwnerTelNumberFilter.class,telnrPrefix),new Filter(CityPrefixFilter.class,niceCityPrefix));
        Set<ReadOwnOwnerDto> responseDtos = deserializeToSet(getMvc().perform(findAll(telNrPrefixFilter,memoryFilters))
                .andReturn().getResponse().getContentAsString(), ReadOwnOwnerDto.class);
        RapidSecurityContext.logout();

        // one dto findOwnDto and one findForeign
        Assertions.assertEquals(1,responseDtos.size());
        ReadOwnOwnerDto kahnDto = findInCollection(responseDtos, savedKahn);
        Assertions.assertNotNull(kahnDto.getDirtySecret());
        Assertions.assertEquals(Owner.DIRTY_SECRET,kahnDto.getDirtySecret());

        compare(kahnDto).with(savedKahn)
                .properties()
                .all()
                .assertEqual();
    }


    @Test
    public void canFindOwnOwnerWithPets() throws Exception {
        Pet savedBello = petRepository.save(bello);
        Pet savedKitty = petRepository.save(kitty);
        ReadOwnOwnerDto savedKahnDto = saveOwnerLinkedToPets(kahn,savedBello.getId(),savedKitty.getId());
        securityContext.login(TestPrincipal.withName(KAHN));
        ReadOwnOwnerDto responseDto = deserialize(getMvc().perform(find(savedKahnDto.getId()))
                .andReturn().getResponse().getContentAsString(), ReadOwnOwnerDto.class);
        Assertions.assertEquals(2,responseDto.getPetIds().size());
        Assertions.assertTrue(responseDto.getPetIds().contains(savedBello.getId()));
        Assertions.assertTrue(responseDto.getPetIds().contains(savedKitty.getId()));
        RapidSecurityContext.logout();
    }

    @Test
    public void anonCanFindForeignOwner() throws Exception {
        ReadOwnOwnerDto savedKahnDto = saveOwnerLinkedToPets(kahn);
        getMvc().perform(find(savedKahnDto.getId()))
                .andExpect(jsonPath("$.lastName").doesNotExist())
                .andExpect(jsonPath("$.city").value(kahn.getCity()));
        RapidSecurityContext.logout();
    }

    @Test
    public void userCanFindForeignOwnOwner() throws Exception {
        ReadOwnOwnerDto savedKahnDto = saveOwnerLinkedToPets(kahn);
        securityContext.login(TestPrincipal.withName(MEIER));
        getMvc().perform(find(savedKahnDto.getId()))
                .andExpect(jsonPath("$.lastName").doesNotExist())
                .andExpect(jsonPath("$.city").value(kahn.getCity()));
        RapidSecurityContext.logout();
    }



    // DELETE TESTS

    @Test
    public void canDeleteOwner_thusUnlinkFromPet() throws Exception {
        Pet savedBello = petRepository.save(bello);

        ReadOwnOwnerDto responseDto = saveOwnerLinkedToPets(kahn,savedBello.getId());

        getMvc().perform(delete(responseDto.getId()))
                .andExpect(status().is2xxSuccessful());

        Assertions.assertFalse(ownerRepository.findByLastName(KAHN).isPresent());

        assertPetHasOwner(BELLO,null);
    }

    @Test
    public void canDeleteOwner_thusUnlinkFromClinicCard() throws Exception {
        ClinicCard savedClinicCard = clinicCardRepository.save(clinicCard);

        ReadOwnOwnerDto responseDto = saveOwnerLinkedToClinicCard(kahn,savedClinicCard);

        getMvc().perform(delete(responseDto.getId()))
                .andExpect(status().is2xxSuccessful());

        Assertions.assertFalse(ownerRepository.findByLastName(KAHN).isPresent());

        assertClinicCardHasOwner(savedClinicCard.getId(),null);
    }

    @Test
    public void canDeleteOwner_thusUnlinkFromPets() throws Exception {
        Pet savedBello = petRepository.save(bello);
        Pet savedKitty = petRepository.save(kitty);

        ReadOwnOwnerDto responseDto = saveOwnerLinkedToPets(kahn,savedBello.getId(),savedKitty.getId());

        MvcResult result = getMvc().perform(delete(responseDto.getId()))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Assertions.assertFalse(ownerRepository.findByLastName(KAHN).isPresent());
        assertPetHasOwner(BELLO,null);
        assertPetHasOwner(KITTY,null);
    }

}
