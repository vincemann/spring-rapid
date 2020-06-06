package io.github.vincemann.springrapid.coretest.config;

import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.coretest.service.ServiceTestTemplate;
import io.github.vincemann.springrapid.coretest.service.resolve.EntityPlaceholder;
import io.github.vincemann.springrapid.coretest.service.resolve.EntityPlaceholderResolver;
import io.github.vincemann.springrapid.coretest.service.result.ServiceTestContext;
import org.junit.jupiter.api.Assertions;

public class GlobalEntityPlaceholderResolver {

    private GlobalEntityPlaceholderResolver(){}

    private static EntityPlaceholderResolver resolver;

    public static EntityPlaceholderResolver getResolver() {
        return resolver;
    }

    static void setResolver(EntityPlaceholderResolver resolver){
        GlobalEntityPlaceholderResolver.resolver=resolver;
    }


    public static <E extends IdentifiableEntity> E resolve(EntityPlaceholder entityPlaceholder){
        ServiceTestContext testContext = ServiceTestTemplate.getTestContext();
        Assertions.assertNotNull(resolver);
        Assertions.assertNotNull(testContext);
        return resolver.resolve(entityPlaceholder, testContext);
    }
}
