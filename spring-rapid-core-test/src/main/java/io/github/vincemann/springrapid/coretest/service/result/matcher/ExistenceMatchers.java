package io.github.vincemann.springrapid.coretest.service.result.matcher;

import io.github.vincemann.springrapid.coretest.service.ServiceTestTemplate;
import io.github.vincemann.springrapid.coretest.service.result.ServiceTestContext;
import io.github.vincemann.springrapid.coretest.service.resolve.RapidEntityPlaceholderResolver;
import io.github.vincemann.springrapid.coretest.service.resolve.EntityPlaceholder;
import io.github.vincemann.springrapid.coretest.service.resolve.EntityPlaceholderResolver;
import org.junit.jupiter.api.Assertions;

import java.io.Serializable;
import java.util.Optional;

public class ExistenceMatchers {

    private static EntityPlaceholderResolver entityPlaceholderResolver = new RapidEntityPlaceholderResolver();

    public static ServiceResultMatcher notPresentInDatabase(Serializable id) {
        return () -> {
            Optional byId = ServiceTestTemplate.getTestContext().getRepository().findById(id);
            Assertions.assertFalse(byId.isPresent());
        };
    }

    public static ServiceResultMatcher presentInDatabase(Serializable id) {
        return () -> {
            Optional byId = ServiceTestTemplate.getTestContext().getRepository().findById(id);
            Assertions.assertTrue(byId.isPresent());
        };
    }

    public static ServiceResultMatcher presentInDatabase(EntityPlaceholder placeholder) {
        return () -> {
            ServiceTestContext testContext = ServiceTestTemplate.getTestContext();
            Optional byId = testContext.getRepository().findById(
                    entityPlaceholderResolver.resolve(placeholder,testContext).getId()
            );
            Assertions.assertTrue(byId.isPresent());
        };
    }
}
