package io.github.vincemann.generic.crud.lib.util;

import junit.framework.AssertionFailedError;
import org.modelmapper.ModelMapper;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.reflectionassert.ReflectionComparatorMode;

public class BeanUtils {

    public static <T> T createDeepCopy(T source, Class<T> clazz){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setDeepCopyEnabled(true);
        return modelMapper.map(source,clazz);
    }

    /**
     * checks whether two Objects are equal by properties
     * -> equals method of object is not used, but property values are compared reflectively
     * order in Collections is ignored
     *
     * @param o1
     * @param o2
     * @return
     */
    public static boolean isDeepEqual(Object o1, Object o2) {
        try {
            //Reihenfolge in Lists wird hier ignored
            ReflectionAssert.assertReflectionEquals(o1, o2, ReflectionComparatorMode.LENIENT_ORDER);
            return true;
        } catch (AssertionFailedError e) {
            //e.printStackTrace();
            return false;
        }

    }
}
