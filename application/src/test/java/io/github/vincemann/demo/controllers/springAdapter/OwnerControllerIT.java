package io.github.vincemann.demo.controllers.springAdapter;


import io.github.vincemann.demo.controllers.EntityInitializer_ControllerIT;
import io.github.vincemann.demo.controllers.OwnerController;
import io.github.vincemann.demo.dtos.OwnerDto;
import io.github.vincemann.demo.model.Owner;
import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.demo.repositories.OwnerRepository;
import io.github.vincemann.demo.service.OwnerService;
import io.github.vincemann.demo.service.PetService;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.plugins.CheckIfDbDeletedPlugin;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.plugins.ServiceDeepEqualPlugin;
import io.github.vincemann.generic.crud.lib.test.deepEqualChecker.EqualChecker;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.PostIntegrationTestCallbackIdBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.controller.create.FailedCreateIntegrationTestBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.controller.create.SuccessfulCreateIntegrationTestBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.controller.delete.DeleteIntegrationTestBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.controller.find.SuccessfulFindIntegrationTestBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.controller.update.FailedUpdateIntegrationTestBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.controller.update.SuccessfulUpdateIntegrationTestBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.controller.update.updateIteration.FailedUpdateTestEntityBundleIteration;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.TestRequestEntity_Factory;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment =
        SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles(value = {"test", "springdatajpa"})
class OwnerControllerIT
        extends EntityInitializer_ControllerIT<Owner, OwnerDto, OwnerRepository, OwnerService, OwnerController> {

    @Autowired
    private PetService petService;
    private Pet pet1;
    private Pet pet2;

    private OwnerDto validOwnerDtoWithoutPets;
    private Owner validOwnerWithoutPets;
    private OwnerDto validOwnerDtoWithManyPets;
    private Owner validOwnerWithManyPets;

    private OwnerDto invalidOwnerDto_becauseBlankCity;


    OwnerControllerIT(@Autowired OwnerController crudController,
                      @Autowired TestRequestEntity_Factory requestEntityFactory,
                      @Autowired CheckIfDbDeletedPlugin checkIfDbDeletedPlugin,
                      @Autowired ServiceDeepEqualPlugin serviceDeepEqualPlugin) {
        super(
                crudController,
                requestEntityFactory,
                checkIfDbDeletedPlugin,
                serviceDeepEqualPlugin
        );
    }

    @Override
    @BeforeEach
    public void before() {
        super.before();
        addPreTestRunnable(() -> {
            try {
                this.pet1 = petService.save(Pet.builder().name("pet1").petType(getTestPetType()).build());
                this.pet2 = petService.save(Pet.builder().name("pet2").petType(getTestPetType()).build());

                validOwnerDtoWithoutPets = OwnerDto.builder()
                        .firstName("Max")
                        .lastName("Müller")
                        .address("other Street 13")
                        .city("munich")
                        .build();
                validOwnerWithoutPets = Owner.builder()
                        .firstName("Max")
                        .lastName("Müller")
                        .address("other Street 13")
                        .city("munich")
                        .build();


                validOwnerDtoWithManyPets = OwnerDto.builder()
                        .firstName("Max")
                        .lastName("Müller")
                        .address("Andere Street 13")
                        .city("München")
                        .petIds(new HashSet<>(Arrays.asList(pet1.getId(), pet2.getId())))
                        .build();
                validOwnerWithManyPets = Owner.builder()
                        .firstName("Max")
                        .lastName("Müller")
                        .address("Andere Street 13")
                        .city("München")
                        .pets(new HashSet<>(Arrays.asList(pet1, pet2)))
                        .build();


                invalidOwnerDto_becauseBlankCity = OwnerDto.builder()
                        .firstName("Hans")
                        .lastName("meier")
                        .address("MegaNiceStreet 5")
                        //blank city
                        .city("")
                        .build();
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    @Transactional
    public void createOwnerWithoutPets_shouldSucceed() throws Exception {
        runPreTestRunnables();
        createEntity_ShouldSucceed(validOwnerDtoWithoutPets);
    }

    @Test
    @Transactional
    public void createOwnerWithPets_shouldSucceed() throws Exception {
        runPreTestRunnables();
        createEntity_ShouldSucceed(validOwnerDtoWithManyPets);
    }

    @Test
    @Transactional
    public void deleteOwner_shouldSucceed() throws Exception {
        runPreTestRunnables();
        OwnerDto savedOwner = createEntity_ShouldSucceed(validOwnerDtoWithManyPets);
        deleteEntity_ShouldSucceed(savedOwner.getId());
    }

    @Transactional
    @Test
    public void findOwner_shouldSucceed() throws Exception {
        runPreTestRunnables();
        OwnerDto savedOwner = createEntity_ShouldSucceed(validOwnerDtoWithManyPets);
        findEntity_ShouldSucceed(savedOwner.getId());
    }

    @Test
    @Transactional
    public void updateOwnerWithDifferentAddress_ShouldSucceed() throws Exception {
        //given
        OwnerDto diffStreetUpdate = OwnerDto.builder()
                .firstName("Max")
                .lastName("Müller")
                .address("other Street 12")
                .city("munich")
                .build();

        OwnerDto savedOwner = createEntity_ShouldSucceed(validOwnerDtoWithManyPets);
        Assertions.assertNotEquals(diffStreetUpdate.getAddress(),savedOwner.getAddress());
        diffStreetUpdate.setId(savedOwner.getId());

        //when
        updateEntity_ShouldSucceed(diffStreetUpdate, new EqualChecker<Owner>() {
            @Override
            public boolean isEqual(Owner object1, Owner object2) {
                return object1.getAddress().equals(object2.getAddress());
            }
        });
    }

    @Transactional
    @Test
    public void updateOwnerWithManyPets_RemovePets_ShouldSucceed() throws Exception {
        //given
        OwnerDto deleteAllPetsUpdate = OwnerDto.builder()
                .firstName("Hans")
                .lastName("Müller")
                .address("mega nice Street 42")
                .city("Berlinasdasd")
                .petIds(Collections.EMPTY_SET)
                .build();
        OwnerDto savedOwner = createEntity_ShouldSucceed(validOwnerDtoWithManyPets);

        //when
        updateEntity_ShouldSucceed(deleteAllPetsUpdate, new EqualChecker<Owner>() {
            @Override
            public boolean isEqual(Owner object1, Owner object2) {
                return object1.getPets().size()==object2.getPets().size()
                        &&
                        object1.getPets().size()==0;
            }
        });

    }


    @Test
    @Transactional
    public void createOwnerWithBlankCity_ShouldFail() throws Exception {
        createEntity_ShouldFail(invalidOwnerDto_becauseBlankCity);
    }




    @Override
    protected List<FailedUpdateIntegrationTestBundle<Owner, OwnerDto,Long>> provideFailedUpdateTestBundles() {
        //setting of invalid pet(-id) should not be possible
        OwnerDto addInvalidPetUpdate = OwnerDto.builder()
                .firstName("Max")
                .lastName("Müller")
                .address("other Street 13")
                .city("munich")
                .petIds(Collections.singleton(-1L))
                .build();

        OwnerDto blankCityUpdate = OwnerDto.builder()
                .firstName("Hans")
                .lastName("meier")
                .address("MegaNiceStreet 5")
                //blank city
                .city("")
                .build();
        return Arrays.asList(
                FailedUpdateIntegrationTestBundle.<Owner, OwnerDto,Long>builder()
                        .entityToUpdate(validOwnerWithoutPets)
                        .updateTestEntityBundleIterations(
                                Arrays.asList(
                                        FailedUpdateTestEntityBundleIteration.<OwnerDto,Long>builder()
                                                .entity(addInvalidPetUpdate)
                                                .postTestCallback(this::assertOwnerDoesNotHavePets)
                                                .build()
                                )
                        )
                        .build(),
                new FailedUpdateIntegrationTestBundle(validOwnerWithoutPets, blankCityUpdate)
        );
    }


    private void assertOwnerDoesNotHavePets(PostIntegrationTestCallbackIdBundle<Long> callbackIdBundle) {
        try {
            Optional<Owner> ownerOptional = getOwnerController().getCrudService().findById(callbackIdBundle.getId());
            Assertions.assertTrue(ownerOptional.get().getPets().isEmpty());
        } catch (NoIdException e) {
            throw new RuntimeException(e);
        }

    }
}
