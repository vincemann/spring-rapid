package io.github.vincemann.demo.controllers;

import io.github.vincemann.demo.model.Owner;
import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.demo.model.PetType;
import io.github.vincemann.demo.model.Specialty;
import io.github.vincemann.demo.service.PetService;
import io.github.vincemann.demo.service.VisitService;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.DtoCrudController_SpringAdapter;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.UrlParamId_DtoCrudController_SpringAdapter_IT;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.TestRequestEntity_Factory;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;

@Getter
@Setter
public abstract class EntityInitializer_ControllerIT
        <
                ServiceE extends IdentifiableEntity<Long>,
                Dto extends IdentifiableEntity<Long>,
                Repo extends CrudRepository<ServiceE, Long>,
                Service extends CrudService<ServiceE, Long,Repo>,
                Controller extends DtoCrudController_SpringAdapter<ServiceE, Dto, Long,Repo, Service>
        >
        extends UrlParamId_DtoCrudController_SpringAdapter_IT<ServiceE,Dto,Repo,Service,Controller,Long> {

    @Autowired
    private PetTypeController petTypeController;
    private PetType testPetType;

    @Autowired
    private SpecialtyController specialtyController;
    private Specialty testSpecialty;

    @Autowired
    private OwnerController ownerController;
    private Owner testOwner;
    @Autowired
    private PetController petController;
    @Autowired
    private VetController vetController;
    @Autowired
    private VisitService visitService;
    @Autowired
    private PetService petService;
    private Pet testPet;

    /*public EntityInitializerControllerIT(String url, Controller crudController, TestRequestEntityFactory requestEntityFactory, Plugin<? super Dto, ? super Long>... plugins) {
        super(url, crudController,requestEntityFactory,plugins);
    }*/

    public EntityInitializer_ControllerIT(Controller crudController, TestRequestEntity_Factory requestEntityFactory, Plugin<? super Dto, ? super ServiceE, ? super Long>... plugins) {
        super(crudController, requestEntityFactory, plugins);
    }

    @Override
    public void beforeEachTest() throws Exception {
        super.beforeEachTest();
        testPetType = petTypeController.getCrudService().save(PetType.builder()
                .name("dog")
                .build());

        testSpecialty = specialtyController.getCrudService().save(Specialty.builder()
                .description("dogliver expert")
                .build());
        testPet = petController.getCrudService().save(Pet.builder()
                .name("bello")
                .birthDate(LocalDate.of(2012,1,23))
                .petType(testPetType)
                .build());

        testOwner = ownerController.getCrudService().save(Owner.builder()
                .firstName("klaus")
                .lastName("Kleber")
                .address("street 123")
                .city("Berlin")
                .build());
    }
}
