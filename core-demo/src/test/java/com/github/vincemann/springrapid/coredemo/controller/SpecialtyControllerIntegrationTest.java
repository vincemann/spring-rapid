package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import com.github.vincemann.springrapid.core.util.Lists;
import com.github.vincemann.springrapid.coredemo.dtos.SpecialtyDto;
import com.github.vincemann.springrapid.coredemo.dtos.VetDto;
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

        assertVetHasSpecialties(KAHN,GASTRO);
        assertVetHasSpecialties(MEIER,GASTRO);
        assertVetHasSpecialties(SCHUHMACHER);
        assertSpecialtyHasVets(GASTRO,KAHN,MEIER);
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
