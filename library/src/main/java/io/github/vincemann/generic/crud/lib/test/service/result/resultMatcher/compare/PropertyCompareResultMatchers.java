package io.github.vincemann.generic.crud.lib.test.service.result.resultMatcher.compare;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.test.service.result.resultMatcher.EntityServiceResultMatcher;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PropertyCompareResultMatchers extends AbstractCompareResultMatchers<PropertyCompareResultMatchers>{

    private List<Function<IdentifiableEntity,?>> gettersToCompare = new ArrayList<>();

    public PropertyCompareResultMatchers(IdentifiableEntity entity) {
        super(entity);
    }

    public PropertyCompareResultMatchers value(Function<IdentifiableEntity,?> getter){
        gettersToCompare.add(getter);
        return this;
    }

    public static PropertyCompareResultMatchers compare(IdentifiableEntity entity){
        return new PropertyCompareResultMatchers(entity);
    }

    public EntityServiceResultMatcher isEqual(){
        return checkGetterEquality(true);
    }

    public EntityServiceResultMatcher isNotEqual(){
        return checkGetterEquality(false);
    }

    private EntityServiceResultMatcher checkGetterEquality(boolean equal){
        return serviceResult -> {
            if(checkReturnedEntity()){
                IdentifiableEntity returnedEntity = ((IdentifiableEntity) serviceResult.getResult());
                assertGetterValues(getEntity(),returnedEntity,"Returned Entity",equal);
            }
            if(checkDbEntity()){
                try {
                    IdentifiableEntity dbEntity = ((IdentifiableEntity)
                            serviceResult.getServiceRequest().getService().findById(getEntity().getId()).get());
                    assertGetterValues(getEntity(),dbEntity,"dbEntity",equal);
                } catch (NoIdException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    private void assertGetterValues(IdentifiableEntity expected, IdentifiableEntity actual, String actualType, boolean equal){
        for (Function<IdentifiableEntity, ?> getter : gettersToCompare) {
            Object entityValue = getter.apply(expected);
            Object returnedEntityValue = getter.apply(actual);
            if(equal) {
                Assertions.assertEquals(entityValue, returnedEntityValue, "Property value from expected entity: " + entityValue
                        + " is not equal to property value from " + actualType + ": " + returnedEntityValue);
            }else {
                Assertions.assertNotEquals(entityValue, returnedEntityValue, "Property value from expected entity: " + entityValue
                        + " is equal to property value from " + actualType + ": " + returnedEntityValue);
            }
        }
    }

}
