package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.core.util.Lists;
import com.github.vincemann.springrapid.coredemo.dtos.VetDto;
import com.github.vincemann.springrapid.coredemo.dtos.owner.CreateOwnerDto;
import com.github.vincemann.springrapid.coredemo.dtos.owner.ReadOwnOwnerDto;
import com.github.vincemann.springrapid.coredemo.model.Owner;
import com.github.vincemann.springrapid.coredemo.model.Specialty;
import com.github.vincemann.springrapid.coredemo.model.Vet;
import com.github.vincemann.springrapid.coredemo.service.VetService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashSet;

import static com.github.vincemann.ezcompare.Comparator.compare;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class VetControllerIntegrationTest extends ManyToManyControllerIntegrationTest<VetController,VetService> {

    // SAVE TESTS

    @Test
    public void canSaveVet_getLinkedToSpecialties() throws Exception {
        Specialty savedDentism = specialtyService.save(dentism);
        Specialty savedGastro = specialtyService.save(gastro);

        VetDto createKahnDto = new VetDto(kahn);
        createKahnDto.setSpecialtyIds(new HashSet<>(Lists.newArrayList(savedDentism.getId(),savedGastro.getId())));



        MvcResult result = getMockMvc().perform(create(createKahnDto))
                .andExpect(status().isOk())
                .andReturn();
        String json = result.getResponse().getContentAsString();
        System.err.println(json);


        VetDto responseDto = deserialize(json, VetDto.class);
        compare(createKahnDto).with(responseDto)
                .properties()
                .all()
                .ignore(VetType::getId)
                .assertEqual();
        Assertions.assertTrue(vetRepository.findByLastName(KAHN).isPresent());
        Vet dbKahn = vetRepository.findByLastName(KAHN).get();

        compare(createKahnDto).with(dbKahn)
                .properties()
                .all()
                .ignore(VetType::getId)
                .ignore(createKahnDto::getSpecialtyIds)
                .assertEqual();

        Assertions.assertEquals(responseDto.getId(),dbKahn.getId());

        assertVetHasSpecialties(KAHN,DENTISM,GASTRO);
        assertSpecialtyHasVets(DENTISM,KAHN);
        assertSpecialtyHasVets(GASTRO,KAHN);
    }

}
