package io.github.vincemann.generic.crud.lib.test.controller.springAdapter;

import io.github.vincemann.generic.crud.lib.controller.springAdapter.DTOCrudControllerSpringAdapter;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.TestEntityBundle;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.UpdateTestBundle;
import io.github.vincemann.generic.crud.lib.util.TestLogUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.util.AbstractMap;
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
@Slf4j
public abstract class ValidationUrlParamIdDTOCrudControllerSpringAdapterIT<ServiceE extends IdentifiableEntity<Id>, DTO extends IdentifiableEntity<Id>, Service extends CrudService<ServiceE, Id>, Controller extends DTOCrudControllerSpringAdapter<ServiceE, DTO, Id, Service>, Id extends Serializable>  extends UrlParamIdDTOCrudControllerSpringAdapterIT<ServiceE,DTO,Service,Controller,Id> {

    private List<DTO> invalidTestDTOs;
    private List<TestEntityBundle<DTO>> invalidTestDtoUpdateBundles;

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
     * @return  a list of {@link TestEntityBundle}s with valid {@link TestEntityBundle#getEntity()} according to the provided {@link io.github.vincemann.generic.crud.lib.controller.springAdapter.validationStrategy.ValidationStrategy}
     *          These DTO's will be used for update tests only in this class
     *          The modfified dtos of {@link TestEntityBundle#getUpdateTestBundles()} should be INVALID, see {@link UpdateTestBundle#getModifiedEntity()}
     */
    protected abstract List<TestEntityBundle<DTO>> provideInvalidUpdateDtoBundles();

    @BeforeEach
    public void before() throws Exception {
        super.before();


        this.invalidTestDTOs =provideInvalidTestDTOs();
        if(invalidTestDTOs==null){
            this.invalidTestDTOs= new ArrayList<>();
        }


        this.invalidTestDtoUpdateBundles =provideInvalidUpdateDtoBundles();
        if(invalidTestDtoUpdateBundles ==null){
            this.invalidTestDtoUpdateBundles = new ArrayList<>();
        }
        invalidTestDtoUpdateBundles.forEach(bundle -> Assertions.assertFalse(bundle.getUpdateTestBundles().isEmpty(),"Must specifiy at least one UpdateTestBundle for each bundle"));
    }



    @Test
    protected void createInvalidEntities(){
        Assumptions.assumeTrue(getCrudController().getEndpointsExposureDetails().isCreateEndpointExposed());
        Assumptions.assumeTrue(!invalidTestDTOs.isEmpty(),"No invalid Entities for createInvalidEntities-Test provided -> skipping. ");
        for(DTO invalidTestDTO: invalidTestDTOs) {
            TestLogUtils.logTestStart(log,"createInvalidEntity",new AbstractMap.SimpleEntry<>("testDto(invalid)",invalidTestDTO));



            ResponseEntity<String> responseEntity = createEntity(invalidTestDTO, HttpStatus.BAD_REQUEST);
            Assertions.assertFalse(isBodyOfDtoType(responseEntity.getBody()));


            TestLogUtils.logTestSucceeded(log,"createInvalidEntity",new AbstractMap.SimpleEntry<>("testDto(invalid)",invalidTestDTO));
        }
    }

    @Test
    protected void updateValidEntityWithInvalidEntities() throws Exception {
        Assumptions.assumeTrue(getCrudController().getEndpointsExposureDetails().isUpdateEndpointExposed());
        Assumptions.assumeTrue(!invalidTestDtoUpdateBundles.isEmpty(),"No invalid Entity Update Dto Bundles for updateValidEntityWithInvalidEntities-Test provided -> skipping. ");

        for(TestEntityBundle<DTO> bundle: invalidTestDtoUpdateBundles) {
            DTO dbEntityDto = createEntityShouldSucceed(bundle.getEntity(), HttpStatus.OK);
            for (UpdateTestBundle<DTO> updateTestBundle : bundle.getUpdateTestBundles()) {
                DTO invalidModificationDto = updateTestBundle.getModifiedEntity();
                TestLogUtils.logTestStart(log,"updateValidEntityWithInvalidEntity",new AbstractMap.SimpleEntry<>("testDto(valid)",bundle.getEntity()),new AbstractMap.SimpleEntry<>("testUpdateDto(invalid)",invalidModificationDto));

                invalidModificationDto.setId(dbEntityDto.getId());
                updateEntityShouldFail(dbEntityDto,invalidModificationDto, HttpStatus.BAD_REQUEST);
                updateTestBundle.getPostUpdateCallback().callback(dbEntityDto);

                TestLogUtils.logTestSucceeded(log,"updateValidEntityWithInvalidEntity",new AbstractMap.SimpleEntry<>("testDto(valid)",bundle.getEntity()),new AbstractMap.SimpleEntry<>("testUpdateDto(invalid)",invalidModificationDto));
            }
        }
    }
}
