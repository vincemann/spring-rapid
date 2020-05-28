package io.github.vincemann.springrapid.coretest.service.result.matcher.compare.template;

import com.github.hervian.reflection.Types;
import io.github.vincemann.springrapid.compare.template.*;
import io.github.vincemann.springrapid.compare.util.MethodNameUtil;
import io.github.vincemann.springrapid.coretest.service.result.ServiceTestContext;
import io.github.vincemann.springrapid.coretest.service.result.matcher.ServiceResultMatcher;
import io.github.vincemann.springrapid.coretest.service.result.matcher.resolve.RapidEntityPlaceholderResolver;
import io.github.vincemann.springrapid.coretest.service.result.matcher.resolve.EntityPlaceholder;
import io.github.vincemann.springrapid.coretest.service.result.matcher.resolve.EntityPlaceholderResolver;
import org.junit.jupiter.api.Assertions;
import org.springframework.lang.Nullable;

import java.util.HashSet;
import java.util.Set;

public class PlaceholderResolvingCompareTemplateMatcher implements MatcherActorConfigurer, MatcherAdditionalActorConfigurer, MatcherPropertyConfigurer, MatcherSelectingPropertyConfigurer, MatcherIgnoringPropertyConfigurer, MatcherAdditionalSelectingPropertyConfigurer, MatcherOperationConfigurer{
    //todo über config reinwiren
    private EntityPlaceholderResolver resolver = new RapidEntityPlaceholderResolver();
    private CompareTemplate compareTemplate;
    private ToCall toCall = new ToCall();
    private ToResolve toResolve = new ToResolve();

    /**
     * Stuff that needs to be called *after* match method is called in {@link this#isEqual()} or {@link this#isNotEqual()}
     * method.
     */
    static class ToCall {
        @Nullable
        private Object allOf;
        private Set<String> propertiesToIgnore = new HashSet<>();

    }

    /**
     * Stuff that needs to be resolved *after* match method is called in {@link this#isEqual()} or {@link this#isNotEqual()}
     * method.
     */
    static class ToResolve{
        @Nullable
        private EntityPlaceholder rootPlaceholder;
        private Set<EntityPlaceholder> comparePlaceholders = new HashSet<>();
        @Nullable
        private EntityPlaceholder allOfPlaceholder;
    }


    public PlaceholderResolvingCompareTemplateMatcher(Object rootEntity) {
        this.compareTemplate = (CompareTemplate) CompareTemplate.compare(rootEntity);
    }

    public PlaceholderResolvingCompareTemplateMatcher(@Nullable EntityPlaceholder rootPlaceholder) {
        this.toResolve.rootPlaceholder=rootPlaceholder;
        this.compareTemplate = (CompareTemplate) CompareTemplate.compare(null);
    }

    @Override
    public MatcherPropertyConfigurer properties() {
        compareTemplate.properties();
        return this;
    }

    @Override
    public MatcherAdditionalActorConfigurer with(EntityPlaceholder actor) {
        this.toResolve.comparePlaceholders.add(actor);
        return this;
    }

    @Override
    public MatcherAdditionalActorConfigurer with(Object actor) {
        compareTemplate.with(actor);
        return this;
    }

    @Override
    public MatcherIgnoringPropertyConfigurer ignore(Types.Supplier<?> getter) {
        toCall.propertiesToIgnore.add(MethodNameUtil.propertyNameOf(getter));
        return this;
    }

    @Override
    public MatcherIgnoringPropertyConfigurer ignore(String propertyName) {
        toCall.propertiesToIgnore.add(propertyName);
        return this;
    }

    @Override
    public MatcherIgnoringPropertyConfigurer allOf(Object o) {
        this.toCall.allOf=o;
        return this;
    }

    @Override
    public MatcherIgnoringPropertyConfigurer allOf(EntityPlaceholder o) {
        this.toResolve.allOfPlaceholder=o;
        return null;
    }

    @Override
    public MatcherIgnoringPropertyConfigurer all() {
        if (toResolve.rootPlaceholder==null){
            //root actor is no placeholder
            this.toCall.allOf=compareTemplate.getRootActor();
        }else {
            //root actor is placeholder
            this.toResolve.allOfPlaceholder=toResolve.rootPlaceholder;
        }
        return this;
    }

    @Override
    public MatcherAdditionalSelectingPropertyConfigurer include(Types.Supplier<?> getter) {
        compareTemplate.include(getter);
        return this;
    }

    @Override
    public MatcherAdditionalSelectingPropertyConfigurer include(String propertyName) {
        compareTemplate.include(propertyName);
        return this;
    }

    @Override
    public ServiceResultMatcher isEqual() {
        return context -> {
            updateTemplateState(context);
            Assertions.assertTrue(compareTemplate.isEqual());
        };
    }

    @Override
    public ServiceResultMatcher isNotEqual() {
        return context -> {
            updateTemplateState(context);
            Assertions.assertTrue(compareTemplate.isNotEqual());
        };
    }



    private void updateTemplateState(ServiceTestContext context){
        //resolve root if necessary
        EntityPlaceholder rootPlaceholder = toResolve.rootPlaceholder;
        if (rootPlaceholder!=null){
            compareTemplate.setRootActor(resolver.resolve(rootPlaceholder,context));
        }
        //resolve compares if necessary
        for (EntityPlaceholder comparePlaceholder : toResolve.comparePlaceholders) {
            compareTemplate.with(resolver.resolve(comparePlaceholder,context));
        }
        //has all of been called with normal object?
        if (toCall.allOf!=null){
            compareTemplate.allOf(toCall.allOf);
        }
        //has all of been called with placeholder?
        if (toResolve.allOfPlaceholder!=null){
            compareTemplate.allOf(resolver.resolve(toResolve.allOfPlaceholder,context));
        }
        //ignored properties?
        for (String ignoreMe : toCall.propertiesToIgnore) {
            compareTemplate.ignore(ignoreMe);
        }
    }

}
