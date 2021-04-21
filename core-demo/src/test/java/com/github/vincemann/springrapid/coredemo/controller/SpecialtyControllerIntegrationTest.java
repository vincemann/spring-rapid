package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import com.github.vincemann.springrapid.core.util.Lists;
import com.github.vincemann.springrapid.coredemo.dtos.SpecialtyDto;
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
import static com.github.vincemann.springrapid.coretest.util.RapidTestUtil.createUpdateJsonLine;
import static com.github.vincemann.springrapid.coretest.util.RapidTestUtil.createUpdateJsonRequest;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SpecialtyControllerIntegrationTest extends ManyToManyControllerIntegrationTest<SpecialtyController, SpecialtyService> {

    @Test
    public void canSaveSpecialty_getLinkedToVets() throws Exception {
        Vet savedMeier = vetRepository.save(meier);
        Vet savedKahn = vetRepository.save(kahn);
        Vet savedSchuhmacher = vetRepository.save(schuhmacher);

        SpecialtyDto createGastroDto = new SpecialtyDto(gastro);
        createGastroDto.setVetIds(new HashSet<>(Lists.newArrayList(savedMeier.getId(),savedKahn.getId())));

        MvcResult result = getMockMvc().perform(create(createGastroDto))
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

        assertSpecialtyHasVets(GASTRO,KAHN,MEIER);

        assertVetHasSpecialties(KAHN,GASTRO);
        assertVetHasSpecialties(MEIER,GASTRO);
        assertVetHasSpecialties(SCHUHMACHER);
    }

    @Test
    public void canUnlinkSpecialtyFromSomeVets_viaUpdate() throws Exception {

        // gastro -> kahn, meier, schuhmacher
        // heart -> kahn, meier
        Vet savedKahn = vetRepository.save(kahn);
        Vet savedMaier = vetRepository.save(meier);
        Vet savedSchuhmacher = vetRepository.save(schuhmacher);

        SpecialtyDto savedGastro = createSpecialtyLinkedToVets(gastro, savedKahn, savedMaier, savedSchuhmacher);
        createSpecialtyLinkedToVets(heart, savedKahn,savedMaier);


        // remove kahn and schuhmacher from gastro
        String removeKahnJson = createUpdateJsonLine("remove", "/vetIds",savedKahn.getId().toString());
        String removeSchuhmacherJson = createUpdateJsonLine("remove", "/vetIds",savedSchuhmacher.getId().toString());
        String updateJson = createUpdateJsonRequest(removeKahnJson,removeSchuhmacherJson);

        SpecialtyDto responseDto = deserialize(getMockMvc().perform(update(updateJson, savedGastro.getId()))
                .andReturn().getResponse().getContentAsString(), SpecialtyDto.class);
        Assertions.assertTrue(responseDto.getVetIds().contains(savedMaier.getId()));
        Assertions.assertEquals(1,responseDto.getVetIds().size());

        assertSpecialtyHasVets(GASTRO,MEIER);
        assertSpecialtyHasVets(HEART,KAHN, MEIER);

        assertVetHasSpecialties(KAHN, HEART);
        assertVetHasSpecialties(MEIER,GASTRO, HEART);
        assertVetHasSpecialties(SCHUHMACHER);


    }

    @Test
    public void canLinkSpecialtyToSomeVets_viaUpdate() throws Exception {

        // gastro -> kahn, meier, schuhmacher
        // heart -> kahn, meier
        Vet savedKahn = vetRepository.save(kahn);
        Vet savedMaier = vetRepository.save(meier);
        Vet savedSchuhmacher = vetRepository.save(schuhmacher);

        SpecialtyDto savedGastro = createSpecialtyLinkedToVets(gastro, savedKahn, savedMaier, savedSchuhmacher);
        createSpecialtyLinkedToVets(heart, savedKahn,savedMaier);
        SpecialtyDto savedDentism = createSpecialtyLinkedToVets(dentism);

        assertSpecialtyHasVets(DENTISM);


        // add dentism to schuhmacher and kahn
        String addKahnJson = createUpdateJsonLine("add", "/vetIds/-",savedKahn.getId().toString());
        String addSchuhmacherJson = createUpdateJsonLine("add", "/vetIds/-",savedSchuhmacher.getId().toString());
        String updateJson = createUpdateJsonRequest(addKahnJson,addSchuhmacherJson);

        SpecialtyDto responseDto = deserialize(getMockMvc().perform(update(updateJson, savedDentism.getId()))
                .andReturn().getResponse().getContentAsString(), SpecialtyDto.class);
        Assertions.assertTrue(responseDto.getVetIds().contains(savedKahn.getId()));
        Assertions.assertTrue(responseDto.getVetIds().contains(savedSchuhmacher.getId()));
        Assertions.assertEquals(2,responseDto.getVetIds().size());

        assertSpecialtyHasVets(DENTISM,KAHN,SCHUHMACHER);
        assertSpecialtyHasVets(GASTRO,KAHN,MEIER,SCHUHMACHER);
        assertSpecialtyHasVets(HEART,KAHN, MEIER);

        assertVetHasSpecialties(KAHN, HEART,GASTRO,DENTISM);
        assertVetHasSpecialties(MEIER,GASTRO, HEART);
        assertVetHasSpecialties(SCHUHMACHER,GASTRO,DENTISM);


    }

    @Test
    public void canReLinkSpecialtyFromSomeVetsToSomeVets_viaUpdate() throws Exception {

        // gastro -> kahn, meier, schuhmacher
        // heart -> kahn, meier
        // dentism -> schuhmacher
        Vet savedKahn = vetRepository.save(kahn);
        Vet savedMaier = vetRepository.save(meier);
        Vet savedSchuhmacher = vetRepository.save(schuhmacher);

        SpecialtyDto savedGastro = createSpecialtyLinkedToVets(gastro, savedKahn, savedMaier, savedSchuhmacher);
        SpecialtyDto savedMeier = createSpecialtyLinkedToVets(heart, savedKahn, savedMaier);
        SpecialtyDto savedDentism = createSpecialtyLinkedToVets(dentism,savedSchuhmacher);


        // remove schuhmacher from dentism and add kahn and meier
        String removeSchuhmacherJson = createUpdateJsonLine("remove", "/vetIds",savedSchuhmacher.getId().toString());
        String addKahnJson = createUpdateJsonLine("add", "/vetIds/-",savedKahn.getId().toString());
        String addMeierJson = createUpdateJsonLine("add", "/vetIds/-",savedMaier.getId().toString());
        String updateJson = createUpdateJsonRequest(removeSchuhmacherJson,addKahnJson,addMeierJson);

        SpecialtyDto responseDto = deserialize(getMockMvc().perform(update(updateJson, savedDentism.getId()))
                .andReturn().getResponse().getContentAsString(), SpecialtyDto.class);
        Assertions.assertTrue(responseDto.getVetIds().contains(savedKahn.getId()));
        Assertions.assertTrue(responseDto.getVetIds().contains(savedMaier.getId()));
        Assertions.assertEquals(2,responseDto.getVetIds().size());

        assertSpecialtyHasVets(DENTISM,KAHN,MEIER);
        assertSpecialtyHasVets(GASTRO,KAHN,MEIER,SCHUHMACHER);
        assertSpecialtyHasVets(HEART,KAHN, MEIER);

        assertVetHasSpecialties(KAHN, HEART,GASTRO,DENTISM);
        assertVetHasSpecialties(MEIER,GASTRO, HEART, DENTISM);
        assertVetHasSpecialties(SCHUHMACHER,GASTRO);


    }

    @Test
    public void canDeleteSpecialty_getUnlinkedFromVets() throws Exception {
        // gastro -> kahn, meier, schuhmacher
        // heart -> kahn, meier
        Vet savedKahn = vetRepository.save(kahn);
        Vet savedMaier = vetRepository.save(meier);
        Vet savedSchuhmacher = vetRepository.save(schuhmacher);

        SpecialtyDto savedGastro = createSpecialtyLinkedToVets(gastro, savedKahn, savedMaier, savedSchuhmacher);
        createSpecialtyLinkedToVets(heart, savedKahn,savedMaier);

        getMockMvc().perform(delete(savedGastro.getId()))
                .andExpect(status().is2xxSuccessful());

        Assertions.assertFalse(specialtyRepository.findByDescription(GASTRO).isPresent());

        assertSpecialtyHasVets(HEART,KAHN, MEIER);

        assertVetHasSpecialties(KAHN, HEART);
        assertVetHasSpecialties(MEIER, HEART);
    }


    private SpecialtyDto createSpecialtyLinkedToVets(Specialty specialty, Vet... vets) throws Exception {
        SpecialtyDto createSpecialtyDto = new SpecialtyDto(specialty);
        createSpecialtyDto.setVetIds(new HashSet<>(
                Arrays.stream(vets)
                        .map(IdentifiableEntityImpl::getId)
                        .collect(Collectors.toList())));
        String json = getMockMvc().perform(create(createSpecialtyDto))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return deserialize(json,SpecialtyDto.class);
    }

}
