package io.github.vincemann.springrapid.coretest.service.result.matcher.newcompare;

import com.github.hervian.reflection.Types;
import io.github.vincemann.springrapid.compare.template.*;
import io.github.vincemann.springrapid.coretest.service.result.matcher.ServiceResultMatcher;
import io.github.vincemann.springrapid.coretest.service.result.matcher.resolve.BasicEntityPlaceholderResolver;
import io.github.vincemann.springrapid.coretest.service.result.matcher.resolve.EntityPlaceholder;
import io.github.vincemann.springrapid.coretest.service.result.matcher.resolve.EntityPlaceholderResolver;

public class CompareTemplateMatcherAdapter implements MatcherActorConfigurer, MatcherOptionalActorConfigurer, MatcherPropertyConfigurer, MatcherSelectingPropertyConfigurer, MatcherIgnoringPropertyConfigurer, MatcherOptionalSelectingPropertyConfigurer, MatcherOperationConfigurer{
    //todo über config reinwiren
    private EntityPlaceholderResolver resolver = new BasicEntityPlaceholderResolver();
    private CompareTemplate compareTemplate;

    public CompareTemplateMatcherAdapter(CompareTemplate compareTemplate) {
        this.compareTemplate = compareTemplate;
    }

    public static CompareTemplateMatcherAdapter compare(Object root) {
        return new CompareTemplateMatcherAdapter((CompareTemplate)CompareTemplate.compare(root));
    }

    @Override
    public MatcherPropertyConfigurer properties() {
        compareTemplate.properties();
        return this;
    }

    @Override
    public MatcherOptionalActorConfigurer with(EntityPlaceholder actor) {
        return null;
    }

    @Override
    public MatcherOptionalActorConfigurer with(Object actor) {
        compareTemplate.with(actor);
        return this;
    }

    @Override
    public MatcherIgnoringPropertyConfigurer ignore(Types.Supplier<?> getter) {
        compareTemplate.ignore(getter);
        return this;
    }

    @Override
    public MatcherIgnoringPropertyConfigurer ignore(String propertyName) {
        compareTemplate.ignore(propertyName);
        return this;
    }

    @Override
    public MatcherIgnoringPropertyConfigurer allOf(Object o) {
        compareTemplate.allOf(o);
        return this;
    }

    @Override
    public MatcherIgnoringPropertyConfigurer all() {
        compareTemplate.all();
        return this;
    }

    @Override
    public MatcherOptionalSelectingPropertyConfigurer include(Types.Supplier<?> getter) {
        compareTemplate.include(getter);
        return this;
    }

    @Override
    public MatcherOptionalSelectingPropertyConfigurer include(String propertyName) {
        compareTemplate.include(propertyName);
        return this;
    }

    @Override
    public ServiceResultMatcher isEqual() {

    }

    @Override
    public ServiceResultMatcher isNotEqual() {

    }
}
