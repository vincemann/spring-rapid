package com.github.vincemann.springrapid.core.util;

import org.springframework.aop.support.AopUtils;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.test.util.AopTestUtils;

import java.lang.reflect.InvocationTargetException;

public class ProxyUtils {


    //use whenever you get errors comparing cglib proxy (all fields null) with normal object or other proxy
    public static boolean isEqual(Object o1, Object o2){
        //other way around is no problem
        //dont forget to actually implement the equals method with getters !
        if (AopUtils.isCglibProxy(o1)){
            return AopTestUtils.getUltimateTargetObject(o1).equals(o2);
        }else {
            return o1.equals(o2);
        }
    }


}
