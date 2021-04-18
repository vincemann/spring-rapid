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
import org.apache.commons.beanutils.BeanUtilsBean;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.github.vincemann.ezcompare.Comparator.compare;
import static com.github.vincemann.springrapid.coretest.service.ExceptionMatchers.noException;
import static com.github.vincemann.springrapid.coretest.service.ExistenceMatchers.notPresentInDatabase;
import static com.github.vincemann.springrapid.coretest.service.PropertyMatchers.propertyAssert;
import static com.github.vincemann.springrapid.coretest.service.request.CrudServiceRequestBuilders.*;
import static com.github.vincemann.springrapid.coretest.service.resolve.EntityPlaceholder.DB_ENTITY;
import static com.github.vincemann.springrapid.coretest.service.resolve.EntityPlaceholder.SERVICE_RETURNED_ENTITY;

public class VerServiceIntegrationTest extends CrudServiceIntegrationTest<VetService, Vet, Long> {

    protected static final String MEIER = "Meier";
    protected static final String KAHN = "Kahn";
    protected static final String DENTISM = "Dentism";
    protected static final String GASTRO = "Gastro";
    protected static final String HEART = "Heart";
    //Types
    Vet VetType = new Vet();
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
                .andExpect(() -> propertyAssert(resolve(SERVICE_RETURNED_ENTITY))
                        .assertEmpty(VetType::getSpecialties)
                )
                .andExpect(() -> compare(meier).with(resolve(SERVICE_RETURNED_ENTITY))
                        .properties()
                        .all()
                        .ignore(VetType::getId)
                        .assertEqual());
    }

    @Test
    public void canSaveVet_getLinkedToSpecialties() throws BadEntityException {
        Specialty savedDentism = specialtyService.save(dentism);
        Specialty savedGastro = specialtyService.save(gastro);

        meier.setSpecialties(new HashSet<>(Lists.newArrayList(savedDentism, savedGastro)));

        test(save(meier));

        assertVetHasSpecialties(MEIER, DENTISM, GASTRO);
        assertSpecialtyHasVets(GASTRO, MEIER);
        assertSpecialtyHasVets(DENTISM, MEIER);
    }

    @Test
    public void canFindVetWithMultipleSpecialties() throws BadEntityException {
        Specialty savedDentism = specialtyService.save(dentism);
        Specialty savedGastro = specialtyService.save(gastro);

        meier.setSpecialties(new HashSet<>(Lists.newArrayList(savedDentism, savedGastro)));

        Vet savedMeier = getServiceUnderTest().save(meier);
        test(findById(savedMeier.getId()))
                .andExpect(() -> compare(savedMeier).with(resolve(DB_ENTITY))
                        .properties()
                        .all()
                        .assertEqual());

        assertVetHasSpecialties(MEIER, DENTISM, GASTRO);
        assertSpecialtyHasVets(GASTRO, MEIER);
        assertSpecialtyHasVets(DENTISM, MEIER);
    }

    // specialty that is already linked to meier, will be also linked to kahn after save(kahn)
    @Test
    public void canAddAnotherVetToSpecialty_viaSave() throws BadEntityException {
        Specialty savedDentism = specialtyService.save(dentism);
        Specialty savedGastro = specialtyService.save(gastro);

        meier.setSpecialties(new HashSet<>(Lists.newArrayList(savedDentism, savedGastro)));

        Vet savedMeier = getServiceUnderTest().save(meier);
        kahn.setSpecialties(new HashSet<>(Lists.newArrayList(savedGastro)));
        test(save(kahn));

        assertVetHasSpecialties(MEIER, DENTISM, GASTRO);
        assertVetHasSpecialties(KAHN, GASTRO);
        assertSpecialtyHasVets(GASTRO, MEIER, KAHN);
        assertSpecialtyHasVets(DENTISM, MEIER);
    }

    @Test
    public void canUnlinkSpecialtyFromVet_viaPartialUpdate() throws BadEntityException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Specialty savedDentism = specialtyService.save(dentism);
        Specialty savedGastro = specialtyService.save(gastro);
        meier.setSpecialties(new HashSet<>(Lists.newArrayList(savedDentism, savedGastro)));
        Vet savedMeier = getServiceUnderTest().save(meier);
        kahn.setSpecialties(new HashSet<>(Lists.newArrayList(savedGastro)));
        Vet savedKahn = getServiceUnderTest().save(kahn);

        // remove dentism from meier
        Vet meierUpdate = Vet.builder()
                .specialties(new HashSet<>(Lists.newArrayList(savedGastro)))
                .build();
        meierUpdate.setId(savedMeier.getId());
        test(partialUpdate(meierUpdate))
                // check that it really was only partial update
                .andExpect(() -> propertyAssert(resolve(DB_ENTITY))
                        .assertEquals(VetType::getLastName, MEIER));

        assertVetHasSpecialties(MEIER, GASTRO);
        assertVetHasSpecialties(KAHN, GASTRO);
        assertSpecialtyHasVets(GASTRO, MEIER, KAHN);
        assertSpecialtyHasVets(DENTISM);
    }

    @Test
    public void canUnlinkSpecialtyFromVet_viaFullUpdate() throws BadEntityException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Specialty savedDentism = specialtyService.save(dentism);
        Specialty savedGastro = specialtyService.save(gastro);
        meier.setSpecialties(new HashSet<>(Lists.newArrayList(savedDentism, savedGastro)));
        Vet savedMeier = getServiceUnderTest().save(meier);
        kahn.setSpecialties(new HashSet<>(Lists.newArrayList(savedGastro)));
        Vet savedKahn = getServiceUnderTest().save(kahn);

        // remove dentism from meier
        Vet meierUpdate = (Vet) BeanUtilsBean.getInstance().cloneBean(savedMeier);
        meierUpdate.setSpecialties(new HashSet<>(Lists.newArrayList(savedGastro)));
        test(update(meierUpdate));

        assertVetHasSpecialties(MEIER, GASTRO);
        assertVetHasSpecialties(KAHN, GASTRO);
        assertSpecialtyHasVets(GASTRO, MEIER, KAHN);
        assertSpecialtyHasVets(DENTISM);
    }

    @Test
    public void canLinkAnotherSpecialtyToVet_viaPartialUpdate() throws BadEntityException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // kahn -> gastro
        // meier -> gastro, dentism
        Specialty savedDentism = specialtyService.save(dentism);
        Specialty savedGastro = specialtyService.save(gastro);
        Specialty savedHeart = specialtyService.save(heart);
        meier.setSpecialties(new HashSet<>(Lists.newArrayList(savedDentism, savedGastro)));
        Vet savedMeier = getServiceUnderTest().save(meier);
        kahn.setSpecialties(new HashSet<>(Lists.newArrayList(savedGastro)));
        Vet savedKahn = getServiceUnderTest().save(kahn);

        // add heart to kahn
        Vet kahnUpdate = Vet.builder()
                .specialties(new HashSet<>(Lists.newArrayList(savedGastro, savedHeart)))
                .build();
        kahnUpdate.setId(savedKahn.getId());
        test(partialUpdate(kahnUpdate));

        assertVetHasSpecialties(MEIER, GASTRO, DENTISM);
        assertVetHasSpecialties(KAHN, GASTRO, HEART);
        assertSpecialtyHasVets(GASTRO, MEIER, KAHN);
        assertSpecialtyHasVets(DENTISM, MEIER);
        assertSpecialtyHasVets(HEART, KAHN);
    }

    @Test
    public void canLinkAnotherSpecialtyToVet_viaFullUpdate() throws BadEntityException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // kahn -> gastro
        // meier -> gastro, dentism
        Specialty savedDentism = specialtyService.save(dentism);
        Specialty savedGastro = specialtyService.save(gastro);
        Specialty savedHeart = specialtyService.save(heart);
        meier.setSpecialties(new HashSet<>(Lists.newArrayList(savedDentism, savedGastro)));
        Vet savedMeier = getServiceUnderTest().save(meier);
        kahn.setSpecialties(new HashSet<>(Lists.newArrayList(savedGastro)));
        Vet savedKahn = getServiceUnderTest().save(kahn);

        // add heart to kahn
        Vet kahnUpdate = (Vet) BeanUtilsBean.getInstance().cloneBean(savedKahn);
        kahnUpdate.setSpecialties(new HashSet<>(Lists.newArrayList(savedGastro, savedHeart)));
        test(update(kahnUpdate));

        assertVetHasSpecialties(MEIER, GASTRO, DENTISM);
        assertVetHasSpecialties(KAHN, GASTRO, HEART);
        assertSpecialtyHasVets(GASTRO, MEIER, KAHN);
        assertSpecialtyHasVets(DENTISM, MEIER);
        assertSpecialtyHasVets(HEART, KAHN);
    }


    @Test
    public void canDeleteVet_getUnlinkedFromSpecialties() throws BadEntityException {
        Specialty savedDentism = specialtyService.save(dentism);
        Specialty savedGastro = specialtyService.save(gastro);

        meier.setSpecialties(new HashSet<>(Lists.newArrayList(savedDentism, savedGastro)));

        Vet savedMeier = getServiceUnderTest().save(meier);
        test(deleteById(savedMeier.getId()))
                .andExpect(noException())
                .andExpect(notPresentInDatabase(savedMeier.getId()));

        assertSpecialtyHasVets(GASTRO);
        assertSpecialtyHasVets(DENTISM);
    }

    private void assertVetHasSpecialties(String vetName, String... descriptions) {
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

    private void assertSpecialtyHasVets(String description, String... vetNames) {
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
