package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import com.github.vincemann.springrapid.coredemo.controller.suite.MyControllerIntegrationTest;
import com.github.vincemann.springrapid.coredemo.dto.owner.CreateOwnerDto;
import com.github.vincemann.springrapid.coredemo.dto.owner.ReadForeignOwnerDto;
import com.github.vincemann.springrapid.coredemo.dto.owner.ReadOwnOwnerDto;
import com.github.vincemann.springrapid.coredemo.model.ClinicCard;
import com.github.vincemann.springrapid.coredemo.model.Owner;
import com.github.vincemann.springrapid.coredemo.model.Pet;
import com.github.vincemann.springrapid.coredemo.service.filter.CityPrefixFilter;
import com.github.vincemann.springrapid.coredemo.service.filter.HasPetsFilter;
import com.github.vincemann.springrapid.coredemo.service.filter.OwnerTelNumberFilter;
import com.github.vincemann.springrapid.coredemo.service.filter.PetNameEndsWithFilter;
import com.github.vincemann.springrapid.coredemo.service.sort.LastNameAscSorting;
import com.github.vincemann.springrapid.coredemo.service.sort.LastNameDescSorting;
import com.github.vincemann.springrapid.coretest.util.TestPrincipal;
import com.github.vincemann.springrapid.coretest.controller.UrlWebExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.github.vincemann.ezcompare.Comparator.compare;
import static com.github.vincemann.springrapid.coretest.util.RapidTestUtil.createUpdateJsonLine;
import static com.github.vincemann.springrapid.coretest.util.RapidTestUtil.createUpdateJsonRequest;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class OwnerControllerIntegrationTest extends MyControllerIntegrationTest {



    // SAVE TESTS

    @Test
    public void canSaveOwnerWithoutPets() throws Exception {
        CreateOwnerDto createDto = new CreateOwnerDto(kahn);
        ReadOwnOwnerDto responseDto = ownerController.create2xx(createDto, ReadOwnOwnerDto.class);
        compare(createDto).with(responseDto)
                .properties()
                .all()
                .ignore(OwnerType::getId)
                .assertEqual();
        Assertions.assertTrue(ownerRepository.findByLastName(KAHN).isPresent());
        Owner dbKahn = ownerRepository.findByLastName(KAHN).get();

        compare(createDto).with(dbKahn)
                .properties()
                .all()
                .ignore(OwnerType::getId)
                .ignore(createDto::getPetIds)
                .ignore(createDto::getClinicCardId)
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
        ReadOwnOwnerDto responseDto = ownerController.update2xx(
                createUpdateJsonRequest(updateJson),createdKahnDto.getId(),ReadOwnOwnerDto.class);
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
        ReadOwnOwnerDto responseDto = ownerController.update2xx(
                createUpdateJsonRequest(updateJsonRequest),createdKahnDto.getId(),ReadOwnOwnerDto.class);
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
        ReadOwnOwnerDto responseDto = ownerController.update2xx(
                createUpdateJsonRequest(updateJson),createdKahnDto.getId(),ReadOwnOwnerDto.class);
        Assertions.assertTrue(responseDto.getPetIds().isEmpty());

        assertOwnerHasPets(KAHN);
        assertPetHasOwner(BELLO,null);
    }

    @Test
    public void canRemoveClinicCardFromOwner_viaUpdate() throws Exception {
        ClinicCard savedClinicCard = clinicCardRepository.save(clinicCard);

        ReadOwnOwnerDto createdKahnDto = saveOwnerLinkedToClinicCard(kahn,savedClinicCard);
        String updateJson = createUpdateJsonLine("remove", "/clinicCardId");
        ReadOwnOwnerDto responseDto = ownerController.update2xx(
                createUpdateJsonRequest(createUpdateJsonRequest(updateJson)),createdKahnDto.getId(),ReadOwnOwnerDto.class);
        Assertions.assertNull(responseDto.getClinicCardId());

        assertOwnerHasClinicCard(KAHN,null);
        assertClinicCardHasOwner(savedClinicCard.getId(),null);
    }

    @Test
    public void canAddClinicCardToOwner_viaUpdate() throws Exception {
        ClinicCard savedClinicCard = clinicCardRepository.save(clinicCard);

        ReadOwnOwnerDto createdKahnDto = saveOwner(kahn);
        String updateJson = createUpdateJsonLine("add", "/clinicCardId",savedClinicCard.getId().toString());
        ReadOwnOwnerDto responseDto = ownerController.update2xx(
                createUpdateJsonRequest(createUpdateJsonRequest(updateJson)),createdKahnDto.getId(),ReadOwnOwnerDto.class);
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
        ReadOwnOwnerDto responseDto = ownerController.update2xx(
                createUpdateJsonRequest(createUpdateJsonRequest(updateJson)),createdKahnDto.getId(),ReadOwnOwnerDto.class);
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
        ReadOwnOwnerDto responseDto = ownerController.update2xx(
                createUpdateJsonRequest(createUpdateJsonRequest(updateJson)),createdKahnDto.getId(),ReadOwnOwnerDto.class);
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
        ReadOwnOwnerDto responseDto = ownerController.update2xx(
                createUpdateJsonRequest(createUpdateJsonRequest(updateJson)),createdKahnDto.getId(),ReadOwnOwnerDto.class);
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
        ReadOwnOwnerDto responseDto = ownerController.update2xx(
                createUpdateJsonRequest(createUpdateJsonRequest(updateJson)),createdKahnDto.getId(),ReadOwnOwnerDto.class);
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

        ReadOwnOwnerDto responseDto = ownerController.update2xx(
                createUpdateJsonRequest(createUpdateJsonRequest(removePetsJson)),createdKahnDto.getId(),ReadOwnOwnerDto.class);
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

        ReadOwnOwnerDto responseDto = ownerController.update2xx(
                createUpdateJsonRequest(createUpdateJsonRequest(removePetsJson)),
                createdKahnDto.getId(),ReadOwnOwnerDto.class);
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
        ReadOwnOwnerDto responseDto = ownerController.update2xx(
                createUpdateJsonRequest(createUpdateJsonRequest(updateJson)),createdKahnDto.getId(),ReadOwnOwnerDto.class);
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
        ReadOwnOwnerDto responseDto = ownerController.update2xx(
                createUpdateJsonRequest(createUpdateJsonRequest(updateJson)),createdKahnDto.getId(),ReadOwnOwnerDto.class);
        Assertions.assertTrue(responseDto.getPetIds().contains(savedBello.getId()));

        assertOwnerHasPets(KAHN,BELLO);
        assertPetHasOwner(BELLO,KAHN);
    }

    // FIND TESTS
    @Test
    public void canFindOwnOwner() throws Exception {
        securityContext.setAuthenticated(TestPrincipal.withName(KAHN));
        ReadOwnOwnerDto kahn = saveOwnerLinkedToPets(this.kahn);
        ReadOwnOwnerDto response = ownerController.find2xx(kahn.getId(), ReadOwnOwnerDto.class);
        Assertions.assertEquals(kahn.getLastName(),response.getLastName());

        compare(kahn).with(response)
                .properties().all()
                .assertEqual();
        Assertions.assertEquals(Owner.SECRET,response.getSecret());

        RapidSecurityContext.unsetAuthenticated();
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


        securityContext.setAuthenticated(TestPrincipal.withName(KAHN));
        // memory filter
        UrlWebExtension hasPetsFilter = new UrlWebExtension(HasPetsFilter.class);
        List<ReadOwnOwnerDto> responseDtos = ownerController.findAll2xx(ReadOwnOwnerDto.class, hasPetsFilter);
        RapidSecurityContext.unsetAuthenticated();

        // one dto findOwnDto and one findForeign
        Assertions.assertEquals(2,responseDtos.size());
        ReadOwnOwnerDto kahnDto = assertCanFindInCollection(responseDtos,dto -> dto.getId().equals(savedKahn.getId()));
        Assertions.assertEquals(Owner.SECRET,kahnDto.getSecret());
        ReadOwnOwnerDto meierDto = assertCanFindInCollection(responseDtos,dto -> dto.getId().equals(savedMeier.getId()));
        Assertions.assertNull(meierDto.getSecret());
    }

    @Test
    public void canFindAllOwnersWithPetsFilter_sortedByLastName() throws Exception {

        // save kahn -> bello
        // meier -> kitty
        // gil -> []
        // find all owners with hasPets filter (in memory filter), authenticated as kahn
        // should find kahn and meier - sorted by name desc -> meier, kahn
        // kahn should be FindOwnOwnerDto and meier FindForeignOwnerDto
        // find again with asc name sorting -> [kahn, meier]

        Pet savedBello = petRepository.save(bello);
        Pet savedKitty = petRepository.save(kitty);

        ReadOwnOwnerDto savedKahn = saveOwnerLinkedToPets(kahn,savedBello.getId());
        ReadOwnOwnerDto savedMeier = saveOwnerLinkedToPets(meier, savedKitty.getId());
        ReadOwnOwnerDto savedGil = saveOwnerLinkedToPets(gil);

        Assertions.assertEquals(3,ownerRepository.findAll().size());


        securityContext.setAuthenticated(TestPrincipal.withName(KAHN));
        // memory filter
        UrlWebExtension hasPetsFilter = new UrlWebExtension(HasPetsFilter.class);
        UrlWebExtension sortByNameDesc = new UrlWebExtension(LastNameDescSorting.class);
        List<ReadOwnOwnerDto> responseDtos = ownerController.findAll2xx(ReadOwnOwnerDto.class, hasPetsFilter, sortByNameDesc);
        RapidSecurityContext.unsetAuthenticated();

        // one dto findOwnDto and one findForeign
        Assertions.assertEquals(2,responseDtos.size());
        // order
        Assertions.assertEquals(savedMeier.getId(),responseDtos.get(0).getId());
        Assertions.assertEquals(savedKahn.getId(),responseDtos.get(1).getId());

        // content
        ReadOwnOwnerDto kahnDto = assertCanFindInCollection(responseDtos,dto -> dto.getId().equals(savedKahn.getId()));
        Assertions.assertEquals(Owner.SECRET,kahnDto.getSecret());
        ReadOwnOwnerDto meierDto = assertCanFindInCollection(responseDtos,dto -> dto.getId().equals(savedMeier.getId()));
        Assertions.assertNull(meierDto.getSecret());


        // diff sorting
        UrlWebExtension sortByNameAsc = new UrlWebExtension(LastNameAscSorting.class);
        securityContext.setAuthenticated(TestPrincipal.withName(KAHN));
        responseDtos = ownerController.findAll2xx(ReadOwnOwnerDto.class, hasPetsFilter, sortByNameAsc);
        RapidSecurityContext.unsetAuthenticated();

        // one dto findOwnDto and one findForeign
        Assertions.assertEquals(2,responseDtos.size());
        // order
        Assertions.assertEquals(savedKahn.getId(),responseDtos.get(0).getId());
        Assertions.assertEquals(savedMeier.getId(),responseDtos.get(1).getId());

        // content
        kahnDto = assertCanFindInCollection(responseDtos,dto -> dto.getId().equals(savedKahn.getId()));
        Assertions.assertEquals(Owner.SECRET,kahnDto.getSecret());
        meierDto = assertCanFindInCollection(responseDtos,dto -> dto.getId().equals(savedMeier.getId()));
        Assertions.assertNull(meierDto.getSecret());
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


        securityContext.setAuthenticated(TestPrincipal.withName(KAHN));
        // memory filter
        UrlWebExtension hasPetsFilter = new UrlWebExtension(HasPetsFilter.class);
        UrlWebExtension telNrPrefixFilter = new UrlWebExtension(OwnerTelNumberFilter.class,telnrPrefix);
        List<ReadOwnOwnerDto> responseDtos = ownerController.findAll2xx(ReadOwnOwnerDto.class,telNrPrefixFilter, hasPetsFilter);
        RapidSecurityContext.unsetAuthenticated();

        // one dto findOwnDto and one findForeign
        Assertions.assertEquals(1,responseDtos.size());
        ReadOwnOwnerDto kahnDto = assertCanFindInCollection(responseDtos,dto -> dto.getId().equals(savedKahn.getId()));
        Assertions.assertEquals(Owner.SECRET,kahnDto.getSecret());
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


        securityContext.setAuthenticated(TestPrincipal.withName(KAHN));
        // memory filter
        UrlWebExtension hasPetsFilter = new UrlWebExtension(HasPetsFilter.class);
        UrlWebExtension petNameEndsWithAFilter = new UrlWebExtension(PetNameEndsWithFilter.class,"a");
        // jpql query filters (always come first)
        UrlWebExtension telNrPrefixFilter = new UrlWebExtension(OwnerTelNumberFilter.class,telnrPrefix);
        List<ReadOwnOwnerDto> responseDtos = ownerController.findAll2xx(ReadOwnOwnerDto.class,telNrPrefixFilter, hasPetsFilter,petNameEndsWithAFilter);
        RapidSecurityContext.unsetAuthenticated();

        // one dto findOwnDto and one findForeign
        Assertions.assertEquals(1,responseDtos.size());
        ReadOwnOwnerDto gilDto = assertCanFindInCollection(responseDtos, dto -> dto.getId().equals(savedGil.getId()));
        ReadForeignOwnerDto dto = assertIsEffectivelyReadForeignDto(gilDto);


        compare(dto).with(savedGil)
                .properties()
                .all()
                .assertEqual();
    }

    private ReadForeignOwnerDto assertIsEffectivelyReadForeignDto(ReadOwnOwnerDto dto){
        Assertions.assertNull(dto.getSecret());
        Assertions.assertNull(dto.getFirstName());
        Assertions.assertNull(dto.getLastName());
        Assertions.assertNull(dto.getClinicCardId());
        return new ModelMapper().map(dto, ReadForeignOwnerDto.class);
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

        securityContext.setAuthenticated(TestPrincipal.withName(KAHN));
        // memory filter
        UrlWebExtension hasPetsFilter = new UrlWebExtension(HasPetsFilter.class);
        // jpql filters (always come first)
        UrlWebExtension telNrPrefixFilter = new UrlWebExtension(OwnerTelNumberFilter.class,telnrPrefix);
        UrlWebExtension cityPrefixFilter = new UrlWebExtension(CityPrefixFilter.class, niceCityPrefix);
        List<ReadOwnOwnerDto> responseDtos = ownerController.findAll2xx(ReadOwnOwnerDto.class,telNrPrefixFilter,cityPrefixFilter, hasPetsFilter);
        RapidSecurityContext.unsetAuthenticated();

        Assertions.assertEquals(1,responseDtos.size());
        ReadOwnOwnerDto kahnDto = assertCanFindInCollection(responseDtos,dto -> dto.getId().equals(savedKahn.getId()));
        Assertions.assertEquals(Owner.SECRET,kahnDto.getSecret());

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
        securityContext.setAuthenticated(TestPrincipal.withName(KAHN));
        ReadOwnOwnerDto responseDto = ownerController.find2xx(savedKahnDto.getId(),ReadOwnOwnerDto.class);
        Assertions.assertEquals(2,responseDto.getPetIds().size());
        Assertions.assertTrue(responseDto.getPetIds().contains(savedBello.getId()));
        Assertions.assertTrue(responseDto.getPetIds().contains(savedKitty.getId()));
        RapidSecurityContext.unsetAuthenticated();
    }

    @Test
    public void anonCanFindForeignOwner() throws Exception {
        ReadOwnOwnerDto savedKahnDto = saveOwnerLinkedToPets(kahn);
        getMvc().perform(ownerController.find(savedKahnDto.getId()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.lastName").doesNotExist())
                .andExpect(jsonPath("$.city").value(kahn.getCity()));
        RapidSecurityContext.unsetAuthenticated();
    }

    @Test
    public void userCanFindForeignOwnOwner() throws Exception {
        ReadOwnOwnerDto savedKahnDto = saveOwnerLinkedToPets(kahn);
        securityContext.setAuthenticated(TestPrincipal.withName(MEIER));
        getMvc().perform(ownerController.find(savedKahnDto.getId()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.lastName").doesNotExist())
                .andExpect(jsonPath("$.city").value(kahn.getCity()));
        RapidSecurityContext.unsetAuthenticated();
    }



    // DELETE TESTS

    @Test
    public void canDeleteOwner_thusUnlinkFromPet() throws Exception {
        Pet savedBello = petRepository.save(bello);

        ReadOwnOwnerDto responseDto = saveOwnerLinkedToPets(kahn,savedBello.getId());

        getMvc().perform(ownerController.delete(responseDto.getId()))
                .andExpect(status().is2xxSuccessful());

        Assertions.assertFalse(ownerRepository.findByLastName(KAHN).isPresent());

        assertPetHasOwner(BELLO,null);
    }

    @Test
    public void canDeleteOwner_thusUnlinkFromClinicCard() throws Exception {
        ClinicCard savedClinicCard = clinicCardRepository.save(clinicCard);

        ReadOwnOwnerDto responseDto = saveOwnerLinkedToClinicCard(kahn,savedClinicCard);

        getMvc().perform(ownerController.delete(responseDto.getId()))
                .andExpect(status().is2xxSuccessful());

        Assertions.assertFalse(ownerRepository.findByLastName(KAHN).isPresent());

        assertClinicCardHasOwner(savedClinicCard.getId(),null);
    }

    @Test
    public void canDeleteOwner_thusUnlinkFromPets() throws Exception {
        Pet savedBello = petRepository.save(bello);
        Pet savedKitty = petRepository.save(kitty);

        ReadOwnOwnerDto responseDto = saveOwnerLinkedToPets(kahn,savedBello.getId(),savedKitty.getId());

        MvcResult result = getMvc().perform(ownerController.delete(responseDto.getId()))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Assertions.assertFalse(ownerRepository.findByLastName(KAHN).isPresent());
        assertPetHasOwner(BELLO,null);
        assertPetHasOwner(KITTY,null);
    }

}
