package com.github.vincemann.springrapid.demo.controller;


import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import com.github.vincemann.springrapid.core.util.ResourceUtils;
import com.github.vincemann.springrapid.coretest.TestPrincipal;
import com.github.vincemann.springrapid.coretest.controller.rapid.AbstractUrlParamIdRapidControllerTest;
import com.github.vincemann.springrapid.demo.dtos.owner.CreateOwnerDto;
import com.github.vincemann.springrapid.demo.dtos.owner.ReadForeignOwnerDto;
import com.github.vincemann.springrapid.demo.dtos.owner.ReadOwnOwnerDto;
import com.github.vincemann.springrapid.demo.model.Owner;
import com.github.vincemann.springrapid.demo.model.Pet;
import com.github.vincemann.springrapid.demo.service.OwnerService;
import com.github.vincemann.springrapid.demo.service.PetService;
import com.google.common.collect.Sets;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
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
        extends AbstractUrlParamIdRapidControllerTest<OwnerController,Long> {

    CreateOwnerDto createOwnerDto;
    ReadForeignOwnerDto readForeignOwnerDto;
    ReadOwnOwnerDto readOwnOwnerDto;
    Owner owner;

    @MockBean
    OwnerService ownerService;
    @MockBean
    PetService petService;

    @MockBean
    CrudServiceLocator crudServiceLocator;

    @Autowired
    RapidSecurityContext<RapidAuthenticatedPrincipal> rapidSecurityContext;

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

        readForeignOwnerDto = ReadForeignOwnerDto.builder()
                .address(address)
                .city(city)
                .telephone(telephone)
                .petIds(new HashSet<>())
                .build();
        readForeignOwnerDto.setId(id);

        readOwnOwnerDto = ReadOwnOwnerDto.Builder()
                .address(address)
                .city(city)
                .telephone(telephone)
                .petIds(new HashSet<>())
                .build();
        readOwnOwnerDto.setId(id);
        readOwnOwnerDto.setDirtySecret(ReadOwnOwnerDto.DIRTY_SECRET);


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
    public void create() throws Exception {
        String readOwnerDtoJson = serialize(readForeignOwnerDto);
        when(ownerService.save(refEq(owner, "id"))).thenReturn(owner);

        getMockMvc().perform(create(createOwnerDto))
                .andExpect(status().isOk())
                .andExpect(content().json(readOwnerDtoJson));

        Mockito.verify(ownerService).save(refEq(owner, "id"));
    }


    @Test
    public void delete() throws Exception {
        getMockMvc().perform(delete(owner.getId()))
                .andExpect(status().isOk());
        Mockito.verify(ownerService).deleteById(owner.getId());
    }


    @Test
    public void findForeignById() throws Exception {
        when(ownerService.findById(owner.getId())).thenReturn(Optional.of(owner));
        String readDtoJson = serialize(readForeignOwnerDto);

        getMockMvc().perform(find(owner.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(readDtoJson));
        Mockito.verify(ownerService).findById(owner.getId());
    }

    @Test
    public void findOwnById() throws Exception {
        rapidSecurityContext.login(TestPrincipal.withName(owner.getLastName()));

        when(ownerService.findById(owner.getId())).thenReturn(Optional.of(owner));
        String readDtoJson = serialize(readOwnOwnerDto);

        getMockMvc().perform(find(owner.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(readDtoJson));
        Mockito.verify(ownerService).findById(owner.getId());

        rapidSecurityContext.logout();
    }

    @Test
    public void update_address() throws Exception {
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
        when(ownerService.update(refEq(ownerPatch), eq(true)))
                .thenReturn(ownerPatch);

        getMockMvc().perform(update(addPetPatch, owner.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.petIds[0]").value(petId));

        verify(ownerService).update(refEq(ownerPatch), eq(true));
    }
}
