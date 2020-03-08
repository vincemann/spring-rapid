package io.github.vincemann.generic.crud.lib.test.service.result.matcher.compare;

import com.github.hervian.reflection.Types;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.test.compare.EntityReflectionComparator;
import io.github.vincemann.generic.crud.lib.test.service.result.matcher.ServiceResultMatcher;
import lombok.Getter;
import lombok.Setter;
import org.opentest4j.AssertionFailedError;

@SuppressWarnings("rawtypes")
@Getter
@Setter
/**
 * Select all properties for comparision and maybe ignore some.
 */
public class ReflectionCompareResultMatchers extends AbstractCompareResultMatchers<ReflectionCompareResultMatchers> {

    private EntityReflectionComparator reflectionComparator;

    public ReflectionCompareResultMatchers(IdentifiableEntity entity) {
        super(entity);
        reflectionComparator  = new EntityReflectionComparator(EntityReflectionComparator.EQUALS_FOR_ENTITIES());
    }

    public static ReflectionCompareResultMatchers fullCompare(IdentifiableEntity entity) {
        return new ReflectionCompareResultMatchers(entity);
    }


    public ReflectionCompareResultMatchers ignore(Types.Supplier<?> supplier){
        getReflectionComparator().ignoreProperty(supplier);
        return this;
    }

    public ReflectionCompareResultMatchers ignore(String property){
        getReflectionComparator().ignoreProperty(property);
        return this;
    }

    /**
     * Only useful, when test is not wrapped in one test transaction.
     * Otherwise the request-, return and dbEntity do all have the same reference.
     * @return
     */
    public ReflectionCompareResultMatchers ignoreIds(){
        return ignore("id");
    }


    public ServiceResultMatcher isEqual() {
        return performDeepCompare(true);
    }

    public ServiceResultMatcher isNotEqual() {
        return performDeepCompare(false);
    }


    private ServiceResultMatcher performDeepCompare(boolean wanted) {
        return (serviceResult,context) -> {
            if (checkDbEntity()) {
                try {
                    IdentifiableEntity result = ((IdentifiableEntity)
                            serviceResult.getServiceRequest().getService().findById(getInputEntity().getId()).get());
                    boolean equal = reflectionComparator.isEqual(getInputEntity(), result);
                    if (equal != wanted) {
                        throw new AssertionFailedError("Object is not deep to Db Entity, check log for details");
                    }
                } catch (NoIdException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }


}
