package io.github.vincemann.generic.crud.lib.test.controller;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.exception.EntityMappingException;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.SpringAdapterDtoCrudController;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.test.controller.crudTests.*;
import io.github.vincemann.generic.crud.lib.test.testExecutionListeners.ResetDatabaseTestExecutionListener;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestExecutionListeners;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Integration Test for a {@link SpringAdapterDtoCrudController} that offers a
 * {@link io.github.vincemann.generic.crud.lib.test.controller.crudTests.abs.AbstractControllerTestTemplate} for every crud Operation.
 *
 * @param <E>
 * @param <Id>
 */
@Slf4j
//clear database after each test
@TestExecutionListeners(
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS,
        listeners = {ResetDatabaseTestExecutionListener.class}
)
@Getter
@Setter
@NoArgsConstructor
public abstract class CrudControllerIntegrationTest<E extends IdentifiableEntity<Id>, Id extends Serializable>
        extends EagerFetchControllerIntegrationTest<E,Id>
{

    private CreateControllerTestTemplate<E,Id> createTemplate;
    private FindControllerTestTemplate<E,Id> findTemplate;
    private UpdateControllerTestTemplate<E,Id> updateTemplate;
    private DeleteControllerTestTemplate<E,Id> deleteTemplate;
    private FindAllControllerTestTemplate<E,Id> findAllTemplate;

    @Autowired
    public void injectCreateControllerTestTemplate(CreateControllerTestTemplate<E, Id> createControllerTestTemplate) {
        this.createTemplate = createControllerTestTemplate;
    }
    @Autowired
    public void injectFindControllerTestTemplate(FindControllerTestTemplate<E, Id> findControllerTestTemplate) {
        this.findTemplate = findControllerTestTemplate;
    }
    @Autowired
    public void injectUpdateControllerTemplate(UpdateControllerTestTemplate<E, Id> updateControllerTemplate) {
        this.updateTemplate = updateControllerTemplate;
    }
    @Autowired
    public void injectDeleteControllerTestTemplate(DeleteControllerTestTemplate<E, Id> deleteControllerTestTemplate) {
        this.deleteTemplate = deleteControllerTestTemplate;
    }
    @Autowired
    public void injectFindAllControllerTestTemplate(FindAllControllerTestTemplate<E, Id> findAllControllerTestTemplate) {
        this.findAllTemplate = findAllControllerTestTemplate;
    }


    //    @Override
//    public void afterPropertiesSet() throws Exception {
//        super.afterPropertiesSet();
//        Assertions.assertTrue(UrlParamIdFetchingStrategy.class.isAssignableFrom(getController().getIdIdFetchingStrategy().getClass()));
//        initTestsWithDefaults();
//    }

//    private void initTestsWithDefaults(){
//        DeleteControllerTestConfigurationFactory<E,Id> deleteTestConfigFactory = new DeleteControllerTestConfigurationFactory<>(this);
//        this.deleteControllerTestTemplate = new DeleteControllerTestTemplate<E,Id>(this,deleteTestConfigFactory);
//
//        FindControllerTestConfigurationFactory<E,Id> findControllerTestConfigurationFactory = new FindControllerTestConfigurationFactory<>(this);
//        this.findControllerTestTemplate = new FindControllerTestTemplate<>(this,findControllerTestConfigurationFactory);
//
//        UpdateControllerTestConfigurationFactory<E,Id> updateControllerTestConfigurationFactory = new UpdateControllerTestConfigurationFactory<>(this);
//        this.updateControllerTemplate = new UpdateControllerTestTemplate<>(this,updateControllerTestConfigurationFactory);
//
//        CreateTestConfigurationFactory<E,Id> createTestConfigurationFactory = new CreateTestConfigurationFactory<>(this);
//        this.createControllerTestTemplate = new CreateControllerTestTemplate<>(this,createTestConfigurationFactory);
//
//        FindAllControllerTestConfigurationFactory<E,Id> findAllControllerTestConfigurationFactory = new FindAllControllerTestConfigurationFactory<>(this);
//        this.findAllControllerTestTemplate = new FindAllControllerTestTemplate<>(this,findAllControllerTestConfigurationFactory);
//    }

    @Autowired
    public void injectCrudController(SpringAdapterDtoCrudController<E, Id> crudController) {
        setController(crudController);
    }


//    @Override
//    protected RequestEntityFactory<Id> provideRequestEntityFactory() {
//        return AbstractUrlParamIdRequestEntityFactory.<Id>builder()
//                .controller(getController())
//                .entityIdParamKey(((UrlParamIdFetchingStrategy<Id>) getController().getIdIdFetchingStrategy()).getIdUrlParamKey())
//                .baseAddressProvider(this)
//                .build();
//    }

    protected <Dto extends IdentifiableEntity<Id>> Dto mapEntityToDto(E entity, Class<Dto> dtoClass) throws EntityMappingException {
        return getController().findMapperAndMapToDto(entity, dtoClass);
    }

    protected E mapDtoToEntity(IdentifiableEntity<Id> dto, Class<? extends IdentifiableEntity<Id>> dtoClass) throws EntityMappingException {
        return getController().findMapperAndMapToEntity(dto, dtoClass);
    }


    protected E saveServiceEntity(E e) throws BadEntityException {
        return getTestService().save(e);
    }

    protected Collection<E> saveServiceEntities(Collection<E> ECollection) throws BadEntityException {
        Collection<E> savedEntities = new ArrayList<>();
        for (E e : ECollection) {
            E savedEntity = saveServiceEntity(e);
            savedEntities.add(savedEntity);
        }
        return savedEntities;
    }
}