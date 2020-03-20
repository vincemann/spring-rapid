package io.github.vincemann.generic.crud.lib.test.compare;

import io.github.vincemann.generic.crud.lib.util.ReflectionUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Setter
public class LevelOneFullComparator extends AbstractEntityComparator implements FullComparator<Object> {

    private List<String> excluded = new ArrayList<>();
    private boolean silentIgnore = false;

    @Override
    public boolean isEqual(Object expected, Object actual) {
        Set<String> included = new HashSet<>();
        included.addAll(getAllProperties(expected));
        included.addAll(getAllProperties(actual));
        included.removeAll(excluded);
        included.remove("this$0");

        compare(expected,actual,included,silentIgnore);
        return super.isEquals(expected,actual);
    }

    private List<String> getAllProperties(Object instance){
        return Arrays.stream(ReflectionUtils.getDeclaredFields(instance.getClass(),true))
                .map(Field::getName).collect(Collectors.toList());
    }

    @Override
    public void reset() {
        excluded.clear();
        getDiff().clear();
    }

    public void ignoreProperty(String property){
        excluded.add(property);
    }

}
