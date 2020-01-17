package io.github.vincemann.demo.controllers.springAdapter;


import io.github.vincemann.demo.controllers.EntityInitializerControllerIntegrationTest;
import io.github.vincemann.demo.dtos.owner.CreateOwnerDto;
import io.github.vincemann.demo.dtos.owner.UpdateOwnerDto;
import io.github.vincemann.demo.model.Owner;
import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.demo.service.PetService;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.UpdateControllerTestConfiguration;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static io.github.vincemann.generic.crud.lib.test.forceEagerFetch.proxy.abs.HibernateForceEagerFetchProxy.EAGER_FETCH_PROXY;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment =
        SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles(value = {"test", "springdatajpa"})
class OwnerControllerIT
        extends EntityInitializerControllerIntegrationTest<Owner> {

    @Autowired
    private PetService petService;
    private Pet pet1;
    private Pet pet2;

    private CreateOwnerDto validOwnerDtoWithoutPets;
    private Owner validOwnerWithoutPets;
    private CreateOwnerDto validOwnerDtoWithManyPets;
    private Owner validOwnerWithManyPets;
    private CreateOwnerDto invalidOwnerDto_becauseBlankCity;

    //uses eagerly fetching service proxy
    @Autowired
    @Qualifier(EAGER_FETCH_PROXY)
    @Override
    public void setTestService(CrudService<Owner, Long, ? extends CrudRepository<Owner, Long>> testService) {
        super.setTestService(testService);
    }


    @BeforeEach
    @Override
    public void setup() throws Exception {
        super.setup();
        this.pet1 = petService.save(Pet.builder().name("pet1").petType(getTestPetType()).build());
        this.pet2 = petService.save(Pet.builder().name("pet2").petType(getTestPetType()).build());

        validOwnerDtoWithoutPets = CreateOwnerDto.builder()
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


        validOwnerDtoWithManyPets = CreateOwnerDto.builder()
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


        invalidOwnerDto_becauseBlankCity = CreateOwnerDto.builder()
                .firstName("Hans")
                .lastName("meier")
                .address("MegaNiceStreet 5")
                //blank city
                .city("")
                .build();
    }

    @Test
    public void createOwnerWithoutPets_shouldSucceed() throws Exception {
        getCreateControllerTest().createEntity_ShouldSucceed(validOwnerDtoWithoutPets);
    }

    @Test
    public void createOwnerWithPets_shouldSucceed() throws Exception {
        getCreateControllerTest().createEntity_ShouldSucceed(validOwnerDtoWithManyPets);
    }

    @Test
    public void deleteOwner_shouldSucceed() throws Exception {
        Owner savedOwner = saveServiceEntity(validOwnerWithManyPets);
        getDeleteControllerTest().deleteEntity_ShouldSucceed(savedOwner.getId());
    }

    @Test
    public void findOwner_shouldSucceed() throws Exception {
        Owner savedOwner = saveServiceEntity(validOwnerWithManyPets);
        getFindControllerTest().findEntity_ShouldSucceed(savedOwner.getId());
    }

    @Test
    public void updateOwnerWithDifferentAddress_ShouldSucceed() throws Exception {
        //given
        UpdateOwnerDto diffAddressUpdate = UpdateOwnerDto.builder()
                .address("other Street 12")
                .build();

        Assertions.assertNotEquals(diffAddressUpdate.getAddress(), validOwnerWithManyPets.getAddress());
        //when
        getUpdateControllerTest().updateEntity_ShouldSucceed(validOwnerWithManyPets, diffAddressUpdate,
                UpdateControllerTestConfiguration.<Owner, Long>Builder()
                        .fullUpdate(false)
                        .postUpdateCallback(afterUpdate -> Assertions.assertEquals(diffAddressUpdate.getAddress(), afterUpdate.getAddress()))
                        .build()
        );
    }

    @Test
    public void updateOwnerWithManyPets_RemovePets_ShouldSucceed() throws Exception {
        //given
        UpdateOwnerDto deleteAllPetsUpdate = UpdateOwnerDto.builder()
                .petIds(Collections.EMPTY_SET)
                .build();

        //when
        getUpdateControllerTest().updateEntity_ShouldSucceed(validOwnerWithManyPets, deleteAllPetsUpdate,
                UpdateControllerTestConfiguration.<Owner, Long>Builder()
                        .fullUpdate(false)
                        .postUpdateCallback((updated) -> updated.getPets().isEmpty())
                        .build()
        );

    }


    @Test
    public void createOwnerWithBlankCity_ShouldFail() throws Exception {
        getCreateControllerTest().createEntity_ShouldFail(invalidOwnerDto_becauseBlankCity);
    }


    @Test
    public void updateOwner_SetInvalidPet_ShouldFail() throws Exception {
        //given
        UpdateOwnerDto setInvalidPetUpdate = UpdateOwnerDto.builder()
                .petIds(Collections.singleton(-1L))
                .build();

        //when
        getUpdateControllerTest().updateEntity_ShouldFail(validOwnerWithoutPets, setInvalidPetUpdate, UpdateControllerTestConfiguration.<Owner, Long>Builder()
                .fullUpdate(false)
                .postUpdateCallback((updated) -> updated.getPets().isEmpty())
                .build()
        );
    }

    @Test
    public void updateOwner_SetBlankCity_ShouldFail() throws Exception {
        UpdateOwnerDto blankCityUpdate = UpdateOwnerDto.builder()
                //blank city
                .city("")
                .build();
        getUpdateControllerTest().updateEntity_ShouldFail(validOwnerWithoutPets, blankCityUpdate, UpdateControllerTestConfiguration.<Owner, Long>Builder()
                .fullUpdate(false)
                .postUpdateCallback((updated) -> {
                    Assertions.assertFalse(updated.getCity().isEmpty());
                    Assertions.assertEquals(validOwnerWithoutPets.getCity(), updated.getCity());
                }).build()
        );
    }
}
