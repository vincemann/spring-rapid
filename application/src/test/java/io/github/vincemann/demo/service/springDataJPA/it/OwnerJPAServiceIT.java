package io.github.vincemann.demo.service.springDataJPA.it;

import io.github.vincemann.demo.model.Owner;
import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.demo.model.PetType;
import io.github.vincemann.demo.repositories.OwnerRepository;
import io.github.vincemann.demo.repositories.PetRepository;
import io.github.vincemann.demo.service.PetTypeService;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.postUpdateCallback.PostUpdateCallback;
import io.github.vincemann.generic.crud.lib.test.service.ForceEagerFetch_CrudServiceIntegrationTest;
import io.github.vincemann.generic.crud.lib.test.service.testApi.UpdateServiceTestApi;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
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
            <
                                        Owner,
                                        Long,
                                        OwnerRepository
            >
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
    public void injectCrudService(CrudService<Owner, Long, OwnerRepository> crudService) {
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
        getSaveServiceTestApi().saveEntity_ShouldSucceed(ownerWithoutPets);
    }

    @Test
    public void saveOwnerWithPet_ShouldSucceed() throws BadEntityException {
        getSaveServiceTestApi().saveEntity_ShouldSucceed(ownerWithOnePet);
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
        getSaveServiceTestApi().saveEntity_ShouldSucceed(owner);
    }


    @Test
    public void updateOwner_ChangeTelephoneNumber_ShouldSucceed() throws BadEntityException, EntityNotFoundException, NoIdException {
        Owner diffTelephoneNumberUpdate = Owner.builder()
                .telephone(ownerWithoutPets.getTelephone()+"123")
                .build();
        getUpdateServiceTestApi().updateEntity_ShouldSucceed(ownerWithoutPets, diffTelephoneNumberUpdate,
                /*UpdateServiceTestApi.SuccessfulTestContext.partialUpdateContextBuilder()
                .postUpdateCallback(new PostUpdateCallback<Owner,Long>() {
                    @Override
                    public void callback(Owner request, Owner afterUpdate) {

                    }
                }).build());*/
                new UpdateServiceTestApi<Owner,Long,OwnerRepository>.SuccessfulTestContext(new PostUpdateCallback<Owner, Long>() {
                    @Override
                    public void callback(Owner request, Owner afterUpdate) {

                    }
                }),getUpdateServiceTestApi().getDefaultSuccessfulContext());
    }

    @Test
    public void updateOwner_addAnotherPet_shouldSucceed() throws BadEntityException, NoIdException, EntityNotFoundException {
        Pet savedPet = petService.save(testPet);
        Pet petToAdd = Pet.builder()
                .name("petToAdd")
                .petType(savedDogPetType)
                .birthDate(LocalDate.now())
                .build();
        Pet savedPetToAdd = petService.save(petToAdd);


        Owner owner = Owner.builder()
                .firstName("owner with one already persisted pet")
                .lastName("owner with one already persisted pet lastName")
                .address("asljnflksamfslkmf")
                .city("n1 city")
                .telephone("12843723847324")
                .pets(new HashSet<>(Arrays.asList(savedPet)))
                .build();
        Owner savedOwner = repoSave(owner);
        savedOwner.getPets().add(savedPetToAdd);

        Owner updatedOwner = updateEntity_ShouldSucceed(savedOwner,false);
        Assertions.assertTrue(updatedOwner.getPets().contains(savedPetToAdd));
    }

    @Test
    public void findByLastName_shouldSucceed() throws BadEntityException {
        Owner savedOwner = saveEntity_ShouldSucceed(ownerWithOnePet);
        Optional<Owner> byLastName = getCastedCrudService().findByLastName(ownerWithOnePet.getLastName());
        Assertions.assertTrue(byLastName.isPresent());
        Assertions.assertTrue(getEqualChecker().isEqual(savedOwner,byLastName.get()));
    }


}