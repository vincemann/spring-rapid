package io.github.vincemann.generic.crud.lib.test.controller.springAdapter;

import io.github.vincemann.generic.crud.lib.controller.springAdapter.DtoCrudControllerSpringAdapter;
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
 * Adds Validation Tests with invalid Entities to {@link UrlParamIdDtoCrudControllerSpringAdapterIT}
 * @param <ServiceE>
 * @param <Dto>
 * @param <Service>
 * @param <Controller>
 * @param <Id>
 */
@Slf4j
public abstract class ValidationUrlParamIdDtoCrudControllerSpringAdapterIT<ServiceE extends IdentifiableEntity<Id>, Dto extends IdentifiableEntity<Id>, Service extends CrudService<ServiceE, Id>, Controller extends DtoCrudControllerSpringAdapter<ServiceE, Dto, Id, Service>, Id extends Serializable>  extends UrlParamIdDtoCrudControllerSpringAdapterIT<ServiceE,Dto,Service,Controller,Id> {

    private List<Dto> invalidTestDtos;
    private List<TestEntityBundle<Dto>> invalidTestDtoUpdateBundles;

    public ValidationUrlParamIdDtoCrudControllerSpringAdapterIT(String url, Controller crudController, Id nonExistingId) {
        super(url, crudController, nonExistingId);
    }

    public ValidationUrlParamIdDtoCrudControllerSpringAdapterIT(Controller crudController, Id nonExistingId) {
        super(crudController, nonExistingId);
    }

    /**
     *
     * @return  a list of Dto's that are invalid according to the provided {@link io.github.vincemann.generic.crud.lib.controller.springAdapter.validationStrategy.ValidationStrategy}
     *          those dtos are tested to NOT get accepted for creation
     */
    protected abstract List<Dto> provideInvalidTestDtos();

    /**
     *
     * @return  a list of {@link TestEntityBundle}s with valid {@link TestEntityBundle#getEntity()} according to the provided {@link io.github.vincemann.generic.crud.lib.controller.springAdapter.validationStrategy.ValidationStrategy}
     *          These Dto's will be used for update tests only in this class
     *          The modfified dtos of {@link TestEntityBundle#getUpdateTestBundles()} should be INVALID, see {@link UpdateTestBundle#getModifiedEntity()}
     */
    protected abstract List<TestEntityBundle<Dto>> provideInvalidUpdateDtoBundles();

    @BeforeEach
    public void before() throws Exception {
        super.before();


        this.invalidTestDtos = provideInvalidTestDtos();
        if(invalidTestDtos==null){
            this.invalidTestDtos= new ArrayList<>();
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
        Assumptions.assumeTrue(!invalidTestDtos.isEmpty(),"No invalid Entities for createInvalidEntities-Test provided -> skipping. ");
        for(Dto invalidTestDto: invalidTestDtos) {
            TestLogUtils.logTestStart(log,"createInvalidEntity",new AbstractMap.SimpleEntry<>("testDto(invalid)",invalidTestDto));



            ResponseEntity<String> responseEntity = createEntity(invalidTestDto, HttpStatus.BAD_REQUEST);
            Assertions.assertFalse(isBodyOfDtoType(responseEntity.getBody()));


            TestLogUtils.logTestSucceeded(log,"createInvalidEntity",new AbstractMap.SimpleEntry<>("testDto(invalid)",invalidTestDto));
        }
    }

    @Test
    protected void updateValidEntityWithInvalidEntities() throws Exception {
        Assumptions.assumeTrue(getCrudController().getEndpointsExposureDetails().isUpdateEndpointExposed());
        Assumptions.assumeTrue(!invalidTestDtoUpdateBundles.isEmpty(),"No invalid Entity Update Dto Bundles for updateValidEntityWithInvalidEntities-Test provided -> skipping. ");

        for(TestEntityBundle<Dto> bundle: invalidTestDtoUpdateBundles) {
            Dto dbEntityDto = createEntityShouldSucceed(bundle.getEntity(), HttpStatus.OK);
            for (UpdateTestBundle<Dto> updateTestBundle : bundle.getUpdateTestBundles()) {
                Dto invalidModificationDto = updateTestBundle.getModifiedEntity();
                TestLogUtils.logTestStart(log,"updateValidEntityWithInvalidEntity",new AbstractMap.SimpleEntry<>("testDto(valid)",bundle.getEntity()),new AbstractMap.SimpleEntry<>("testUpdateDto(invalid)",invalidModificationDto));

                invalidModificationDto.setId(dbEntityDto.getId());
                updateEntityShouldFail(dbEntityDto,invalidModificationDto, HttpStatus.BAD_REQUEST);
                updateTestBundle.getPostUpdateCallback().callback(dbEntityDto);

                TestLogUtils.logTestSucceeded(log,"updateValidEntityWithInvalidEntity",new AbstractMap.SimpleEntry<>("testDto(valid)",bundle.getEntity()),new AbstractMap.SimpleEntry<>("testUpdateDto(invalid)",invalidModificationDto));
            }
        }
    }
}
