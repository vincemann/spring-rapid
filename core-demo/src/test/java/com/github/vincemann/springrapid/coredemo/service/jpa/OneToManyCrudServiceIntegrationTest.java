package com.github.vincemann.springrapid.coredemo.service.jpa;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.coredemo.model.Owner;
import com.github.vincemann.springrapid.coredemo.model.Pet;
import com.github.vincemann.springrapid.coredemo.model.PetType;
import com.github.vincemann.springrapid.coredemo.repo.OwnerRepository;
import com.github.vincemann.springrapid.coredemo.repo.PetRepository;
import com.github.vincemann.springrapid.coredemo.repo.PetTypeRepository;
import com.github.vincemann.springrapid.coredemo.service.OwnerService;
import com.github.vincemann.springrapid.coredemo.service.PetService;
import com.github.vincemann.springrapid.coredemo.service.PetTypeService;
import com.github.vincemann.springrapid.coredemo.service.plugin.OwnerOfTheYearExtension;
import com.github.vincemann.springrapid.coretest.service.CrudServiceIntegrationTest;
import com.github.vincemann.springrapid.coretest.util.RapidTestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;

public class OneToManyCrudServiceIntegrationTest<
        S extends CrudService<E, Id>,
        E extends IdentifiableEntity<Id>,
        Id extends Serializable
        > extends CrudServiceIntegrationTest<S,E,Id> {

    //Types
    Owner OwnerType = new Owner();

    protected static final String MEIER = "Meier";
    protected static final String KAHN = "Kahn";

    protected static final String BELLO = "Bello";
    protected static final String BELLA = "Bella";
    protected static final String KITTY = "Kitty";

    Owner meier;
    Owner kahn;

    Pet bello;
    Pet kitty;
    Pet bella;

    PetType savedDogPetType;
    PetType savedCatPetType;

    @SpyBean
    OwnerOfTheYearExtension ownerOfTheYearExtension;

    @Autowired
    PetService petService;
    @Autowired
    PetRepository petRepository;

    @Autowired
    PetTypeService petTypeService;
    @Autowired
    PetTypeRepository petTypeRepository;

    @Autowired
    OwnerRepository ownerRepository;
    @Autowired
    OwnerService ownerService;

    @BeforeEach
    public void setupTestData() throws Exception {
        savedDogPetType = petTypeService.save(new PetType("Dog"));
        savedCatPetType = petTypeService.save(new PetType("Cat"));

        bello = Pet.builder()
                .petType(savedDogPetType)
                .name(BELLO)
                .birthDate(LocalDate.now())
                .build();

        bella = Pet.builder()
                .petType(savedDogPetType)
                .name(BELLA)
                .birthDate(LocalDate.now())
                .build();

        kitty = Pet.builder()
                .petType(savedCatPetType)
                .name(KITTY)
                .birthDate(LocalDate.now())
                .build();

        meier = Owner.builder()
                .firstName("Max")
                .lastName(MEIER)
                .address("asljnflksamfslkmf")
                .city("n1 city")
                .telephone("12843723847324")
                .pets(new HashSet<>())
                .build();

        kahn = Owner.builder()
                .firstName("Olli")
                .lastName(KAHN)
                .address("asljnflksamfslkmf")
                .city("n1 city")
                .telephone("12843723847324")
                .pets(new HashSet<>())
                .build();
    }

    @AfterEach
    void tearDown() {
        RapidTestUtil.clear(petService);
        RapidTestUtil.clear(ownerService);
        RapidTestUtil.clear(petTypeService);
    }
}
