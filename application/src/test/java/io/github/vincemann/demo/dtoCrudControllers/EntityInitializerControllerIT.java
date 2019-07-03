package io.github.vincemann.demo.dtoCrudControllers;

import io.github.vincemann.demo.dtos.OwnerDTO;
import io.github.vincemann.demo.dtos.PetTypeDTO;
import io.github.vincemann.demo.dtos.SpecialtyDTO;
import io.github.vincemann.demo.model.Owner;
import io.github.vincemann.demo.model.PetType;
import io.github.vincemann.demo.model.Specialty;
import io.github.vincemann.demo.service.VisitService;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.DTOCrudControllerSpringAdapter;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.ValidationUrlParamIdDTOCrudControllerSpringAdapterIT;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
public abstract class EntityInitializerControllerIT<ServiceE extends IdentifiableEntity<Id>, DTO extends IdentifiableEntity<Id>, Service extends CrudService<ServiceE, Id>, Controller extends DTOCrudControllerSpringAdapter<ServiceE, DTO, Id, Service>, Id extends Serializable> extends ValidationUrlParamIdDTOCrudControllerSpringAdapterIT<ServiceE,DTO,Service,Controller,Id> {

    @Autowired
    private PetTypeController petTypeController;
    private PetTypeDTO testPetType;

    @Autowired
    private SpecialtyController specialtyController;
    private SpecialtyDTO testSpecialty;

    @Autowired
    private OwnerController ownerController;
    private OwnerDTO testOwner;
    @Autowired
    private PetController petController;
    @Autowired
    private VetController vetController;
    @Autowired
    private VisitService visitService;

    public EntityInitializerControllerIT(String url, Controller crudController, Id nonExistingId) {
        super(url, crudController, nonExistingId);
    }

    public EntityInitializerControllerIT(Controller crudController, Id nonExistingId) {
        super(crudController, nonExistingId);
    }

    @BeforeEach
    @Override
    public void before() throws Exception {
        //PetType abspeichern, den muss es vorher geben , bevor ich ein Pet abspeicher
        //ich möchte den nicht per cascade erstellen lassen wenn jmd ein pet added und da ein unbekannter pettype drinhängt
        PetType petType = PetType.builder().name("Hund").build();
        Specialty specialty = Specialty.builder().description("HundeLeber Experte").build();
        Owner testOwner = Owner.builder().firstName("klaus").lastName("Kleber").address("street 123").city("Berlin").build();
        this.testPetType = petTypeController.getServiceEntityToDTOMapper().map(petTypeController.getCrudService().save(petType));
        this.testSpecialty=specialtyController.getServiceEntityToDTOMapper().map(specialtyController.getCrudService().save(specialty));
        this.testOwner=ownerController.provideServiceEntityToDTOMapper().map(ownerController.getCrudService().save(testOwner));
        super.before();
    }


    @AfterEach
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        cleanAllServiceEntities(petController.getCrudService(),petTypeController.getCrudService(),ownerController.getCrudService(),specialtyController.getCrudService(),vetController.getCrudService(),visitService);

    }

    private void cleanAllServiceEntities(CrudService... crudServices) throws NoIdException, EntityNotFoundException {
        for(CrudService crudService: crudServices) {
            System.out.println("cleaning up Enitities managed by_: " + crudService.getClass().getSimpleName());
            Set<IdentifiableEntity> allEntitesOfService = crudService.findAll();
            for (IdentifiableEntity identifyableEntity : allEntitesOfService) {
                crudService.delete(identifyableEntity);
            }
            Assertions.assertTrue(crudService.findAll().isEmpty());
        }
    }
}
