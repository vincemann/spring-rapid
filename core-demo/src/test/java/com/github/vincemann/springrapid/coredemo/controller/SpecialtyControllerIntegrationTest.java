package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.core.util.Lists;
import com.github.vincemann.springrapid.coredemo.controller.suite.MyIntegrationTest;
import com.github.vincemann.springrapid.coredemo.controller.suite.template.SpecialtyControllerTestTemplate;
import com.github.vincemann.springrapid.coredemo.dto.SpecialtyDto;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag(value = "demo-projects")
public class SpecialtyControllerIntegrationTest extends MyIntegrationTest {


    @Autowired
    SpecialtyControllerTestTemplate controller;

    @Test
    public void canCreateSpecialtyLinkedToSavedVets() throws Exception {
        // given
        Vet max = vetService.create(testData.getVetMax());
        Vet poldi = vetService.create(testData.getVetPoldi());
        Vet dicaprio = vetService.create(testData.getVetDiCaprio());

        // when
        SpecialtyDto createGastroDto = new SpecialtyDto(testData.getGastro());
        createGastroDto.setVetIds(new HashSet<>(Lists.newArrayList(max.getId(),poldi.getId())));
        SpecialtyDto dto = controller.create2xx(createGastroDto, SpecialtyDto.class);
        // then
        Assertions.assertTrue(specialtyService.findByDescription(GASTRO).isPresent());
        Specialty gastro = specialtyService.findByDescription(GASTRO).get();
        Assertions.assertEquals(dto.getId(),gastro.getId());
        assertSpecialtyHasVets(GASTRO, VET_POLDI, VET_MAX);
        assertVetHasSpecialties(VET_POLDI,GASTRO);
        assertVetHasSpecialties(VET_MAX,GASTRO);
        assertVetHasSpecialties(VET_DICAPRIO);
    }

    @Test
    public void canUnlinkSpecialtyFromMultipleVetsViaUpdateSpecialty() throws Exception {

        // gastro -> poldi, max, diCaprio
        // heart -> poldi, max

        // given
        Vet poldi = vetService.create(testData.getVetPoldi());
        Vet meier = vetService.create(testData.getVetMax());
        Vet diCaprio = vetService.create(testData.getVetDiCaprio());
        SpecialtyDto savedGastro = helper.createSpecialtyLinkedToVets(testData.getGastro(), poldi, meier, diCaprio);
        helper.createSpecialtyLinkedToVets(testData.getHeart(), poldi,meier);


        // remove poldi and diCaprio from gastro
        // when
        String removePoldi = createUpdateJsonLine("remove", "/vetIds",poldi.getId().toString());
        String removeDicaprio = createUpdateJsonLine("remove", "/vetIds",diCaprio.getId().toString());
        String jsonPatch = createUpdateJsonRequest(removePoldi,removeDicaprio);
        SpecialtyDto dto = controller.update2xx(jsonPatch, savedGastro.getId(), SpecialtyDto.class);
        // then
        Assertions.assertTrue(dto.getVetIds().contains(meier.getId()));
        Assertions.assertEquals(1,dto.getVetIds().size());

        assertSpecialtyHasVets(GASTRO, VET_MAX);
        assertSpecialtyHasVets(HEART, VET_POLDI, VET_MAX);

        assertVetHasSpecialties(VET_POLDI, HEART);
        assertVetHasSpecialties(VET_MAX,GASTRO, HEART);
        assertVetHasSpecialties(VET_DICAPRIO);


    }

    @Test
    public void givenSpecialtyIsAlreadyLinkedToOneVet_canLinkMoreVetsToSpecialtyViaUpdateSpecialty() throws Exception {

        // gastro -> poldi, max, diCaprio
        // heart -> poldi, max

        // given
        Vet poldi = vetService.create(testData.getVetPoldi());
        Vet savedMaier = vetService.create(testData.getVetMax());
        Vet DiCaprio = vetService.create(testData.getVetDiCaprio());
        SpecialtyDto savedGastro = helper.createSpecialtyLinkedToVets(testData.getGastro(), poldi, savedMaier, DiCaprio);
        helper.createSpecialtyLinkedToVets(testData.getHeart(), poldi,savedMaier);
        SpecialtyDto savedDentism = helper.createSpecialtyLinkedToVets(testData.getDentism());
        assertSpecialtyHasVets(DENTISM);

        // add dentism to diCaprio and poldi
        // when
        String addVetPoldiJson = createUpdateJsonLine("add", "/vetIds/-",poldi.getId().toString());
        String addVetDiCaprioJson = createUpdateJsonLine("add", "/vetIds/-",DiCaprio.getId().toString());
        String updateJson = createUpdateJsonRequest(addVetPoldiJson,addVetDiCaprioJson);
        SpecialtyDto dto = controller.update2xx(updateJson, savedDentism.getId(), SpecialtyDto.class);
        // then
        Assertions.assertTrue(dto.getVetIds().contains(poldi.getId()));
        Assertions.assertTrue(dto.getVetIds().contains(DiCaprio.getId()));
        Assertions.assertEquals(2,dto.getVetIds().size());

        assertSpecialtyHasVets(DENTISM, VET_POLDI, VET_DICAPRIO);
        assertSpecialtyHasVets(GASTRO, VET_POLDI, VET_MAX, VET_DICAPRIO);
        assertSpecialtyHasVets(HEART, VET_POLDI, VET_MAX);

        assertVetHasSpecialties(VET_POLDI, HEART,GASTRO,DENTISM);
        assertVetHasSpecialties(VET_MAX,GASTRO, HEART);
        assertVetHasSpecialties(VET_DICAPRIO,GASTRO,DENTISM);


    }

    @Test
    public void relinkMultipleSpecialtiesOfVetsViaSingleUpdateSpecialtyRequest() throws Exception {

        // gastro -> poldi, max, diCaprio
        // heart -> poldi, max
        // dentism -> diCaprio

        // given
        Vet poldi = vetService.create(testData.getVetPoldi());
        Vet savedMaier = vetService.create(testData.getVetMax());
        Vet DiCaprio = vetService.create(testData.getVetDiCaprio());

        SpecialtyDto gastro = helper.createSpecialtyLinkedToVets(testData.getGastro(), poldi, savedMaier, DiCaprio);
        SpecialtyDto max = helper.createSpecialtyLinkedToVets(testData.getHeart(), poldi, savedMaier);
        SpecialtyDto dentism = helper.createSpecialtyLinkedToVets(testData.getDentism(),DiCaprio);

        // when
        // remove diCaprio from dentism and add poldi and max
        String removeDicaprio = createUpdateJsonLine("remove", "/vetIds",DiCaprio.getId().toString());
        String addPoldi = createUpdateJsonLine("add", "/vetIds/-",poldi.getId().toString());
        String addMax = createUpdateJsonLine("add", "/vetIds/-",savedMaier.getId().toString());
        String jsonPatch = createUpdateJsonRequest(removeDicaprio,addPoldi,addMax);

        SpecialtyDto dto = controller.update2xx(jsonPatch, dentism.getId(), SpecialtyDto.class);
        // then
        Assertions.assertTrue(dto.getVetIds().contains(poldi.getId()));
        Assertions.assertTrue(dto.getVetIds().contains(savedMaier.getId()));
        Assertions.assertEquals(2,dto.getVetIds().size());

        assertSpecialtyHasVets(DENTISM, VET_POLDI, VET_MAX);
        assertSpecialtyHasVets(GASTRO, VET_POLDI, VET_MAX, VET_DICAPRIO);
        assertSpecialtyHasVets(HEART, VET_POLDI, VET_MAX);

        assertVetHasSpecialties(VET_POLDI, HEART,GASTRO,DENTISM);
        assertVetHasSpecialties(VET_MAX,GASTRO, HEART, DENTISM);
        assertVetHasSpecialties(VET_DICAPRIO,GASTRO);


    }

    @Test
    public void givenVetsLinkedToSpecialty_whenRemoveSpecialty_thenVetsUnlinked() throws Exception {
        // gastro -> poldi, max, diCaprio
        // heart -> poldi, max

        // given
        Vet poldi = vetService.create(testData.getVetPoldi());
        Vet savedMaier = vetService.create(testData.getVetMax());
        Vet DiCaprio = vetService.create(testData.getVetDiCaprio());
        SpecialtyDto savedGastro = helper.createSpecialtyLinkedToVets(testData.getGastro(), poldi, savedMaier, DiCaprio);
        helper.createSpecialtyLinkedToVets(testData.getHeart(), poldi,savedMaier);

        // when
        getMvc().perform(controller.delete(savedGastro.getId()))
                .andExpect(status().is2xxSuccessful());

        // then
        Assertions.assertFalse(specialtyService.findByDescription(GASTRO).isPresent());

        assertSpecialtyHasVets(HEART, VET_POLDI, VET_MAX);

        assertVetHasSpecialties(VET_POLDI, HEART);
        assertVetHasSpecialties(VET_MAX, HEART);
    }

}
