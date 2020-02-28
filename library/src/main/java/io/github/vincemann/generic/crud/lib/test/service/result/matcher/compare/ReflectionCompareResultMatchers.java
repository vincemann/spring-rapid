package io.github.vincemann.generic.crud.lib.test.service.result.matcher.compare;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.test.deepCompare.ReflectionComparator;
import org.opentest4j.AssertionFailedError;
import org.springframework.context.ApplicationContext;

@SuppressWarnings("rawtypes")
public class ReflectionCompareResultMatchers extends AbstractCompareResultMatchers<ReflectionCompareResultMatchers> {

    private ReflectionComparator reflectionComparator;

    public ReflectionCompareResultMatchers(IdentifiableEntity entity) {
        super(entity);
    }

    public static ReflectionCompareResultMatchers deepCompare(IdentifiableEntity entity) {
        return new ReflectionCompareResultMatchers(entity);
    }


    public ReflectionCompareResultMatchers useComparator(ReflectionComparator fuzzyEqualChecker) {
        this.reflectionComparator = fuzzyEqualChecker;
        return this;
    }


    public ServiceResultMatcher isEqual() {
        return performDeepCompare(true);
    }

    public ServiceResultMatcher isNotEqual() {
        return performDeepCompare(false);
    }


    private ServiceResultMatcher performDeepCompare(boolean wanted) {
        return (serviceResult,context) -> {
            init(context);
            if (checkReturnedEntity()) {
                IdentifiableEntity result = serviceResult.getExpectedResult();
                boolean equal = reflectionComparator.isEqual(getEntity(), result);
                if (equal != wanted) {
                    throw new AssertionFailedError("Object is not deep equal to Returned, check log for details");
                }
            }
            if (checkDbEntity()) {
                try {
                    IdentifiableEntity result = ((IdentifiableEntity)
                            serviceResult.getServiceRequest().getService().findById(getEntity().getId()).get());
                    boolean equal = reflectionComparator.isEqual(getEntity(), result);
                    if (equal != wanted) {
                        throw new AssertionFailedError("Object is not deep to Db Entity, check log for details");
                    }
                } catch (NoIdException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    private void init(ApplicationContext context) {
        if (reflectionComparator == null)
            this.reflectionComparator = context.getBean(ReflectionComparator.class);
    }


}
