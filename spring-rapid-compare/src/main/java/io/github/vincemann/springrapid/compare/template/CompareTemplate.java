package io.github.vincemann.springrapid.compare.template;

import com.github.hervian.reflection.Types;
import io.github.vincemann.springrapid.commons.ReflectionUtils;
import io.github.vincemann.springrapid.compare.refeq.RapidEqualsBuilder;
import io.github.vincemann.springrapid.compare.refeq.RapidReflectionEquals;
import org.junit.jupiter.api.Assertions;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class CompareTemplate implements ActorConfigurer, OptionalActorConfigurer, PropertyConfigurer, IgnoringPropertyConfigurer, SelectingPropertyConfigurer, OptionalSelectingPropertyConfigurer, OperationConfigurer {
    private Object rootActor;
    private List<Object> actors = new ArrayList<>();
    private Set<String> properties = new HashSet<>();
    private RapidEqualsBuilder.MinimalDiff minimalDiff;

    private CompareTemplate(Object rootActor) {
        this.rootActor = rootActor;
    }

    public static ActorConfigurer compare(Object rootActor) {
        return new CompareTemplate(rootActor);
    }


    @Override
    public OptionalActorConfigurer with(Object actor) {
        actors.add(actor);
        return this;
    }

    @Override
    public PropertyConfigurer properties() {
        return this;
    }


    @Override
    public IgnoringPropertyConfigurer ignore(Types.Supplier<?> getter) {
        String propertyName = getPropertyName(getter);
        Assertions.assertTrue(properties.contains(propertyName));
        properties.remove(propertyName);
        return this;
    }

    @Override
    public IgnoringPropertyConfigurer ignore(String propertyName) {
        Assertions.assertTrue(properties.contains(propertyName));
        properties.remove(propertyName);
        return this;
    }

    @Override
    public IgnoringPropertyConfigurer allOf(Object o) {
        Assertions.assertNotNull(o);
        Assertions.assertTrue(actors.contains(o) || rootActor.equals(o));
        properties.addAll(getAllProperties(o));
        return this;
    }

    @Override
    public IgnoringPropertyConfigurer all() {
        properties.addAll(getAllProperties(rootActor));
        return this;
    }

    @Override
    public OptionalSelectingPropertyConfigurer include(Types.Supplier<?> getter) {
        properties.add(getPropertyName(getter));
        return this;
    }

    @Override
    public OptionalSelectingPropertyConfigurer include(String propertyName) {
        properties.add(propertyName);
        return this;
    }

    @Override
    public boolean isEqual() {
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
    public boolean isNotEqual() {
        return !isEqual();
    }

    private Set<String> getIgnoredProperties(){
        Set<String> ignoredProperties = getAllProperties(rootActor);
        ignoredProperties.removeAll(properties);
        return ignoredProperties;
    }

    private String getPropertyName(Types.Supplier<?> getter) {
        Method method = Types.createMethod(getter);
        Assertions.assertTrue(method.getName().startsWith("get"), "Not a getter method: " + method.getName());
        String propertyName = method.getName().replaceFirst("get", "");
        return propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
    }

    private Set<String> getAllProperties(Object o) {
        return Arrays.stream(ReflectionUtils.getDeclaredFields(o.getClass(), true))
                .map(Field::getName)
                .collect(Collectors.toSet());
    }
}
