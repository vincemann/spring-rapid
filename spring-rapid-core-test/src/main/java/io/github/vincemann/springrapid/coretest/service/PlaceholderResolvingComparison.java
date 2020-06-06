package io.github.vincemann.springrapid.coretest.service;

import com.github.hervian.reflection.Types;
import io.github.vincemann.ezcompare.*;
import io.github.vincemann.springrapid.coretest.service.result.ServiceTestContext;
import io.github.vincemann.springrapid.coretest.service.result.matcher.ServiceResultMatcher;
import io.github.vincemann.springrapid.coretest.service.result.matcher.compare.MatcherOperationConfigurer;
import io.github.vincemann.springrapid.coretest.service.result.matcher.compare.PlaceholderResolvingActorConfigurer;
import io.github.vincemann.springrapid.coretest.service.result.matcher.resolve.EntityPlaceholder;
import io.github.vincemann.springrapid.coretest.service.result.matcher.resolve.EntityPlaceholderResolver;
import io.github.vincemann.springrapid.coretest.service.result.matcher.resolve.RapidEntityPlaceholderResolver;
import org.junit.jupiter.api.Assertions;
import org.springframework.lang.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * Adapter for {@link io.github.vincemann.ezcompare.Comparison} including support for resolving {@link EntityPlaceholder}s.
 */
public class PlaceholderResolvingComparison implements
        PlaceholderResolvingActorConfigurer, SelectedActorConfigurer,
        SelectiveOptionsConfigurer, FullCompareOptionsConfigurer, PartialCompareOptionsConfigurer, CompareOptionsConfigurer,
        PropertyBridge,
        SelectivePropertiesConfigurer, FullComparePropertyConfigurer, PartialComparePropertyConfigurer, SelectedPartialComparePropertyConfigurer,
        OperationConfigurer,
        ResultProvider
{
    //todo über config reinwiren
    private EntityPlaceholderResolver resolver = new RapidEntityPlaceholderResolver();
    private Comparison comparison;
//    private ToCall toCall = new ToCall();
    private ToResolve toResolve = new ToResolve();

//    /**
//     * Stuff that needs to be called *after* match method is called in {@link this#isEqual()} or {@link this#isNotEqual()}
//     * method.
//     */
//    static class ToCall {
//        @Nullable
//        private Object allOf;
//        private Set<String> propertiesToIgnore = new HashSet<>();
//
//    }

    /**
     * Stuff that needs to be resolved *after* match method is called in {@link MatcherOperationConfigurer}.
     */
    static class ToResolve{
        @Nullable
        private EntityPlaceholder rootPlaceholder;
        private Set<EntityPlaceholder> comparePlaceholders = new HashSet<>();
    }


    public PlaceholderResolvingComparison(Object rootEntity) {
        this.comparison = (Comparison) Comparison.compare(rootEntity);
    }

    public PlaceholderResolvingComparison(@Nullable EntityPlaceholder rootPlaceholder) {
        this.toResolve.rootPlaceholder=rootPlaceholder;
        this.comparison = (Comparison) Comparison.compare(null);
    }

    @Override
    public FullCompareOptionsConfigurer ignoreNull(boolean value) {
        comparison.ignoreNull(value);
        return this;
    }

    @Override
    public FullCompareOptionsConfigurer ignoreNotFound(boolean value) {
        comparison.ignoreNotFound(value);
        return this;
    }

    @Override
    public ResultProvider go() {

        return null;
    }

    @Override
    public FullCompareOptionsConfigurer configureFullCompare(FullCompareConfigConfigurer configurer) {
        comparison.configureFullCompare(configurer);
        return this;
    }

    @Override
    public FullComparePropertyConfigurer ignore(Types.Supplier<?>... getter) {
        comparison.ignore(getter);
        return this;
    }

    @Override
    public FullComparePropertyConfigurer ignore(String... propertyName) {
        comparison.ignore(propertyName);
        return this;
    }

    @Override
    public PartialCompareOptionsConfigurer configurePartial(PartialCompareConfigConfigurer configurer) {
        comparison.configurePartial(configurer);
        return this;
    }

    @Override
    public CompareOptionsConfigurer fullDiff(boolean value) {
        comparison.fullDiff(value);
        return this;
    }

    @Override
    public FullComparePropertyConfigurer all() {
        comparison.all();
        return this;
    }

    @Override
    public SelectedPartialComparePropertyConfigurer include(Types.Supplier<?>... getters) {
        comparison.include(getters);
        return this;
    }

    @Override
    public SelectedPartialComparePropertyConfigurer include(String... propertyNames) {
        comparison.include(propertyNames);
        return this;
    }

    @Override
    public SelectedActorConfigurer and() {
        comparison.and();
        return new PlaceholderResolvingComparison(comparison.getRoot()).with(comparison.getCompare());
    }


    @Override
    public RapidEqualsBuilder.Diff getDiff() {
        return null;
    }

    @Override
    public boolean isEqual() {
        return false;
    }

    @Override
    public SelectivePropertiesConfigurer properties() {
        comparison.properties();
        return this;
    }


    @Override
    public ServiceResultMatcher assertDiff(DiffAssertion assertion) {
        return context -> {
            resolveAndCompare(context);
            assertion.go(comparison.getDiff());
        };
    }

    @Override
    public ActorBridge with(Object actor) {
        return this;
    }

    @Override
    public ActorBridge with(EntityPlaceholder actor) {
        return this;
    }

    @Override
    public ServiceResultMatcher assertEqual() {
        return context -> {
            boolean equal = resolveAndCompare(context);
            Assertions.assertTrue(equal);
        };
    }

    @Override
    public ServiceResultMatcher assertNotEqual() {
        return context -> {
            boolean equal = resolveAndCompare(context);
            Assertions.assertFalse(equal);
        };
    }

    protected boolean resolveAndCompare(ServiceTestContext context){
        resolvePlaceholders(context);
        comparison.go();
        return comparison.isEqual();
    }



    private void resolvePlaceholders(ServiceTestContext context){
        //resolve root if necessary
        EntityPlaceholder rootPlaceholder = toResolve.rootPlaceholder;
        if (rootPlaceholder!=null){
            comparison.setRoot(resolver.resolve(rootPlaceholder,context));
        }
        //resolve compares if necessary
        for (EntityPlaceholder comparePlaceholder : toResolve.comparePlaceholders) {
            comparison.with(resolver.resolve(comparePlaceholder,context));
        }
//        //ignored properties?
//        for (String ignoreMe : toCall.propertiesToIgnore) {
//            comparison.ignore(ignoreMe);
//        }
    }

}
