package io.github.vincemann.generic.crud.lib.test.compare;

//@Component
//@Qualifier(PARTIAL_UPDATE_EQUAL_CHECKER_QUALIFIER)
//public class PartialUpdateIgnoreEntitiesFuzzyDeepEqualChecker<T> extends IgnoreEntitiesFuzzyDeepEqualChecker<T> {
//
//    @Override
//    public boolean isFuzzyEqual(T expected, T actual) {
//        //todo just use lenient assert?
//        //copy non null values from update to entityToUpdate
//        try {
//            BeanUtilsBean beanUtilsBean = new BeanUtilsBean();
//            T tempClone = (T)beanUtilsBean.cloneBean(actual);
//            NullAwareBeanUtils.copyProperties(tempClone, expected);
//            return super.isFuzzyEqual(tempClone, actual);
//        }catch (IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException e){
//            throw new RuntimeException(e);
//        }
//    }
//}
