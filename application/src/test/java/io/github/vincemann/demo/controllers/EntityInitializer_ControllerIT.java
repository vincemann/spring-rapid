package io.github.vincemann.demo.controllers;

import io.github.vincemann.demo.model.*;
import io.github.vincemann.demo.repositories.*;
import io.github.vincemann.demo.service.OwnerService;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.DtoCrudController_SpringAdapter;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.UrlParamId_ControllerIntegrationTest;
import io.github.vincemann.generic.crud.lib.test.forceEagerFetch.Hibernate_ForceEagerFetch_Helper;
import io.github.vincemann.generic.crud.lib.test.forceEagerFetch.proxy.CrudService_HibernateForceEagerFetch_Proxy;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.time.LocalDate;

import static io.github.vincemann.generic.crud.lib.service.plugin.CrudService_PluginProxy.PLUGIN_PROXY;

@Getter
@Setter
@Slf4j
public abstract class EntityInitializer_ControllerIT
        <
                ServiceE extends IdentifiableEntity<Long>,
                Dto extends IdentifiableEntity<Long>,
                Repo extends CrudRepository<ServiceE, Long>,
                Service extends CrudService<ServiceE, Long,Repo>,
                Controller extends DtoCrudController_SpringAdapter<ServiceE, Dto, Long,Repo, Service>
        >
        extends UrlParamId_ControllerIntegrationTest<ServiceE,Dto,Repo,Service,Controller,Long>

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
    private Hibernate_ForceEagerFetch_Helper hibernate_forceEagerFetch_helper;


    @Autowired
    public void injectOwnerService(CrudService<Owner, Long, OwnerRepository> ownerService) {
        Assertions.assertNotNull(ownerService);
        this.ownerService = wrapWithEagerFetchProxy(ownerService);
    }
    @Autowired
    public void injectPetService(CrudService<Pet, Long, PetRepository> petService) {
        Assertions.assertNotNull(petService);
        this.petService = wrapWithEagerFetchProxy(petService);
    }
    @Autowired
    public void injectPetTypeService(CrudService<PetType, Long, PetTypeRepository> petTypeService) {
        Assertions.assertNotNull(petTypeService);
        this.petTypeService = wrapWithEagerFetchProxy(petTypeService);
    }
    @Autowired
    public void injectSpecialtyService(CrudService<Specialty, Long, SpecialtyRepository> specialtyService) {
        Assertions.assertNotNull(specialtyService);
        this.specialtyService = wrapWithEagerFetchProxy(specialtyService);
    }

    @BeforeEach
    public void setup() throws Exception {
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
