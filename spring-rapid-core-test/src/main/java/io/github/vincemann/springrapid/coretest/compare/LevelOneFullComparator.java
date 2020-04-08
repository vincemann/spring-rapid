package io.github.vincemann.springrapid.coretest.compare;

import io.github.vincemann.springrapid.core.util.ReflectionUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Getter
public class LevelOneFullComparator extends AbstractEntityComparator implements FullComparator<Object> {

    private List<String> excluded = new ArrayList<>();
    private boolean silentIgnore = false;
    private boolean ignoreNull =false;

    @Override
    public boolean isEqual(Object expected, Object actual) {
        Set<String> included = new HashSet<>();
        included.addAll(getAllProperties(expected));
        included.addAll(getAllProperties(actual));
        included.removeAll(excluded);
        included.remove("this$0");

        compare(expected,actual,included,silentIgnore,ignoreNull);
        return super.isEquals(expected,actual);
    }

    private List<String> getAllProperties(Object instance){
        return Arrays.stream(ReflectionUtils.getDeclaredFields(instance.getClass(),true))
                .map(Field::getName).collect(Collectors.toList());
    }

    @Override
    public void ignoreNull(boolean ignore) {
        this.ignoreNull=ignore;
    }

    @Override
    public void silentIgnore(boolean ignore) {
        this.silentIgnore=ignore;
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
