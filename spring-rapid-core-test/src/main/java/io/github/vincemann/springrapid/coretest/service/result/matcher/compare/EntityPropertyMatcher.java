package io.github.vincemann.springrapid.coretest.service.result.matcher.compare;

import com.github.hervian.reflection.Types;
import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.coretest.service.result.matcher.ServiceResultMatcher;
import io.github.vincemann.springrapid.coretest.service.result.matcher.resolve.EntityPlaceholder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.junit.jupiter.api.Assertions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Asserts GetterValues of Entity, that can either be a concrete entity or a {@link EntityPlaceholder}.
 */
public class EntityPropertyMatcher extends PlaceholderResolvingEntityMatcherContext {
    private Map<Method, Object> getterValueMap = new LinkedHashMap<>();
    private List<CompareMetaData> compareMetaData = new ArrayList<>();

    public EntityPropertyMatcher(IdentifiableEntity compareRoot) {
        super(compareRoot);
    }

    @AllArgsConstructor
    @Builder
    static class CompareMetaData{
        private Boolean equal;
        private Boolean collectionSizeCheck;
    }

    public EntityPropertyMatcher(EntityPlaceholder compareRootPlaceholder) {
        super(compareRootPlaceholder);
    }

    public EntityPropertyMatcher shouldMatch(Types.Supplier<?> getter, Object expectedValue) {
        Method method = Types.createMethod(getter);
        getterValueMap.put(method, expectedValue);
        compareMetaData.add(CompareMetaData.builder()
                .collectionSizeCheck(false)
                .equal(true)
                .build());
        return this;
    }

    public EntityPropertyMatcher shouldNotMatch(Types.Supplier<?> getter, Object unexpected) {
        Method method = Types.createMethod(getter);
        getterValueMap.put(method, unexpected);
        compareMetaData.add(CompareMetaData.builder()
                .collectionSizeCheck(false)
                .equal(false)
                .build());
        return this;
    }

    public EntityPropertyMatcher shouldMatchSize(Types.Supplier<?> getter, int collectionSize) {
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
                    Object actualValue = getterValuePair.getKey().invoke(getCompareRoot());
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
}
