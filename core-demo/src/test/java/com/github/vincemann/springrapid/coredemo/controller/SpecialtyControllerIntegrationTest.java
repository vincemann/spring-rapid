package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import com.github.vincemann.springrapid.core.util.Lists;
import com.github.vincemann.springrapid.coredemo.dto.SpecialtyDto;
import com.github.vincemann.springrapid.coredemo.model.Specialty;
import com.github.vincemann.springrapid.coredemo.model.Vet;
import com.github.vincemann.springrapid.coredemo.service.SpecialtyService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

import static com.github.vincemann.ezcompare.Comparator.compare;
import static com.github.vincemann.springrapid.coretest.util.TransactionalRapidTestUtil.createUpdateJsonLine;
import static com.github.vincemann.springrapid.coretest.util.TransactionalRapidTestUtil.createUpdateJsonRequest;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SpecialtyControllerIntegrationTest extends AbstractControllerIntegrationTest<SpecialtyController, SpecialtyService> {

    @Test
    public void canSaveSpecialty_getLinkedToVets() throws Exception {
        Vet savedVetMax = vetRepository.save(vetMax);
        Vet savedVetPoldi = vetRepository.save(vetPoldi);
        Vet savedVetDiCaprio = vetRepository.save(vetDiCaprio);

        SpecialtyDto createGastroDto = new SpecialtyDto(gastro);
        createGastroDto.setVetIds(new HashSet<>(Lists.newArrayList(savedVetMax.getId(),savedVetPoldi.getId())));

        MvcResult result = getMvc().perform(create(createGastroDto))
                .andExpect(status().isOk())
                .andReturn();
        String json = result.getResponse().getContentAsString();
        System.err.println(json);


        SpecialtyDto responseDto = deserialize(json, SpecialtyDto.class);
        compare(createGastroDto).with(responseDto)
                .properties()
                .all()
                .ignore(SpecialtyType::getId)
                .assertEqual();
        Assertions.assertTrue(specialtyRepository.findByDescription(GASTRO).isPresent());
        Specialty dbGastro = specialtyRepository.findByDescription(GASTRO).get();

        compare(createGastroDto).with(dbGastro)
                .properties()
                .all()
                .ignore(SpecialtyType::getId)
                .ignore(createGastroDto::getVetIds)
                .assertEqual();

        Assertions.assertEquals(responseDto.getId(),dbGastro.getId());

        assertSpecialtyHasVets(GASTRO, VET_POLDI, VET_MAX);

        assertVetHasSpecialties(VET_POLDI,GASTRO);
        assertVetHasSpecialties(VET_MAX,GASTRO);
        assertVetHasSpecialties(VET_DICAPRIO);
    }

    @Test
    public void canUnlinkSpecialtyFromSomeVets_viaUpdate() throws Exception {

        // gastro -> poldi, max, diCaprio
        // heart -> poldi, max
        Vet savedVetPoldi = vetRepository.save(vetPoldi);
        Vet savedMaier = vetRepository.save(vetMax);
        Vet savedVetDiCaprio = vetRepository.save(vetDiCaprio);

        SpecialtyDto savedGastro = createSpecialtyLinkedToVets(gastro, savedVetPoldi, savedMaier, savedVetDiCaprio);
        createSpecialtyLinkedToVets(heart, savedVetPoldi,savedMaier);


        // remove poldi and diCaprio from gastro
        String removeVetPoldiJson = createUpdateJsonLine("remove", "/vetIds",savedVetPoldi.getId().toString());
        String removeVetDiCaprioJson = createUpdateJsonLine("remove", "/vetIds",savedVetDiCaprio.getId().toString());
        String updateJson = createUpdateJsonRequest(removeVetPoldiJson,removeVetDiCaprioJson);

        SpecialtyDto responseDto = deserialize(getMvc().perform(update(updateJson, savedGastro.getId()))
                .andReturn().getResponse().getContentAsString(), SpecialtyDto.class);
        Assertions.assertTrue(responseDto.getVetIds().contains(savedMaier.getId()));
        Assertions.assertEquals(1,responseDto.getVetIds().size());

        assertSpecialtyHasVets(GASTRO, VET_MAX);
        assertSpecialtyHasVets(HEART, VET_POLDI, VET_MAX);

        assertVetHasSpecialties(VET_POLDI, HEART);
        assertVetHasSpecialties(VET_MAX,GASTRO, HEART);
        assertVetHasSpecialties(VET_DICAPRIO);


    }

    @Test
    public void canLinkSpecialtyToSomeVets_viaUpdate() throws Exception {

        // gastro -> poldi, max, diCaprio
        // heart -> poldi, max
        Vet savedVetPoldi = vetRepository.save(vetPoldi);
        Vet savedMaier = vetRepository.save(vetMax);
        Vet savedVetDiCaprio = vetRepository.save(vetDiCaprio);

        SpecialtyDto savedGastro = createSpecialtyLinkedToVets(gastro, savedVetPoldi, savedMaier, savedVetDiCaprio);
        createSpecialtyLinkedToVets(heart, savedVetPoldi,savedMaier);
        SpecialtyDto savedDentism = createSpecialtyLinkedToVets(dentism);

        assertSpecialtyHasVets(DENTISM);


        // add dentism to diCaprio and poldi
        String addVetPoldiJson = createUpdateJsonLine("add", "/vetIds/-",savedVetPoldi.getId().toString());
        String addVetDiCaprioJson = createUpdateJsonLine("add", "/vetIds/-",savedVetDiCaprio.getId().toString());
        String updateJson = createUpdateJsonRequest(addVetPoldiJson,addVetDiCaprioJson);

        SpecialtyDto responseDto = deserialize(getMvc().perform(update(updateJson, savedDentism.getId()))
                .andReturn().getResponse().getContentAsString(), SpecialtyDto.class);
        Assertions.assertTrue(responseDto.getVetIds().contains(savedVetPoldi.getId()));
        Assertions.assertTrue(responseDto.getVetIds().contains(savedVetDiCaprio.getId()));
        Assertions.assertEquals(2,responseDto.getVetIds().size());

        assertSpecialtyHasVets(DENTISM, VET_POLDI, VET_DICAPRIO);
        assertSpecialtyHasVets(GASTRO, VET_POLDI, VET_MAX, VET_DICAPRIO);
        assertSpecialtyHasVets(HEART, VET_POLDI, VET_MAX);

        assertVetHasSpecialties(VET_POLDI, HEART,GASTRO,DENTISM);
        assertVetHasSpecialties(VET_MAX,GASTRO, HEART);
        assertVetHasSpecialties(VET_DICAPRIO,GASTRO,DENTISM);


    }

    @Test
    public void canReLinkSpecialtyFromSomeVetsToSomeVets_viaUpdate() throws Exception {

        // gastro -> poldi, max, diCaprio
        // heart -> poldi, max
        // dentism -> diCaprio
        Vet savedVetPoldi = vetRepository.save(vetPoldi);
        Vet savedMaier = vetRepository.save(vetMax);
        Vet savedVetDiCaprio = vetRepository.save(vetDiCaprio);

        SpecialtyDto savedGastro = createSpecialtyLinkedToVets(gastro, savedVetPoldi, savedMaier, savedVetDiCaprio);
        SpecialtyDto savedVetMax = createSpecialtyLinkedToVets(heart, savedVetPoldi, savedMaier);
        SpecialtyDto savedDentism = createSpecialtyLinkedToVets(dentism,savedVetDiCaprio);


        // remove diCaprio from dentism and add poldi and max
        String removeVetDiCaprioJson = createUpdateJsonLine("remove", "/vetIds",savedVetDiCaprio.getId().toString());
        String addVetPoldiJson = createUpdateJsonLine("add", "/vetIds/-",savedVetPoldi.getId().toString());
        String addVetMaxJson = createUpdateJsonLine("add", "/vetIds/-",savedMaier.getId().toString());
        String updateJson = createUpdateJsonRequest(removeVetDiCaprioJson,addVetPoldiJson,addVetMaxJson);

        SpecialtyDto responseDto = deserialize(getMvc().perform(update(updateJson, savedDentism.getId()))
                .andReturn().getResponse().getContentAsString(), SpecialtyDto.class);
        Assertions.assertTrue(responseDto.getVetIds().contains(savedVetPoldi.getId()));
        Assertions.assertTrue(responseDto.getVetIds().contains(savedMaier.getId()));
        Assertions.assertEquals(2,responseDto.getVetIds().size());

        assertSpecialtyHasVets(DENTISM, VET_POLDI, VET_MAX);
        assertSpecialtyHasVets(GASTRO, VET_POLDI, VET_MAX, VET_DICAPRIO);
        assertSpecialtyHasVets(HEART, VET_POLDI, VET_MAX);

        assertVetHasSpecialties(VET_POLDI, HEART,GASTRO,DENTISM);
        assertVetHasSpecialties(VET_MAX,GASTRO, HEART, DENTISM);
        assertVetHasSpecialties(VET_DICAPRIO,GASTRO);


    }

    @Test
    public void canDeleteSpecialty_getUnlinkedFromVets() throws Exception {
        // gastro -> poldi, max, diCaprio
        // heart -> poldi, max
        Vet savedVetPoldi = vetRepository.save(vetPoldi);
        Vet savedMaier = vetRepository.save(vetMax);
        Vet savedVetDiCaprio = vetRepository.save(vetDiCaprio);

        SpecialtyDto savedGastro = createSpecialtyLinkedToVets(gastro, savedVetPoldi, savedMaier, savedVetDiCaprio);
        createSpecialtyLinkedToVets(heart, savedVetPoldi,savedMaier);

        getMvc().perform(delete(savedGastro.getId()))
                .andExpect(status().is2xxSuccessful());

        Assertions.assertFalse(specialtyRepository.findByDescription(GASTRO).isPresent());

        assertSpecialtyHasVets(HEART, VET_POLDI, VET_MAX);

        assertVetHasSpecialties(VET_POLDI, HEART);
        assertVetHasSpecialties(VET_MAX, HEART);
    }


    private SpecialtyDto createSpecialtyLinkedToVets(Specialty specialty, Vet... vets) throws Exception {
        SpecialtyDto createSpecialtyDto = new SpecialtyDto(specialty);
        createSpecialtyDto.setVetIds(new HashSet<>(
                Arrays.stream(vets)
                        .map(IdentifiableEntityImpl::getId)
                        .collect(Collectors.toList())));
        String json = getMvc().perform(create(createSpecialtyDto))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return deserialize(json,SpecialtyDto.class);
    }

}
