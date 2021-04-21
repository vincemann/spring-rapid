package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.core.controller.GenericCrudController;
import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import com.github.vincemann.springrapid.coredemo.model.*;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.coredemo.repo.OwnerRepository;
import com.github.vincemann.springrapid.coredemo.repo.PetRepository;
import com.github.vincemann.springrapid.coredemo.repo.PetTypeRepository;
import com.github.vincemann.springrapid.coredemo.repo.ToyRepository;
import com.github.vincemann.springrapid.coredemo.service.OwnerService;
import com.github.vincemann.springrapid.coredemo.service.PetService;
import com.github.vincemann.springrapid.coredemo.service.PetTypeService;
import com.github.vincemann.springrapid.coredemo.service.ToyService;
import com.github.vincemann.springrapid.coredemo.service.plugin.OwnerOfTheYearExtension;
import com.github.vincemann.springrapid.coretest.controller.urlparamid.AutoMockUrlParamIdControllerTest;
import com.github.vincemann.springrapid.coretest.controller.urlparamid.IntegrationUrlParamIdControllerTest;
import com.github.vincemann.springrapid.coretest.util.RapidTestUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
@Slf4j
// currently unused, keep for future integration controller demo tests
public abstract class OneToManyControllerIntegrationTest<C extends GenericCrudController<?,Long,S,?,?>,S extends CrudService<?,Long>>
        extends IntegrationUrlParamIdControllerTest<C,Long,S>

{

    //Types
    final Owner OwnerType = new Owner();
    final Pet PetType = new Pet();
    final Toy ToyType = new Toy();

    protected static final String MEIER = "Meier";
    protected static final String KAHN = "Kahn";

    protected static final String BELLO = "Bello";
    protected static final String BELLA = "Bella";
    protected static final String KITTY = "Kitty";

    protected static final String BALL = "ball";
    protected static final String BONE = "bone";
    protected static final String RUBBER_DUCK = "rubberDuck";

    Owner meier;
    Owner kahn;

    Pet bello;
    Pet kitty;
    Pet bella;

    PetType savedDogPetType;
    PetType savedCatPetType;

    Toy rubberDuck;
    Toy bone;
    Toy ball;


    @Autowired
    ToyService toyService;
    @Autowired
    ToyRepository toyRepository;

    @Autowired
    PetService petService;
    @Autowired
    PetRepository petRepository;



    @Autowired
    PetTypeService petTypeService;
    @Autowired
    PetTypeRepository petTypeRepository;



    @SpyBean
    OwnerOfTheYearExtension ownerOfTheYearExtension;

    @Autowired
    OwnerRepository ownerRepository;
    @Autowired
    OwnerService ownerService;

    @Autowired
    RapidSecurityContext<RapidAuthenticatedPrincipal> securityContext;

    @BeforeEach
    public void setupTestData() throws Exception {

        savedDogPetType = petTypeService.save(new PetType("Dog"));
        savedCatPetType = petTypeService.save(new PetType("Cat"));


        rubberDuck = Toy.builder()
                .name(RUBBER_DUCK)
                .build();

        ball = Toy.builder()
                .name(BALL)
                .build();

        bone = Toy.builder()
                .name(BONE)
                .build();

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
                .telephone("0123456789")
                .build();

        kahn = Owner.builder()
                .firstName("Olli")
                .lastName(KAHN)
                .address("asljnflksamfslkmf")
                .city("n1 city")
                .telephone("1234567890")
                .build();
    }

    protected void assertPetHasToys(String petName, String... toyNames) {
        Optional<Pet> petOptional = petRepository.findByName(petName);
        Assertions.assertTrue(petOptional.isPresent());
        Pet pet = petOptional.get();

        Set<Toy> toys = new HashSet<>();
        for (String toyName : toyNames) {
            Optional<Toy> optionalToy = toyRepository.findByName(toyName);
            Assertions.assertTrue(optionalToy.isPresent());
            toys.add(optionalToy.get());
        }
        System.err.println("Checking pet: " + petName);
        Assertions.assertEquals(toys, pet.getToys());
    }

    protected void assertOwnerHasPets(String ownerName, String... petNames) {
        Optional<Owner> ownerOptional = ownerRepository.findByLastName(ownerName);
        Assertions.assertTrue(ownerOptional.isPresent());
        Owner owner = ownerOptional.get();

        Set<Pet> pets = new HashSet<>();
        for (String petName : petNames) {
            Optional<Pet> optionalPet = petRepository.findByName(petName);
            Assertions.assertTrue(optionalPet.isPresent());
            pets.add(optionalPet.get());
        }
        System.err.println("Checking owner: " + ownerName);
        Assertions.assertEquals(pets, owner.getPets());
    }

    protected void assertToyHasPet(String toyName, String petName) {
        Pet pet = null;
        if (petName!=null){
            Optional<Pet> petOptional = petRepository.findByName(petName);
            Assertions.assertTrue(petOptional.isPresent());
            pet = petOptional.get();
        }
        Optional<Toy> optionalToy = toyRepository.findByName(toyName);
        Assertions.assertTrue(optionalToy.isPresent());
        Toy toy = optionalToy.get();
        System.err.println("Checking toy: " + toyName);
        Assertions.assertEquals(pet, toy.getPet());
    }

    protected void assertPetHasOwner(String petName, String ownerName) {
        Owner owner = null;
        if (ownerName!=null){
            Optional<Owner> ownerOptional = ownerRepository.findByLastName(ownerName);
            Assertions.assertTrue(ownerOptional.isPresent());
            owner = ownerOptional.get();
        }
        Optional<Pet> optionalPet = petRepository.findByName(petName);
        Assertions.assertTrue(optionalPet.isPresent());
        Pet pet = optionalPet.get();
        System.err.println("Checking pet: " + petName);
        Assertions.assertEquals(owner, pet.getOwner());
    }


    @AfterEach
    void tearDown() {
        RapidTestUtil.clear(petService);
        RapidTestUtil.clear(toyService);
        RapidTestUtil.clear(ownerService);
        RapidTestUtil.clear(petTypeService);
    }


}
