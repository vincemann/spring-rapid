package io.github.vincemann.generic.crud.lib.test.compare;

import de.danielbechler.diff.ObjectDifferBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtilsBean;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@Setter
public class EntityFullComparator extends AbstractEntityComparator implements FullComparator<Object> {

    private List<String> excluded = new ArrayList<>();

    @Override
    public boolean isEqual(Object expected, Object actual) {
        exclude();
        return super.isEqual(expected, actual);
    }

    private void exclude() {
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
        excluded.clear();
        setObjectDiffer(createDefaultBuilder().build());
    }

    public void ignoreProperty(String property){
        excluded.add(property);
    }

}
