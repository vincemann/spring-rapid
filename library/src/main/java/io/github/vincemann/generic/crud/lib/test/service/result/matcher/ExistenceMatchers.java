package io.github.vincemann.generic.crud.lib.test.service.result.matcher;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import org.junit.jupiter.api.Assertions;

import java.io.Serializable;
import java.util.Optional;

public class ExistenceMatchers {

    public static ServiceResultMatcher notPresentInDatabase(Serializable id) {
        return (serviceResult, context) -> {
            try {
                Optional byId = serviceResult.getServiceRequest().getService().findById(id);
                Assertions.assertFalse(byId.isPresent());
            } catch (NoIdException e) {
                throw new IllegalArgumentException(e);
            }
        };
    }
}
