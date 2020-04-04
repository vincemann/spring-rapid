package io.github.vincemann.springrapid.core.test.service.result.matcher.compare;

import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.test.service.result.ServiceTestContext;
import io.github.vincemann.springrapid.core.test.service.result.matcher.compare.resolve.CompareEntityPlaceholder;

import java.util.ArrayList;
import java.util.List;

public class CompareEntityMatcherContext extends PlaceholderResolvingEntityMatcherContext {
    private List<IdentifiableEntity> compareTos = new ArrayList<>();
    private List<CompareEntityPlaceholder> compareToPlaceholders = new ArrayList<>();

    public CompareEntityMatcherContext(IdentifiableEntity compareRoot) {
        super(compareRoot);
    }

    public CompareEntityMatcherContext(CompareEntityPlaceholder compareRootPlaceholder) {
        super(compareRootPlaceholder);
    }

    public CompareEntityMatcherContext with(IdentifiableEntity compareTo) {
        compareTos.add(compareTo);
        return this;
    }

    public CompareEntityMatcherContext with(CompareEntityPlaceholder compareTo) {
        compareToPlaceholders.add(compareTo);
        return this;
    }

    public CompareEntityMatcherContext withDbEntity() {
        compareToPlaceholders.add(CompareEntityPlaceholder.DB_ENTITY);
        return this;
    }
    public CompareEntityMatcherContext withServiceReturnedEntity() {
        compareToPlaceholders.add(CompareEntityPlaceholder.SERVICE_RETURNED_ENTITY);
        return this;
    }

    public CompareEntityMatcherContext withServiceInputEntity() {
        compareToPlaceholders.add(CompareEntityPlaceholder.SERVICE_INPUT_ENTITY);
        return this;
    }


    protected List<IdentifiableEntity> getCompareTos() {
        return compareTos;
    }

    protected List<CompareEntityPlaceholder> getCompareToPlaceholders() {
        return compareToPlaceholders;
    }

    public FullCompareEntityMatcher fullEqualCheck() {
        return new FullCompareEntityMatcher(this);
    }

    public PartialCompareEntityMatcher partialEqualCheck() {
        return new PartialCompareEntityMatcher(this);
    }

    protected void resolvePlaceholders(ServiceTestContext testContext) {
        resolveCompareRoot(testContext);
        for (CompareEntityPlaceholder compareToPlaceholder : compareToPlaceholders) {
            compareTos.add(getResolver().resolve(compareToPlaceholder, testContext));
        }
    }







}
