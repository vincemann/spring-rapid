package com.github.vincemann.springrapid.coretest.service;

import com.github.vincemann.springrapid.coretest.service.result.ContextAwareServiceResultMatcher;
import com.github.vincemann.springrapid.coretest.service.result.matcher.ServiceResultMatcher;
import org.junit.jupiter.api.Assertions;

import java.io.Serializable;
import java.util.Optional;

public class ExistenceMatchers {


    public static ContextAwareServiceResultMatcher notPresentInDatabase(Serializable id) {
        return (testContext) -> {
            Optional byId = testContext.getRepository().findById(id);
            Assertions.assertFalse(byId.isPresent());
        };
    }

    public static ContextAwareServiceResultMatcher presentInDatabase(Serializable id) {
        return (testContext) -> {
            Optional byId = testContext.getRepository().findById(id);
            Assertions.assertTrue(byId.isPresent());
        };
    }

}
