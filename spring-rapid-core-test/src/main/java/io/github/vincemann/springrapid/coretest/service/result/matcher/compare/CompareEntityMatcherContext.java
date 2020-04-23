package io.github.vincemann.springrapid.coretest.service.result.matcher.compare;

import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.coretest.service.result.ServiceTestContext;
import io.github.vincemann.springrapid.coretest.service.result.matcher.resolve.EntityPlaceholder;

import java.util.ArrayList;
import java.util.List;

public class CompareEntityMatcherContext extends PlaceholderResolvingEntityMatcherContext {
    private List<IdentifiableEntity> compareTos = new ArrayList<>();
    private List<EntityPlaceholder> compareToPlaceholders = new ArrayList<>();

    public CompareEntityMatcherContext(IdentifiableEntity compareRoot) {
        super(compareRoot);
    }

    public CompareEntityMatcherContext(EntityPlaceholder compareRootPlaceholder) {
        super(compareRootPlaceholder);
    }

    public CompareEntityMatcherContext with(IdentifiableEntity compareTo) {
        compareTos.add(compareTo);
        return this;
    }

    public CompareEntityMatcherContext with(EntityPlaceholder compareTo) {
        compareToPlaceholders.add(compareTo);
        return this;
    }

    public CompareEntityMatcherContext withDbEntity() {
        compareToPlaceholders.add(EntityPlaceholder.DB_ENTITY);
        return this;
    }
    public CompareEntityMatcherContext withServiceReturnedEntity() {
        compareToPlaceholders.add(EntityPlaceholder.SERVICE_RETURNED_ENTITY);
        return this;
    }

    public CompareEntityMatcherContext withServiceInputEntity() {
        compareToPlaceholders.add(EntityPlaceholder.SERVICE_INPUT_ENTITY);
        return this;
    }


    protected List<IdentifiableEntity> getCompareTos() {
        return compareTos;
    }

    protected List<EntityPlaceholder> getCompareToPlaceholders() {
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
        for (EntityPlaceholder compareToPlaceholder : compareToPlaceholders) {
            compareTos.add(getResolver().resolve(compareToPlaceholder, testContext));
        }
    }







}
