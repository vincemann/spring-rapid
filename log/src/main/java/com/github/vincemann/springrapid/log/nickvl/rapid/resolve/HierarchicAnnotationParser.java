package com.github.vincemann.springrapid.log.nickvl.rapid.resolve;

import com.github.vincemann.springrapid.log.nickvl.AnnotationInfo;
import com.github.vincemann.springrapid.log.nickvl.ClassAnnotationInfo;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

///**
// * Resolves AOP and CGLIB Proxies.
// * {@link com.github.vincemann.springrapid.log.nickvl.annotation.Logging} can be placed on methods and classes and will be searched for in that order.* Scans interfaces in hierarchical order for JDK Runtime Proxies to find {@link com.github.vincemann.springrapid.log.nickvl.annotation.Logging}
// * and uses latest annotation -> lowest in hierarchy.
// * Also scans interfaces like that, when it cant find annotation on target method and class via getDeclaredAnnotation in that order for each hierarchy level.
// * -> search order for non JDK Proxy: target-method, target-class, class hierarchy+1-method, class hierarchy+1-class, ... ,  class hierarchyEnd-method, class hierarchyEnd-class, firstInterface-hierarchy-1-method, firstInterface-hierarchy-1-class, ...
// * -> search order for JDK Proxy: firstInterface-hierarchy-1-method, firstInterface-hierarchy-1-class, ...
// *
// **/
//todo cache all
public class HierarchicAnnotationParser implements AnnotationParser {

    @Override
    public <A extends Annotation> A fromMethod(Method method, Class<A> type) {
        return AnnotationUtils.findAnnotation(method,type);
    }

    @Override
    public <A extends Annotation> ClassAnnotationInfo<A> fromClass(Class<?> clazz, Class<A> type) {
        A annotation = AnnotationUtils.findAnnotation(clazz, type);
        return ClassAnnotationInfo.<A>builder()
                .annotation(annotation)
                .targetClass(AnnotationUtils.findAnnotationDeclaringClass(type,type))
                .build();
    }

    @Override
    public <A extends Annotation> AnnotationInfo<A> fromMethodOrClass(Method method, Class<A> type) {
        A fromMethod = fromMethod(method, type);
        if (fromMethod==null){
            ClassAnnotationInfo<A> fromClass = fromClass(method.getDeclaringClass(), type);
            return fromClass==null ? null
                    : new AnnotationInfo<>(fromClass);
        }else {
            return AnnotationInfo.<A>builder().annotation(fromMethod).classLevel(false).build();
        }
    }

    //    //dont cache this
//    @Override
//    public AnnotationInfo parse(Object target, MethodSignature signature) {
//        //resolve aop and cglib proxies, jdk runtime proxies remain
//        Object unproxied = AopTestUtils.getUltimateTargetObject(target);
//        Class<?> targetClass = unproxied.getClass();
//
//
//        //proxy
//        if (isProxyClass(targetClass)){
//            return extractFromInterfaces(targetClass,signature);
//        }
//        //no proxy
//        else {
//            AnnotationInfo annotationInfo = extractFromClass(targetClass, signature);
//            if (annotationInfo !=null) {
//                return annotationInfo;
//            }else {
//                return extractFromInterfaces(targetClass, signature);
//            }
//        }
//    }
//
//
//    //todo cache this
//    private static AnnotationInfo extractFromClass(Class<?> targetClass, MethodSignature signature){
//        Method method = MethodUtils.getMatchingMethod(targetClass, signature.getName(), signature.getParameterTypes());
//
//        //also searches for Logging as meta annotation
//        Logging methodAnnotation = AnnotationUtils.findAnnotation(method, Logging.class);
//        if (methodAnnotation!=null){
//            return AnnotationInfo.builder()
//                    .annotation(methodAnnotation)
//                    .classLevel(false)
//                    .fromInterface(false)
//                    .targetClass(targetClass)
//                    .method(method)
//                    .build();
//        }
//
//        //also searches for Logging as meta annotation
//        Logging classAnnotation = AnnotationUtils.findAnnotation(targetClass, Logging.class);
//        if (classAnnotation!=null){
//            return AnnotationInfo.builder()
//                    .annotation(classAnnotation)
//                    .classLevel(true)
//                    .fromInterface(false)
//                    .targetClass(targetClass)
//                    .build();
//        }
//        return null;
//    }
//
//    //go through all interfaces in order low -> high in hierarchy and return first match's annotation
//    //method gets checked first than type (for each)
//    //todo cache this
//    private static AnnotationInfo extractFromInterfaces(Class<?> targetClass, MethodSignature signature){
//        //order is: first match will be lowest in hierachy -> first match will be latest
//        List<Class<?>> interfaces = ClassUtils.getAllInterfaces(targetClass);
//        for (Class<?> iface :interfaces){
//            Method method = MethodUtils.getMatchingMethod(iface, signature.getName(), signature.getParameterTypes());
//            if (method!=null) {
//                //interface has method
//                //method
//                Logging mehtodAnnotation = AnnotationUtils.findAnnotation(method,Logging.class);
//                if (mehtodAnnotation != null) {
//                    return AnnotationInfo.builder()
//                            .annotation(mehtodAnnotation)
//                            .classLevel(false)
//                            .fromInterface(true)
//                            .targetClass(iface)
//                            .method(method)
//                            .build();
//                }
//                //type
//                Logging classAnnotation = AnnotationUtils.findAnnotation(iface,Logging.class);
//                if (classAnnotation!=null){
//                    return AnnotationInfo.builder()
//                            .annotation(mehtodAnnotation)
//                            .classLevel(true)
//                            .fromInterface(true)
//                            .targetClass(iface)
//                            .build();
//                }
//            }
//
//        }
//        return null;
//    }
//
//    private static boolean isProxyClass(final Class<?> target) {
//        return target.getCanonicalName().contains("$Proxy");
//    }
}
