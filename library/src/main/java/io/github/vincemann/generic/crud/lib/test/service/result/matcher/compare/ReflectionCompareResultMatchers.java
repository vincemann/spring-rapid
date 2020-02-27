package io.github.vincemann.generic.crud.lib.test.service.result.matcher.compare;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.test.equalChecker.ReflectionComparator;
import io.github.vincemann.generic.crud.lib.test.service.result.matcher.EntityServiceResultMatcher;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@SuppressWarnings("rawtypes")
public class ReflectionCompareResultMatchers extends AbstractCompareResultMatchers<ReflectionCompareResultMatchers>
        implements ApplicationContextAware {

    private ReflectionComparator reflectionComparator;
    private ApplicationContext context;

    public ReflectionCompareResultMatchers(IdentifiableEntity entity) {
        super(entity);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context=applicationContext;
    }

    public static ReflectionCompareResultMatchers deepCompare(IdentifiableEntity entity){
        return new ReflectionCompareResultMatchers(entity);
    }

    public ReflectionCompareResultMatchers useComparator(ReflectionComparator fuzzyEqualChecker){
        this.reflectionComparator =fuzzyEqualChecker;
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
            if(checkReturnedEntity()){
                IdentifiableEntity result = ((IdentifiableEntity) serviceResult.getResult());
                boolean equal = reflectionComparator.isEqual(getEntity(), result);
                if(equal!=wanted){
                    throw new AssertionFailedError("Object is not fuzzyEqual to Returned, check log for details");
                }
            }
            if(checkDbEntity()){
                try {
                    IdentifiableEntity result = ((IdentifiableEntity)
                            serviceResult.getServiceRequest().getService().findById(getEntity().getId()).get());
                    boolean equal = reflectionComparator.isEqual(getEntity(), result);
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
        this.reflectionComparator = context.getBean(ReflectionComparator.class);
    }



}
