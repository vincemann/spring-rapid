package io.github.vincemann.demo.service.springDataJPA.it;

import io.github.vincemann.demo.model.Owner;
import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.demo.model.PetType;
import io.github.vincemann.demo.repositories.PetRepository;
import io.github.vincemann.demo.service.OwnerService;
import io.github.vincemann.demo.service.PetTypeService;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.test.callback.PostUpdateServiceTestCallback;
import io.github.vincemann.generic.crud.lib.test.service.ForceEagerFetch_CrudServiceIntegrationTest;
import io.github.vincemann.generic.crud.lib.test.exception.InvalidConfigurationModificationException;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.update.SuccessfulUpdateServiceTestConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static io.github.vincemann.generic.crud.lib.test.forceEagerFetch.proxy.abs.Hibernate_ForceEagerFetch_Proxy.EAGER_FETCH_PROXY;

//@DataJpaTest cant be used because i need autowired components from generic-crud-lib
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment =
        SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = {"test", "springdatajpa"})
class OwnerJPAServiceIT
        extends ForceEagerFetch_CrudServiceIntegrationTest
            <Owner,Long>
{

    private Owner ownerWithoutPets;
    private Owner ownerWithOnePet;
    private Pet testPet;
    private PetType savedDogPetType;

    @Autowired
    private CrudService<Pet, Long, PetRepository> petService;
    @Autowired
    private PetTypeService petTypeService;


    @Autowired
    @Qualifier(EAGER_FETCH_PROXY)
    @Override
    public void injectCrudService(CrudService<Owner, Long, ? extends CrudRepository<Owner,Long>> crudService) {
        super.injectCrudService(crudService);
    }

    @BeforeEach
    public void setUp() throws Exception {
        //proxyfy service
        this.petService = wrapWithEagerFetchProxy(petService);
        savedDogPetType = petTypeService.save(new PetType("Dog"));

        testPet = Pet.builder()
                .petType(savedDogPetType)
                .name("Bello")
                .birthDate(LocalDate.now())
                .build();

        ownerWithoutPets = Owner.builder()
                .firstName("owner without pets")
                .lastName("owner without pets lastName")
                .address("asljnflksamfslkmf")
                .city("n1 city")
                .telephone("12843723847324")
                .build();

        ownerWithOnePet = Owner.builder()
                .firstName("owner with one pet")
                .lastName("owner with one pet lastName")
                .address("asljnflksamfslkmf")
                .city("n1 city")
                .telephone("12843723847324")
                .pets(new HashSet<>(Arrays.asList(testPet)))
                .build();
    }

    @Test
    public void saveOwnerWithoutPets_ShouldSucceed() throws BadEntityException {
        getSaveServiceTest().saveEntity_ShouldSucceed(ownerWithoutPets);
    }

    @Test
    public void saveOwnerWithPet_ShouldSucceed() throws BadEntityException {
        getSaveServiceTest().saveEntity_ShouldSucceed(ownerWithOnePet);
    }

    @Test
    public void saveOwnerWithPersistedPet_ShouldSucceed() throws BadEntityException {
        Pet savedPet = petService.save(testPet);


        Owner owner = Owner.builder()
                .firstName("owner with one already persisted pet")
                .lastName("owner with one already persisted pet lastName")
                .address("asljnflksamfslkmf")
                .city("n1 city")
                .telephone("12843723847324")
                .pets(new HashSet<>(Arrays.asList(savedPet)))
                .build();
        getSaveServiceTest().saveEntity_ShouldSucceed(owner);
    }


    @Test
    public void updateOwner_ChangeTelephoneNumber_ShouldSucceed() throws BadEntityException, EntityNotFoundException, NoIdException, InvalidConfigurationModificationException {
        Owner diffTelephoneNumberUpdate = Owner.builder()
                .telephone(ownerWithoutPets.getTelephone()+"123")
                .build();
        getUpdateServiceTest().updateEntity_ShouldSucceed(ownerWithoutPets, diffTelephoneNumberUpdate,
                SuccessfulUpdateServiceTestConfiguration.<Owner, Long>builder()
                        .fullUpdate(false)
                        .postUpdateCallback(new PostUpdateServiceTestCallback<Owner, Long>() {
                            @Override
                            public void callback(Owner request, Owner afterUpdate) {
                                Assertions.assertEquals(request.getTelephone(),afterUpdate.getTelephone());
                            }
                        })
                        .build());
    }

    @Test
    public void updateOwner_addAnotherPet_shouldSucceed() throws BadEntityException, EntityNotFoundException, InvalidConfigurationModificationException, NoIdException {
        Pet savedPet = petService.save(testPet);
        String newPetName = "petToAdd";
        Pet newPet = Pet.builder()
                .name(newPetName)
                .petType(savedDogPetType)
                .birthDate(LocalDate.now())
                .build();
        Pet savedPetToAdd = petService.save(newPet);


        Owner owner = Owner.builder()
                .firstName("owner with one already persisted pet")
                .lastName("owner with one already persisted pet lastName")
                .address("asljnflksamfslkmf")
                .city("n1 city")
                .telephone("12843723847324")
                .pets(new HashSet<>(Arrays.asList(savedPet)))
                .build();

        Owner ownerUpdateRequest = new Owner();
        ownerUpdateRequest.getPets().addAll(owner.getPets());
        //here comes the new pet
        ownerUpdateRequest.getPets().add(savedPetToAdd);

        Owner updatedOwner = getUpdateServiceTest().updateEntity_ShouldSucceed(owner,ownerUpdateRequest,
                SuccessfulUpdateServiceTestConfiguration.<Owner, Long>builder()
                        .fullUpdate(false)
                        .postUpdateCallback((request, afterUpdate) -> {
                            Assertions.assertEquals(2,afterUpdate.getPets().size());
                            Assertions.assertEquals(1, afterUpdate.getPets().stream().filter(owner1 -> owner1.getName().equals(newPetName)).count());
                        })
                .build());
    }

    @Test
    public void findByLastName_shouldSucceed(){
        Owner savedOwner = repoSave(ownerWithOnePet);
        OwnerService ownerService = getCastedCrudService();
        Optional<Owner> byLastName = ownerService.findByLastName(ownerWithOnePet.getLastName());
        Assertions.assertTrue(byLastName.isPresent());
        Assertions.assertTrue(getDefaultEqualChecker().isEqual(savedOwner,byLastName.get()));
    }


}