package io.github.vincemann.generic.crud.lib.test.controller.springAdapter;

import io.github.vincemann.generic.crud.lib.controller.springAdapter.DTOCrudControllerSpringAdapter;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.TestDtoBundle;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.UpdateTestBundle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * adds Validation Tests with invalid Entites to {@link UrlParamIdDTOCrudControllerSpringAdapterIT}
 * @param <ServiceE>
 * @param <DTO>
 * @param <Service>
 * @param <Controller>
 * @param <Id>
 */
public abstract class ValidationUrlParamIdDTOCrudControllerSpringAdapterIT<ServiceE extends IdentifiableEntity<Id>, DTO extends IdentifiableEntity<Id>, Service extends CrudService<ServiceE, Id>, Controller extends DTOCrudControllerSpringAdapter<ServiceE, DTO, Id, Service>, Id extends Serializable>  extends UrlParamIdDTOCrudControllerSpringAdapterIT<ServiceE,DTO,Service,Controller,Id> {

    private List<DTO> invalidTestDTOs;
    private List<TestDtoBundle<DTO>> invalidUpdateDtoBundles;

    public ValidationUrlParamIdDTOCrudControllerSpringAdapterIT(String url, Controller crudController, Id nonExistingId) {
        super(url, crudController, nonExistingId);
    }

    public ValidationUrlParamIdDTOCrudControllerSpringAdapterIT(Controller crudController, Id nonExistingId) {
        super(crudController, nonExistingId);
    }

    /**
     *
     * @return  a list of DTO's that are invalid according to the provided {@link io.github.vincemann.generic.crud.lib.controller.springAdapter.validationStrategy.ValidationStrategy}
     *          those dtos are tested to NOT get accepted for creation
     */
    protected abstract List<DTO> provideInvalidTestDTOs();

    /**
     *
     * @return  a list of {@link TestDtoBundle}s with valid {@link TestDtoBundle#getDto()} according to the provided {@link io.github.vincemann.generic.crud.lib.controller.springAdapter.validationStrategy.ValidationStrategy}
     *          These DTO's will be used for update tests only in this class
     *          The modfified dtos of {@link TestDtoBundle#getUpdateTestBundles()} should be INVALID, see {@link UpdateTestBundle#getModifiedDto()}
     */
    protected abstract List<TestDtoBundle<DTO>> provideInvalidUpdateDtoBundles();

    @BeforeEach
    public void before() throws Exception {
        super.before();


        this.invalidTestDTOs =provideInvalidTestDTOs();
        if(invalidTestDTOs==null){
            this.invalidTestDTOs= new ArrayList<>();
        }


        this.invalidUpdateDtoBundles=provideInvalidUpdateDtoBundles();
        if(invalidUpdateDtoBundles==null){
            this.invalidUpdateDtoBundles= new ArrayList<>();
        }
        invalidUpdateDtoBundles.forEach(bundle -> Assertions.assertFalse(bundle.getUpdateTestBundles().isEmpty(),"Must specifiy at least one UpdateTestBundle"));
    }



    @Test
    protected void createInvalidEntities(){
        if(invalidTestDTOs.isEmpty()){
            System.err.println("no create invalid Entites Test");
            return;
        }
        for(DTO invalidTestDTO: invalidTestDTOs) {
            System.err.println("create invalid EntityTest with testDTO: " + invalidTestDTO.toString());
            ResponseEntity<String> responseEntity = createEntity(invalidTestDTO, HttpStatus.BAD_REQUEST);
            Assertions.assertFalse(isBodyOfDtoType(responseEntity.getBody()));
            System.err.println("Test succeeded");
        }
    }

    @Test
    protected void updateValidEntityWithInvalidEntities() throws Exception {
        if(invalidUpdateDtoBundles.isEmpty()){
            System.err.println("No update tests");
            return;
        }
        for(TestDtoBundle<DTO> bundle: invalidUpdateDtoBundles) {
            System.err.println("update valid Entity with invalid UpdateDto Test with valid testDTO: " + bundle.getDto().toString());
            DTO dbEntityDto = createEntityShouldSucceed(bundle.getDto(), HttpStatus.OK);
            for (UpdateTestBundle<DTO> updateTestBundle : bundle.getUpdateTestBundles()) {
                DTO invalidModificationDto = updateTestBundle.getModifiedDto();
                System.err.println("Testing invalid update dto: " + invalidModificationDto.toString());
                invalidModificationDto.setId(dbEntityDto.getId());
                updateEntityShouldFail(dbEntityDto,invalidModificationDto, HttpStatus.BAD_REQUEST);
                System.err.println("invalid update Test succeeded");
                updateTestBundle.getPostUpdateCallback().callback(dbEntityDto);
            }
            System.err.println("Test succeeded");
        }
    }
}
