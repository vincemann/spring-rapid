package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.acltest.controller.AclIntegrationCrudControllerTest;
import com.github.vincemann.springrapid.acldemo.auth.MyRoles;
import com.github.vincemann.springrapid.acldemo.controller.templates.OwnerControllerTestTemplate;
import com.github.vincemann.springrapid.acldemo.controller.templates.PetControllerTestTemplate;
import com.github.vincemann.springrapid.acldemo.controller.templates.VetControllerTestTemplate;
import com.github.vincemann.springrapid.acldemo.dto.VisitDto;
import com.github.vincemann.springrapid.acldemo.dto.owner.CreateOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.owner.FullOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.pet.FullPetDto;
import com.github.vincemann.springrapid.acldemo.dto.pet.OwnerCreatesPetDto;
import com.github.vincemann.springrapid.acldemo.dto.user.UUIDSignupResponseDto;
import com.github.vincemann.springrapid.acldemo.dto.vet.CreateVetDto;
import com.github.vincemann.springrapid.acldemo.dto.vet.FullVetDto;
import com.github.vincemann.springrapid.acldemo.model.*;
import com.github.vincemann.springrapid.acldemo.repositories.*;
import com.github.vincemann.springrapid.acldemo.service.*;
import com.github.vincemann.springrapid.auth.dto.SignupDto;
import com.github.vincemann.springrapid.authtest.controller.template.UserControllerTestTemplate;
import com.github.vincemann.springrapid.core.controller.GenericCrudController;
import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.coretest.util.TransactionalRapidTestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.vincemann.springrapid.coretest.util.TransactionalRapidTestUtil.createUpdateJsonLine;
import static com.github.vincemann.springrapid.coretest.util.TransactionalRapidTestUtil.createUpdateJsonRequest;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
// add admin before each test
public class AbstractControllerIntegrationTest<C extends GenericCrudController<?,Long,S,?,?>,S extends CrudService<?,Long>>
        extends AclIntegrationCrudControllerTest<C,S>
{

    //Types
    protected final Vet VetType = new Vet();
    protected final Specialty SpecialtyType = new Specialty();
    protected final Owner OwnerType = new Owner();
    protected final Pet PetType = new Pet();
    protected final Illness IllnessType = new Illness();
    protected final User UserType = new User();


    protected static final String CONTACT_INFORMATION_SUFFIX = "@guerilla-mail.com";

    // admin created via AdminInitializer, see config
    protected static final String ADMIN = "admin";
    protected static final String ADMIN_PASSWORD = "AdminPassword123!";
    protected static final String ADMIN_CONTACT_INFORMATION = "admin@example.com";

    protected static final String VET_MAX = "Max";
    protected static final String VET_MAX_PASSWORD = "MaxPassword123?";
    protected static final String VET_MAX_CONTACT_INFORMATION = VET_MAX+CONTACT_INFORMATION_SUFFIX;

    protected static final String VET_POLDI = "Poldi";
    protected static final String VET_POLDI_PASSWORD = "PoldiPassword123?";
    protected static final String VET_POLDI_CONTACT_INFORMATION = VET_POLDI+CONTACT_INFORMATION_SUFFIX;

    protected static final String VET_DICAPRIO = "Dicaprio";
    protected static final String VET_DICAPRIO_PASSWORD = "DicaprioPassword123?";
    protected static final String VET_DICAPRIO_CONTACT_INFORMATION = VET_DICAPRIO+CONTACT_INFORMATION_SUFFIX;

    protected static final String OWNER_MEIER = "Meier";
    protected static final String OWNER_MEIER_PASSWORD = "MeierPassword123?";
    protected static final String OWNER_MEIER_CONTACT_INFORMATION = OWNER_MEIER+CONTACT_INFORMATION_SUFFIX;

    protected static final String OWNER_KAHN = "Kahn";
    protected static final String OWNER_KAHN_PASSWORD = "KahnPassword123?";
    protected static final String OWNER_KAHN_CONTACT_INFORMATION = OWNER_KAHN+CONTACT_INFORMATION_SUFFIX;

    protected static final String MUSCLE = "Muscle";
    protected static final String DENTISM = "Dentism";
    protected static final String GASTRO = "Gastro";
    protected static final String HEART = "Heart";



    protected static final String BELLO = "Bello";
    protected static final String BELLA = "Bella";
    protected static final String KITTY = "Kitty";

    protected static final String GASTRITIS = "Gastritis";
    protected static final String TEETH_PAIN = "teeth pain";
    protected static final String WEAK_HEART = "wek heart";




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

    protected Illness gastritis;
    protected Illness teethPain;
    protected Illness weakHeart;

    protected com.github.vincemann.springrapid.acldemo.model.PetType savedDogPetType;
    protected PetType savedCatPetType;


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
    protected IllnessService illnessService;
    @Autowired
    protected IllnessRepository illnessRepository;

    @Autowired
    protected PetService petService;
    @Autowired
    protected PetRepository petRepository;



    @Autowired
    protected PetTypeService petTypeService;
    @Autowired
    protected PetTypeRepository petTypeRepository;

    @Autowired
    protected VisitRepository visitRepository;
    @Autowired
    protected VisitService visitService;


    @Autowired
    protected OwnerRepository ownerRepository;
    @Autowired
    protected OwnerService ownerService;

    @Autowired
    protected MyUserService userService;

    @Autowired
    protected UserControllerTestTemplate userController;

    @Autowired
    protected OwnerControllerTestTemplate ownerController;

    @Autowired
    protected PetControllerTestTemplate petController;

    @Autowired
    protected VetControllerTestTemplate vetController;

    @Autowired
    protected RapidSecurityContext<RapidAuthenticatedPrincipal> securityContext;


    @Override
    protected DefaultMockMvcBuilder createMvcBuilder() {
        DefaultMockMvcBuilder mvcBuilder = super.createMvcBuilder();
        mvcBuilder.apply(SecurityMockMvcConfigurers.springSecurity());
        return mvcBuilder;
    }

    @BeforeEach
    public void setupTestData() throws Exception {

        savedDogPetType = petTypeService.save(new PetType("Dog"));
        savedCatPetType = petTypeService.save(new PetType("Cat"));


        gastritis = Illness.builder()
                .name(GASTRITIS)
                .pets(new HashSet<>())
                .build();

        teethPain = Illness.builder()
                .name(TEETH_PAIN)
                .pets(new HashSet<>())
                .build();

        weakHeart = Illness.builder()
                .name(WEAK_HEART)
                .pets(new HashSet<>())
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
                .lastName(OWNER_MEIER)
                .address("asljnflksamfslkmf")
                .city("n1 city")
                .telephone("0123456789")
                .build();

        kahn = Owner.builder()
                .firstName("Olli")
                .lastName(OWNER_KAHN)
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

    protected void assertPetHasIllnesses(String petName, String... illnessNames) {
        Optional<Pet> petOptional = petRepository.findByName(petName);
        Assertions.assertTrue(petOptional.isPresent());
        Pet pet = petOptional.get();

        Set<Illness> illnesses = new HashSet<>();
        for (String illness : illnessNames) {
            Optional<Illness> optionalIllness = illnessRepository.findByName(illness);
            Assertions.assertTrue(optionalIllness.isPresent());
            illnesses.add(optionalIllness.get());
        }
        System.err.println("Checking pet: " + petName);
        Assertions.assertEquals(illnesses, pet.getIllnesss());
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

    protected String registerOwnerWithPets(Owner owner, String contactInformation, String password, Pet... pets) throws Exception {
        Owner dbOwner = registerOwner(owner, contactInformation, password);
        String token = userController.login2xx(contactInformation,password);
        for (Pet pet : pets) {
            OwnerCreatesPetDto createPetDto = new OwnerCreatesPetDto(pet,dbOwner.getId());
            FullPetDto savedPetDto = performDs2xx(petController.create(createPetDto)
                    .header(HttpHeaders.AUTHORIZATION, token), FullPetDto.class);
        }
        return token;
    }

    protected Vet registerVet(Vet vet, String contactInformation, String password) throws Exception {
        SignupDto signupDto = SignupDto.builder()
                .contactInformation(contactInformation)
                .password(password)
                .build();
        UUIDSignupResponseDto signedUpDto = performDs2xx(userController.signup(signupDto),UUIDSignupResponseDto.class);
        String uuid = signedUpDto.getUuid();

        CreateVetDto createVetDto = new CreateVetDto(vet,uuid);
        FullVetDto fullVetDto = performDs2xx(vetController.create(createVetDto), FullVetDto.class);
        return vetService.findById(fullVetDto.getId()).get();
    }

    protected Vet registerEnabledVet(Vet vet, String contactInformation, String password) throws Exception {
        Vet registerVet = registerVet(vet, contactInformation, password);
        String adminToken = userController.login2xx(ADMIN_CONTACT_INFORMATION, ADMIN_PASSWORD);
        String verifyVetJson = createUpdateJsonRequest(
                createUpdateJsonLine("add", "/roles/-", MyRoles.VET),
                createUpdateJsonLine("remove", "/roles", MyRoles.NEW_VET)
        );

        mvc.perform(userController.update(verifyVetJson, registerVet.getUser().getId().toString())
                .header(HttpHeaders.AUTHORIZATION, adminToken))
                .andExpect(status().is2xxSuccessful());

        return vetRepository.findById(registerVet.getId()).get();
    }


    protected Visit createVisit(String token, Owner owner, Vet vet,Visit visit, Pet... pets) throws Exception {
        VisitDto createVisitDto = new VisitDto(visit);
        createVisitDto.setOwnerId(owner.getId());
        createVisitDto.setPetIds(Arrays.stream(pets).map(Pet::getId).collect(Collectors.toSet()));
        createVisitDto.setVetId(vet.getId());

        VisitDto responseDto = performDs2xx(create(createVisitDto)
                        .header(HttpHeaders.AUTHORIZATION,token)
                , VisitDto.class);
        return visitRepository.findById(responseDto.getId()).get();
    }

    protected Owner registerOwner(Owner owner, String contactInformation, String password) throws Exception {
        SignupDto signupDto = SignupDto.builder()
                .contactInformation(contactInformation)
                .password(password)
                .build();
        UUIDSignupResponseDto signedUpDto = performDs2xx(userController.signup(signupDto),UUIDSignupResponseDto.class);
        String uuid = signedUpDto.getUuid();

        CreateOwnerDto createOwnerDto = new CreateOwnerDto(owner,uuid);
        FullOwnerDto fullOwnerDto = performDs2xx(ownerController.create(createOwnerDto), FullOwnerDto.class);
        return ownerService.findById(fullOwnerDto.getId()).get();
    }

    @AfterEach
    void tearDown() {
        TransactionalRapidTestUtil.clear(visitService);
        TransactionalRapidTestUtil.clear(petService);
        TransactionalRapidTestUtil.clear(illnessService);
        TransactionalRapidTestUtil.clear(ownerService);
        TransactionalRapidTestUtil.clear(petTypeService);
        TransactionalRapidTestUtil.clear(specialtyService);
        TransactionalRapidTestUtil.clear(vetService);
        TransactionalRapidTestUtil.clear(userService);
    }
}
