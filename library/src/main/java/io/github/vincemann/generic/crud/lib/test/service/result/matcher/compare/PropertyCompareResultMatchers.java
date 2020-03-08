package io.github.vincemann.generic.crud.lib.test.service.result.matcher.compare;

import com.github.hervian.reflection.Types;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.test.service.result.ServiceResult;
import io.github.vincemann.generic.crud.lib.test.service.result.matcher.ServiceResultMatcher;
import org.junit.jupiter.api.Assertions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Select single properties for comparision.
 */
public class PropertyCompareResultMatchers extends AbstractCompareResultMatchers<PropertyCompareResultMatchers>{

    private List<Method> gettersToCompare = new ArrayList<>();

    public PropertyCompareResultMatchers(IdentifiableEntity entity) {
        super(entity);
    }

    public PropertyCompareResultMatchers property(Types.Supplier<?> getter){
        Method method = Types.createMethod(getter);
        gettersToCompare.add(method);
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
        return (serviceResult,context) -> {
            assertWasSuccessful(serviceResult);
            try {
                assertEquals(
                        value,
                        gettersToCompare.stream().findFirst().get().invoke(getInputEntity())
                );
            } catch (IllegalAccessException|InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        };
    }



    public ServiceResultMatcher sizeIs(int value){
        if(gettersToCompare.size()!=1){
            throw new IllegalArgumentException("Cant compare multiple getters to one value");
        }
        return (serviceResult,context) -> {
            assertWasSuccessful(serviceResult);
            try {
                assertEquals(
                                value,
                                ((Collection) gettersToCompare.stream().findFirst().get().invoke(getInputEntity())).size()
                );
            } catch (IllegalAccessException|InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private ServiceResultMatcher checkGetterEquality(boolean equal){
        return (serviceResult,context) -> {
            assertWasSuccessful(serviceResult);
            if(checkDbEntity()){
                try {
                    IdentifiableEntity dbEntity = ((IdentifiableEntity)
                            serviceResult.getServiceRequest().getService().findById(getInputEntity().getId()).get());
                    compareGetterValues(dbEntity, equal);
                } catch (NoIdException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    private void assertWasSuccessful(ServiceResult serviceResult){
        if(!serviceResult.wasSuccessful()) {
            throw new AssertionError("Service method raised exception : " + serviceResult.getRaisedException());
        }
    }

    private void compareGetterValues(IdentifiableEntity checkedAgainst, boolean equal){
        try {
            for (Method getter : gettersToCompare) {
                Object entityValue = getter.invoke(getInputEntity());
                Object checkedAgainstValue = getter.invoke(checkedAgainst);
                if(equal) {
                    assertEquals(entityValue, checkedAgainstValue, "Property value from expected entity: " + entityValue
                            + " is not equal to property value from " + "dbEntity" + ": " + checkedAgainstValue);
                }else {
                    Assertions.assertNotEquals(entityValue, checkedAgainstValue, "Property value from expected entity: " + entityValue
                            + " is equal to property value from " + "dbEntity" + ": " + checkedAgainstValue);
                }
            }
        }catch (IllegalAccessException | InvocationTargetException e){
            throw new RuntimeException("Did not find getter by name",e);
        }

    }

}
