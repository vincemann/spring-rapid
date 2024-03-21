package com.github.vincemann.springrapid.syncdemo.controller.suite;

import com.github.vincemann.springrapid.coretest.controller.AbstractMvcTest;
import com.github.vincemann.springrapid.syncdemo.controller.suite.template.OwnerControllerTestTemplate;
import com.github.vincemann.springrapid.syncdemo.controller.suite.template.PetControllerTestTemplate;
import com.github.vincemann.springrapid.syncdemo.dto.owner.CreateOwnerDto;
import com.github.vincemann.springrapid.syncdemo.dto.owner.ReadOwnerDto;
import com.github.vincemann.springrapid.syncdemo.dto.pet.CreatePetDto;
import com.github.vincemann.springrapid.syncdemo.dto.pet.ReadPetDto;
import com.github.vincemann.springrapid.syncdemo.model.*;
import com.github.vincemann.springrapid.syncdemo.repo.*;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Sql(scripts = "classpath:clear-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class MyIntegrationTest extends AbstractMvcTest {

    protected static final String MEIER = "Meier";
    protected static final String KAHN = "Kahn";
    protected static final String GIL = "Gil";

    protected static final String BELLO = "Bello";
    protected static final String BELLA = "Bella";
    protected static final String KITTY = "Kitty";

    protected static final String BALL = "ball";
    protected static final String BONE = "bone";
    protected static final String RUBBER_DUCK = "rubberDuck";

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

    protected ClinicCard clinicCard;
    protected ClinicCard secondClinicCard;


    @Autowired
    protected PetRepository petRepository;
    @Autowired
    protected ClinicCardRepository clinicCardRepository;
    @Autowired
    protected PetTypeRepository petTypeRepository;
    @Autowired
    protected OwnerRepository ownerRepository;
    @Autowired
    protected ToyRepository toyRepository;


    @Autowired
    protected TransactionTemplate transactionTemplate;

    @Autowired
    protected OwnerControllerTestTemplate ownerController;

    @Autowired
    protected PetControllerTestTemplate petController;

    @BeforeEach
    public void setupTestData() throws Exception {

        savedDogPetType = petTypeRepository.save(new PetType("Dog"));
        savedCatPetType = petTypeRepository.save(new PetType("Cat"));


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

        clinicCard = ClinicCard.builder()
                .registrationDate(new Date())
                .registrationReason("stationary pet treatment")
                .build();

        secondClinicCard = ClinicCard.builder()
                .registrationDate(new Date())
                .registrationReason("ambulant pet treatment")
                .build();
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

    protected Owner createOwnerLinkedToPets(Owner owner, Long... petIds) throws Exception {
        CreateOwnerDto createOwnerDto = new CreateOwnerDto(owner);
        createOwnerDto.getPetIds().addAll(Lists.newArrayList(petIds));


        ReadOwnerDto readOwnerDto = ownerController.create2xx(createOwnerDto);
        Assertions.assertNotNull(readOwnerDto.getId());
        Owner saved = fetchOwner(readOwnerDto.getId());
        Assertions.assertNotNull(saved.getCreatedDate());
//        Assertions.assertNotNull(saved.getCreatedById());
        Assertions.assertNotNull(saved.getLastModifiedDate());
//        Assertions.assertNotNull(saved.getLastModifiedById());
        return saved;
    }


    protected Owner createOwnerLinkedToClinicCard(Owner owner, ClinicCard clinicCard) throws Exception {
        CreateOwnerDto createOwnerDto = new CreateOwnerDto(owner);
        createOwnerDto.setClinicCardId(clinicCard.getId());
        ReadOwnerDto readOwnerDto = ownerController.create2xx(createOwnerDto);
        Assertions.assertNotNull(readOwnerDto.getId());
        Owner saved = fetchOwner(readOwnerDto.getId());
        Assertions.assertNotNull(saved.getCreatedDate());
//        Assertions.assertNotNull(saved.getCreatedById());
        Assertions.assertNotNull(saved.getLastModifiedDate());
//        Assertions.assertNotNull(saved.getLastModifiedById());
        return saved;
    }


    protected ReadOwnerDto createOwner(Owner owner) throws Exception {
        CreateOwnerDto createOwnerDto = new CreateOwnerDto(owner);
        return ownerController.create2xx(createOwnerDto);
    }

    protected ReadPetDto createPetLinkedToOwnerAndToys(Pet pet, Long ownerId, Toy... toys) throws Exception {
        CreatePetDto createPetDto = new CreatePetDto(pet);
        if (ownerId != null)
            createPetDto.setOwnerId(ownerId);
        if (toys.length > 0)
            createPetDto.setToyIds(Arrays.stream(toys)
                    .map(Toy::getId)
                    .collect(Collectors.toSet()));

        return petController.create2xx(createPetDto);
    }
}
