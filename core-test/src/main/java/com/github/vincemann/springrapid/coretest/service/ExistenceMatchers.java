package com.github.vincemann.springrapid.coretest.service;

import com.github.vincemann.springrapid.coretest.service.result.matcher.ServiceResultMatcher;
import org.junit.jupiter.api.Assertions;

import java.io.Serializable;
import java.util.Optional;

public class ExistenceMatchers {


    public static ServiceResultMatcher notPresentInDatabase(Serializable id) {
        return () -> {
            Optional byId = ServiceTestTemplate.getInstance().getContext().getRepository().findById(id);
            Assertions.assertFalse(byId.isPresent());
        };
    }

    public static ServiceResultMatcher presentInDatabase(Serializable id) {
        return () -> {
            Optional byId = ServiceTestTemplate.getInstance().getContext().getRepository().findById(id);
            Assertions.assertTrue(byId.isPresent());
        };
    }

}
