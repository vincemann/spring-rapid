package com.github.vincemann.springrapid.coredemo.controller;


import com.github.vincemann.springrapid.core.Entity;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import com.github.vincemann.springrapid.core.util.ResourceUtils;
import com.github.vincemann.springrapid.coredemo.dto.owner.CreateOwnerDto;
import com.github.vincemann.springrapid.coredemo.dto.owner.ReadForeignOwnerDto;
import com.github.vincemann.springrapid.coredemo.dto.owner.ReadOwnOwnerDto;
import com.github.vincemann.springrapid.coredemo.model.Owner;
import com.github.vincemann.springrapid.coredemo.model.Pet;
import com.github.vincemann.springrapid.coredemo.service.OwnerService;
import com.github.vincemann.springrapid.coredemo.service.PetService;
import com.github.vincemann.springrapid.coretest.TestPrincipal;
import com.github.vincemann.springrapid.coretest.controller.automock.AutoMockCrudControllerTest;
import com.google.common.collect.Sets;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



// todo fix
@Disabled
public class MockServiceOwnerControllerTest
        extends AutoMockCrudControllerTest<OwnerController> {

    CreateOwnerDto createOwnerDto;
    ReadForeignOwnerDto readForeignOwnerDto;
    ReadOwnOwnerDto readOwnOwnerDto;
    Owner owner;

    @Autowired
    OwnerService ownerService;

    @Autowired
    PetService petService;

    @Autowired
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
    protected void setupTestData() throws Exception {
//        super.setup();
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
                .firstName(firstName)
                .lastName(lastName)
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
    public void canCreateOwner() throws Exception {

        String readOwnerDtoJson = serialize(readForeignOwnerDto);
        when(ownerService.save(refEq(owner, "id"))).thenReturn(owner);

        getMvc().perform(create(createOwnerDto))
                .andExpect(status().isOk())
                .andExpect(content().json(readOwnerDtoJson));

        Mockito.verify(ownerService).save(refEq(owner, "id"));
    }


    @Test
    public void canDeleteOwner() throws Exception {
        getMvc().perform(delete(owner.getId()))
                .andExpect(status().is2xxSuccessful());

        Mockito.verify(ownerService).deleteById(owner.getId());
    }


    @Test
    public void canFindForeignOwnerById() throws Exception {
        when(ownerService.findById(owner.getId())).thenReturn(Optional.of(owner));
        String readDtoJson = serialize(readForeignOwnerDto);

        getMvc().perform(find(owner.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(readDtoJson));

        Mockito.verify(ownerService).findById(owner.getId());
    }

    @Test
    public void canFindOwnOwnerById() throws Exception {
        rapidSecurityContext.login(TestPrincipal.withName(owner.getLastName()));

        when(ownerService.findById(owner.getId())).thenReturn(Optional.of(owner));
        String readDtoJson = serialize(readOwnOwnerDto);

        getMvc().perform(find(owner.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(readDtoJson));

        Mockito.verify(ownerService).findById(owner.getId());
        RapidSecurityContext.logout();
    }

    @Test
    public void canUpdateOwnersAddress() throws Exception {
        //given
        String updatedAddress = "other Street 12";

        // exclude hobbies bc emtpy collections that should not get updated, will be set to null by EntityReflectionUtils.setNonMatchingFieldsNull(patchEntity,allUpdatedFields);
        Owner ownerPatch = Entity.createUpdate(owner);
        ownerPatch.setAddress(updatedAddress);

        when(ownerService.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(ownerService.partialUpdate(refEq(ownerPatch),any(),any())).thenReturn(ownerPatch);

        //when
        getMvc().perform(update(addressPatch, owner.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value(updatedAddress));

        ownerPatch.setPets(null);
        ownerPatch.setHobbies(null);
        Mockito.verify(ownerService).partialUpdate(refEq(ownerPatch),any(),any());

    }


    // Constraint Validation Exception cannot be mocked
    @Disabled
    @Test
    public void cantUpdateWithBlankCity() throws Exception {
        // exclude hobbies bc emtpy collections that should not get updated, will be set to null by EntityReflectionUtils.setNonMatchingFieldsNull(patchEntity,allUpdatedFields);
        Owner ownerPatch = Entity.createUpdate(owner);
        ownerPatch.setCity("");

        when(ownerService.findById(owner.getId()))
                .thenReturn(Optional.of(owner));

        when(ownerService.partialUpdate(any(),any(),any()))
                .thenThrow(ConstraintViolationException.class);

        getMvc().perform(update(blankCityPatch, owner.getId()))
                .andExpect(status().isBadRequest());


        verify(ownerService, never()).partialUpdate(any(),any(),any());
    }

    @Test
    public void canLinkPetToOwner_viaUpdate() throws Exception {
        Long petId = 43L;
        Pet pet = Pet.builder().name("myPet").build();
        pet.setId(petId);

        // exclude hobbies bc emtpy collections that should not get updated, will be set to null by EntityReflectionUtils.setNonMatchingFieldsNull(patchEntity,allUpdatedFields);
        Owner ownerPatch = Entity.createUpdate(owner);
        ownerPatch.setPets(Sets.newHashSet(pet));


        when(ownerService.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));
        when(petService.findById(petId))
                .thenReturn(Optional.of(pet));
        when(crudServiceLocator.find(Pet.class))
                .thenReturn(petService);
        when(ownerService.partialUpdate(refEq(ownerPatch),any(),any()))
                .thenReturn(ownerPatch);

        getMvc().perform(update(addPetPatch, owner.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.petIds[0]").value(petId));

        ownerPatch.setHobbies(null);
        verify(ownerService).partialUpdate(refEq(ownerPatch),any(),any());
    }
}
