package io.github.vincemann.generic.crud.lib.test.equalChecker;

import org.apache.commons.beanutils.BeanUtilsBean;

import java.lang.reflect.InvocationTargetException;

public class PartialUpdate_ReflectionEqualChecker<T> extends ReflectionEqualChecker<T> {

    @Override
    public boolean isEqual(T request, T updated) {
        //copy non null values from update to entityToUpdate
        try {
            BeanUtilsBean notNull = new BeanUtilsBean();
            notNull.copyProperties(request, updated);
            return super.isEqual(request, updated);
        }catch (IllegalAccessException| InvocationTargetException e){
            throw new RuntimeException(e);
        }
    }
}
