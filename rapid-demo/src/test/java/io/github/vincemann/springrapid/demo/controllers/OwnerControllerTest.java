package io.github.vincemann.springrapid.demo.controllers;


import io.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import io.github.vincemann.springrapid.core.util.ResourceUtils;
import io.github.vincemann.springrapid.coretest.controller.rapid.AbstractUrlParamIdRapidControllerTest;
import io.github.vincemann.springrapid.demo.dtos.owner.CreateOwnerDto;
import io.github.vincemann.springrapid.demo.dtos.owner.ReadOwnerDto;
import io.github.vincemann.springrapid.demo.model.Owner;
import io.github.vincemann.springrapid.demo.model.Pet;
import io.github.vincemann.springrapid.demo.service.OwnerService;
import io.github.vincemann.springrapid.demo.service.PetService;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class OwnerControllerTest
        extends AbstractUrlParamIdRapidControllerTest<OwnerService, Owner, Long> {

    CreateOwnerDto createOwnerDto;
    ReadOwnerDto readOwnerDto;
    Owner owner;

    @MockBean
    OwnerService ownerService;
    @MockBean
    PetService petService;

    @MockBean
    CrudServiceLocator crudServiceLocator;

    String addressPatch;
    String blankCityPatch;
    String addPetPatch;

    @Value("classpath:/update-owner/patch-address.json")
    public void setAddressPatch(Resource patch) throws IOException {
        this.addressPatch = ResourceUtils.toStr(patch);
    }

    @Value("classpath:/update-owner/patch-add-pet.json")
    public void setPetPatch(Resource patch) throws IOException {
        this.addPetPatch = ResourceUtils.toStr(patch);
    }

    @Value("classpath:/update-owner/patch-blank-city.json")
    public void setBlankCityPatch(Resource patch) throws IOException {
        this.blankCityPatch = ResourceUtils.toStr(patch);
    }

    @BeforeEach
    public void setup() throws Exception {
        super.setup();
        Long id = 42L;
        String address = "Other Street 13";
        String firstName = "Max";
        String lastName = "Müller";
        String city = "Munich";
        String telephone = "0176546231";

        createOwnerDto = CreateOwnerDto.builder()
                .firstName(firstName)
                .lastName(lastName)
                .address(address)
                .city(city)
                .telephone(telephone)
                .build();

        readOwnerDto = ReadOwnerDto.builder()
                .address(address)
                .city(city)
                .telephone(telephone)
                .petIds(new HashSet<>())
                .build();
        readOwnerDto.setId(id);

        owner = Owner.builder()
                .firstName(firstName)
                .lastName(lastName)
                .address(address)
                .telephone(telephone)
                .pets(new HashSet<>())
                .city(city)
                .build();
        owner.setId(id);
    }

    @Test
    public void create_shouldSucceed() throws Exception {
        String readOwnerDtoJson = serialize(readOwnerDto);
        when(ownerService.save(refEq(owner, "id"))).thenReturn(owner);

        getMockMvc().perform(create(createOwnerDto))
                .andExpect(status().isOk())
                .andExpect(content().json(readOwnerDtoJson));

        Mockito.verify(ownerService).save(refEq(owner, "id"));
    }


    @Test
    public void delete_shouldSucceed() throws Exception {
        getMockMvc().perform(delete(owner.getId()))
                .andExpect(status().isOk());
        Mockito.verify(ownerService).deleteById(owner.getId());
    }


    @Test
    public void findById_shouldSucceed() throws Exception {
        when(ownerService.findById(owner.getId())).thenReturn(Optional.of(owner));
        String readDtoJson = serialize(readOwnerDto);

        getMockMvc().perform(find(owner.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(readDtoJson));
        Mockito.verify(ownerService).findById(owner.getId());
    }

    @Test
    public void update_address_shouldSucceed() throws Exception {
        //given
        String updatedAddress = "other Street 12";

        Owner ownerPatch = (Owner) BeanUtilsBean.getInstance().cloneBean(owner);
        ownerPatch.setAddress(updatedAddress);

        when(ownerService.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(ownerService.update(any(Owner.class), eq(true))).thenReturn(ownerPatch);

        //when
        getMockMvc().perform(update(addressPatch, owner.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value(updatedAddress));

        Mockito.verify(ownerService).update(refEq(ownerPatch), anyBoolean());

    }


    @Test
    public void update_withBlankCity_shouldFail_with422() throws Exception {
        Owner ownerPatch = (Owner) BeanUtilsBean.getInstance().cloneBean(owner);
        ownerPatch.setCity(null);
        when(ownerService.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        getMockMvc().perform(update(blankCityPatch, owner.getId()))
                .andExpect(status().isUnprocessableEntity());


        verify(ownerService, never()).update(any(), any());
    }

    @Test
    public void update_linkPetToOwner() throws Exception {
        Long petId = 43L;
        Pet pet = Pet.builder().name("myPet").build();
        pet.setId(petId);

        Owner ownerPatch = (Owner) BeanUtilsBean.getInstance().cloneBean(owner);
        ownerPatch.getPets().add(pet);


        when(ownerService.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));
        when(petService.findById(petId))
                .thenReturn(Optional.of(pet));
        when(crudServiceLocator.find(Pet.class))
                .thenReturn(petService);
        when(ownerService.update(refEq(ownerPatch),eq(true)))
                .thenReturn(ownerPatch);

        getMockMvc().perform(update(addPetPatch,owner.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.petIds[0]").value(petId));

        verify(ownerService).update(refEq(ownerPatch),eq(true));
    }
}
