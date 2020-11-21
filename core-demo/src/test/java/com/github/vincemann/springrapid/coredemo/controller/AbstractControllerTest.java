package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.core.controller.GenericCrudController;
import com.github.vincemann.springrapid.coredemo.model.Owner;
import com.github.vincemann.springrapid.coredemo.model.Pet;
import com.github.vincemann.springrapid.coredemo.model.PetType;
import com.github.vincemann.springrapid.coredemo.model.Specialty;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.coretest.controller.AbstractUrlParamIdCrudControllerTest;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

@Getter
@Setter
@Slf4j
public abstract class AbstractControllerTest<C extends GenericCrudController<?,Long,?,?,?>>
        extends AbstractUrlParamIdCrudControllerTest<C,Long>

{

    private PetType testPetType;
    private Specialty testSpecialty;
    private Owner testOwner;
    private Pet testPet;

    private CrudService<Owner,Long> ownerService;
    private CrudService<Pet,Long> petService;
    private CrudService<PetType,Long> petTypeService;
    private CrudService<Specialty,Long> specialtyService;



    @Autowired
    public void setOwnerService(CrudService<Owner, Long> ownerService) {
        this.ownerService = ownerService;
    }
    @Autowired
    public void setPetService(CrudService<Pet, Long> petService) {
        this.petService = petService;
    }
    @Autowired
    public void setPetTypeService(CrudService<PetType, Long> petTypeService) {
        this.petTypeService = petTypeService;
    }
    @Autowired
    public void setSpecialtyService(CrudService<Specialty, Long> specialtyService) {
        this.specialtyService = specialtyService;
    }

    @BeforeEach
    protected void setup() throws Exception {
        testPetType = petTypeService.save(PetType.builder()
                .name("dog")
                .build());

        testSpecialty = specialtyService.save(Specialty.builder()
                .description("dogliver expert")
                .build());
        testPet = petService.save(Pet.builder()
                .name("bello")
                .birthDate(LocalDate.of(2012,1,23))
                .petType(testPetType)
                .build());

        testOwner = ownerService.save(Owner.builder()
                .firstName("klaus")
                .lastName("Kleber")
                .address("street 123")
                .city("Berlin")
                .build());
    }

}
