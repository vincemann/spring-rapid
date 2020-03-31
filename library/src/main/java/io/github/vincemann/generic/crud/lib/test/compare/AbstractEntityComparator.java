package io.github.vincemann.generic.crud.lib.test.compare;

import io.github.vincemann.generic.crud.lib.util.ReflectionUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;

import java.lang.reflect.Field;
import java.util.*;

@Getter
@Setter
@Slf4j
public abstract class AbstractEntityComparator {
    private Map<String, List<Object>> diff = new LinkedHashMap<>();
    private static Map<Class,List<Field>> fieldCache = new HashMap<>();

    protected boolean isEquals(Object expected, Object actual) {
        boolean equals = diff.isEmpty();
        if (equals) {
            log.debug("Instance: " + expected + " and " + actual + " are level 1 reflectionEqual");
        } else {
            log.debug("Instance: " + expected + " and " + actual + " are not level 1 reflectionEqual, changes:");
            for (Map.Entry<String, List<Object>> propertyValueEntry : getDiff().entrySet()) {
                log.debug("property: " + propertyValueEntry.getKey()
                        + ", expected: " + propertyValueEntry.getValue().get(0)
                        + ", actual: " + propertyValueEntry.getValue().get(1));
            }
        }
        return equals;
    }

    protected void compare(Object expected, Object actual, Collection<String> properties, boolean silentIgnore) {
        try {
            for (String property : properties) {
                try {
                    Field expectedField = findField(expected.getClass(),property);
                    Field actualField = findField(actual.getClass(),property);
                    expectedField.setAccessible(true);
                    actualField.setAccessible(true);
                    Object expectedValue = expectedField.get(expected);
                    Object actualValue = actualField.get(actual);
                    boolean equals = Objects.equals(expectedValue,actualValue);
                    if (!equals) {
                        getDiff().put(property, Lists.newArrayList(expectedValue, actualValue));
                    }
                }catch (FieldNotFoundException e){
                    if(silentIgnore) {
                        log.debug("Field: " + property + " not found, silent ignoring");
                        continue;
                    }else {
                        throw new RuntimeException(e);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Field findField(Class clazz, String property) throws FieldNotFoundException{
        List<Field> cachedFields = fieldCache.get(clazz);
        if(cachedFields==null){
            cachedFields = Lists.newArrayList(ReflectionUtils.getDeclaredFields(clazz,true));
        }
        Optional<Field> field = cachedFields.stream().filter(f -> f.getName().equals(property)).findFirst();
        if(!field.isPresent()){
            throw new FieldNotFoundException("Field not found: " + property + " class: " + clazz);
        }
        return field.get();
    }
}
