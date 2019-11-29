package io.github.vincemann.demo.controllers.springAdapter;


import io.github.vincemann.demo.controllers.EntityInitializer_ControllerIT;
import io.github.vincemann.demo.controllers.OwnerController;
import io.github.vincemann.demo.dtos.OwnerDto;
import io.github.vincemann.demo.model.Owner;
import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.demo.repositories.OwnerRepository;
import io.github.vincemann.demo.service.OwnerService;
import io.github.vincemann.demo.service.PetService;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.plugins.DatabaseDeletedCheck_Plugin;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.plugins.ServiceDeepEqualPlugin;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.postUpdateCallback.PostUpdateCallback;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.TestRequestEntity_Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

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
                      @Autowired DatabaseDeletedCheck_Plugin databaseDeletedCheckPlugin,
                      @Autowired ServiceDeepEqualPlugin serviceDeepEqualPlugin) {
        super(
                crudController,
                requestEntityFactory,
                databaseDeletedCheckPlugin,
                serviceDeepEqualPlugin
        );
    }

    @Override
    public void beforeEachTest() throws Exception {
        super.beforeEachTest();
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
    }

    @Test
    public void createOwnerWithoutPets_shouldSucceed() throws Exception {
        createEntity_ShouldSucceed(validOwnerDtoWithoutPets);
    }

    @Test
    public void createOwnerWithPets_shouldSucceed() throws Exception {
        createEntity_ShouldSucceed(validOwnerDtoWithManyPets);
    }

    @Test
    public void deleteOwner_shouldSucceed() throws Exception {
        OwnerDto savedOwner = createEntity_ShouldSucceed(validOwnerDtoWithManyPets);
        deleteEntity_ShouldSucceed(savedOwner.getId());
    }

    @Test
    public void findOwner_shouldSucceed() throws Exception {
        OwnerDto savedOwner = createEntity_ShouldSucceed(validOwnerDtoWithManyPets);
        findEntity_ShouldSucceed(savedOwner.getId());
    }

    @Test
    public void updateOwnerWithDifferentAddress_ShouldSucceed() throws Exception {
        //given
        OwnerDto diffAddressUpdate = OwnerDto.builder()
                .firstName("Max")
                .lastName("Müller")
                .address("other Street 12")
                .city("munich")
                .build();

        Assertions.assertNotEquals(diffAddressUpdate.getAddress(),validOwnerWithManyPets.getAddress());
        //when
        updateEntity_ShouldSucceed(validOwnerWithManyPets,diffAddressUpdate, new PostUpdateCallback<Owner>() {
            @Override
            public void callback(Owner entityToUpdate, Owner updatedEntity) {
                Assertions.assertEquals(updatedEntity.getAddress(),diffAddressUpdate.getAddress());
            }
        });
    }

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

        //when
        updateEntity_ShouldSucceed(validOwnerWithManyPets,deleteAllPetsUpdate, new PostUpdateCallback<Owner>() {
            @Override
            public void callback(Owner entityToUpdate, Owner updatedEntity) {
                Assertions.assertTrue(updatedEntity.getPets().isEmpty());
            }
        });

    }


    @Test
    public void createOwnerWithBlankCity_ShouldFail() throws Exception {
        createEntity_ShouldFail(invalidOwnerDto_becauseBlankCity);
    }


    @Test
    public void updateOwner_SetInvalidPet_ShouldFail() throws Exception {
        //given
        OwnerDto setInvalidPetUpdate = OwnerDto.builder()
                .firstName("Max")
                .lastName("Müller")
                .address("other Street 13")
                .city("munich")
                .petIds(Collections.singleton(-1L))
                .build();
        
        //when
        updateEntity_ShouldFail(validOwnerWithoutPets,setInvalidPetUpdate, new PostUpdateCallback<Owner>() {
            @Override
            public void callback(Owner entityToUpdate, Owner updatedEntity) {
                 Assertions.assertTrue(updatedEntity.getPets().isEmpty());
            }
        });
    }

    @Test
    public void updateOwner_SetBlankCity_ShouldFail() throws Exception {
        OwnerDto blankCityUpdate = OwnerDto.builder()
                .firstName("Hans")
                .lastName("meier")
                .address("MegaNiceStreet 5")
                //blank city
                .city("")
                .build();
        updateEntity_ShouldFail(validOwnerWithoutPets,blankCityUpdate, new PostUpdateCallback<Owner>() {
            @Override
            public void callback(Owner entityToUpdate, Owner updatedEntity) {
                Assertions.assertFalse(updatedEntity.getCity().isEmpty());
                Assertions.assertEquals(validOwnerWithoutPets.getCity(),updatedEntity.getCity());
            }
        });
    }
}
