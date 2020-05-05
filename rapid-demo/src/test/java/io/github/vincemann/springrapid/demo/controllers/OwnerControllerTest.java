package io.github.vincemann.springrapid.demo.controllers;


import io.github.vincemann.springrapid.core.service.exception.BadEntityException;
import io.github.vincemann.springrapid.core.util.ResourceUtils;
import io.github.vincemann.springrapid.coretest.controller.rapid.AbstractUrlParamIdRapidControllerTest;
import io.github.vincemann.springrapid.demo.dtos.owner.CreateOwnerDto;
import io.github.vincemann.springrapid.demo.dtos.owner.ReadOwnerDto;
import io.github.vincemann.springrapid.demo.dtos.owner.UpdateOwnerDto;
import io.github.vincemann.springrapid.demo.model.Owner;
import io.github.vincemann.springrapid.demo.service.OwnerService;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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
        extends AbstractUrlParamIdRapidControllerTest<OwnerService,Owner,Long> {

    CreateOwnerDto createOwnerDto;
    ReadOwnerDto readOwnerDto;
    Owner owner;
    
    @MockBean
    OwnerService mockedService;

    String addressPatch;
    String blankCityPatch;

    @Value("classpath:/update-owner/patch-address.json")
    public void setUserPatch(Resource patch) throws IOException {
        this.addressPatch = ResourceUtils.toStr(patch);
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
        when(mockedService.save(refEq(owner,"id"))).thenReturn(owner);

        getMockMvc().perform(create(createOwnerDto))
                .andExpect(status().isOk())
                .andExpect(content().json(readOwnerDtoJson));

        Mockito.verify(mockedService).save(refEq(owner,"id"));
    }


    @Test
    public void delete_shouldSucceed() throws Exception {
        getMockMvc().perform(delete(owner.getId()))
                .andExpect(status().isOk());
        Mockito.verify(mockedService).deleteById(owner.getId());
    }


    @Test
    public void findById_shouldSucceed() throws Exception {
        when(mockedService.findById(owner.getId())).thenReturn(Optional.of(owner));
        String readDtoJson = serialize(readOwnerDto);

        getMockMvc().perform(find(owner.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(readDtoJson));
        Mockito.verify(mockedService).findById(owner.getId());
    }

    @Test
    public void update_address_shouldSucceed() throws Exception {
        //given
//        UpdateOwnerDto diffAddressUpdate = UpdateOwnerDto.builder()
//                .address()
//                .build();
//        diffAddressUpdate.setId(owner.getId());

        String updatedAddress = "other Street 12";

        Owner updatedOwner = (Owner) BeanUtilsBean.getInstance().cloneBean(owner);
        updatedOwner.setAddress(updatedAddress);

        when(mockedService.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(mockedService.update(any(Owner.class),eq(true))).thenReturn(updatedOwner);

        //when
        getMockMvc().perform(update(addressPatch,owner.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value(updatedOwner.getAddress()));

        ArgumentCaptor<Owner> updateArg = ArgumentCaptor.forClass(Owner.class);
        Mockito.verify(mockedService).update(updateArg.capture(),anyBoolean());
        Assertions.assertEquals(updatedAddress,updateArg.getValue().getAddress());
    }


    @Test
    public void update_withBlankCity_shouldFail_with422() throws Exception {
//        UpdateOwnerDto blankCityUpdate = UpdateOwnerDto.builder()
//                //blank city
//                .city("")
//                .build();
        when(mockedService.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        getMockMvc().perform(update(blankCityPatch,owner.getId()))
                .andExpect(status().isUnprocessableEntity());


        verify(mockedService,never()).update(any(),any());
    }
}
