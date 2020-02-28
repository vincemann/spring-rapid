package io.github.vincemann.generic.crud.lib.test.service.result.matcher.compare;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import org.junit.jupiter.api.Assertions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class PropertyCompareResultMatchers extends AbstractCompareResultMatchers<PropertyCompareResultMatchers>{

    private List<Supplier<Object>> gettersToCompare = new ArrayList<>();

    public PropertyCompareResultMatchers(IdentifiableEntity entity) {
        super(entity);
    }

    public PropertyCompareResultMatchers property(Supplier<Object> getter){
        gettersToCompare.add(getter);
        return this;
    }

    public static PropertyCompareResultMatchers compare(IdentifiableEntity entity){
        return new PropertyCompareResultMatchers(entity);
    }

    public ServiceResultMatcher isEqual(){
        return checkGetterEquality(true);
    }

    public ServiceResultMatcher isNotEqual(){
        return checkGetterEquality(false);
    }

    public ServiceResultMatcher is(Object value){
        if(gettersToCompare.size()!=1){
            throw new IllegalArgumentException("Cant compare multiple getters to one value");
        }
        return (serviceResult,context) -> Assertions.assertEquals(value,gettersToCompare.stream().findFirst().get().get());
    }



    public ServiceResultMatcher sizeIs(Integer value){
        if(gettersToCompare.size()!=1){
            throw new IllegalArgumentException("Cant compare multiple getters to one value");
        }
        return (serviceResult,context) -> Assertions.assertEquals(value, ((Collection) gettersToCompare.stream().findFirst().get().get()).size());
    }



    private ServiceResultMatcher checkGetterEquality(boolean equal){
        return (serviceResult,context) -> {
            if(checkReturnedEntity()){
                IdentifiableEntity returnedEntity = ((IdentifiableEntity) serviceResult.getResult());
                assertGetterValues(returnedEntity,"Returned Entity",equal);
            }
            if(checkDbEntity()){
                try {
                    IdentifiableEntity dbEntity = ((IdentifiableEntity)
                            serviceResult.getServiceRequest().getService().findById(getEntity().getId()).get());
                    assertGetterValues(dbEntity,"dbEntity",equal);
                } catch (NoIdException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    private void assertGetterValues(IdentifiableEntity actual, String actualType, boolean equal){
        try {
            for (Supplier<Object> getter : gettersToCompare) {
                Object entityValue = getter.get();
                String getterMethodName = getter.toString();
                Object returnedEntityValue = actual.getClass().getDeclaredMethod(getterMethodName).invoke(actual);
                if(equal) {
                    Assertions.assertEquals(entityValue, returnedEntityValue, "Property value from expected entity: " + entityValue
                            + " is not equal to property value from " + actualType + ": " + returnedEntityValue);
                }else {
                    Assertions.assertNotEquals(entityValue, returnedEntityValue, "Property value from expected entity: " + entityValue
                            + " is equal to property value from " + actualType + ": " + returnedEntityValue);
                }
            }
        }catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e){
            throw new RuntimeException("Did not find getter by name",e);
        }

    }

}
