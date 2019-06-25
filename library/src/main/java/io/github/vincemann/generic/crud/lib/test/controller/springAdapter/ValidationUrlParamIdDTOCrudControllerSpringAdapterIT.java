package io.github.vincemann.generic.crud.lib.test.controller.springAdapter;

import io.github.vincemann.generic.crud.lib.controller.springAdapter.DTOCrudControllerSpringAdatper;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.util.List;

/**
 * adds Validation Tests to {@link UrlParamIdDTOCrudControllerSpringAdapterIT}
 * @param <ServiceE>
 * @param <DTO>
 * @param <Service>
 * @param <Controller>
 * @param <Id>
 */
public abstract class ValidationUrlParamIdDTOCrudControllerSpringAdapterIT<ServiceE extends IdentifiableEntity<Id>, DTO extends IdentifiableEntity<Id>, Service extends CrudService<ServiceE, Id>, Controller extends DTOCrudControllerSpringAdatper<ServiceE, DTO, Id, Service>, Id extends Serializable>  extends UrlParamIdDTOCrudControllerSpringAdapterIT<ServiceE,DTO,Service,Controller,Id> {

    private List<DTO> invalidTestDTOs;

    public ValidationUrlParamIdDTOCrudControllerSpringAdapterIT(String url, Controller crudController, Id nonExistingId) {
        super(url, crudController, nonExistingId);
    }

    public ValidationUrlParamIdDTOCrudControllerSpringAdapterIT(Controller crudController, Id nonExistingId) {
        super(crudController, nonExistingId);
    }

    protected abstract List<DTO> provideInvalidTestDTOs();

    @BeforeEach
    public void before() throws Exception {
        super.before();
        this.invalidTestDTOs =provideInvalidTestDTOs();
    }

    @Test
    protected void createInvalidEntities(){
        for(DTO invalidTestDTO: invalidTestDTOs) {
            System.err.println("create invalid EntityTest with testDTO: " + invalidTestDTO.toString());
            ResponseEntity<String> responseEntity = createEntity(invalidTestDTO, HttpStatus.BAD_REQUEST);
            Assertions.assertFalse(isBodyOfDtoType(responseEntity.getBody()));
            System.err.println("Test succeeded");
        }
    }

    @Test
    protected void updateInvalidEntities() throws Exception {
        DTO savedValidEntity = createEntityShouldSucceed(getTestDTOs().get(0),HttpStatus.OK);
        for(DTO invalidTestDTO: invalidTestDTOs) {
            System.err.println("update invalid EntityTest with testDTO: " + invalidTestDTO.toString());
            invalidTestDTO.setId(savedValidEntity.getId());
            updateEntityShouldFail(savedValidEntity,invalidTestDTO, HttpStatus.BAD_REQUEST);
            System.err.println("Test succeeded");
        }

    }
}
