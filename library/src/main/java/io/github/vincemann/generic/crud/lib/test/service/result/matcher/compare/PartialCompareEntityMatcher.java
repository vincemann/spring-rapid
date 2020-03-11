package io.github.vincemann.generic.crud.lib.test.service.result.matcher.compare;

import com.github.hervian.reflection.Types;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.service.result.matcher.ServiceResultMatcher;
import io.github.vincemann.generic.crud.lib.util.MethodNameUtil;
import org.junit.jupiter.api.Assertions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class PartialCompareEntityMatcher extends CompareEntityMatcher{
    private List<String> propertiesToCheck = new ArrayList<>();

    public PartialCompareEntityMatcher(CompareEntityMatcherContext compareEntityContext) {
        super(compareEntityContext);
    }

    //include functions -> getter:value pairs zwischenspeichern und dann einfach auf allen entities die getter aufrufen und gucken ob werte gleich sind..
    public PartialCompareEntityMatcher include(Types.Supplier<?> getter) {
        propertiesToCheck.add(MethodNameUtil.propertyNameOf(getter));
        return this;
    }

    public PartialCompareEntityMatcher include(String property) {
        propertiesToCheck.add(property);
        return this;
    }

    public ServiceResultMatcher isEqual() {
        return performCompare(true);
    }


    public ServiceResultMatcher isNotEqual() {
        return performCompare(false);
    }

    private ServiceResultMatcher performCompare(boolean equal) {
        return testContext -> {
            getCompareEntityContext().resolvePlaceholders(testContext);
            try {
                IdentifiableEntity compareRoot = getCompareEntityContext().getCompareRoot();
                for (String propertyName : propertiesToCheck) {
                    Method getter = compareRoot.getClass().getMethod("get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1));
                    Object expected = getter.invoke(compareRoot);
                    List<IdentifiableEntity> compareTos = getCompareEntityContext().getCompareTos();
                    for (IdentifiableEntity compareTo : compareTos) {
                        Object actual = getter.invoke(compareTo);
                        if(equal) {
                            Assertions.assertEquals(expected, actual);
                        }else {
                            Assertions.assertNotEquals(expected,actual);
                        }
                    }
                }
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException("Could not find getter of property", e);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalArgumentException("Error while calling getter for property comparison");
            }
        };
    }
}
