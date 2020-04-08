package io.github.vincemann.springrapid.coretest.service.result.matcher.compare;

import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.coretest.service.result.ServiceTestContext;
import io.github.vincemann.springrapid.coretest.service.result.matcher.compare.resolve.BasicCompareEntityPlaceholderResolver;
import io.github.vincemann.springrapid.coretest.service.result.matcher.compare.resolve.CompareEntityPlaceholder;
import io.github.vincemann.springrapid.coretest.service.result.matcher.compare.resolve.CompareEntityPlaceholderResolver;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;

@AllArgsConstructor
public class PlaceholderResolvingEntityMatcherContext {
    private CompareEntityPlaceholderResolver resolver = new BasicCompareEntityPlaceholderResolver();
    private IdentifiableEntity compareRoot;
    private CompareEntityPlaceholder compareRootPlaceholder;

    protected CompareEntityPlaceholderResolver getResolver() {
        return resolver;
    }

    protected IdentifiableEntity getCompareRoot() {
        return compareRoot;
    }

    protected CompareEntityPlaceholder getCompareRootPlaceholder() {
        return compareRootPlaceholder;
    }

    public PlaceholderResolvingEntityMatcherContext(IdentifiableEntity compareRoot) {
        this.compareRoot = compareRoot;
    }

    public PlaceholderResolvingEntityMatcherContext(CompareEntityPlaceholder compareRootPlaceholder) {
        this.compareRootPlaceholder = compareRootPlaceholder;
    }

    protected void resolveCompareRoot(ServiceTestContext testContext){
        if(compareRootPlaceholder !=null){
            Assertions.assertNull(compareRoot,"cannot specify compare root entity and compare root entity placeholder at the same time");
            this.compareRoot = getResolver().resolve(compareRootPlaceholder,testContext);
        }
    }
}
