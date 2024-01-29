package com.github.vincemann.springrapid.syncdemo.controller;

import com.github.vincemann.springrapid.core.sec.AuthenticatedPrincipalImpl;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import com.github.vincemann.springrapid.coretest.controller.integration.MvcIntegrationTest;
import com.github.vincemann.springrapid.syncdemo.controller.template.OwnerControllerTestTemplate;
import com.github.vincemann.springrapid.syncdemo.dto.owner.CreateOwnerDto;
import com.github.vincemann.springrapid.syncdemo.dto.owner.ReadOwnOwnerDto;
import com.github.vincemann.springrapid.syncdemo.model.*;
import com.github.vincemann.springrapid.syncdemo.repo.*;
import com.github.vincemann.springrapid.syncdemo.service.*;
import com.github.vincemann.springrapid.syncdemo.service.ext.OwnerOfTheYearExtension;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Sql(scripts = "classpath:clear-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class MyControllerIntegrationTest extends MvcIntegrationTest {

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
    protected static final String GIL = "Gil";

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

    protected Owner gil;

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
    protected RapidSecurityContext<AuthenticatedPrincipalImpl> securityContext;

    @Autowired
    protected TransactionTemplate transactionTemplate;

    @Autowired
    protected OwnerControllerTestTemplate ownerController;

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
                .telephone("0176456789")
                .build();

        gil = Owner.builder()
                .firstName("dessen")
                .lastName(GIL)
                .address("dessen address")
                .city("n2 city")
                .telephone("0176567110")
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
        transactionTemplate.executeWithoutResult(transactionStatus -> {
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
        });

    }

    protected void assertSpecialtyHasVets(String description, String... vetNames) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
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
        });

    }

    //    @Transactional
    public void assertPetHasToys(String petName, String... toyNames) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
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
        });
    }

    protected void assertOwnerHasPets(String ownerName, String... petNames) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
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
        });
    }

    protected void assertOwnerHasClinicCard(String ownerName, Long clinicCardId) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            Optional<Owner> ownerOptional = ownerRepository.findByLastName(ownerName);
            Assertions.assertTrue(ownerOptional.isPresent());
            Owner owner = ownerOptional.get();
            System.err.println("Checking owner: " + ownerName);
            if (clinicCardId == null) {
                Assertions.assertNull(owner.getClinicCard());
            } else {
                ClinicCard clinicCard = clinicCardRepository.findById(clinicCardId).get();
                Assertions.assertEquals(clinicCard, owner.getClinicCard());
            }
        });
    }

    protected void assertClinicCardHasOwner(Long clinicCardId, String ownerName) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            if (ownerName == null) {
                ClinicCard clinicCard = clinicCardRepository.findById(clinicCardId).get();
                Assertions.assertNull(clinicCard.getOwner());
            } else {
                Optional<Owner> ownerOptional = ownerRepository.findByLastName(ownerName);
                Assertions.assertTrue(ownerOptional.isPresent());
                Owner owner = ownerOptional.get();
                ClinicCard clinicCard = clinicCardRepository.findById(clinicCardId).get();
                System.err.println("Checking owner: " + ownerName);
                Assertions.assertEquals(owner, clinicCard.getOwner());
            }
        });

    }

    protected void assertToyHasPet(String toyName, String petName) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            Pet pet = null;
            if (petName != null) {
                Optional<Pet> petOptional = petRepository.findByName(petName);
                Assertions.assertTrue(petOptional.isPresent());
                pet = petOptional.get();
            }
            Optional<Toy> optionalToy = toyRepository.findByName(toyName);
            Assertions.assertTrue(optionalToy.isPresent());
            Toy toy = optionalToy.get();
            System.err.println("Checking toy: " + toyName);
            Assertions.assertEquals(pet, toy.getPet());
        });
    }

    protected void assertPetHasOwner(String petName, String ownerName) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            Owner owner = null;
            if (ownerName != null) {
                Optional<Owner> ownerOptional = ownerRepository.findByLastName(ownerName);
                Assertions.assertTrue(ownerOptional.isPresent());
                owner = ownerOptional.get();
            }
            Optional<Pet> optionalPet = petRepository.findByName(petName);
            Assertions.assertTrue(optionalPet.isPresent());
            Pet pet = optionalPet.get();
            System.err.println("Checking pet: " + petName);
            Assertions.assertEquals(owner, pet.getOwner());
        });
    }

    protected Owner fetchOwner(Long id) {
        Optional<Owner> byId = ownerRepository.findById(id);
        Assertions.assertTrue(byId.isPresent());
        return byId.get();
    }

    protected Owner saveOwnerLinkedToPets(Owner owner, Long... petIds) throws Exception {
        CreateOwnerDto createOwnerDto = new CreateOwnerDto(owner);
        createOwnerDto.getPetIds().addAll(Lists.newArrayList(petIds));


        ReadOwnOwnerDto readOwnOwnerDto = performDs2xx(ownerController.create(createOwnerDto), ReadOwnOwnerDto.class);
        Assertions.assertNotNull(readOwnOwnerDto.getId());
        Owner saved = fetchOwner(readOwnOwnerDto.getId());
        Assertions.assertNotNull(saved.getCreatedDate());
//        Assertions.assertNotNull(saved.getCreatedById());
        Assertions.assertNotNull(saved.getLastModifiedDate());
//        Assertions.assertNotNull(saved.getLastModifiedById());
        return saved;
    }


    protected Owner saveOwnerLinkedToClinicCard(Owner owner, ClinicCard clinicCard) throws Exception {
        CreateOwnerDto createOwnerDto = new CreateOwnerDto(owner);
        createOwnerDto.setClinicCardId(clinicCard.getId());
        ReadOwnOwnerDto readOwnOwnerDto = performDs2xx(ownerController.create(createOwnerDto), ReadOwnOwnerDto.class);
        Assertions.assertNotNull(readOwnOwnerDto.getId());
        Owner saved = fetchOwner(readOwnOwnerDto.getId());
        Assertions.assertNotNull(saved.getCreatedDate());
//        Assertions.assertNotNull(saved.getCreatedById());
        Assertions.assertNotNull(saved.getLastModifiedDate());
//        Assertions.assertNotNull(saved.getLastModifiedById());
        return saved;
    }


    protected ReadOwnOwnerDto saveOwner(Owner owner) throws Exception {
        CreateOwnerDto createOwnerDto = new CreateOwnerDto(owner);
        return performDs2xx(ownerController.create(createOwnerDto), ReadOwnOwnerDto.class);
    }


//    @AfterEach
//    void tearDown() {
//        TransactionalRapidTestUtil.clear(visitService);
//        TransactionalRapidTestUtil.clear(petService);
//        TransactionalRapidTestUtil.clear(toyService);
//        TransactionalRapidTestUtil.clear(ownerService);
//        TransactionalRapidTestUtil.clear(petTypeService);
//        TransactionalRapidTestUtil.clear(specialtyService);
//        TransactionalRapidTestUtil.clear(vetService);
//        TransactionalRapidTestUtil.clear(clinicCardService);
//    }
}
