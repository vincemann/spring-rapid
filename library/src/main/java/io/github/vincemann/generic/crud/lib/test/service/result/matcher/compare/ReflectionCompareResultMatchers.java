package io.github.vincemann.generic.crud.lib.test.service.result.matcher.compare;

import de.danielbechler.diff.ObjectDifferBuilder;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.test.deepCompare.EntityReflectionComparator;
import io.github.vincemann.generic.crud.lib.test.service.result.matcher.ServiceResultMatcher;
import lombok.Getter;
import lombok.Setter;
import org.opentest4j.AssertionFailedError;

import java.util.function.Supplier;

@SuppressWarnings("rawtypes")
@Getter
@Setter
public class ReflectionCompareResultMatchers extends AbstractCompareResultMatchers<ReflectionCompareResultMatchers> {

    private EntityReflectionComparator reflectionComparator;

    public ReflectionCompareResultMatchers(IdentifiableEntity entity) {
        super(entity);
        reflectionComparator  = new EntityReflectionComparator(EntityReflectionComparator.EQUALS_FOR_ENTITIES());
    }

    public static ReflectionCompareResultMatchers deepCompare(IdentifiableEntity entity) {
        return new ReflectionCompareResultMatchers(entity);
    }


    public ReflectionCompareResultMatchers ignoreProperty(Supplier supplier){
        String getter = supplier.toString();
        String propertyName = "";
        if (getter.startsWith("get")){
            propertyName = getter.replace("get","");
            propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
        }else if(getter.startsWith("is")){
            propertyName = getter.replace("is","");
            propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
        }else {
            throw new IllegalArgumentException("Not a getter: " + supplier.toString());
        }
        return ignoreProperty(propertyName);
    }

    public ReflectionCompareResultMatchers ignoreProperty(String property){
        ObjectDifferBuilder builder = reflectionComparator.getBuilder()
                .inclusion()
                .exclude()
                .propertyName(property)
                .and();
        reflectionComparator.setObjectDiffer(builder.build());
        reflectionComparator.setBuilder(builder);
        return this;
    }

    /**
     * Only useful, when test is not wrapped in one test transaction.
     * Otherwise the request-, return and dbEntity do all have the same reference.
     * @return
     */
    public ReflectionCompareResultMatchers ignoreIds(){
        return ignoreProperty("id");
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
