package io.github.vincemann.demo.controllers.springAdapter;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.vincemann.demo.dtos.owner.CreateOwnerDto;
import io.github.vincemann.demo.dtos.owner.ReadOwnerDto;
import io.github.vincemann.demo.dtos.owner.UpdateOwnerDto;
import io.github.vincemann.demo.model.Owner;
import io.github.vincemann.demo.service.OwnerService;
import io.github.vincemann.generic.crud.lib.test.controller.UrlParamIdMvcControllerTest;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.HashSet;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class OwnerControllerTest
        extends UrlParamIdMvcControllerTest<OwnerService,Owner,Long> {

    CreateOwnerDto createOwnerDto;
    ReadOwnerDto readOwnerDto;
    Owner owner;
    
    @MockBean
    OwnerService mockedService;
    
    @Autowired
    ObjectMapper mapper;

    @BeforeEach
    public void setup() throws Exception {
        super.setup();
        Long id = 42L;
        String address = "Other Street 13";
        String firstName = "Max";
        String lastName = "Müller";
        String city = "Munich";
        String telephone = "0176546231";
        //Set<Long> petIds = new HashSet<>(Lists.newArrayList(3L,12L));

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
    public void createOwner_shouldSucceed() throws Exception {
        String readOwnerDtoJson = mapper.writeValueAsString(readOwnerDto);
        when(mockedService.save(refEq(owner,"id"))).thenReturn(owner);

        getMockMvc().perform(create(createOwnerDto))
                .andExpect(status().isOk())
                .andExpect(content().json(readOwnerDtoJson));

        Mockito.verify(mockedService).save(refEq(owner,"id"));
    }


    @Test
    public void deleteOwner_shouldSucceed() throws Exception {
        getMockMvc().perform(delete(owner.getId()))
                .andExpect(status().isOk());
        Mockito.verify(mockedService).deleteById(owner.getId());
    }


    @Test
    public void findOwner_shouldSucceed() throws Exception {
        when(mockedService.findById(owner.getId())).thenReturn(Optional.of(owner));
        String readDtoJson = mapper.writeValueAsString(readOwnerDto);
        getMockMvc().perform(find(owner.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(readDtoJson));
        Mockito.verify(mockedService).findById(owner.getId());
    }

    @Test
    public void updateOwner_shouldSucceed() throws Exception {
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
    public void updateBlankCityOwner_shouldFailWithBadRequest() throws Exception {
        UpdateOwnerDto blankCityUpdate = UpdateOwnerDto.builder()
                //blank city
                .city("")
                .build();
        getMockMvc().perform(partialUpdate(blankCityUpdate))
                .andExpect(status().isBadRequest());

        verify(mockedService,never()).update(any(),any());
    }
}
