package io.github.vincemann.generic.crud.lib.test.service.result.matcher;

import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import org.junit.jupiter.api.Assertions;

import java.io.Serializable;
import java.util.Optional;

public class ExistenceMatchers {

    public static ServiceResultMatcher notPresentInDatabase(Serializable id) {
        return (serviceResult, context, repository) -> {
            Optional byId = repository.findById(id);
            Assertions.assertFalse(byId.isPresent());
        };
    }

    public static ServiceResultMatcher presentInDatabase(Serializable id) {
        return (serviceResult, context, repository) -> {
            Optional byId = repository.findById(id);
            Assertions.assertTrue(byId.isPresent());
        };
    }
}
