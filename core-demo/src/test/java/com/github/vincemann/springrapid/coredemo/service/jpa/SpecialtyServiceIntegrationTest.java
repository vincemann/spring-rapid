package com.github.vincemann.springrapid.coredemo.service.jpa;

import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.util.Lists;
import com.github.vincemann.springrapid.coredemo.model.Specialty;
import com.github.vincemann.springrapid.coredemo.model.Vet;
import com.github.vincemann.springrapid.coredemo.service.SpecialtyService;
import com.github.vincemann.springrapid.coredemo.service.VetService;
import com.github.vincemann.springrapid.coretest.service.CrudServiceIntegrationTest;
import com.github.vincemann.springrapid.coretest.service.resolve.EntityPlaceholder;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static com.github.vincemann.ezcompare.Comparator.compare;
import static com.github.vincemann.springrapid.coretest.service.ExceptionMatchers.noException;
import static com.github.vincemann.springrapid.coretest.service.ExistenceMatchers.notPresentInDatabase;
import static com.github.vincemann.springrapid.coretest.service.PropertyMatchers.propertyAssert;
import static com.github.vincemann.springrapid.coretest.service.request.CrudServiceRequestBuilders.deleteById;
import static com.github.vincemann.springrapid.coretest.service.request.CrudServiceRequestBuilders.save;

public class SpecialtyServiceIntegrationTest
        extends ManyToManyIntegrationTest<SpecialtyService, Specialty, Long> {


    @Test
    public void canSaveSpecialty_getLinkedToVets() throws BadEntityException {
        Vet savedMeier = vetService.save(meier);
        Vet savedKahn = vetService.save(kahn);

        dentism.setVets(new HashSet<>(Lists.newArrayList(savedMeier,savedKahn)));
        test(save(dentism))
                .andExpect(() -> propertyAssert(resolve(EntityPlaceholder.DB_ENTITY))
                        .assertSize(SpecialtyType::getVets,2));

        assertVetHasSpecialties(MEIER, DENTISM);
        assertVetHasSpecialties(KAHN, DENTISM);
        assertSpecialtyHasVets(DENTISM, MEIER, KAHN);
    }

    @Test
    public void canSaveAnotherSpecialty_getLinkedToVets() throws BadEntityException {
        Vet savedMeier = vetService.save(meier);
        Vet savedKahn = vetService.save(kahn);

        dentism.setVets(new HashSet<>(Lists.newArrayList(savedMeier,savedKahn)));
        getServiceUnderTest().save(dentism);


        gastro.setVets(new HashSet<>(Lists.newArrayList(savedKahn)));
        test(save(gastro))
                .andExpect(() -> propertyAssert(resolve(EntityPlaceholder.DB_ENTITY))
                        .assertSize(SpecialtyType::getVets,1));

        assertVetHasSpecialties(MEIER, DENTISM);
        assertVetHasSpecialties(KAHN, DENTISM, GASTRO);
        assertSpecialtyHasVets(DENTISM, MEIER, KAHN);
        assertSpecialtyHasVets(GASTRO, KAHN);
    }

    @Test
    public void canRemoveSpecialty_getUnlinkedFromVets() throws BadEntityException {
        // meier -> dentism
        // kahn -> dentism, gastro
        Vet savedMeier = vetService.save(meier);
        Vet savedKahn = vetService.save(kahn);
        dentism.setVets(new HashSet<>(Lists.newArrayList(savedMeier,savedKahn)));
        Specialty savedDentism = getServiceUnderTest().save(dentism);
        gastro.setVets(new HashSet<>(Lists.newArrayList(savedKahn)));
        getServiceUnderTest().save(gastro);

        // remove dentism
        test(deleteById(savedDentism.getId()))
                .andExpect(noException())
                .andExpect(notPresentInDatabase(savedDentism.getId()));


        assertVetHasSpecialties(MEIER);
        assertVetHasSpecialties(KAHN, GASTRO);
        assertSpecialtyHasVets(GASTRO, KAHN);
    }



}
