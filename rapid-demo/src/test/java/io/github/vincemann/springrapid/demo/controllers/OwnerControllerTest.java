package io.github.vincemann.springrapid.demo.controllers;


import io.github.vincemann.springrapid.coretest.controller.rapid.UrlParamIdRapidControllerTest;
import io.github.vincemann.springrapid.demo.dtos.owner.CreateOwnerDto;
import io.github.vincemann.springrapid.demo.dtos.owner.ReadOwnerDto;
import io.github.vincemann.springrapid.demo.dtos.owner.UpdateOwnerDto;
import io.github.vincemann.springrapid.demo.model.Owner;
import io.github.vincemann.springrapid.demo.service.OwnerService;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.HashSet;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class OwnerControllerTest
        extends UrlParamIdRapidControllerTest<OwnerService,Owner,Long> {

    CreateOwnerDto createOwnerDto;
    ReadOwnerDto readOwnerDto;
    Owner owner;
    
    @MockBean
    OwnerService mockedService;

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
    public void partialUpdate_address_shouldSucceed() throws Exception {
        //given
        UpdateOwnerDto diffAddressUpdate = UpdateOwnerDto.builder()
                .address("other Street 12")
                .build();
        diffAddressUpdate.setId(owner.getId());

        Owner updatedOwner = (Owner) BeanUtilsBean.getInstance().cloneBean(owner);
        updatedOwner.setAddress(diffAddressUpdate.getAddress());

        when(mockedService.update(eq(owner),eq(false))).thenReturn(updatedOwner);

        //when
        getMockMvc().perform(partialUpdate(diffAddressUpdate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value(updatedOwner.getAddress()));

        Mockito.verify(mockedService).update(eq(owner),eq(false));
    }


    @Test
    public void partialUpdate_withBlankCity_shouldFail_withBadRequest() throws Exception {
        UpdateOwnerDto blankCityUpdate = UpdateOwnerDto.builder()
                //blank city
                .city("")
                .build();
        getMockMvc().perform(partialUpdate(blankCityUpdate))
                .andExpect(status().isBadRequest());

        verify(mockedService,never()).update(any(),any());
    }
}
