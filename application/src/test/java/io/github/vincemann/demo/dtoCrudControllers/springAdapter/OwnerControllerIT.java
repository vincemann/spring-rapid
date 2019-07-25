package io.github.vincemann.demo.dtoCrudControllers.springAdapter;


import io.github.vincemann.demo.dtoCrudControllers.EntityInitializerControllerIT;
import io.github.vincemann.demo.dtoCrudControllers.OwnerController;
import io.github.vincemann.demo.dtos.OwnerDto;
import io.github.vincemann.demo.model.Owner;
import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.demo.service.OwnerService;
import io.github.vincemann.demo.service.PetService;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.TestEntityBundle;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.UpdateTestBundle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment =
        SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = {"test", "springdatajpa"})
class OwnerControllerIT extends EntityInitializerControllerIT<Owner, OwnerDto, OwnerService, OwnerController> {

    @Autowired
    private PetService petService;
    private Pet pet1;
    private Pet pet2;

    OwnerControllerIT(@Autowired OwnerController crudController) {
        super(crudController);
    }

    @BeforeEach
    @Override
    public void before() throws Exception {
        this.pet1 = petService.save(Pet.builder().name("pet1").petType(getTestPetType()).build());
        this.pet2 = petService.save(Pet.builder().name("pet2").petType(getTestPetType()).build());
        super.before();
    }

    @Override
    protected List<TestEntityBundle<OwnerDto>> provideValidTestDTOs() {
        //OwnerDto without pets
        OwnerDto ownerWithoutPets = OwnerDto.builder()
                .firstName("Max")
                .lastName("Müller")
                .address("other Street 13")
                .city("munich")
                .build();
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

        //OwnerDto with single persisted pet
        OwnerDto ownerWithPersistedPet = OwnerDto.builder()
                .firstName("Hans")
                .lastName("Müller")
                .address("mega nice Street 42")
                .city("Berlin")
                .petIds(Collections.singleton(getTestPet().getId()))
                .build();
        OwnerDto deletedPetUpdate = OwnerDto.builder()
                .firstName("Hans")
                .lastName("Müller")
                .address("mega nice Street 42")
                .city("Berlinasdasd")
                .petIds(Collections.EMPTY_SET)
                .build();

        //OwnerDto with multiple persisted pets
        OwnerDto ownerWithManyPets = OwnerDto.builder()
                .firstName("Max")
                .lastName("Müller")
                .address("Andere Street 13")
                .city("München")
                .petIds(new HashSet<>(Arrays.asList(pet1.getId(), pet2.getId())))
                .build();


        return Arrays.asList(

                new TestEntityBundle<>(ownerWithoutPets, diffStreetUpdate, diffLastNameUpdate),
                new TestEntityBundle<>(ownerWithPersistedPet, deletedPetUpdate),
                //OwnerDto with many Pets (no update test)
                new TestEntityBundle<>(ownerWithManyPets)
        );
    }

    @Override
    protected List<OwnerDto> provideInvalidTestDTOs() {
        return Arrays.asList(
                OwnerDto.builder()
                        .firstName("Hans")
                        .lastName("meier")
                        .address("MegaNiceStreet 5")
                        //blank city
                        .city("")
                        .build()
        );
    }

    @Override
    protected List<TestEntityBundle<OwnerDto>> provideInvalidUpdateDtoBundles() {
        //OwnerDto without pets
        OwnerDto ownerWithoutPets = OwnerDto.builder()
                .firstName("Max")
                .lastName("Müller")
                .address("other Street 13")
                .city("munich")
                .build();

        //setting of invalid pet(-id) should not be possible
        OwnerDto addInvalidPetUpdate = OwnerDto.builder()
                .firstName("Max")
                .lastName("Müller")
                .address("other Street 13")
                .city("munich")
                .petIds(Collections.singleton(-1L))
                .build();

        return Arrays.asList(
                new TestEntityBundle<>(
                        ownerWithoutPets,
                        //update owner without pets, by adding a nonexisting pet -> should fail
                        //after the update test assert that the saved owner indeed has no pets
                        new UpdateTestBundle<>(addInvalidPetUpdate, this::assertOwnerDoesNotHavePets)
                )
        );
    }

    private void assertOwnerDoesNotHavePets(OwnerDto ownerAfterUpdate) {
        try {
            Optional<Owner> ownerOptional = getOwnerController().getCrudService().findById(ownerAfterUpdate.getId());
            Assertions.assertTrue(ownerOptional.get().getPets().isEmpty());
        } catch (NoIdException e) {
            throw new RuntimeException(e);
        }

    }
}
