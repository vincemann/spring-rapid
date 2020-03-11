package io.github.vincemann.generic.crud.lib.test.service.result.matcher.compare;

import com.github.hervian.reflection.Types;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.test.service.result.matcher.ServiceResultMatcher;
import io.github.vincemann.generic.crud.lib.test.service.result.matcher.compare.resolve.CompareEntityPlaceholder;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class EntityPropertyMatcher {
    private IdentifiableEntity compareRoot;
    private CompareEntityPlaceholder rootCompareResolvable;
    private Map<Method,Object> getterValueMap = new HashMap<>();

    public EntityPropertyMatcher(IdentifiableEntity compareRoot) {
        this.compareRoot = compareRoot;
    }

    public EntityPropertyMatcher(CompareEntityPlaceholder rootCompareResolvable) {
        this.rootCompareResolvable = rootCompareResolvable;
    }

    public EntityPropertyMatcher property(Types.Supplier<?> getter, Object expectedValue){
        Method method = Types.createMethod(getter);
        getterValueMap.put(method,expectedValue);
        return this;
    }

    public ServiceResultMatcher isEqual(){
        return checkGetterEquality(true);
    }

    private ServiceResultMatcher checkGetterEquality(boolean equal){
        return (testContext) -> {
            resolveToCompare(serviceResult);
            if(checkDbEntity()){
                try {
                    IdentifiableEntity dbEntity = ((IdentifiableEntity)
                            serviceResult.getServiceRequest().getService().findById(getToCompare().getId()).get());
                    compareGetterValues(dbEntity, equal);
                } catch (NoIdException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
