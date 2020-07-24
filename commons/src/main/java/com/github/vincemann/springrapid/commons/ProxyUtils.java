package com.github.vincemann.springrapid.commons;

import org.springframework.aop.support.AopUtils;
import org.springframework.test.util.AopTestUtils;

public class ProxyUtils {


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
