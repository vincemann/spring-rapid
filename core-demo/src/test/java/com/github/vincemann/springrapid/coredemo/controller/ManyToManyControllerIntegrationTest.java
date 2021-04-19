package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.core.controller.GenericCrudController;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.coredemo.model.Specialty;
import com.github.vincemann.springrapid.coredemo.model.Vet;
import com.github.vincemann.springrapid.coredemo.repo.SpecialtyRepository;
import com.github.vincemann.springrapid.coredemo.repo.VetRepository;
import com.github.vincemann.springrapid.coredemo.service.SpecialtyService;
import com.github.vincemann.springrapid.coredemo.service.VetService;
import com.github.vincemann.springrapid.coretest.controller.urlparamid.IntegrationUrlParamIdControllerTest;
import com.github.vincemann.springrapid.coretest.util.RapidTestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ManyToManyControllerIntegrationTest <C extends GenericCrudController<?,Long,S,?,?>,S extends CrudService<?,Long>>
        extends IntegrationUrlParamIdControllerTest<C,Long,S>
{
    protected static final String MEIER = "Meier";
    protected static final String KAHN = "Kahn";
    protected static final String SCHUHMACHER = "Schuhmacher";


    protected static final String DENTISM = "Dentism";
    protected static final String GASTRO = "Gastro";
    protected static final String HEART = "Heart";
    //Types
    Vet VetType = new Vet();
    Specialty SpecialtyType = new Specialty();


    Vet meier;
    Vet kahn;
    Vet schuhmacher;

    Specialty dentism;
    Specialty gastro;
    Specialty heart;

    @Autowired
    SpecialtyService specialtyService;
    @Autowired
    SpecialtyRepository specialtyRepository;


    @Autowired
    VetRepository vetRepository;
    @Autowired
    VetService vetService;

    @BeforeEach
    public void setupTestData() throws Exception {

        dentism = Specialty.builder()
                .description(DENTISM)
                .build();

        heart = Specialty.builder()
                .description(HEART)
                .build();

        gastro = Specialty.builder()
                .description(GASTRO)
                .build();

        meier = Vet.builder()
                .firstName("Max")
                .lastName(MEIER)
                .specialties(new HashSet<>())
                .build();

        schuhmacher = Vet.builder()
                .firstName("michael")
                .lastName(SCHUHMACHER)
                .specialties(new HashSet<>())
                .build();

        kahn = Vet.builder()
                .firstName("Olli")
                .lastName(KAHN)
                .specialties(new HashSet<>())
                .build();
    }

    protected void assertVetHasSpecialties(String vetName, String... descriptions) {
        Optional<Vet> vetOptional = vetRepository.findByLastName(vetName);
        Assertions.assertTrue(vetOptional.isPresent());
        Vet vet = vetOptional.get();

        Set<Specialty> specialties = new HashSet<>();
        for (String description : descriptions) {
            Optional<Specialty> optionalSpecialty = specialtyRepository.findByDescription(description);
            Assertions.assertTrue(optionalSpecialty.isPresent());
            specialties.add(optionalSpecialty.get());
        }
        Assertions.assertEquals(specialties, vet.getSpecialties());
    }

    protected void assertSpecialtyHasVets(String description, String... vetNames) {
        Optional<Specialty> optionalSpecialty = specialtyRepository.findByDescription(description);
        Assertions.assertTrue(optionalSpecialty.isPresent());
        Specialty specialty = optionalSpecialty.get();

        Set<Vet> vets = new HashSet<>();
        for (String vetName : vetNames) {
            Optional<Vet> optionalVet = vetRepository.findByLastName(vetName);
            Assertions.assertTrue(optionalVet.isPresent());
            vets.add(optionalVet.get());
        }
        Assertions.assertEquals(vets, specialty.getVets());
    }

    @AfterEach
    void tearDown() {
//        specialtyRepository.deleteAll();
//        vetRepository.deleteAll();
        RapidTestUtil.clear(specialtyService);
        RapidTestUtil.clear(vetService);
    }
}
