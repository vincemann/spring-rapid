package io.github.vincemann.demo.controllers.springAdapter;


import io.github.vincemann.demo.controllers.EntityInitializerControllerIT;
import io.github.vincemann.demo.controllers.OwnerController;
import io.github.vincemann.demo.dtos.OwnerDto;
import io.github.vincemann.demo.model.Owner;
import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.demo.service.OwnerService;
import io.github.vincemann.demo.service.PetService;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.plugins.CheckIfDbDeletedPlugin;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.plugins.ServiceDeepEqualPlugin;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.PostIntegrationTestCallbackIdBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.controller.create.FailedCreateIntegrationTestBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.controller.create.SuccessfulCreateIntegrationTestBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.controller.delete.DeleteIntegrationTestBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.controller.find.SuccessfulFindIntegrationTestBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.controller.update.FailedUpdateIntegrationTestBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.controller.update.SuccessfulUpdateIntegrationTestBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.controller.update.updateIteration.FailedUpdateTestEntityBundleIteration;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.TestRequestEntityFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment =
        SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles(value = {"test", "springdatajpa"})
class OwnerControllerIT extends EntityInitializerControllerIT<Owner, OwnerDto, OwnerService, OwnerController> {

    @Autowired
    private PetService petService;
    private Pet pet1;
    private Pet pet2;

    private OwnerDto validOwnerDtoWithoutPets;
    private Owner validOwnerWithoutPets;
    private OwnerDto validOwnerDtoWithOnePet;
    private Owner validOwnerWithOnePet;
    private OwnerDto validOwnerDtoWithManyPets;
    private Owner validOwnerWithManyPets;

    private OwnerDto invalidOwnerDto_becauseBlankCity;


    OwnerControllerIT(@Autowired OwnerController crudController,
                      @Autowired PlatformTransactionManager platformTransactionManager,
                      @Autowired TestRequestEntityFactory requestEntityFactory,
                      @Autowired CheckIfDbDeletedPlugin checkIfDbDeletedPlugin,
                      @Autowired ServiceDeepEqualPlugin serviceDeepEqualPlugin) {
        super(
                crudController,
                requestEntityFactory,
                platformTransactionManager,
                checkIfDbDeletedPlugin,
                serviceDeepEqualPlugin
        );
    }


    @Override
    protected void onBeforeProvideEntityBundles() throws Exception {
        super.onBeforeProvideEntityBundles();
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


        validOwnerDtoWithOnePet = OwnerDto.builder()
                .firstName("Hans")
                .lastName("Müller")
                .address("mega nice Street 42")
                .city("Berlin")
                .petIds(Collections.singleton(getTestPet().getId()))
                .build();
        validOwnerWithOnePet = Owner.builder()
                .firstName("Hans")
                .lastName("Müller")
                .address("mega nice Street 42")
                .city("Berlin")
                .pets(Collections.singleton(getTestPet()))
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
    }

    @Override
    protected List<SuccessfulCreateIntegrationTestBundle<OwnerDto>> provideSuccessfulCreateTestEntityBundles() {
        return Arrays.asList(
                new SuccessfulCreateIntegrationTestBundle<>(validOwnerDtoWithoutPets),
                new SuccessfulCreateIntegrationTestBundle<>(validOwnerDtoWithOnePet),
                new SuccessfulCreateIntegrationTestBundle<>(validOwnerDtoWithManyPets)

        );
    }

    @Override
    protected List<DeleteIntegrationTestBundle<Owner,Long>> provideSuccessfulDeleteTestEntityBundles() {
        return Arrays.asList(
                new DeleteIntegrationTestBundle<Owner,Long>(validOwnerWithManyPets),
                new DeleteIntegrationTestBundle<Owner,Long>(validOwnerWithoutPets),
                new DeleteIntegrationTestBundle<Owner,Long>(validOwnerWithOnePet)
        );
    }

    @Override
    public List<SuccessfulFindIntegrationTestBundle<OwnerDto, Owner>> provideSuccessfulFindTestEntityBundles() {
        return Arrays.asList(
                new SuccessfulFindIntegrationTestBundle<>(validOwnerWithoutPets),
                new SuccessfulFindIntegrationTestBundle<>(validOwnerWithOnePet),
                new SuccessfulFindIntegrationTestBundle<>(validOwnerWithManyPets)

        );
    }

    @Override
    protected List<SuccessfulUpdateIntegrationTestBundle<Owner, OwnerDto>> provideSuccessfulUpdateTestEntityBundles() {
        OwnerDto diffStreetUpdate = OwnerDto.builder()
                .firstName("Max")
                .lastName("Müller")
                .address("other Street 12")
                .city("munich")
                .build();
        OwnerDto diffLastNameUpdate = OwnerDto.builder()
                .firstName("MODIFIED")
                .lastName("Müller")
                .address("other Street 13")
                .city("munich")
                .build();


        OwnerDto deleteAllPetsUpdate = OwnerDto.builder()
                .firstName("Hans")
                .lastName("Müller")
                .address("mega nice Street 42")
                .city("Berlinasdasd")
                .petIds(Collections.EMPTY_SET)
                .build();

        return Arrays.asList(
                new SuccessfulUpdateIntegrationTestBundle<>(validOwnerWithoutPets, diffLastNameUpdate, diffStreetUpdate),
                new SuccessfulUpdateIntegrationTestBundle<>(validOwnerWithOnePet, deleteAllPetsUpdate),
                new SuccessfulUpdateIntegrationTestBundle<>(validOwnerWithManyPets, deleteAllPetsUpdate)
        );
    }

    @Override
    protected List<FailedCreateIntegrationTestBundle<OwnerDto,Long>> provideFailingCreateTestBundles() {
        return Arrays.asList(
                new FailedCreateIntegrationTestBundle<>(invalidOwnerDto_becauseBlankCity)
        );
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
