package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.plugins;

import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.UrlParamIdDtoCrudControllerSpringAdapterIT;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Optional;

/**
 * Checks if Entities deleted, are actually delete from the database.
 * This is done by calling {@link io.github.vincemann.generic.crud.lib.service.CrudService#findById(Serializable)},
 * if there is no result, then it is assumed the entity is deleted properly.
 */
@Component
public class CheckIfDbDeletedPlugin extends UrlParamIdDtoCrudControllerSpringAdapterIT.Plugin {

    @Override
    public void onAfterDeleteEntityShouldSucceed(Serializable id, ResponseEntity responseEntity) throws Exception {
        Optional entity = getIntegrationTest().getCrudController().getCrudService().findById(id);
        Assertions.assertFalse(entity.isPresent());
        super.onAfterDeleteEntityShouldSucceed(id, responseEntity);
    }
}
