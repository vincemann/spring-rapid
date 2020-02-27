package io.github.vincemann.demo.controllers.springAdapter;


import io.github.vincemann.demo.config.ServiceConfig;
import io.github.vincemann.demo.controllers.TestDataInitMvcControllerTest;
import io.github.vincemann.demo.dtos.owner.CreateOwnerDto;
import io.github.vincemann.demo.dtos.owner.UpdateOwnerDto;
import io.github.vincemann.demo.model.Owner;
import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.demo.service.OwnerService;
import io.github.vincemann.demo.service.PetService;
import io.github.vincemann.generic.crud.lib.config.TestConfig;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import java.util.Arrays;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles(value = {"test", "springdatajpa"})
@SpringJUnitWebConfig({TestConfig.class, ServiceConfig.class})
class OwnerMvcControllerTest
        extends TestDataInitMvcControllerTest<OwnerService,Owner> {

    @Autowired
    PetService petService;
    Pet pet1;
    Pet pet2;

    CreateOwnerDto validOwnerDtoWithoutPets;
    Owner validOwnerWithoutPets;
    CreateOwnerDto validOwnerDtoWithManyPets;
    Owner validOwnerWithManyPets;
    CreateOwnerDto invalidOwnerDto_becauseBlankCity;

    @MockBean
    OwnerService ownerServiceMock;

    @BeforeEach
    public void setup() throws Exception {
        super.setup();
        initTestData();
    }

    void initTestData() throws BadEntityException {
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
        performCreate(validOwnerDtoWithoutPets).andExpect(status().isOk());
        Mockito.verify(ownerServiceMock).save(map(validOwnerWithoutPets));
    }


    @Test
    public void deleteOwner_shouldSucceed() throws Exception {
        Owner saved = getTestService().save(validOwnerWithManyPets);
        performDelete(saved.getId()).andExpect(status().isOk());
        Mockito.verify(ownerServiceMock).deleteById(validOwnerWithManyPets.getId());
    }

    @Test
    public void findOwner_shouldSucceed() throws Exception {
        Owner saved = getTestService().save(validOwnerWithManyPets);
        performFind(saved.getId()).andExpect(status().isOk());
        Mockito.verify(ownerServiceMock).findById(validOwnerWithManyPets.getId());
    }

    @Test
    public void updateOwner_shouldSucceed() throws Exception {
        //given
        UpdateOwnerDto diffAddressUpdate = UpdateOwnerDto.builder()
                .address("other Street 12")
                .build();

        Long id = 42L;
        Owner savedOwner = (Owner) BeanUtilsBean.getInstance().cloneBean(validOwnerWithManyPets);
        savedOwner.setId(id);
        Owner modOwner = (Owner) BeanUtilsBean.getInstance().cloneBean(savedOwner);
        modOwner.setAddress(diffAddressUpdate.getAddress());
        diffAddressUpdate.setId(savedOwner.getId());

        Mockito.when(ownerServiceMock.save(validOwnerWithManyPets))
                .thenReturn(savedOwner);
        Mockito.when(ownerServiceMock.update(any(),any()))
                .thenReturn(modOwner);
        getTestService().save(validOwnerWithManyPets);


        //when
        performPartialUpdate(diffAddressUpdate)
                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(ownerServiceMock).save(any());
        Mockito.verify(ownerServiceMock).update(map(diffAddressUpdate),false);
        Mockito.verifyNoMoreInteractions(ownerServiceMock);
    }


    @Test
    public void updateInvalidOwner__shouldFail() throws Exception {
        UpdateOwnerDto blankCityUpdate = UpdateOwnerDto.builder()
                //blank city
                .city("")
                .build();
//        getUpdateTemplate().updateEntity_ShouldFail(validOwnerWithoutPets, blankCityUpdate,
//                partialUpdate(),
//                postUpdateCallback((PostUpdateControllerTestCallback<Owner, Long>)
//
//                        updated -> {
//                            Assertions.assertFalse(updated.getCity().isEmpty());
//                            Assertions.assertEquals(validOwnerWithoutPets.getCity(), updated.getCity());
//                        })
//        );
    }
}
