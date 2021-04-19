package com.github.vincemann.springrapid.coredemo.service.jpa;

import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.util.Lists;
import com.github.vincemann.springrapid.coredemo.model.Specialty;
import com.github.vincemann.springrapid.coredemo.model.Vet;
import com.github.vincemann.springrapid.coredemo.service.VetService;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;

import static com.github.vincemann.ezcompare.Comparator.compare;
import static com.github.vincemann.springrapid.coretest.service.ExceptionMatchers.noException;
import static com.github.vincemann.springrapid.coretest.service.ExistenceMatchers.notPresentInDatabase;
import static com.github.vincemann.springrapid.coretest.service.PropertyMatchers.propertyAssert;
import static com.github.vincemann.springrapid.coretest.service.request.CrudServiceRequestBuilders.*;
import static com.github.vincemann.springrapid.coretest.service.resolve.EntityPlaceholder.DB_ENTITY;
import static com.github.vincemann.springrapid.coretest.service.resolve.EntityPlaceholder.SERVICE_RETURNED_ENTITY;

/**
 * Test to showcase that auto management of many-many bidir-relationships work for parent side
 */
public class VerServiceIntegrationTest extends ManyToManyServiceIntegrationTest<VetService, Vet, Long> {



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


}
