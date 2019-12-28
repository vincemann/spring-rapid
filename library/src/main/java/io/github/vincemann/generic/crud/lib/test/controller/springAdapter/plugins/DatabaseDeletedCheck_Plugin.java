package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.plugins;

import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.UrlParamId_ControllerIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Optional;

/**
 * Checks if Entities deleted, are actually delete from the database.
 * This is done by asking the Repository Layer ({@link org.springframework.data.repository.CrudRepository}), whether the entity is still there by calling ,
 */
/*
@Component
public class DatabaseDeletedCheck_Plugin extends UrlParamId_ControllerIntegrationTest.Plugin {

    @Override
    public void onAfterDeleteEntityShouldSucceed(Serializable id, ResponseEntity responseEntity) throws Exception {
        Optional entity = getIntegrationTest().getCrudController().getCrudService().getRepository().findById(id);
        Assertions.assertFalse(entity.isPresent());
        super.onAfterDeleteEntityShouldSucceed(id, responseEntity);
    }
}
*/