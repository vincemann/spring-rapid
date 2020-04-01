package io.github.vincemann.generic.crud.lib.test.compare;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@Setter
public class LevelOnePropertyComparator extends AbstractEntityComparator implements PropertyComparator {
    private List<String> includedProperties = new ArrayList<>();

    @Override
    public void reset() {
        includedProperties.clear();
        getDiff().clear();
    }

    @Override
    public boolean isEqual(Object expected, Object actual) {
        getDiff().clear();
        compare(expected, actual, includedProperties, false, false);
        return super.isEquals(expected, actual);
    }

    @Override
    public void includeProperty(String property) {
        includedProperties.add(property);
    }


}
