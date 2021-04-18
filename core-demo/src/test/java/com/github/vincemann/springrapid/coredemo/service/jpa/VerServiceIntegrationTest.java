package com.github.vincemann.springrapid.coredemo.service.jpa;

import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.util.Lists;
import com.github.vincemann.springrapid.coredemo.model.*;
import com.github.vincemann.springrapid.coredemo.repo.SpecialtyRepository;
import com.github.vincemann.springrapid.coredemo.repo.VetRepository;
import com.github.vincemann.springrapid.coredemo.service.SpecialtyService;
import com.github.vincemann.springrapid.coredemo.service.VetService;
import com.github.vincemann.springrapid.coretest.service.CrudServiceIntegrationTest;
import com.github.vincemann.springrapid.coretest.util.RapidTestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.github.vincemann.ezcompare.Comparator.compare;
import static com.github.vincemann.springrapid.coretest.service.PropertyMatchers.propertyAssert;
import static com.github.vincemann.springrapid.coretest.service.request.CrudServiceRequestBuilders.save;
import static com.github.vincemann.springrapid.coretest.service.resolve.EntityPlaceholder.DB_ENTITY;
import static com.github.vincemann.springrapid.coretest.service.resolve.EntityPlaceholder.SERVICE_RETURNED_ENTITY;

public class VerServiceIntegrationTest extends CrudServiceIntegrationTest<VetService,Vet,Long> {

    //Types
    Vet VetType = new Vet();

    protected static final String MEIER = "Meier";
    protected static final String KAHN = "Kahn";

    protected static final String DENTISM = "Dentism";
    protected static final String GASTRO = "Gastro";
    protected static final String HEART = "Heart";

    Vet meier;
    Vet kahn;

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
                .description(GASTRO)
                .build();

        gastro = Specialty.builder()
                .description(HEART)
                .build();

        meier = Vet.builder()
                .firstName("Max")
                .lastName(MEIER)
                .specialties(new HashSet<>())
                .build();

        kahn = Vet.builder()
                .firstName("Olli")
                .lastName(KAHN)
                .specialties(new HashSet<>())
                .build();
    }

    @Test
    public void canSaveVetWithoutSpecialty() {
                test(save(meier))
                        .andExpect(() -> compare(meier)
                                // resolve db entity makes sure entity is actually saved in repo
                                .with(resolve(DB_ENTITY))
                                .properties()
                                .all()
                                .ignore(VetType::getId)
                                .assertEqual())
                        // is implicitly tested by next statement, just want to make it explicit
                        .andExpect(()-> propertyAssert(resolve(SERVICE_RETURNED_ENTITY))
                                .assertEmpty(VetType::getSpecialties)
                        )
                        .andExpect(() -> compare(meier).with(resolve(SERVICE_RETURNED_ENTITY))
                                .properties()
                                .all()
                                .ignore(VetType::getId)
                                .assertEqual());
    }

    @Test
    public void canSaveVetWithSpecialties() throws BadEntityException {
        Specialty savedDentism = specialtyService.save(dentism);
        Specialty savedGastro = specialtyService.save(gastro);

        meier.setSpecialties(new HashSet<>(Lists.newArrayList(savedDentism,savedGastro)));

        test(save(meier));

        assertVetHasSpecialties(MEIER,DENTISM,GASTRO);
        assertSpecialtyHasVets(GASTRO,MEIER);
        assertSpecialtyHasVets(DENTISM,MEIER);
    }

    private void assertVetHasSpecialties(String vetName, String... descriptions){
        Optional<Vet> vetOptional = vetRepository.findByLastName(vetName);
        Assertions.assertTrue(vetOptional.isPresent());
        Vet vet = vetOptional.get();

        Set<Specialty> specialties = new HashSet<>();
        for (String description : descriptions) {
            Optional<Specialty> optionalSpecialty = specialtyRepository.findByDescription(description);
            Assertions.assertTrue(optionalSpecialty.isPresent());
            specialties.add(optionalSpecialty.get());
        }
        Assertions.assertEquals(specialties,vet.getSpecialties());
    }

    private void assertSpecialtyHasVets(String description, String... vetNames){
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

//    private Vet findVet(Specialty specialty, String name){
//        Optional<Vet> optionalVet = specialty.getVets().stream().filter(vet -> vet.getLastName().equals(name)).findFirst();
//        Assertions.assertTrue(optionalVet.isPresent());
//        return optionalVet.get();
//    }
//
//    private Specialty findSpecialty(Vet vet, String description){
//        Optional<Specialty> optionalSpecialty = vet.getSpecialties().stream().filter(specialty -> specialty.getDescription().equals(description)).findFirst();
//        Assertions.assertTrue(optionalSpecialty.isPresent());
//        return optionalSpecialty.get();
//    }

    @AfterEach
    void tearDown() {
        RapidTestUtil.clear(specialtyService);
        RapidTestUtil.clear(vetService);
    }

}
