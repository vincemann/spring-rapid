package com.github.vincemann.springrapid.demo.controllers;

import com.github.vincemann.springrapid.demo.model.Owner;
import com.github.vincemann.springrapid.demo.model.Pet;
import com.github.vincemann.springrapid.demo.model.PetType;
import com.github.vincemann.springrapid.demo.model.Specialty;
import com.github.vincemann.springrapid.demo.repo.OwnerRepository;
import com.github.vincemann.springrapid.demo.repo.PetRepository;
import com.github.vincemann.springrapid.demo.repo.PetTypeRepository;
import com.github.vincemann.springrapid.demo.repo.SpecialtyRepository;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.coretest.controller.rapid.AbstractUrlParamIdRapidControllerTest;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;

@Getter
@Setter
@Slf4j
public abstract class TestDataInitControllerTest<S extends CrudService<E,Long,? extends CrudRepository<E,Long>>,E extends IdentifiableEntity<Long>>
        extends AbstractUrlParamIdRapidControllerTest<S,E,Long>

{

    private PetType testPetType;
    private Specialty testSpecialty;
    private Owner testOwner;
    private Pet testPet;

    private CrudService<Owner,Long, OwnerRepository> ownerService;
    private CrudService<Pet,Long, PetRepository> petService;
    private CrudService<PetType,Long, PetTypeRepository> petTypeService;
    private CrudService<Specialty,Long,SpecialtyRepository> specialtyService;



    @Autowired
    public void setOwnerService(CrudService<Owner, Long, OwnerRepository> ownerService) {
        this.ownerService = ownerService;
    }
    @Autowired
    public void setPetService(CrudService<Pet, Long, PetRepository> petService) {
        this.petService = petService;
    }
    @Autowired
    public void setPetTypeService(CrudService<PetType, Long, PetTypeRepository> petTypeService) {
        this.petTypeService = petTypeService;
    }
    @Autowired
    public void setSpecialtyService(CrudService<Specialty, Long, SpecialtyRepository> specialtyService) {
        this.specialtyService = specialtyService;
    }

    @BeforeEach
    public void setup() throws Exception {
        super.setup();
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
