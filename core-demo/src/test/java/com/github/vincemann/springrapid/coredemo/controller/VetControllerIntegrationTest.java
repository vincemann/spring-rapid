package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import com.github.vincemann.springrapid.core.util.Lists;
import com.github.vincemann.springrapid.coredemo.controller.suite.MyControllerIntegrationTest;
import com.github.vincemann.springrapid.coredemo.controller.suite.template.VetControllerTestTemplate;
import com.github.vincemann.springrapid.coredemo.dto.VetDto;
import com.github.vincemann.springrapid.coredemo.model.Specialty;
import com.github.vincemann.springrapid.coredemo.model.Vet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

import static com.github.vincemann.ezcompare.Comparator.compare;
import static com.github.vincemann.springrapid.coretest.util.RapidTestUtil.createUpdateJsonLine;
import static com.github.vincemann.springrapid.coretest.util.RapidTestUtil.createUpdateJsonRequest;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class VetControllerIntegrationTest extends MyControllerIntegrationTest {


    @Autowired
    VetControllerTestTemplate controller;
    @Test
    public void canSaveVet_getLinkedToSpecialties() throws Exception {
        Specialty savedDentism = specialtyService.create(dentism);
        Specialty savedGastro = specialtyService.create(gastro);

        VetDto createPoldiDto = new VetDto(vetPoldi);
        createPoldiDto.setSpecialtyIds(new HashSet<>(Lists.newArrayList(savedDentism.getId(),savedGastro.getId())));



        MvcResult result = getMvc().perform(controller.create(createPoldiDto))
                .andExpect(status().isOk())
                .andReturn();
        String json = result.getResponse().getContentAsString();
        System.err.println(json);


        VetDto responseDto = deserialize(json, VetDto.class);
        compare(createPoldiDto).with(responseDto)
                .properties()
                .all()
                .ignore(VetType::getId)
                .assertEqual();
        Assertions.assertTrue(vetRepository.findByLastName(VET_POLDI).isPresent());
        Vet dbPoldi = vetRepository.findByLastName(VET_POLDI).get();

        compare(createPoldiDto).with(dbPoldi)
                .properties()
                .all()
                .ignore(VetType::getId)
                .ignore(createPoldiDto::getSpecialtyIds)
                .assertEqual();

        Assertions.assertEquals(responseDto.getId(),dbPoldi.getId());

        assertVetHasSpecialties(VET_POLDI,DENTISM,GASTRO);
        assertSpecialtyHasVets(DENTISM, VET_POLDI);
        assertSpecialtyHasVets(GASTRO, VET_POLDI);
    }

    // todo sometimes fails when run with all other tests
    @Test
//    @RepeatedIfExceptionsTest(repeats = 3, exceptions = IllegalArgumentException.class, name = "Rerun failed test. Attempt {currentRepetition} of {totalRepetitions}")
    public void canRemoveMultipleSpecialtiesFromVet_viaUpdate() throws Exception {
        // todo run class tests and stop here with debugger, check db state to find why fails run launched with other tests
        // poldi -> dentism, gastro, heart
        // max -> dentism, heart
        Specialty savedDentism = specialtyService.create(dentism);
        Specialty savedGastro = specialtyService.create(gastro);
        Specialty savedHeart = specialtyService.create(heart);
        VetDto createdPoldiDto = createVetLinkedToSpecialties(vetPoldi,savedDentism,savedGastro,savedHeart);
        VetDto createdMaxDto = createVetLinkedToSpecialties(vetMax,savedDentism,savedHeart);

        // remove poldis dentism and gastro
        String removeDentismJson = createUpdateJsonLine("remove", "/specialtyIds",savedDentism.getId().toString());
        String removeGastroJson = createUpdateJsonLine("remove", "/specialtyIds",savedGastro.getId().toString());
        String updateJson = createUpdateJsonRequest(removeDentismJson, removeGastroJson);
        VetDto responseDto = deserialize(getMvc().perform(controller.update(updateJson, createdPoldiDto.getId())).andReturn().getResponse().getContentAsString(),VetDto.class);
//        ExceptionAssert.assertTrue(responseDto.getSpecialtyIds().contains(savedHeart.getId()));
        Assertions.assertTrue(responseDto.getSpecialtyIds().contains(savedHeart.getId()));
        Assertions.assertEquals(1,responseDto.getSpecialtyIds().size());

        assertVetHasSpecialties(VET_POLDI,HEART);
        assertVetHasSpecialties(VET_MAX,DENTISM, HEART);

        assertSpecialtyHasVets(DENTISM, VET_MAX);
        assertSpecialtyHasVets(GASTRO);
        assertSpecialtyHasVets(HEART, VET_POLDI, VET_MAX);
    }

    @Test
    public void canAddSingleSpecialtyToVet_viaUpdate() throws Exception {
        // poldi -> dentism
        // max -> dentism, heart
        Specialty savedDentism = specialtyService.create(dentism);
        Specialty savedGastro = specialtyService.create(gastro);
        Specialty savedHeart = specialtyService.create(heart);
        VetDto createdPoldiDto = createVetLinkedToSpecialties(vetPoldi,savedDentism);
        VetDto createdMaxDto = createVetLinkedToSpecialties(vetMax,savedDentism,savedHeart);

        // add gastro to poldi
        String addDentismJson = createUpdateJsonLine("add", "/specialtyIds/-",savedGastro.getId().toString());
        String updateJson = createUpdateJsonRequest(addDentismJson);
        VetDto responseDto = deserialize(getMvc().perform(controller.update(updateJson, createdPoldiDto.getId())).andReturn().getResponse().getContentAsString(),VetDto.class);
        Assertions.assertTrue(responseDto.getSpecialtyIds().contains(savedGastro.getId()));
        Assertions.assertTrue(responseDto.getSpecialtyIds().contains(savedDentism.getId()));
        Assertions.assertEquals(2,responseDto.getSpecialtyIds().size());

        assertVetHasSpecialties(VET_POLDI,DENTISM,GASTRO);
        assertVetHasSpecialties(VET_MAX,DENTISM, HEART);

        assertSpecialtyHasVets(DENTISM, VET_MAX, VET_POLDI);
        assertSpecialtyHasVets(GASTRO, VET_POLDI);
        assertSpecialtyHasVets(HEART, VET_MAX);
    }

    @Test
    public void canAddMultipleSpecialtiesToVet_viaUpdate() throws Exception {
        // poldi -> dentism
        // max -> dentism, heart
        Specialty savedDentism = specialtyService.create(dentism);
        Specialty savedGastro = specialtyService.create(gastro);
        Specialty savedHeart = specialtyService.create(heart);
        VetDto createdPoldiDto = createVetLinkedToSpecialties(vetPoldi,savedDentism);
        VetDto createdMaxDto = createVetLinkedToSpecialties(vetMax,savedDentism,savedHeart);

        // add gastro and heart to poldi
        String addDentismJson = createUpdateJsonLine("add", "/specialtyIds/-",savedGastro.getId().toString());
        String addGastroJson = createUpdateJsonLine("add", "/specialtyIds/-",savedHeart.getId().toString());
        String updateJson = createUpdateJsonRequest(addDentismJson, addGastroJson);
        VetDto responseDto = deserialize(getMvc().perform(controller.update(updateJson, createdPoldiDto.getId())).andReturn().getResponse().getContentAsString(),VetDto.class);
        Assertions.assertTrue(responseDto.getSpecialtyIds().contains(savedHeart.getId()));
        Assertions.assertTrue(responseDto.getSpecialtyIds().contains(savedGastro.getId()));
        Assertions.assertTrue(responseDto.getSpecialtyIds().contains(savedDentism.getId()));
        Assertions.assertEquals(3,responseDto.getSpecialtyIds().size());

        assertVetHasSpecialties(VET_POLDI,HEART,DENTISM,GASTRO);
        assertVetHasSpecialties(VET_MAX,DENTISM, HEART);

        assertSpecialtyHasVets(DENTISM, VET_MAX, VET_POLDI);
        assertSpecialtyHasVets(GASTRO, VET_POLDI);
        assertSpecialtyHasVets(HEART, VET_POLDI, VET_MAX);
    }


    @Test
    public void canRemoveAllSpecialtiesFromVet_viaUpdate() throws Exception {
        // poldi -> dentism, heart
        // max -> dentism, heart
        Specialty savedDentism = specialtyService.create(dentism);
        Specialty savedGastro = specialtyService.create(gastro);
        Specialty savedHeart = specialtyService.create(heart);
        VetDto createdPoldiDto = createVetLinkedToSpecialties(vetPoldi,savedDentism,savedHeart);
        VetDto createdMaxDto = createVetLinkedToSpecialties(vetMax,savedDentism,savedHeart);

        // remove all from poldi
        String removeAllJson = createUpdateJsonLine("remove", "/specialtyIds");
        String updateJson = createUpdateJsonRequest(removeAllJson);
        VetDto responseDto = deserialize(getMvc().perform(controller.update(updateJson, createdPoldiDto.getId())).andReturn().getResponse().getContentAsString(),VetDto.class);
        Assertions.assertEquals(0,responseDto.getSpecialtyIds().size());

        assertVetHasSpecialties(VET_POLDI);
        assertVetHasSpecialties(VET_MAX,DENTISM, HEART);

        assertSpecialtyHasVets(DENTISM, VET_MAX);
        assertSpecialtyHasVets(GASTRO);
        assertSpecialtyHasVets(HEART, VET_MAX);
    }

    @Test
    public void canRemoveAllAndAddOneSpecialtyToVet_viaUpdate() throws Exception {
        // poldi -> dentism, heart
        // max -> dentism, heart
        Specialty savedDentism = specialtyService.create(dentism);
        Specialty savedGastro = specialtyService.create(gastro);
        Specialty savedHeart = specialtyService.create(heart);
        VetDto createdPoldiDto = createVetLinkedToSpecialties(vetPoldi,savedDentism,savedHeart);
        VetDto createdMaxDto = createVetLinkedToSpecialties(vetMax,savedDentism,savedHeart);

        // remove all from poldi then add gastro
        String removeAllJson = createUpdateJsonLine("remove", "/specialtyIds");
        String addGastroJson = createUpdateJsonLine("add", "/specialtyIds",savedGastro.getId().toString());
        String updateJson = createUpdateJsonRequest(removeAllJson,addGastroJson);
        VetDto responseDto = deserialize(getMvc().perform(controller.update(updateJson, createdPoldiDto.getId())).andReturn().getResponse().getContentAsString(),VetDto.class);
        Assertions.assertTrue(responseDto.getSpecialtyIds().contains(savedGastro.getId()));
        Assertions.assertEquals(1,responseDto.getSpecialtyIds().size());

        assertVetHasSpecialties(VET_POLDI,GASTRO);
        assertVetHasSpecialties(VET_MAX,DENTISM, HEART);

        assertSpecialtyHasVets(DENTISM, VET_MAX);
        assertSpecialtyHasVets(GASTRO, VET_POLDI);
        assertSpecialtyHasVets(HEART, VET_MAX);
    }

    @Test
    public void canRemoveSpecificSpeciality_andAddDiffSpecialtyToVet_viaUpdate() throws Exception {
        // poldi -> dentism, heart
        // max -> dentism, heart
        Specialty savedDentism = specialtyService.create(dentism);
        Specialty savedGastro = specialtyService.create(gastro);
        Specialty savedHeart = specialtyService.create(heart);
        VetDto createdPoldiDto = createVetLinkedToSpecialties(vetPoldi,savedDentism,savedHeart);
        VetDto createdMaxDto = createVetLinkedToSpecialties(vetMax,savedDentism,savedHeart);

        // remove dentism from poldi then add gastro
        String removeDentismJson = createUpdateJsonLine("remove", "/specialtyIds",savedDentism.getId().toString());
        String addGastroJson = createUpdateJsonLine("add", "/specialtyIds/-",savedGastro.getId().toString());
        String updateJson = createUpdateJsonRequest(removeDentismJson,addGastroJson);
        VetDto responseDto = deserialize(getMvc().perform(controller.update(updateJson, createdPoldiDto.getId())).andReturn().getResponse().getContentAsString(),VetDto.class);
        Assertions.assertTrue(responseDto.getSpecialtyIds().contains(savedGastro.getId()));
        Assertions.assertTrue(responseDto.getSpecialtyIds().contains(savedHeart.getId()));
        Assertions.assertEquals(2,responseDto.getSpecialtyIds().size());

        assertVetHasSpecialties(VET_POLDI,GASTRO,HEART);
        assertVetHasSpecialties(VET_MAX,DENTISM, HEART);

        assertSpecialtyHasVets(DENTISM, VET_MAX);
        assertSpecialtyHasVets(GASTRO, VET_POLDI);
        assertSpecialtyHasVets(HEART, VET_MAX, VET_POLDI);
    }



    @Test
    public void canRemoveAllAndAddMultipleSpecialtiesInNextRequestToVet_viaUpdate() throws Exception {
        // poldi -> dentism, heart
        // max -> dentism, heart
        Specialty savedDentism = specialtyService.create(dentism);
        Specialty savedGastro = specialtyService.create(gastro);
        Specialty savedHeart = specialtyService.create(heart);
        Specialty savedMuscle = specialtyService.create(muscle);
        VetDto createdPoldiDto = createVetLinkedToSpecialties(vetPoldi,savedDentism,savedHeart);
        VetDto createdMaxDto = createVetLinkedToSpecialties(vetMax,savedDentism,savedHeart);

        // remove all from poldi then add gastro and muscle
        String removeAllJson = createUpdateJsonLine("remove", "/specialtyIds");
        String updateJson = createUpdateJsonRequest(removeAllJson);
        VetDto responseDto = deserialize(getMvc().perform(controller.update(updateJson, createdPoldiDto.getId())).andReturn().getResponse().getContentAsString(),VetDto.class);
        Assertions.assertEquals(0,responseDto.getSpecialtyIds().size());

        String addGastroJson = createUpdateJsonLine("add", "/specialtyIds/-",savedGastro.getId().toString());
        String addMuscleJson = createUpdateJsonLine("add", "/specialtyIds/-",savedMuscle.getId().toString());
        updateJson = createUpdateJsonRequest(addGastroJson,addMuscleJson);
        responseDto = deserialize(getMvc().perform(controller.update(updateJson, createdPoldiDto.getId())).andReturn().getResponse().getContentAsString(),VetDto.class);
        Assertions.assertTrue(responseDto.getSpecialtyIds().contains(savedGastro.getId()));
        Assertions.assertTrue(responseDto.getSpecialtyIds().contains(savedMuscle.getId()));
        Assertions.assertEquals(2,responseDto.getSpecialtyIds().size());

        assertVetHasSpecialties(VET_POLDI,GASTRO, MUSCLE);
        assertVetHasSpecialties(VET_MAX,DENTISM, HEART);

        assertSpecialtyHasVets(DENTISM, VET_MAX);
        assertSpecialtyHasVets(GASTRO, VET_POLDI);
        assertSpecialtyHasVets(MUSCLE, VET_POLDI);
        assertSpecialtyHasVets(HEART, VET_MAX);
    }

    private VetDto createVetLinkedToSpecialties(Vet vet, Specialty... specialtys) throws Exception {
        VetDto createVetDto = new VetDto(vet);
        createVetDto.setSpecialtyIds(new HashSet<>(
                Arrays.stream(specialtys)
                        .map(IdentifiableEntityImpl::getId)
                        .collect(Collectors.toList())));
        String json = getMvc().perform(controller.create(createVetDto))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return deserialize(json,VetDto.class);
    }

}
