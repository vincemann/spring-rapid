package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import com.github.vincemann.springrapid.coredemo.controller.suite.MyIntegrationTest;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.web.servlet.MvcResult;

import java.util.*;

import static com.github.vincemann.springrapid.coredemo.controller.suite.TestData.*;
import static com.github.vincemann.springrapid.coretest.util.RapidTestUtil.createUpdateJsonLine;
import static com.github.vincemann.springrapid.coretest.util.RapidTestUtil.createUpdateJsonRequest;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestExecutionListeners(
        listeners = WithSecurityContextTestExecutionListener.class,
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
public class OwnerControllerIntegrationTest extends MyIntegrationTest {


    @Autowired
    RapidSecurityContext securityContext;

    // SAVE TESTS


    @Test
    public void canCreateOwnerWithoutPets() throws Exception {
        // when
        CreateOwnerDto createDto = new CreateOwnerDto(testData.getKahn());
        ReadOwnOwnerDto responseDto = ownerController.create2xx(createDto, ReadOwnOwnerDto.class);
        // then
        Optional<Owner> kahn = ownerService.findByLastName(KAHN);
        Assertions.assertTrue(kahn.isPresent());
        Owner dbKahn = kahn.get();
        Assertions.assertEquals(responseDto.getId(),dbKahn.getId());
        Assertions.assertTrue(kahn.get().getPets().isEmpty());
    }

    @Test
    public void canSaveOwnerWithManyHobbies() throws Exception {
        // when
        String bodybuilding = "bodybuilding";
        Set<String> hobbies = new HashSet<>(Arrays.asList("swimming","biking",bodybuilding,"jogging","eating"));
        testData.getKahn().setHobbies(hobbies);
        ReadOwnOwnerDto responseDto = helper.saveOwnerLinkedToPets(testData.getKahn());
        // then
        Assertions.assertEquals(hobbies,responseDto.getHobbies());
        Owner kahn = ownerService.findByLastName(KAHN).get();
        Assertions.assertEquals(hobbies,kahn.getHobbies());
    }

    @Test
    public void canSaveOwnerLinkedToSavedPet() throws Exception {
        // given
        Pet bello = petService.create(testData.getBello());
        // when
        ReadOwnOwnerDto responseDto = helper.saveOwnerLinkedToPets(testData.getKahn(),bello.getId());
        // then
        Assertions.assertTrue(responseDto.getPetIds().contains(bello.getId()));
        assertOwnerHasPets(KAHN,BELLO);
        assertPetHasOwner(BELLO,KAHN);
    }

    @Test
    public void canSaveOwnerLinkedToSavedClinicCard() throws Exception {
        // given
        ClinicCard clinicCard = clinicCardService.create(testData.getClinicCard());
        // when
        ReadOwnOwnerDto responseDto = helper.saveOwnerLinkedToClinicCard(testData.getKahn(),clinicCard);
        // then
        Assertions.assertEquals(responseDto.getClinicCardId(),clinicCard.getId());
        assertOwnerHasClinicCard(KAHN,clinicCard.getId());
        assertClinicCardHasOwner(clinicCard.getId(),KAHN);
    }

    @Test
    public void canSaveOwnerLinkedToMultipleSavedPets() throws Exception {
        // given
        Pet savedBello = petService.create(testData.getBello());
        Pet savedKitty = petService.create(testData.getKitty());
        // when
        ReadOwnOwnerDto responseDto = helper.saveOwnerLinkedToPets(testData.getKahn(),savedBello.getId(),savedKitty.getId());
        // then
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
        // given
        ReadOwnOwnerDto createdKahnDto = helper.saveOwnerLinkedToPets(testData.getKahn());
        // when
        String newCity = testData.getKahn().getCity()+"new";
        String updateJson = createUpdateJsonLine("replace", "/city",newCity);
        ReadOwnOwnerDto responseDto = ownerController.update2xx(
                createUpdateJsonRequest(updateJson),createdKahnDto.getId(),ReadOwnOwnerDto.class);
        // then
        Assertions.assertEquals(newCity,responseDto.getCity());
        Owner kahn = ownerService.findByLastName(KAHN).get();
        Assertions.assertEquals(newCity,kahn.getCity());
    }

    @Test
    public void canUpdateOwnersCityAndAddressInOneRequest() throws Exception {
        // given
        ReadOwnOwnerDto kahnDto = helper.saveOwnerLinkedToPets(testData.getKahn());
        // when
        String newCity = kahnDto.getCity()+"new";
        String newAdr = kahnDto.getAddress()+"new";
        String updateCityJson = createUpdateJsonLine("replace", "/city",newCity);
        String updateAdrJson = createUpdateJsonLine("replace", "/address",newAdr);
        String updateJsonRequest = createUpdateJsonRequest(updateCityJson, updateAdrJson);
        ReadOwnOwnerDto responseDto = ownerController.update2xx(
                createUpdateJsonRequest(updateJsonRequest),kahnDto.getId(),ReadOwnOwnerDto.class);
        // then
        Assertions.assertEquals(newCity,responseDto.getCity());
        Assertions.assertEquals(newAdr,responseDto.getAddress());
        Owner kahn = ownerService.findByLastName(KAHN).get();
        Assertions.assertEquals(newCity,kahn.getCity());
        Assertions.assertEquals(newAdr,kahn.getAddress());
    }


    @Test
    public void givenOwnerLinkedToPet_whenRemoveAllPetsOfOwnerViaUpdateOwner_thenPetsUnlinked() throws Exception {
        // given
        Pet savedBello = petService.create(testData.getBello());
        ReadOwnOwnerDto createdKahnDto = helper.saveOwnerLinkedToPets(testData.getKahn(),savedBello.getId());
        // when
        String updateJson = createUpdateJsonLine("remove", "/petIds");
        ReadOwnOwnerDto responseDto = ownerController.update2xx(
                createUpdateJsonRequest(updateJson),createdKahnDto.getId(),ReadOwnOwnerDto.class);
        // then
        Assertions.assertTrue(responseDto.getPetIds().isEmpty());
        assertOwnerHasPets(KAHN);
        assertPetHasOwner(BELLO,null);
    }

    @Test
    public void canUnlinkClinicCardFromOwnerViaUpdateOwner() throws Exception {
        // given
        ClinicCard savedClinicCard = clinicCardService.create(testData.getClinicCard());
        ReadOwnOwnerDto createdKahnDto = helper.saveOwnerLinkedToClinicCard(testData.getKahn(),savedClinicCard);
        // when
        String updateJson = createUpdateJsonLine("remove", "/clinicCardId");
        ReadOwnOwnerDto responseDto = ownerController.update2xx(
                createUpdateJsonRequest(createUpdateJsonRequest(updateJson)),createdKahnDto.getId(),ReadOwnOwnerDto.class);
        // then
        Assertions.assertNull(responseDto.getClinicCardId());
        assertOwnerHasClinicCard(KAHN,null);
        assertClinicCardHasOwner(savedClinicCard.getId(),null);
    }

    @Test
    public void canLinkClinicCardToOwnerViaUpdateOwner() throws Exception {
        // given
        ClinicCard savedClinicCard = clinicCardService.create(testData.getClinicCard());
        ReadOwnOwnerDto createdKahnDto = helper.saveOwner(testData.getKahn());
        // when
        String updateJson = createUpdateJsonLine("add", "/clinicCardId",savedClinicCard.getId().toString());
        ReadOwnOwnerDto responseDto = ownerController.update2xx(
                createUpdateJsonRequest(createUpdateJsonRequest(updateJson)),createdKahnDto.getId(),ReadOwnOwnerDto.class);
        // then
        Assertions.assertEquals(savedClinicCard.getId(),responseDto.getClinicCardId());
        assertOwnerHasClinicCard(KAHN,savedClinicCard.getId());
        assertClinicCardHasOwner(savedClinicCard.getId(),KAHN);
    }

    @Test
    public void canRelinkDiffClinicCardToOwnerViaUpdateOwner() throws Exception {
        // given
        ClinicCard savedClinicCard = clinicCardService.create(testData.getClinicCard());
        ClinicCard savedSecondClinicCard = clinicCardService.create(testData.getSecondClinicCard());
        ReadOwnOwnerDto createdKahnDto = helper.saveOwnerLinkedToClinicCard(testData.getKahn(),savedClinicCard);
        // when
        String updateJson = createUpdateJsonLine("replace", "/clinicCardId",savedSecondClinicCard.getId().toString());
        ReadOwnOwnerDto responseDto = ownerController.update2xx(
                createUpdateJsonRequest(createUpdateJsonRequest(updateJson)),createdKahnDto.getId(),ReadOwnOwnerDto.class);
        // then
        Assertions.assertEquals(savedSecondClinicCard.getId(),responseDto.getClinicCardId());
        assertOwnerHasClinicCard(KAHN,savedSecondClinicCard.getId());
        assertClinicCardHasOwner(savedClinicCard.getId(),null);
        assertClinicCardHasOwner(savedSecondClinicCard.getId(),KAHN);
    }

    @Test
    public void canUnlinkPetFromOwnerViaRemoveSpecificPetInUpdateOwner() throws Exception {
        //given
        Pet bello = petService.create(testData.getBello());
        ReadOwnOwnerDto createdKahnDto = helper.saveOwnerLinkedToPets(testData.getKahn(),bello.getId());

        // when
        String updateJson = createUpdateJsonLine("remove", "/petIds",bello.getId().toString());
        ReadOwnOwnerDto responseDto = ownerController.update2xx(
                createUpdateJsonRequest(createUpdateJsonRequest(updateJson)),createdKahnDto.getId(),ReadOwnOwnerDto.class);
        // then
        Assertions.assertTrue(responseDto.getPetIds().isEmpty());
        assertOwnerHasPets(KAHN);
        assertPetHasOwner(BELLO,null);
    }

    @Test
    public void canUnlinkMultiplePetsFromOwnerViaRemoveAllPetsInUpdateOwner() throws Exception {
        // given
        Pet bello = petService.create(testData.getBello());
        Pet kitty = petService.create(testData.getKitty());
        ReadOwnOwnerDto createdKahnDto = helper.saveOwnerLinkedToPets(testData.getKahn(),bello.getId(),kitty.getId());
        // when
        String updateJson = createUpdateJsonLine("remove", "/petIds");
        ReadOwnOwnerDto responseDto = ownerController.update2xx(
                createUpdateJsonRequest(createUpdateJsonRequest(updateJson)),createdKahnDto.getId(),ReadOwnOwnerDto.class);
        // then
        Assertions.assertTrue(responseDto.getPetIds().isEmpty());
        assertOwnerHasPets(KAHN);
        assertPetHasOwner(BELLO,null);
        assertPetHasOwner(KITTY,null);
    }

    @Test
    public void canUnlinkOneOfManyPetsFromOwnerViaUpdateOwner() throws Exception {
        // given
        Pet bello = petService.create(testData.getBello());
        Pet kitty = petService.create(testData.getKitty());
        ReadOwnOwnerDto createdKahnDto = helper.saveOwnerLinkedToPets(testData.getKahn(),bello.getId(),kitty.getId());
        // when
        String updateJson = createUpdateJsonLine("remove", "/petIds",bello.getId().toString());
        ReadOwnOwnerDto responseDto = ownerController.update2xx(
                createUpdateJsonRequest(createUpdateJsonRequest(updateJson)),createdKahnDto.getId(),ReadOwnOwnerDto.class);
        // then
        Assertions.assertTrue(responseDto.getPetIds().contains(kitty.getId()));
        Assertions.assertEquals(1,responseDto.getPetIds().size());
        assertOwnerHasPets(KAHN,KITTY);
        assertPetHasOwner(BELLO,null);
        assertPetHasOwner(KITTY,KAHN);
    }

    @Test
    public void canUnlinkSomeOfManyPetsFromOwnerViaUpdateOwner() throws Exception {
        // given
        Pet bello = petService.create(testData.getBello());
        Pet kitty = petService.create(testData.getKitty());
        Pet bella = petService.create(testData.getBella());
        ReadOwnOwnerDto createdKahnDto = helper.saveOwnerLinkedToPets(testData.getKahn(),bello.getId(),kitty.getId(),bella.getId());

        // when
        String removeBelloJson = createUpdateJsonLine("remove", "/petIds",bello.getId().toString());
        String removeKittyJson = createUpdateJsonLine("remove", "/petIds",kitty.getId().toString());
        String removePetsJson = createUpdateJsonRequest(removeBelloJson, removeKittyJson);
        ReadOwnOwnerDto responseDto = ownerController.update2xx(
                createUpdateJsonRequest(createUpdateJsonRequest(removePetsJson)),createdKahnDto.getId(),ReadOwnOwnerDto.class);
        // then
        Assertions.assertTrue(responseDto.getPetIds().contains(bella.getId()));
        Assertions.assertEquals(1,responseDto.getPetIds().size());
        assertOwnerHasPets(KAHN,BELLA);
        assertPetHasOwner(BELLO,null);
        assertPetHasOwner(KITTY,null);
        assertPetHasOwner(BELLA,KAHN);
    }

    @Test
    public void canUnlinkSomeOfManyPetsFromOwnerViaUpdateOwnerInDiffOrder() throws Exception {
        // given
        Pet bello = petService.create(testData.getBello());
        Pet kitty = petService.create(testData.getKitty());
        Pet bella = petService.create(testData.getBella());
        ReadOwnOwnerDto createdKahnDto = helper.saveOwnerLinkedToPets(testData.getKahn(),bello.getId(),kitty.getId(),bella.getId());

        // when
        String removeBelloJson = createUpdateJsonLine("remove", "/petIds",bello.getId().toString());
        String removeKittyJson = createUpdateJsonLine("remove", "/petIds",bella.getId().toString());
        String removePetsJson = createUpdateJsonRequest(removeBelloJson, removeKittyJson);
        ReadOwnOwnerDto responseDto = ownerController.update2xx(
                createUpdateJsonRequest(createUpdateJsonRequest(removePetsJson)),
                createdKahnDto.getId(),ReadOwnOwnerDto.class);
        // then
        Assertions.assertTrue(responseDto.getPetIds().contains(kitty.getId()));
        Assertions.assertEquals(1,responseDto.getPetIds().size());
        assertOwnerHasPets(KAHN,KITTY);
        assertPetHasOwner(BELLO,null);
        assertPetHasOwner(KITTY,KAHN);
        assertPetHasOwner(BELLA,null);
    }

    @Test
    public void canRemoveOneOfManyHobbiesFromOwnerViaUpdateOwner() throws Exception {
        // given
        String hobbyToRemove = "bodybuilding";
        Set<String> hobbies = new HashSet<>(Arrays.asList("swimming","biking",hobbyToRemove,"jogging","eating"));
        testData.getKahn().setHobbies(hobbies);
        ReadOwnOwnerDto createdKahnDto = helper.saveOwnerLinkedToPets(testData.getKahn());
        // when
        String updateJson = createUpdateJsonLine("remove", "/hobbies", hobbyToRemove);
        ReadOwnOwnerDto responseDto = ownerController.update2xx(
                createUpdateJsonRequest(createUpdateJsonRequest(updateJson)),createdKahnDto.getId(),ReadOwnOwnerDto.class);
        // then
        Assertions.assertFalse(responseDto.getHobbies().contains(hobbyToRemove));
        Assertions.assertEquals(hobbies.size()-1,responseDto.getHobbies().size());
        Owner dbKahn = ownerService.findByLastName(KAHN).get();
        Assertions.assertFalse(dbKahn.getHobbies().contains(hobbyToRemove));
        Assertions.assertEquals(hobbies.size()-1,dbKahn.getHobbies().size());
    }

    @Test
    public void canLinkSavedPetToOwnerViaUpdateOwner() throws Exception {
        // given
        Pet bello = petService.create(testData.getBello());
        ReadOwnOwnerDto createdKahnDto = helper.saveOwnerLinkedToPets(testData.getKahn());

        // when
        String updateJson = createUpdateJsonLine("add", "/petIds", bello.getId().toString());
        ReadOwnOwnerDto responseDto = ownerController.update2xx(
                createUpdateJsonRequest(createUpdateJsonRequest(updateJson)),createdKahnDto.getId(),ReadOwnOwnerDto.class);
        // then
        Assertions.assertTrue(responseDto.getPetIds().contains(bello.getId()));
        assertOwnerHasPets(KAHN,BELLO);
        assertPetHasOwner(BELLO,KAHN);
    }

    // FIND TESTS
    @Test
    @WithMockUser(username = KAHN)
    public void ownerCanFindOwnOwner() throws Exception {
        // given
        ReadOwnOwnerDto kahn = helper.saveOwnerLinkedToPets(testData.getKahn());
        // when
        ReadOwnOwnerDto response = ownerController.find2xx(kahn.getId(), ReadOwnOwnerDto.class);
        // then
        Assertions.assertEquals(kahn.getLastName(),response.getLastName());
        Assertions.assertEquals(Owner.SECRET,response.getSecret());
    }

    @Test
    public void canFindAllOwnersWithPetsFilter() throws Exception {

        // save kahn -> bello
        // meier -> kitty
        // gil -> []
        // find all owners with hasPets filter (in memory filter), authenticated as kahn
        // should find kahn and meier
        // kahn should be FindOwnOwnerDto and meier FindForeignOwnerDto

        // given
        Pet bello = petService.create(testData.getBello());
        Pet kitty = petService.create(testData.getKitty());

        ReadOwnOwnerDto savedKahn = helper.saveOwnerLinkedToPets(testData.getKahn(),bello.getId());
        ReadOwnOwnerDto savedMeier = helper.saveOwnerLinkedToPets(testData.getMeier(), kitty.getId());
        ReadOwnOwnerDto savedGil = helper.saveOwnerLinkedToPets(testData.getGil());
        Assertions.assertEquals(3,ownerService.findAll().size());

        // when
        securityContext.setAuthenticated(TestPrincipal.withName(KAHN));
        // memory filter
        UrlWebExtension hasPetsFilter = new UrlWebExtension(HasPetsFilter.class);
        List<ReadOwnOwnerDto> responseDtos = ownerController.findAll2xx(ReadOwnOwnerDto.class, hasPetsFilter);
        RapidSecurityContext.unsetAuthenticated();
        // then
        // one dto findOwnDto and one findForeign
        Assertions.assertEquals(2,responseDtos.size());
        ReadOwnOwnerDto kahnDto = assertCanFindInCollection(responseDtos,dto -> dto.getId().equals(savedKahn.getId()));
        Assertions.assertEquals(Owner.SECRET,kahnDto.getSecret());
        ReadOwnOwnerDto meierDto = assertCanFindInCollection(responseDtos,dto -> dto.getId().equals(savedMeier.getId()));
        Assertions.assertNull(meierDto.getSecret());
    }

    @Test
    public void canFindAllOwnersWithPetsFilterSortedByLastNameDsc() throws Exception {

        // save kahn -> bello
        // meier -> kitty
        // gil -> []
        // find all owners with hasPets filter (in memory filter), authenticated as kahn
        // should find kahn and meier - sorted by name desc -> meier, kahn
        // kahn should be FindOwnOwnerDto and meier FindForeignOwnerDto
        // find again with asc name sorting -> [kahn, meier]

        // given
        Pet bello = petService.create(testData.getBello());
        Pet kitty = petService.create(testData.getKitty());

        ReadOwnOwnerDto savedKahn = helper.saveOwnerLinkedToPets(testData.getKahn(),bello.getId());
        ReadOwnOwnerDto savedMeier = helper.saveOwnerLinkedToPets(testData.getMeier(), kitty.getId());
        ReadOwnOwnerDto savedGil = helper.saveOwnerLinkedToPets(testData.getGil());
        Assertions.assertEquals(3,ownerService.findAll().size());

        // when
        securityContext.setAuthenticated(TestPrincipal.withName(KAHN));
        // memory filter
        UrlWebExtension hasPetsFilter = new UrlWebExtension(HasPetsFilter.class);
        UrlWebExtension sortByNameDesc = new UrlWebExtension(LastNameDescSorting.class);
        List<ReadOwnOwnerDto> responseDtos = ownerController.findAll2xx(ReadOwnOwnerDto.class, hasPetsFilter, sortByNameDesc);
        RapidSecurityContext.unsetAuthenticated();

        // then
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
    }

    @Test
    public void canFindAllOwnersWithPetsFilterSortedByLastNameAsc() throws Exception {

        // save kahn -> bello
        // meier -> kitty
        // gil -> []
        // find all owners with hasPets filter (in memory filter), authenticated as kahn
        // should find kahn and meier - sorted by name asc -> kahn, meier
        // kahn should be FindOwnOwnerDto and meier FindForeignOwnerDto

        // given
        Pet bello = petService.create(testData.getBello());
        Pet kitty = petService.create(testData.getKitty());

        ReadOwnOwnerDto savedKahn = helper.saveOwnerLinkedToPets(testData.getKahn(),bello.getId());
        ReadOwnOwnerDto savedMeier = helper.saveOwnerLinkedToPets(testData.getMeier(), kitty.getId());
        ReadOwnOwnerDto savedGil = helper.saveOwnerLinkedToPets(testData.getGil());
        Assertions.assertEquals(3,ownerService.findAll().size());

        // when
        securityContext.setAuthenticated(TestPrincipal.withName(KAHN));
        // memory filter
        UrlWebExtension hasPetsFilter = new UrlWebExtension(HasPetsFilter.class);
        UrlWebExtension sortByNameAsc = new UrlWebExtension(LastNameAscSorting.class);
        securityContext.setAuthenticated(TestPrincipal.withName(KAHN));
        List<ReadOwnOwnerDto> responseDtos = ownerController.findAll2xx(ReadOwnOwnerDto.class, hasPetsFilter, sortByNameAsc);
        RapidSecurityContext.unsetAuthenticated();

        // one dto findOwnDto and one findForeign
        Assertions.assertEquals(2,responseDtos.size());
        // order
        Assertions.assertEquals(savedKahn.getId(),responseDtos.get(0).getId());
        Assertions.assertEquals(savedMeier.getId(),responseDtos.get(1).getId());

        // content
        ReadOwnOwnerDto kahnDto = assertCanFindInCollection(responseDtos,dto -> dto.getId().equals(savedKahn.getId()));
        Assertions.assertEquals(Owner.SECRET,kahnDto.getSecret());
        ReadOwnOwnerDto meierDto = assertCanFindInCollection(responseDtos,dto -> dto.getId().equals(savedMeier.getId()));
        Assertions.assertNull(meierDto.getSecret());
    }



    // can combine query filter with memory filter
    @Test
    public void canFindAllOwnersWithPetsFilterAndInMemoryTelNrPrefix() throws Exception {
        // save kahn -> bello
        // meier -> kitty
        // gil -> []
        // find all owners with hasPets filter (in memory filter), authenticated as kahn
        // and combine with jpql filter checking telnr prefix for 0176
        // should find kahn only
        // kahn should be FindOwnOwnerDto
        // given
        Pet bello = petService.create(testData.getBello());
        Pet kitty = petService.create(testData.getKitty());

        ReadOwnOwnerDto savedKahn = helper.saveOwnerLinkedToPets(testData.getKahn(),bello.getId());
        ReadOwnOwnerDto savedMeier = helper.saveOwnerLinkedToPets(testData.getMeier(), kitty.getId());
        ReadOwnOwnerDto savedGil = helper.saveOwnerLinkedToPets(testData.getGil());
        Assertions.assertEquals(3,ownerService.findAll().size());

        String telnrPrefix = "0176";
        Assertions.assertTrue(savedKahn.getTelephone().startsWith(telnrPrefix));
        Assertions.assertTrue(savedGil.getTelephone().startsWith(telnrPrefix));
        Assertions.assertFalse(savedMeier.getTelephone().startsWith(telnrPrefix));


        // when
        securityContext.setAuthenticated(TestPrincipal.withName(KAHN));
        // memory filter
        UrlWebExtension hasPetsFilter = new UrlWebExtension(HasPetsFilter.class);
        UrlWebExtension telNrPrefixFilter = new UrlWebExtension(OwnerTelNumberFilter.class,telnrPrefix);
        List<ReadOwnOwnerDto> responseDtos = ownerController.findAll2xx(ReadOwnOwnerDto.class,telNrPrefixFilter, hasPetsFilter);
        RapidSecurityContext.unsetAuthenticated();
        // then

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

        // given
        Pet bello = petService.create(testData.getBello());
        Pet kitty = petService.create(testData.getKitty());

        ReadOwnOwnerDto savedKahn = helper.saveOwnerLinkedToPets(testData.getKahn(),bello.getId());
        ReadOwnOwnerDto savedMeier = helper.saveOwnerLinkedToPets(testData.getMeier(), kitty.getId());
        ReadOwnOwnerDto savedGil = helper.saveOwnerLinkedToPets(testData.getGil());
        Assertions.assertEquals(3,ownerService.findAll().size());

        String telnrPrefix = "0176";
        Assertions.assertTrue(savedKahn.getTelephone().startsWith(telnrPrefix));
        Assertions.assertTrue(savedGil.getTelephone().startsWith(telnrPrefix));
        Assertions.assertFalse(savedMeier.getTelephone().startsWith(telnrPrefix));


        // when
        securityContext.setAuthenticated(TestPrincipal.withName(KAHN));
        // memory filter
        UrlWebExtension hasPetsFilter = new UrlWebExtension(HasPetsFilter.class);
        UrlWebExtension petNameEndsWithAFilter = new UrlWebExtension(PetNameEndsWithFilter.class,"a");
        // jpql query filters (always come first)
        UrlWebExtension telNrPrefixFilter = new UrlWebExtension(OwnerTelNumberFilter.class,telnrPrefix);
        List<ReadOwnOwnerDto> responseDtos = ownerController.findAll2xx(ReadOwnOwnerDto.class,telNrPrefixFilter, hasPetsFilter,petNameEndsWithAFilter);
        RapidSecurityContext.unsetAuthenticated();
        // then

        // one dto findOwnDto and one findForeign
        Assertions.assertEquals(1,responseDtos.size());
        ReadOwnOwnerDto gilDto = assertCanFindInCollection(responseDtos, dto -> dto.getId().equals(savedGil.getId()));
        ReadForeignOwnerDto dto = assertIsEffectivelyReadForeignDto(gilDto);
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

        // given
        Pet bello = petService.create(testData.getBello());
        Pet kitty = petService.create(testData.getKitty());

        ReadOwnOwnerDto savedKahn = helper.saveOwnerLinkedToPets(testData.getKahn(),bello.getId());
        ReadOwnOwnerDto savedMeier = helper.saveOwnerLinkedToPets(testData.getMeier(), kitty.getId());
        ReadOwnOwnerDto savedGil = helper.saveOwnerLinkedToPets(testData.getGil());
        Assertions.assertEquals(3,ownerService.findAll().size());

        String telnrPrefix = "0176";
        Assertions.assertTrue(savedKahn.getTelephone().startsWith(telnrPrefix));
        Assertions.assertTrue(savedGil.getTelephone().startsWith(telnrPrefix));
        Assertions.assertFalse(savedMeier.getTelephone().startsWith(telnrPrefix));

        String niceCityPrefix = "n1";
        Assertions.assertTrue(savedKahn.getCity().startsWith(niceCityPrefix));
        Assertions.assertFalse(savedGil.getCity().startsWith(niceCityPrefix));

        // when
        securityContext.setAuthenticated(TestPrincipal.withName(KAHN));
        // memory filter
        UrlWebExtension hasPetsFilter = new UrlWebExtension(HasPetsFilter.class);
        // jpql filters (always come first)
        UrlWebExtension telNrPrefixFilter = new UrlWebExtension(OwnerTelNumberFilter.class,telnrPrefix);
        UrlWebExtension cityPrefixFilter = new UrlWebExtension(CityPrefixFilter.class, niceCityPrefix);
        List<ReadOwnOwnerDto> responseDtos = ownerController.findAll2xx(ReadOwnOwnerDto.class,telNrPrefixFilter,cityPrefixFilter, hasPetsFilter);
        RapidSecurityContext.unsetAuthenticated();
        // then

        Assertions.assertEquals(1,responseDtos.size());
        ReadOwnOwnerDto kahnDto = assertCanFindInCollection(responseDtos,dto -> dto.getId().equals(savedKahn.getId()));
        Assertions.assertEquals(Owner.SECRET,kahnDto.getSecret());
    }


    @Test
    public void canFindOwnOwnerWithPets() throws Exception {
        // given
        Pet bello = petService.create(testData.getBello());
        Pet kitty = petService.create(testData.getKitty());
        ReadOwnOwnerDto savedKahn = helper.saveOwnerLinkedToPets(testData.getKahn(),bello.getId(), kitty.getId());
        // when
        securityContext.setAuthenticated(TestPrincipal.withName(KAHN));
        ReadOwnOwnerDto responseDto = ownerController.find2xx(savedKahn.getId(),ReadOwnOwnerDto.class);
        // then
        Assertions.assertEquals(2,responseDto.getPetIds().size());
        Assertions.assertTrue(responseDto.getPetIds().contains(bello.getId()));
        Assertions.assertTrue(responseDto.getPetIds().contains(kitty.getId()));
        RapidSecurityContext.unsetAuthenticated();
    }

    @Test
    public void anonCanFindForeignOwner() throws Exception {
        // given
        ReadOwnOwnerDto savedKahnDto = helper.saveOwnerLinkedToPets(testData.getKahn());
        // when
        getMvc().perform(ownerController.find(savedKahnDto.getId()))
        // then
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.lastName").doesNotExist())
                .andExpect(jsonPath("$.city").value(savedKahnDto.getCity()));
        RapidSecurityContext.unsetAuthenticated();
    }

    @Test
    public void userCanFindForeignOwnOwner() throws Exception {
        // given
        ReadOwnOwnerDto savedKahnDto = helper.saveOwnerLinkedToPets(testData.getKahn());
        // when
        securityContext.setAuthenticated(TestPrincipal.withName(MEIER));
        getMvc().perform(ownerController.find(savedKahnDto.getId()))
        // then
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.lastName").doesNotExist())
                .andExpect(jsonPath("$.city").value(savedKahnDto.getCity()));
        RapidSecurityContext.unsetAuthenticated();
    }



    // DELETE TESTS

    @Test
    public void givenPetLinkedToOwner_whenDeletingOwner_thenPetGetsUnlinked() throws Exception {
        // given
        Pet bello = petService.create(testData.getBello());
        ReadOwnOwnerDto responseDto = helper.saveOwnerLinkedToPets(testData.getKahn(),bello.getId());

        // when
        getMvc().perform(ownerController.delete(responseDto.getId()))
                .andExpect(status().is2xxSuccessful());
        // then
        Assertions.assertFalse(ownerService.findByLastName(KAHN).isPresent());
        assertPetHasOwner(BELLO,null);
    }

    @Test
    public void givenCardLinkedToOwner_whenDeletingOwner_thenCardGetsUnlinked() throws Exception {
        // given
        ClinicCard savedClinicCard = clinicCardService.create(testData.getClinicCard());
        ReadOwnOwnerDto responseDto = helper.saveOwnerLinkedToClinicCard(testData.getKahn(),savedClinicCard);
        // when
        getMvc().perform(ownerController.delete(responseDto.getId()))
                .andExpect(status().is2xxSuccessful());
        // then
        Assertions.assertFalse(ownerService.findByLastName(KAHN).isPresent());
        assertClinicCardHasOwner(savedClinicCard.getId(),null);
    }

    @Test
    public void givenMultiplePetsLinkedToOwner_whenRemoveOwner_petsGetUnlinked() throws Exception {
        // given
        Pet savedBello = petService.create(testData.getBello());
        Pet savedKitty = petService.create(testData.getKitty());
        ReadOwnOwnerDto responseDto = helper.saveOwnerLinkedToPets(testData.getKahn(),savedBello.getId(),savedKitty.getId());

        // when
        MvcResult result = getMvc().perform(ownerController.delete(responseDto.getId()))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        // then
        Assertions.assertFalse(ownerService.findByLastName(KAHN).isPresent());
        assertPetHasOwner(BELLO,null);
        assertPetHasOwner(KITTY,null);
    }

}
