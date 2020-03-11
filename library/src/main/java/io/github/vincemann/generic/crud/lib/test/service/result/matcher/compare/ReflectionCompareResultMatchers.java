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
 * Compare all properties of either returned entity (by service) or specified entity (usually input entity e for service operation i.e. save(e)).
 * Some properties can be ignored.
 * Compare with db entity.
 */
public class ReflectionCompareResultMatchers extends AbstractCompareResultMatchers<ReflectionCompareResultMatchers> {

    private EntityReflectionComparator reflectionComparator;

    public ReflectionCompareResultMatchers(IdentifiableEntity entity) {
        super(entity);
        reflectionComparator  = new EntityReflectionComparator(EntityReflectionComparator.EQUALS_FOR_ENTITIES());
    }

    public ReflectionCompareResultMatchers() {
        this(null);
    }


    public static ReflectionCompareResultMatchers fullCompare(IdentifiableEntity entity) {
        return new ReflectionCompareResultMatchers(entity);
    }

    /**
     * Using service Result as object to compare.
     * @return
     */
    public static ReflectionCompareResultMatchers fullCompare() {
        return new ReflectionCompareResultMatchers();
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
        return (serviceResult, context, repository) -> {
            resolveToCompare(serviceResult);
            if (checkDbEntity()) {
                try {
                    IdentifiableEntity result = ((IdentifiableEntity)
                            serviceResult.getServiceRequest().getService().findById(getToCompare().getId()).get());
                    boolean equal = reflectionComparator.isEqual(getToCompare(), result);
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
