package io.github.vincemann.springrapid.coretest.compare.comparator;

import com.github.hervian.reflection.Types;
import io.github.vincemann.springrapid.core.util.ReflectionUtils;
import org.junit.jupiter.api.Assertions;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class CompareTemplate implements ActorConfigurer, OptionalActorConfigurer, PropertyConfigurer, IgnoringPropertyConfigurer, SelectingPropertyConfigurer, OptionalSelectingPropertyConfigurer, OperationConfigurer{
    private Object rootActor;
    private List<Object> actors = new ArrayList<>();
    private Set<String> properties = new HashSet<>();

    private CompareTemplate(Object rootActor){
        this.rootActor = rootActor;
    }

    public static CompareTemplate compare(Object rootActor){
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
        addAllProperties(o);
        return this;
    }

    @Override
    public IgnoringPropertyConfigurer all() {
        addAllProperties(rootActor);
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
    public void isEqual() {

    }

    @Override
    public void isNotEqual() {

    }

    private String getPropertyName(Types.Supplier<?> getter){
        Method method = Types.createMethod(getter);
        Assertions.assertTrue(method.getName().startsWith("get"), "Not a getter method: " + method.getName());
        String propertyName = method.getName().replaceFirst("get", "");
        return propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
    }

    private void addAllProperties(Object o){
        Arrays.stream(ReflectionUtils.getDeclaredFields(o.getClass(), true))
                .map(Field::getName)
                .forEach(fieldName -> properties.add(fieldName));
    }
}
