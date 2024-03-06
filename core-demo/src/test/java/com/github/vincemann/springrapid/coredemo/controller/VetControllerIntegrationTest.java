package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.core.util.Lists;
import com.github.vincemann.springrapid.coredemo.controller.suite.MyIntegrationTest;
import com.github.vincemann.springrapid.coredemo.controller.suite.template.VetControllerTestTemplate;
import com.github.vincemann.springrapid.coredemo.dto.VetDto;
import com.github.vincemann.springrapid.coredemo.model.Specialty;
import com.github.vincemann.springrapid.coredemo.model.Vet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;

import static com.github.vincemann.springrapid.coredemo.controller.suite.TestData.*;
import static com.github.vincemann.springrapid.coretest.util.RapidTestUtil.createUpdateJsonLine;
import static com.github.vincemann.springrapid.coretest.util.RapidTestUtil.createUpdateJsonRequest;

@Tag(value = "demo-projects")
public class VetControllerIntegrationTest extends MyIntegrationTest {


    @Autowired
    VetControllerTestTemplate controller;

    @Test
    public void canSaveVetLinkedToSavedSpecialties() throws Exception {
        // given
        Specialty dentism = specialtyService.create(testData.getDentism());
        Specialty gastro = specialtyService.create(testData.getGastro());

        // when
        VetDto createPoldiDto = new VetDto(testData.getVetPoldi());
        createPoldiDto.setSpecialtyIds(new HashSet<>(Lists.newArrayList(dentism.getId(),gastro.getId())));
        VetDto responseDto = controller.create2xx(createPoldiDto, VetDto.class);
        // then
        Assertions.assertTrue(vetService.findByLastName(VET_POLDI).isPresent());
        Vet poldi = vetService.findByLastName(VET_POLDI).get();
        Assertions.assertEquals(responseDto.getId(),poldi.getId());

        assertVetHasSpecialties(VET_POLDI,DENTISM,GASTRO);
        assertSpecialtyHasVets(DENTISM, VET_POLDI);
        assertSpecialtyHasVets(GASTRO, VET_POLDI);
    }

    @Test
    public void canUnlinkMultipleSpecialtiesFromVetViaSingleUpdateVetRequest() throws Exception {
        // poldi -> dentism, gastro, heart
        // max -> dentism, heart
        
        // given
        Specialty dentism = specialtyService.create(testData.getDentism());
        Specialty gastro = specialtyService.create(testData.getGastro());
        Specialty savedHeart = specialtyService.create(testData.getHeart());
        VetDto poldi = helper.createVetLinkedToSpecialties(testData.getVetPoldi(),dentism,gastro,savedHeart);
        VetDto max = helper.createVetLinkedToSpecialties(testData.getVetMax(),dentism,savedHeart);

        // when
        // remove poldis dentism and gastro specialty
        String removeDentism = createUpdateJsonLine("remove", "/specialtyIds",dentism.getId().toString());
        String removeGastro = createUpdateJsonLine("remove", "/specialtyIds",gastro.getId().toString());
        String jsonPatch = createUpdateJsonRequest(removeDentism, removeGastro);
        VetDto responseDto = controller.update2xx(jsonPatch, poldi.getId(), VetDto.class);
        // then
        Assertions.assertTrue(responseDto.getSpecialtyIds().contains(savedHeart.getId()));
        Assertions.assertEquals(1,responseDto.getSpecialtyIds().size());

        assertVetHasSpecialties(VET_POLDI,HEART);
        assertVetHasSpecialties(VET_MAX,DENTISM, HEART);

        assertSpecialtyHasVets(DENTISM, VET_MAX);
        assertSpecialtyHasVets(GASTRO);
        assertSpecialtyHasVets(HEART, VET_POLDI, VET_MAX);
    }

    @Test
    public void canAddSpecialtyToVetViaUpdateVet() throws Exception {
        // poldi -> dentism
        // max -> dentism, heart
        // given
        Specialty dentism = specialtyService.create(testData.getDentism());
        Specialty gastro = specialtyService.create(testData.getGastro());
        Specialty savedHeart = specialtyService.create(testData.getHeart());
        VetDto poldi = helper.createVetLinkedToSpecialties(testData.getVetPoldi(),dentism);
        VetDto max = helper.createVetLinkedToSpecialties(testData.getVetMax(),dentism,savedHeart);

        // when
        // add gastro to poldi
        String addDentismJson = createUpdateJsonLine("add", "/specialtyIds/-",gastro.getId().toString());
        String jsonPatch = createUpdateJsonRequest(addDentismJson);
        VetDto responseDto = controller.update2xx(jsonPatch, poldi.getId(), VetDto.class);
        // then
        Assertions.assertTrue(responseDto.getSpecialtyIds().contains(gastro.getId()));
        Assertions.assertTrue(responseDto.getSpecialtyIds().contains(dentism.getId()));
        Assertions.assertEquals(2,responseDto.getSpecialtyIds().size());

        assertVetHasSpecialties(VET_POLDI,DENTISM,GASTRO);
        assertVetHasSpecialties(VET_MAX,DENTISM, HEART);

        assertSpecialtyHasVets(DENTISM, VET_MAX, VET_POLDI);
        assertSpecialtyHasVets(GASTRO, VET_POLDI);
        assertSpecialtyHasVets(HEART, VET_MAX);
    }

    @Test
    public void canLinkMultipleSpecialtiesToVetViaSingleUpdateVetRequest() throws Exception {
        // poldi -> dentism
        // max -> dentism, heart
        // given
        Specialty dentism = specialtyService.create(testData.getDentism());
        Specialty gastro = specialtyService.create(testData.getGastro());
        Specialty savedHeart = specialtyService.create(testData.getHeart());
        VetDto poldi = helper.createVetLinkedToSpecialties(testData.getVetPoldi(),dentism);
        VetDto max = helper.createVetLinkedToSpecialties(testData.getVetMax(),dentism,savedHeart);

        // when
        // add gastro and heart to poldi
        String addDentismJson = createUpdateJsonLine("add", "/specialtyIds/-",gastro.getId().toString());
        String addGastroJson = createUpdateJsonLine("add", "/specialtyIds/-",savedHeart.getId().toString());
        String jsonPatch = createUpdateJsonRequest(addDentismJson, addGastroJson);
        VetDto responseDto = controller.update2xx(jsonPatch, poldi.getId(), VetDto.class);
        // then
        Assertions.assertTrue(responseDto.getSpecialtyIds().contains(savedHeart.getId()));
        Assertions.assertTrue(responseDto.getSpecialtyIds().contains(gastro.getId()));
        Assertions.assertTrue(responseDto.getSpecialtyIds().contains(dentism.getId()));
        Assertions.assertEquals(3,responseDto.getSpecialtyIds().size());

        assertVetHasSpecialties(VET_POLDI,HEART,DENTISM,GASTRO);
        assertVetHasSpecialties(VET_MAX,DENTISM, HEART);

        assertSpecialtyHasVets(DENTISM, VET_MAX, VET_POLDI);
        assertSpecialtyHasVets(GASTRO, VET_POLDI);
        assertSpecialtyHasVets(HEART, VET_POLDI, VET_MAX);
    }


    @Test
    public void canUnlinkAllSpecialtiesFromVetViaUpdateVet() throws Exception {
        // poldi -> dentism, heart
        // max -> dentism, heart

        // given
        Specialty dentism = specialtyService.create(testData.getDentism());
        Specialty gastro = specialtyService.create(testData.getGastro());
        Specialty savedHeart = specialtyService.create(testData.getHeart());
        VetDto poldi = helper.createVetLinkedToSpecialties(testData.getVetPoldi(),dentism,savedHeart);
        VetDto max = helper.createVetLinkedToSpecialties(testData.getVetMax(),dentism,savedHeart);

        // when
        // remove all from poldi
        String removeAllJson = createUpdateJsonLine("remove", "/specialtyIds");
        String jsonPatch = createUpdateJsonRequest(removeAllJson);
        VetDto responseDto = controller.update2xx(jsonPatch, poldi.getId(), VetDto.class);
        // then
        Assertions.assertEquals(0,responseDto.getSpecialtyIds().size());

        assertVetHasSpecialties(VET_POLDI);
        assertVetHasSpecialties(VET_MAX,DENTISM, HEART);

        assertSpecialtyHasVets(DENTISM, VET_MAX);
        assertSpecialtyHasVets(GASTRO);
        assertSpecialtyHasVets(HEART, VET_MAX);
    }

    @Test
    public void canUnlinkAllSpecialtiesFromVetAndAddOneViaSingleUpdateVetRequest() throws Exception {
        // poldi -> dentism, heart
        // max -> dentism, heart

        // given
        Specialty dentism = specialtyService.create(testData.getDentism());
        Specialty gastro = specialtyService.create(testData.getGastro());
        Specialty savedHeart = specialtyService.create(testData.getHeart());
        VetDto poldi = helper.createVetLinkedToSpecialties(testData.getVetPoldi(),dentism,savedHeart);
        VetDto max = helper.createVetLinkedToSpecialties(testData.getVetMax(),dentism,savedHeart);

        // when
        // remove all from poldi then add gastro
        String removeAllJson = createUpdateJsonLine("remove", "/specialtyIds");
        String addGastroJson = createUpdateJsonLine("add", "/specialtyIds",gastro.getId().toString());
        String jsonPatch = createUpdateJsonRequest(removeAllJson,addGastroJson);
        VetDto responseDto = controller.update2xx(jsonPatch, poldi.getId(), VetDto.class);
        // then
        Assertions.assertTrue(responseDto.getSpecialtyIds().contains(gastro.getId()));
        Assertions.assertEquals(1,responseDto.getSpecialtyIds().size());

        assertVetHasSpecialties(VET_POLDI,GASTRO);
        assertVetHasSpecialties(VET_MAX,DENTISM, HEART);

        assertSpecialtyHasVets(DENTISM, VET_MAX);
        assertSpecialtyHasVets(GASTRO, VET_POLDI);
        assertSpecialtyHasVets(HEART, VET_MAX);
    }

    @Test
    public void canUnlinkSpecificSpecialityFromVet_andAddDiffSpecialtyToVet_viaSingleUpdateVetRequest() throws Exception {
        // poldi -> dentism, heart
        // max -> dentism, heart
        // given
        Specialty dentism = specialtyService.create(testData.getDentism());
        Specialty gastro = specialtyService.create(testData.getGastro());
        Specialty savedHeart = specialtyService.create(testData.getHeart());
        VetDto poldi = helper.createVetLinkedToSpecialties(testData.getVetPoldi(),dentism,savedHeart);
        VetDto max = helper.createVetLinkedToSpecialties(testData.getVetMax(),dentism,savedHeart);

        // when
        // remove dentism from poldi then add gastro
        String removeDentism = createUpdateJsonLine("remove", "/specialtyIds",dentism.getId().toString());
        String addGastroJson = createUpdateJsonLine("add", "/specialtyIds/-",gastro.getId().toString());
        String jsonPatch = createUpdateJsonRequest(removeDentism,addGastroJson);
        VetDto responseDto = controller.update2xx(jsonPatch, poldi.getId(), VetDto.class);
        // then
        Assertions.assertTrue(responseDto.getSpecialtyIds().contains(gastro.getId()));
        Assertions.assertTrue(responseDto.getSpecialtyIds().contains(savedHeart.getId()));
        Assertions.assertEquals(2,responseDto.getSpecialtyIds().size());

        assertVetHasSpecialties(VET_POLDI,GASTRO,HEART);
        assertVetHasSpecialties(VET_MAX,DENTISM, HEART);

        assertSpecialtyHasVets(DENTISM, VET_MAX);
        assertSpecialtyHasVets(GASTRO, VET_POLDI);
        assertSpecialtyHasVets(HEART, VET_MAX, VET_POLDI);
    }



    @Test
    public void givenAllSpecialtiesFromVetUnlinkedViaUpdatePetRequest_canAddMultipleSpecialtiesToVetInSubsequentUpdateVetRequest() throws Exception {
        // poldi -> dentism, heart
        // max -> dentism, heart

        // given
        Specialty dentism = specialtyService.create(testData.getDentism());
        Specialty gastro = specialtyService.create(testData.getGastro());
        Specialty savedHeart = specialtyService.create(testData.getHeart());
        Specialty savedMuscle = specialtyService.create(testData.getMuscle());
        VetDto poldi = helper.createVetLinkedToSpecialties(testData.getVetPoldi(),dentism,savedHeart);
        VetDto max = helper.createVetLinkedToSpecialties(testData.getVetMax(),dentism,savedHeart);

        // remove all from poldi then add gastro and muscle
        String removeAllJson = createUpdateJsonLine("remove", "/specialtyIds");
        String jsonPatch = createUpdateJsonRequest(removeAllJson);
        VetDto responseDto = controller.update2xx(jsonPatch, poldi.getId(), VetDto.class);
        Assertions.assertEquals(0,responseDto.getSpecialtyIds().size());

        // when
        String addGastroJson = createUpdateJsonLine("add", "/specialtyIds/-",gastro.getId().toString());
        String addMuscleJson = createUpdateJsonLine("add", "/specialtyIds/-",savedMuscle.getId().toString());
        jsonPatch = createUpdateJsonRequest(addGastroJson,addMuscleJson);
        responseDto = controller.update2xx(jsonPatch, poldi.getId(), VetDto.class);

        // then
        Assertions.assertTrue(responseDto.getSpecialtyIds().contains(gastro.getId()));
        Assertions.assertTrue(responseDto.getSpecialtyIds().contains(savedMuscle.getId()));
        Assertions.assertEquals(2,responseDto.getSpecialtyIds().size());

        assertVetHasSpecialties(VET_POLDI,GASTRO, MUSCLE);
        assertVetHasSpecialties(VET_MAX,DENTISM, HEART);

        assertSpecialtyHasVets(DENTISM, VET_MAX);
        assertSpecialtyHasVets(GASTRO, VET_POLDI);
        assertSpecialtyHasVets(MUSCLE, VET_POLDI);
        assertSpecialtyHasVets(HEART, VET_MAX);
    }

}
