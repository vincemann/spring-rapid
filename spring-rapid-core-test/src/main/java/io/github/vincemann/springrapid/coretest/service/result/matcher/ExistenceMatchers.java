package io.github.vincemann.springrapid.coretest.service.result.matcher;

import io.github.vincemann.springrapid.coretest.service.result.matcher.resolve.BasicEntityPlaceholderResolver;
import io.github.vincemann.springrapid.coretest.service.result.matcher.resolve.EntityPlaceholder;
import io.github.vincemann.springrapid.coretest.service.result.matcher.resolve.EntityPlaceholderResolver;
import org.junit.jupiter.api.Assertions;

import java.io.Serializable;
import java.util.Optional;

public class ExistenceMatchers {

    private static EntityPlaceholderResolver entityPlaceholderResolver = new BasicEntityPlaceholderResolver();

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

    public static ServiceResultMatcher presentInDatabase(EntityPlaceholder placeholder) {
        return (testContext) -> {
            Optional byId = testContext.getRepository().findById(
                    entityPlaceholderResolver.resolve(placeholder,testContext).getId()
            );
            Assertions.assertTrue(byId.isPresent());
        };
    }
}
