package com.github.vincemann.springrapid.core.advice.log.resolve;

import com.github.vincemann.springrapid.core.advice.log.LogInteraction;
import com.github.vincemann.springrapid.core.advice.log.LogInteractionInfo;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.test.util.AopTestUtils;

import java.lang.reflect.Method;
import java.util.List;

public class ProxyAwareLogInteractionInfoResolver implements LogInteractionInfoResolver {

    //dont cache this
    @Override
    public LogInteractionInfo resolve(Object target,MethodSignature signature) {
        //resolve aop and cglib proxies, jdk runtime proxies remain
        Object unproxied = AopTestUtils.getUltimateTargetObject(target);
        Class<?> targetClass = unproxied.getClass();

        //proxy
        if (isProxyClass(targetClass)){
            return extractFromInterfaces(targetClass,signature);
        }
        //no proxy
        else {
            LogInteractionInfo logInteractionInfo = extractFromClass(targetClass, signature);
            if (logInteractionInfo!=null) {
                return logInteractionInfo;
            }else {
                return extractFromInterfaces(targetClass, signature);
            }
        }
    }


    //todo cache this
    private static LogInteractionInfo extractFromClass(Class<?> targetClass,MethodSignature signature){
        Method method = MethodUtils.getMatchingMethod(targetClass, signature.getName(), signature.getParameterTypes());
        LogInteraction methodAnnotation = method.getDeclaredAnnotation(LogInteraction.class);
        if (methodAnnotation!=null){
            return LogInteractionInfo.builder()
                    .annotation(methodAnnotation)
                    .classLevel(false)
                    .fromInterface(false)
                    .targetClass(targetClass)
                    .method(method)
                    .build();
        }
        LogInteraction classAnnotation = targetClass.getDeclaredAnnotation(LogInteraction.class);
        if (classAnnotation!=null){
            return LogInteractionInfo.builder()
                    .annotation(classAnnotation)
                    .classLevel(true)
                    .fromInterface(false)
                    .targetClass(targetClass)
                    .build();
        }
        return null;
    }

    //go through all interfaces in order low -> high in hierarchy and return first match's annotation
    //method gets checked first than type (for each)
    //todo cache this
    private static LogInteractionInfo extractFromInterfaces(Class<?> targetClass,MethodSignature signature){
        //order is: first match will be lowest in hierachy -> first match will be latest
        List<Class<?>> interfaces = ClassUtils.getAllInterfaces(targetClass);
        for (Class<?> candidate :interfaces){
            Method method = MethodUtils.getMatchingMethod(candidate, signature.getName(), signature.getParameterTypes());
            if (method!=null) {
                //interface has method
                //method
                LogInteraction annotation = method.getAnnotation(LogInteraction.class);
                if (annotation != null) {
                    return LogInteractionInfo.builder()
                            .annotation(annotation)
                            .classLevel(false)
                            .fromInterface(true)
                            .targetClass(candidate)
                            .method(method)
                            .build();
                }
                //type
                LogInteraction classAnnotation = candidate.getAnnotation(LogInteraction.class);
                if (classAnnotation!=null){
                    return LogInteractionInfo.builder()
                            .annotation(annotation)
                            .classLevel(true)
                            .fromInterface(true)
                            .targetClass(candidate)
                            .build();
                }
            }

        }
        return null;
    }

    private static boolean isProxyClass(final Class<?> target) {
        return target.getCanonicalName().contains("$Proxy");
    }
}
