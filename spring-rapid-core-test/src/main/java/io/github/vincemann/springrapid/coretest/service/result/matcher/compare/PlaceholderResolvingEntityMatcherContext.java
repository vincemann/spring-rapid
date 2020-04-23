package io.github.vincemann.springrapid.coretest.service.result.matcher.compare;

import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.coretest.service.result.ServiceTestContext;
import io.github.vincemann.springrapid.coretest.service.result.matcher.resolve.BasicEntityPlaceholderResolver;
import io.github.vincemann.springrapid.coretest.service.result.matcher.resolve.EntityPlaceholder;
import io.github.vincemann.springrapid.coretest.service.result.matcher.resolve.EntityPlaceholderResolver;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;

/**
 * Context for entity matching, that has a {@link EntityPlaceholderResolver}.
 */
@AllArgsConstructor
public class PlaceholderResolvingEntityMatcherContext {
    private EntityPlaceholderResolver resolver = new BasicEntityPlaceholderResolver();
    private IdentifiableEntity compareRoot;
    private EntityPlaceholder compareRootPlaceholder;

    protected EntityPlaceholderResolver getResolver() {
        return resolver;
    }

    protected IdentifiableEntity getCompareRoot() {
        return compareRoot;
    }

    protected EntityPlaceholder getCompareRootPlaceholder() {
        return compareRootPlaceholder;
    }

    public PlaceholderResolvingEntityMatcherContext(IdentifiableEntity compareRoot) {
        this.compareRoot = compareRoot;
    }

    public PlaceholderResolvingEntityMatcherContext(EntityPlaceholder compareRootPlaceholder) {
        this.compareRootPlaceholder = compareRootPlaceholder;
    }

    protected void resolveCompareRoot(ServiceTestContext testContext){
        if(compareRootPlaceholder !=null){
            Assertions.assertNull(compareRoot,"cannot specify compare root entity and compare root entity placeholder at the same time");
            this.compareRoot = getResolver().resolve(compareRootPlaceholder,testContext);
        }
    }
}
