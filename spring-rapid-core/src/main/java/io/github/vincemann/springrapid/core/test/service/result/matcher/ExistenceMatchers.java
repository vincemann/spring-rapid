package io.github.vincemann.springrapid.core.test.service.result.matcher;

import org.junit.jupiter.api.Assertions;

import java.io.Serializable;
import java.util.Optional;

public class ExistenceMatchers {

    public static ServiceResultMatcher notPresentInDatabase(Serializable id) {
        return (testContext) -> {
            Optional byId = testContext.getRepository().findById(id);
            Assertions.assertFalse(byId.isPresent());
        };
    }

    public static ServiceResultMatcher presentInDatabase(Serializable id) {
        return (testContext) -> {
            Optional byId = testContext.getRepository().findById(id);
            Assertions.assertTrue(byId.isPresent());
        };
    }
}
