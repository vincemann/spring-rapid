package io.github.vincemann.springrapid.compare.template;

import com.github.hervian.reflection.Types;
import io.github.vincemann.springrapid.commons.ReflectionUtils;
import io.github.vincemann.springrapid.compare.refeq.RapidEqualsBuilder;
import io.github.vincemann.springrapid.compare.refeq.RapidReflectionEquals;
import io.github.vincemann.springrapid.compare.util.MethodNameUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Slf4j
public class CompareTemplate implements ActorConfigurer, AdditionalActorConfigurer, PropertyConfigurer, IgnoringPropertyConfigurer, SelectingPropertyConfigurer, AdditionalSelectingPropertyConfigurer, OperationConfigurer {
    public static Set<String> ALWAYS_IGNORE = new HashSet<>();

    private Object rootActor;
    private List<Object> actors = new ArrayList<>();
    private Set<String> properties = new HashSet<>();
    private Set<String> explicitlyIncluded = new HashSet<>();
    @Getter
    private RapidEqualsBuilder.MinimalDiff minimalDiff;

    protected CompareTemplate(Object rootActor) {
        this.rootActor = rootActor;
    }

    public static ActorConfigurer compare(Object rootActor) {
        return new CompareTemplate(rootActor);
    }


    @Override
    public AdditionalActorConfigurer with(Object actor) {
        actors.add(actor);
        return this;
    }

    @Override
    public PropertyConfigurer properties() {
        return this;
    }


    @Override
    public IgnoringPropertyConfigurer ignore(Types.Supplier<?> getter) {
        String propertyName = MethodNameUtil.propertyNameOf(getter);
        Assertions.assertTrue(properties.contains(propertyName),"No Property known named: " + propertyName + " that could be ignored.");
        properties.remove(propertyName);
        return this;
    }

    @Override
    public IgnoringPropertyConfigurer ignore(String propertyName) {
        Assertions.assertTrue(properties.contains(propertyName),"No Property known named: " + propertyName + " that could be ignored.");
        properties.remove(propertyName);
        return this;
    }

    @Override
    public IgnoringPropertyConfigurer allOf(Object o) {
        Assertions.assertNotNull(o);
        Assertions.assertTrue(actors.contains(o) || rootActor.equals(o));
        properties.addAll(ReflectionUtils.getProperties(o.getClass()));
        return this;
    }

    @Override
    public IgnoringPropertyConfigurer all() {
        properties.addAll(ReflectionUtils.getProperties(rootActor.getClass()));
        return this;
    }

    @Override
    public AdditionalSelectingPropertyConfigurer include(Types.Supplier<?> getter) {
        include(MethodNameUtil.propertyNameOf(getter));
        return this;
    }

    @Override
    public AdditionalSelectingPropertyConfigurer include(String propertyName) {
        properties.add(propertyName);
        explicitlyIncluded.add(propertyName);
        return this;
    }

    @Override
    public boolean isEqual() {
        ignoreDefaultProperties();
        RapidReflectionEquals equalMatcher = new RapidReflectionEquals(rootActor, getIgnoredProperties().toArray(new String[0]));
        boolean finalEqual = true;
        for (Object actor : actors) {
            boolean equal = equalMatcher.matches(actor);
            if (!equal) {
                finalEqual = false;
            }
//            Assertions.assertTrue(equal,"Objects differ. See last log above for diff or enable logging for: io.github.vincemann.springrapid.coretest");
        }
        this.minimalDiff = equalMatcher.getMinimalDiff();
        return finalEqual;
    }

    @Override
    public void assertEqual() {
        Assertions.assertTrue(isEqual());
    }

    @Override
    public RapidEqualsBuilder.MinimalDiff assertNotEqual() {
        Assertions.assertFalse(isEqual());
        return minimalDiff;
    }

    protected void ignoreDefaultProperties(){
        for (String ignoreMe : ALWAYS_IGNORE) {
            if (!explicitlyIncluded.contains(ignoreMe))
                properties.remove(ignoreMe);
            else
                log.debug("Explicitly included property on always ignore list: " +ignoreMe + "-> will compare property.");
        }
    }

    @Override
    public boolean isNotEqual() {
        return !isEqual();
    }

    private Set<String> getIgnoredProperties(){
        Set<String> ignoredProperties = ReflectionUtils.getProperties(rootActor.getClass());
        ignoredProperties.removeAll(properties);
        return ignoredProperties;
    }


}
