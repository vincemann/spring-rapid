package com.github.vincemann.springrapid.core.util;

public abstract class AopProxyUtils {

    private AopProxyUtils(){}

//    public static boolean isEqual(Object o1, Object o2){
//        //other way around is no problem
//        //dont forget to actually implement the equals method with getters !
//        if (AopUtils.isCglibProxy(o1)){
//            return getUltimateTargetObject(o1).equals(o2);
//        }else {
//            return o1.equals(o2);
//        }
//    }

    public static <T> T getUltimateTargetObject(T object) {
        if (org.springframework.aop.framework.AopProxyUtils.ultimateTargetClass(object) != null) {
            return (T) org.springframework.aop.framework.AopProxyUtils.ultimateTargetClass(object).cast(object);
        } else {
            return object;
        }
    }
}
