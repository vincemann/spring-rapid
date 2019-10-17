package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.plugins;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.EntityMappingException;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.UrlParamIdDtoCrudControllerSpringAdapterIT;
import io.github.vincemann.generic.crud.lib.util.BeanUtils;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

import static io.github.vincemann.generic.crud.lib.util.BeanUtils.isDeepEqual;

/**
 * This plugin checks for find-,create- and updateTests if serviceEntity with id of dto returned by httpRequest is {@link BeanUtils#isDeepEqual(Object, Object)} to it.
 */
@Component
public class ServiceDeepEqualPlugin extends UrlParamIdDtoCrudControllerSpringAdapterIT.Plugin<IdentifiableEntity<Long>,Long> {
    

    @Override
    public void onAfterFindAllEntitiesShouldSucceed(Set<? extends IdentifiableEntity<Long>> dtos) throws Exception {
        for (IdentifiableEntity<Long>dto : dtos) {
            Assertions.assertTrue(isSavedServiceEntityDeepEqual(dto));
        }
        super.onAfterFindAllEntitiesShouldSucceed(dtos);
    }

    @Override
    public void onAfterFindEntityShouldSucceed(Long id, IdentifiableEntity<Long>responseDto) throws Exception {
        Assertions.assertTrue(isSavedServiceEntityDeepEqual(responseDto));
        super.onAfterFindEntityShouldSucceed(id, responseDto);
    }


    @Override
    public void onAfterCreateEntityShouldSucceed(IdentifiableEntity<Long>dtoToCreate, IdentifiableEntity<Long>responseDto) throws Exception {
        Assertions.assertTrue(isSavedServiceEntityDeepEqual(responseDto));
        super.onAfterCreateEntityShouldSucceed(dtoToCreate, responseDto);
    }

    @Override
    public void onAfterUpdateEntityShouldSucceed(IdentifiableEntity<Long>oldEntity, IdentifiableEntity<Long>newEntity, IdentifiableEntity<Long>responseDto) throws Exception {
        Assertions.assertTrue(isSavedServiceEntityDeepEqual(responseDto));
        super.onAfterUpdateEntityShouldSucceed(oldEntity, newEntity, responseDto);
    }

    @Override
    public void onAfterUpdateEntityShouldFail(IdentifiableEntity<Long>oldEntity, IdentifiableEntity<Long>newEntity, ResponseEntity<String> responseEntity) throws Exception {
        Assertions.assertTrue(isSavedServiceEntityDeepEqual(oldEntity));
        super.onAfterUpdateEntityShouldFail(oldEntity, newEntity, responseEntity);
    }

    /**
     * 1. Map DtoEntity to ServiceEntity = RequestServiceEntity
     * 2. Fetch ServiceEntity from Service (ultimately from the persistence layer) by Id = dbServiceEntity
     * 3. Validate that RequestServiceEntity and dbServiceEntity are deep equal via {@link BeanUtils#isDeepEqual(Object, Object)}
     *
     * @param httpResponseEntity the Dto entity returned by Backend after http request
     * @return
     * @throws NoIdException
     */
    private boolean isSavedServiceEntityDeepEqual(IdentifiableEntity<Long>httpResponseEntity) throws EntityMappingException {
        try {
            IdentifiableEntity<Long>serviceHttpResponseEntity = getIntegrationTest().getCrudController().getDtoMapper().mapDtoToServiceEntity(httpResponseEntity, getIntegrationTest().getCrudController().getServiceEntityClass());
            Assertions.assertNotNull(serviceHttpResponseEntity);
            Serializable httpResponseEntityId = serviceHttpResponseEntity.getId();
            Assertions.assertNotNull(httpResponseEntityId);

            //Compare httpEntity with saved Entity From Service
            Optional entityFromService = getIntegrationTest().getCrudController().getCrudService().findById(httpResponseEntityId);
            Assertions.assertTrue(entityFromService.isPresent());
            return isDeepEqual(entityFromService.get(), serviceHttpResponseEntity);
        } catch (NoIdException e) {
            throw new EntityMappingException(e);
        }

    }
}
