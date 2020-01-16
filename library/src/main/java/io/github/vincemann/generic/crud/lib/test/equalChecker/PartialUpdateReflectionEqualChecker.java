package io.github.vincemann.generic.crud.lib.test.equalChecker;

import io.github.vincemann.generic.crud.lib.util.NullAwareBeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

import static io.github.vincemann.generic.crud.lib.test.service.CrudServiceIntegrationTest.PARTIAL_UPDATE_EQUAL_CHECKER_QUALIFIER;

@Component
@Qualifier(PARTIAL_UPDATE_EQUAL_CHECKER_QUALIFIER)
public class PartialUpdateReflectionEqualChecker<T> extends ReflectionEqualChecker<T> {

    @Override
    public boolean isEqual(T request, T updated) {
        //copy non null values from update to entityToUpdate
        try {
            BeanUtilsBean beanUtilsBean = new BeanUtilsBean();
            T tempClone = (T)beanUtilsBean.cloneBean(updated);
            NullAwareBeanUtils.copyProperties(tempClone,request);
            return super.isEqual(tempClone, updated);
        }catch (IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException e){
            throw new RuntimeException(e);
        }
    }
}
