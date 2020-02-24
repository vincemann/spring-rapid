package io.github.vincemann.generic.crud.lib.test.service.result.resultMatcher.compare;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.test.equalChecker.FuzzyComparator;
import io.github.vincemann.generic.crud.lib.test.service.result.resultMatcher.EntityServiceResultMatcher;
import junit.framework.AssertionFailedError;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@SuppressWarnings("rawtypes")
public class FuzzyCompareResultMatchers extends AbstractCompareResultMatchers<FuzzyCompareResultMatchers>
        implements ApplicationContextAware {

    private FuzzyComparator fuzzyEqualChecker;
    private ApplicationContext context;

    public FuzzyCompareResultMatchers(IdentifiableEntity entity) {
        super(entity);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context=applicationContext;
    }

    public static FuzzyCompareResultMatchers fuzzyCompare(IdentifiableEntity entity){
        return new FuzzyCompareResultMatchers(entity);
    }

    public FuzzyCompareResultMatchers useComparator(FuzzyComparator fuzzyEqualChecker){
        this.fuzzyEqualChecker=fuzzyEqualChecker;
        return this;
    }


    public EntityServiceResultMatcher isEqual(){
        init();
        return performEqualCheck(true);
    }

    public EntityServiceResultMatcher isNotEqual(){
        init();
        return performEqualCheck(false);
    }



    private EntityServiceResultMatcher performEqualCheck(boolean wanted){
        return serviceResult -> {
            if(isCheckReturnedEntity()){
                IdentifiableEntity result = ((IdentifiableEntity) serviceResult.getResult());
                boolean equal = fuzzyEqualChecker.isFuzzyEqual(getEntity(), result);
                if(equal!=wanted){
                    throw new AssertionFailedError("Object is not fuzzyEqual to Returned, check log for details");
                }
            }
            if(isCheckDbEntity()){
                try {
                    IdentifiableEntity result = ((IdentifiableEntity)
                            serviceResult.getServiceRequest().getService().findById(getEntity().getId()).get());
                    boolean equal = fuzzyEqualChecker.isFuzzyEqual(getEntity(), result);
                    if(equal!=wanted){
                        throw new AssertionFailedError("Object is not fuzzyEqual to Db Entity, check log for details");
                    }
                } catch (NoIdException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
    private void init(){
        this.fuzzyEqualChecker = context.getBean(FuzzyComparator.class);
    }



}
