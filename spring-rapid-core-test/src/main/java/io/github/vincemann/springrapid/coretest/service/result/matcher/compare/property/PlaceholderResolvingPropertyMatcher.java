package io.github.vincemann.springrapid.coretest.service.result.matcher.compare.property;

import com.github.hervian.reflection.Types;
import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.coretest.service.result.ServiceTestContext;
import io.github.vincemann.springrapid.coretest.service.result.matcher.ServiceResultMatcher;
import io.github.vincemann.springrapid.coretest.service.result.matcher.resolve.RapidEntityPlaceholderResolver;
import io.github.vincemann.springrapid.coretest.service.result.matcher.resolve.EntityPlaceholder;
import io.github.vincemann.springrapid.coretest.service.result.matcher.resolve.EntityPlaceholderResolver;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.junit.jupiter.api.Assertions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Asserts GetterValues of Entity, that can either be a concrete entity or a {@link EntityPlaceholder}.
 */
public class PlaceholderResolvingPropertyMatcher {
    private EntityPlaceholderResolver resolver = new RapidEntityPlaceholderResolver();
    private Object compareRoot;
    private EntityPlaceholder compareRootPlaceholder;
    private Map<Method, Object> getterValueMap = new LinkedHashMap<>();
    private List<CompareMetaData> compareMetaData = new ArrayList<>();

    public PlaceholderResolvingPropertyMatcher(IdentifiableEntity compareRoot) {
        this.compareRoot=compareRoot;
    }

    @AllArgsConstructor
    @Builder
    static class CompareMetaData{
        private Boolean equal;
        private Boolean collectionSizeCheck;
    }

    public PlaceholderResolvingPropertyMatcher(EntityPlaceholder compareRootPlaceholder) {
        this.compareRootPlaceholder=compareRootPlaceholder;
    }

    public PlaceholderResolvingPropertyMatcher shouldMatch(Types.Supplier<?> getter, Object expectedValue) {
        Method method = Types.createMethod(getter);
        getterValueMap.put(method, expectedValue);
        compareMetaData.add(CompareMetaData.builder()
                .collectionSizeCheck(false)
                .equal(true)
                .build());
        return this;
    }

    public PlaceholderResolvingPropertyMatcher shouldNotMatch(Types.Supplier<?> getter, Object unexpected) {
        Method method = Types.createMethod(getter);
        getterValueMap.put(method, unexpected);
        compareMetaData.add(CompareMetaData.builder()
                .collectionSizeCheck(false)
                .equal(false)
                .build());
        return this;
    }

    public PlaceholderResolvingPropertyMatcher shouldMatchSize(Types.Supplier<?> getter, int collectionSize) {
        Method method = Types.createMethod(getter);
        getterValueMap.put(method, collectionSize);
        compareMetaData.add(CompareMetaData.builder()
                .collectionSizeCheck(true)
                .equal(true)
                .build());
        return this;
    }

    public ServiceResultMatcher go(){
        return assertGetterValueEquality();
    }

    private ServiceResultMatcher assertGetterValueEquality() {
        return (testContext) -> {
            resolveCompareRoot(testContext);
            int index = 0;
            try {
                for (Map.Entry<Method, Object> getterValuePair : getterValueMap.entrySet()) {
                    Object actualValue = getterValuePair.getKey().invoke(compareRoot);
                    CompareMetaData compareMetaData = this.compareMetaData.get(index);
                    if(compareMetaData.collectionSizeCheck){
                        assertEqualCollectionSizes((Integer) getterValuePair.getValue(),actualValue);
                        return;
                    }
                    if(compareMetaData.equal) {
                        Assertions.assertEquals(getterValuePair.getValue(), actualValue);
                    }else {
                        Assertions.assertNotEquals(getterValuePair.getValue(), actualValue);
                    }
                    index++;
                }
            } catch (IllegalAccessException|InvocationTargetException e){
                throw new RuntimeException(e);
            }
        };
    }

    private void assertEqualCollectionSizes(int expectedSize, Object actualCollection){
        Assertions.assertTrue(actualCollection instanceof Collection);
        Assertions.assertEquals(expectedSize, ((Collection) actualCollection).size());
    }

    private void resolveCompareRoot(ServiceTestContext testContext){
        if(compareRootPlaceholder !=null){
            Assertions.assertNull(compareRoot,"cannot specify compare root entity and compare root entity placeholder at the same time");
            this.compareRoot = resolver.resolve(compareRootPlaceholder,testContext);
        }
    }
}
