package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.core.controller.GenericCrudController;
import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.coredemo.model.*;
import com.github.vincemann.springrapid.coredemo.repo.*;
import com.github.vincemann.springrapid.coredemo.service.*;
import com.github.vincemann.springrapid.coredemo.service.extensions.OwnerOfTheYearExtension;
import com.github.vincemann.springrapid.coretest.controller.integration.IntegrationCrudControllerTest;
import com.github.vincemann.springrapid.coretest.util.TransactionalRapidTestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class AbstractControllerIntegrationTest<C extends GenericCrudController<?,Long,S,?,?>,S extends CrudService<?,Long>>
        extends IntegrationCrudControllerTest<C,S>
{

    //Types
    protected final Vet VetType = new Vet();
    protected final Specialty SpecialtyType = new Specialty();
    protected final Owner OwnerType = new Owner();
    protected final Pet PetType = new Pet();
    protected final Toy ToyType = new Toy();
    protected final ClinicCard ClinicCardType = new ClinicCard();

    protected static final String VET_MAX = "Max";
    protected static final String VET_POLDI = "Poldi";
    protected static final String VET_DICAPRIO = "Dicaprio";

    protected static final String MUSCLE = "Muscle";
    protected static final String DENTISM = "Dentism";
    protected static final String GASTRO = "Gastro";
    protected static final String HEART = "Heart";

    protected static final String MEIER = "Meier";
    protected static final String KAHN = "Kahn";

    protected static final String BELLO = "Bello";
    protected static final String BELLA = "Bella";
    protected static final String KITTY = "Kitty";

    protected static final String BALL = "ball";
    protected static final String BONE = "bone";
    protected static final String RUBBER_DUCK = "rubberDuck";



    protected Vet vetMax;
    protected Vet vetPoldi;
    protected Vet vetDiCaprio;

    protected Specialty dentism;
    protected Specialty gastro;
    protected Specialty heart;
    protected Specialty muscle;

    protected Owner meier;
    protected Owner kahn;

    protected Pet bello;
    protected Pet kitty;
    protected Pet bella;

    protected PetType savedDogPetType;
    protected PetType savedCatPetType;

    protected Toy rubberDuck;
    protected Toy bone;
    protected Toy ball;

    protected Visit checkTeethVisit;
    protected Visit checkHeartVisit;

    @Autowired
    protected SpecialtyService specialtyService;
    @Autowired
    protected SpecialtyRepository specialtyRepository;


    @Autowired
    protected VetRepository vetRepository;
    @Autowired
    protected VetService vetService;


    @Autowired
    protected ToyService toyService;
    @Autowired
    protected ToyRepository toyRepository;

    @Autowired
    protected PetService petService;
    @Autowired
    protected PetRepository petRepository;


    @Autowired
    protected ClinicCardService clinicCardService;
    @Autowired
    protected ClinicCardRepository clinicCardRepository;

    @Autowired
    protected PetTypeService petTypeService;
    @Autowired
    protected PetTypeRepository petTypeRepository;

    @Autowired
    protected VisitRepository visitRepository;
    @Autowired
    protected VisitService visitService;

    @SpyBean
    protected OwnerOfTheYearExtension ownerOfTheYearExtension;

    @Autowired
    protected OwnerRepository ownerRepository;
    @Autowired
    protected OwnerService ownerService;


    protected ClinicCard clinicCard;
    protected ClinicCard secondClinicCard;

    @Autowired
    protected RapidSecurityContext<RapidAuthenticatedPrincipal> securityContext;

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

        dentism = Specialty.builder()
                .description(DENTISM)
                .build();

        heart = Specialty.builder()
                .description(HEART)
                .build();

        muscle = Specialty.builder()
                .description(MUSCLE)
                .build();

        gastro = Specialty.builder()
                .description(GASTRO)
                .build();

        vetMax = Vet.builder()
                .firstName("Max")
                .lastName(VET_MAX)
                .specialtys(new HashSet<>())
                .build();

        vetDiCaprio = Vet.builder()
                .firstName("michael")
                .lastName(VET_DICAPRIO)
                .specialtys(new HashSet<>())
                .build();

        vetPoldi = Vet.builder()
                .firstName("Olli")
                .lastName(VET_POLDI)
                .specialtys(new HashSet<>())
                .build();

        checkHeartVisit = Visit.builder()
                .date(LocalDate.now())
                .pets(new HashSet<>())
                .reason("heart problems")
                .build();

        checkTeethVisit = Visit.builder()
                .date(LocalDate.now())
                .pets(new HashSet<>())
                .reason("teeth hurt")
                .build();

        clinicCard = ClinicCard.builder()
                .registrationDate(new Date())
                .registrationReason("stationary pet treatment")
                .build();

        secondClinicCard = ClinicCard.builder()
                .registrationDate(new Date())
                .registrationReason("ambulant pet treatment")
                .build();
    }

    protected void assertVetHasSpecialties(String vetName, String... descriptions) {
        Optional<Vet> vetOptional = vetRepository.findByLastName(vetName);
        Assertions.assertTrue(vetOptional.isPresent());
        Vet vet = vetOptional.get();

        Set<Specialty> specialtys = new HashSet<>();
        for (String description : descriptions) {
            Optional<Specialty> optionalSpecialty = specialtyRepository.findByDescription(description);
            Assertions.assertTrue(optionalSpecialty.isPresent());
            specialtys.add(optionalSpecialty.get());
        }
        System.err.println("Checking vet: " + vetName);
        Assertions.assertEquals(specialtys, vet.getSpecialtys());
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
        System.err.println("Checking Specialty: " + description);
        Assertions.assertEquals(vets, specialty.getVets());
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
        // todo lazy init exception, merge into transactional context before asserting, os he can load everything he needs
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

    protected void assertOwnerHasClinicCard(String ownerName, Long clinicCardId) {
        Optional<Owner> ownerOptional = ownerRepository.findByLastName(ownerName);
        Assertions.assertTrue(ownerOptional.isPresent());
        Owner owner = ownerOptional.get();
        System.err.println("Checking owner: " + ownerName);
        if(clinicCardId == null ){
            Assertions.assertNull(owner.getClinicCard());
        }else {
            ClinicCard clinicCard = clinicCardRepository.findById(clinicCardId).get();
            Assertions.assertEquals(clinicCard, owner.getClinicCard());
        }
    }

    protected void assertClinicCardHasOwner(Long clinicCardId, String ownerName) {
        if (ownerName == null){
            ClinicCard clinicCard = clinicCardRepository.findById(clinicCardId).get();
            Assertions.assertNull(clinicCard.getOwner());
        }else {
            Optional<Owner> ownerOptional = ownerRepository.findByLastName(ownerName);
            Assertions.assertTrue(ownerOptional.isPresent());
            Owner owner = ownerOptional.get();
            ClinicCard clinicCard = clinicCardRepository.findById(clinicCardId).get();
            System.err.println("Checking owner: " + ownerName);
            Assertions.assertEquals(owner, clinicCard.getOwner());
        }
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
        TransactionalRapidTestUtil.clear(visitService);
        TransactionalRapidTestUtil.clear(petService);
        TransactionalRapidTestUtil.clear(toyService);
        TransactionalRapidTestUtil.clear(ownerService);
        TransactionalRapidTestUtil.clear(petTypeService);
        TransactionalRapidTestUtil.clear(specialtyService);
        TransactionalRapidTestUtil.clear(vetService);
        TransactionalRapidTestUtil.clear(clinicCardService);
    }
}
