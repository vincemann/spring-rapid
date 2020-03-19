package io.github.vincemann.generic.crud.lib.test.compare;

import de.danielbechler.diff.ObjectDifferBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtilsBean;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Setter
public class EntityPropertyComparator extends AbstractEntityComparator implements PropertyComparator {

    private List<String> includedProperties = new ArrayList<>();

    private void ignoreAllPropertiesExceptIncluded(Object o1, Object o2) {
        Set<String> excluded = new HashSet<>();
        excluded.addAll(Arrays.stream(o1.getClass().getDeclaredFields()).map(Field::getName).collect(Collectors.toList()));
        excluded.addAll(Arrays.stream(o2.getClass().getDeclaredFields()).map(Field::getName).collect(Collectors.toList()));
        excluded.removeAll(includedProperties);

        ObjectDifferBuilder builder = createDefaultBuilder();

        for (String exclude : excluded) {
            builder = builder
                    .inclusion()
                    .exclude()
                    .propertyName(exclude)
                    .and();
        }
        setObjectDiffer(builder.build());
    }

    @Override
    public void reset() {
        setObjectDiffer(createDefaultBuilder().build());
        includedProperties.clear();
    }

    @Override
    public boolean isEqual(Object expected, Object actual) {
        ignoreAllPropertiesExceptIncluded(expected, actual);
        return super.isEqual(expected, actual);
    }

    @Override
    public void includeProperty(String property) {
        includedProperties.add(property);
    }


}
