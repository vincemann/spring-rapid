package io.github.vincemann.generic.crud.lib.test.service.result.matcher.compare;

import com.github.hervian.reflection.Types;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.compare.FullComparator;
import io.github.vincemann.generic.crud.lib.test.service.result.matcher.ServiceResultMatcher;
import io.github.vincemann.generic.crud.lib.util.MethodNameUtil;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.junit.jupiter.api.Assertions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class FullCompareEntityMatcher extends CompareEntityMatcher {
    private FullComparator fullComparator;
    private List<String> propertiesToIgnore = new ArrayList<>();


    public FullCompareEntityMatcher(CompareEntityMatcherContext compareEntityContext) {
        super(compareEntityContext);
    }


    public FullCompareEntityMatcher ignore(Types.Supplier<?> getter) {
        propertiesToIgnore.add(MethodNameUtil.propertyNameOf(getter));
        return this;
    }

    public FullCompareEntityMatcher ignore(String property) {
        propertiesToIgnore.add(property);
        return this;
    }

    public FullCompareEntityMatcher ignoreId() {
        propertiesToIgnore.add("id");
        return this;
    }

    public ServiceResultMatcher isEqual() {
        return performReflectionCompare(true);
    }


    public ServiceResultMatcher isNotEqual() {
        return performReflectionCompare(false);
    }

    protected FullComparator getFullComparator() {
        return fullComparator;
    }

    protected List<String> getPropertiesToIgnore() {
        return propertiesToIgnore;
    }

    private ServiceResultMatcher performReflectionCompare(boolean equal) {
        return testContext -> {
            try {
                //get comparator from application context and clone it, so property ignore changes do not disturb application wide comparator
                fullComparator = (FullComparator) BeanUtilsBean.getInstance().cloneBean(
                        testContext.getApplicationContext().getBean(FullComparator.class)
                );
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
                throw new IllegalArgumentException("Could not clone stateful " + FullComparator.class.getSimpleName(),e);
            }
            getCompareEntityContext().resolvePlaceholders(testContext);
            IdentifiableEntity compareRoot = getCompareEntityContext().getCompareRoot();
            List<IdentifiableEntity> compareTos = getCompareEntityContext().getCompareTos();
            for (IdentifiableEntity compareTo : compareTos) {
                Assertions.assertTrue(fullComparator.isEqual(compareRoot, compareTo));
            }
        };
    }

}

