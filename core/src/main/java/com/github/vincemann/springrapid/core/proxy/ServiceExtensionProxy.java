package com.github.vincemann.springrapid.core.proxy;

import com.github.vincemann.aoplog.MethodUtils;
import com.github.vincemann.springrapid.commons.Lists;
import com.github.vincemann.springrapid.core.service.CrudService;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@Slf4j
public class ServiceExtensionProxy<S extends CrudService<?,?,?>>
        implements ServiceExtensionProxyController, InvocationHandler {

    private final Map<MethodIdentifier, Method> methods = new HashMap<>();
    private List<String> ignoredMethods = Lists.newArrayList("getEntityClass", "getRepository", "toString", "equals", "hashCode", "getClass", "clone", "notify", "notifyAll", "wait", "finalize");
    private S proxied;
    private List<ServiceExtension> extensions = new ArrayList<>();
    private ConcurrentHashMap<MethodIdentifier,List<ExtensionLink>> methodExtensionChains = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Thread, State> thead_method_map = new ConcurrentHashMap<>();

    public ServiceExtensionProxy(S proxied, ServiceExtension<? super S>... extensions) {
        for (Method method : proxied.getClass().getMethods()) {
            this.methods.put(new MethodIdentifier(method), method);
        }

        this.proxied = proxied;
        this.extensions.addAll(Arrays.asList(extensions));
    }


    private static class State{
        @Getter
        private MethodIdentifier methodIdentifier;
        private boolean callTargetMethod;

        public State(Method method) {
            this.methodIdentifier = new MethodIdentifier(method);
        }

        public boolean isCallTargetMethod() {
            return callTargetMethod;
        }

        public void setCallTargetMethod(boolean callTargetMethod) {
            this.callTargetMethod = callTargetMethod;
        }
    }

    @EqualsAndHashCode
    @AllArgsConstructor
    private static class MethodIdentifier{
        String methodName;
        Class<?>[] argTypes;

        public MethodIdentifier(Method method){
            this.methodName=method.getName();
            this.argTypes=method.getParameterTypes();
        }

    }

    //link of a chain
    @AllArgsConstructor
    class ExtensionLink {
        ServiceExtension serviceExtension;
        Method method;

        Object invoke(Object... args) throws InvocationTargetException, IllegalAccessException {
            return method.invoke(serviceExtension,args);
        }
    }

    @Override
    public final Object invoke(Object o, Method method, Object[] args) throws Throwable {
        if (isIgnored(method)) {
            return invokeProxied(method,args);
        } else {
            if (args == null) {
                args = new Object[]{};
            }

            List<ExtensionLink> extensionChain = createExtensionChain(method);
            if (!extensionChain.isEmpty()){
                thead_method_map.put(Thread.currentThread(),new State(method));
                return extensionChain.get(0).invoke(args);
            }else {
                return invokeProxied(method,args);
            }
        }
    }

    private Object invokeProxied(Method method,Object... args) throws InvocationTargetException, IllegalAccessException {
        return getMethods().get(new MethodIdentifier(method))
                .invoke(getProxied(), args);
    }

    @Override
    public <T> T getNext(ServiceExtension<T> extension) {
        State state = thead_method_map.get(Thread.currentThread());
        List<ExtensionLink> extensionChain = methodExtensionChains.get(state.getMethodIdentifier());
        int extensionIndex = extensionChain.indexOf(extension);
        int nextIndex = extensionIndex+1;
        if (nextIndex>=extensionChain.size()){
            //no further extension available, return proxied
            //this cast is safe
            return (T) proxied;
        }else {
            //this cast is also safe
            return (T) extensionChain.get(nextIndex);
        }
    }

    protected List<ExtensionLink> createExtensionChain(Method method){
        MethodIdentifier methodIdentifier = new MethodIdentifier(method);
        //first look in cache
        List<ExtensionLink> extensionChain = methodExtensionChains.get(methodIdentifier);
        if (extensionChain==null){
            //start from end of extensions for start and identify all extensions having the requested method
            //all matching methods together form a chain
            //each link of the chain also saves its declared method
            Map.Entry<MethodIdentifier,List<ExtensionLink>> method_chain_entry = new HashMap.SimpleEntry<>(methodIdentifier,new ArrayList<>());
            for (int i = extensions.size()-1; i > 0 ; i--) {
                ServiceExtension<?> extension = extensions.get(i);
                try {
                    Method extensionsMethod = MethodUtils.findMethod(extension.getClass(), method.getName(), method.getParameterTypes());
                    method_chain_entry.getValue().add(new ExtensionLink(extension,extensionsMethod));
                } catch (NoSuchMethodException e) {
                }
            }
            methodExtensionChains.entrySet().add(method_chain_entry);
            extensionChain = methodExtensionChains.get(methodIdentifier);
        }
        return extensionChain;
    }

    protected boolean isIgnored(Method method) {
        return getIgnoredMethods().contains(method.getName());
    }

    @Override
    public void dontCallTargetMethod() {

    }


    //    protected MethodHandle findMethod(String name, Object target) {
//        List<Method> methods = Arrays.stream(target.getClass().getMethods())
//                .filter(m ->
//                        m.getName().equals(name) &&
//                                !m.isBridge()
//                )
//                .collect(Collectors.toList());
//        if (methods.size() > 1) {
//            return MethodHandle.create(extractOverridingMethod(methods), target);
//        }
//        //Assert.isTrue(!methods.isEmpty(),"Could not find method with name: " + name + " on target: " + target);
//        return methods.isEmpty() ? null : MethodHandle.create(methods.get(0), target);
//    }
//
//    private Method extractOverridingMethod(List<Method> conflicting) {
//        //use method highest in the class hierachy -> the one that overrides
//        //check if methods are in same class -> design flaw
//        List<Class> classes = new ArrayList<>();
//        Method highestInHierarchy = null;
//        for (Method method : conflicting) {
//            Class<?> declaringClass = method.getDeclaringClass();
//            if (classes.contains(declaringClass)) {
//                throw new IllegalArgumentException("Found multiple methods with same name -> illegal for rule/plugin hook methods");
//            }
//            if (highestInHierarchy == null) {
//                highestInHierarchy = method;
//            } else if (highestInHierarchy.getDeclaringClass().isAssignableFrom(declaringClass)) {
//                highestInHierarchy = method;
//            }
//            classes.add(declaringClass);
//        }
//        return highestInHierarchy;
//    }
//
//    protected String createPrefixedMethodName(String prefix, String targetMethodName) {
//        String capitalFirstLetterMethodName = targetMethodName.substring(0, 1).toUpperCase() + targetMethodName.substring(1);
//        return prefix + capitalFirstLetterMethodName;
//    }
//
//    protected boolean needsToBeInvoked(MethodHandle method) {
//        return doesUserPassRoleLimitations(method);
//    }
//
//    protected boolean doesUserPassRoleLimitations(MethodHandle method) {
//        if (method.hasAnnotation(ApplyIfRole.class)) {
//            ApplyIfRole annotation = method.getMethod().getAnnotation(ApplyIfRole.class);
//            HashSet<String> requiredRoles = new HashSet<>(Lists.newArrayList(annotation.is()));
//            HashSet<String> blacklistedRoles = new HashSet<>(Lists.newArrayList(annotation.isNot()));
//            boolean allowAnon = annotation.allowAnon();
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            if (authentication != null) {
//                Set<String> userRoles = authentication.getAuthorities().stream()
//                        .map(GrantedAuthority::getAuthority)
//                        .collect(Collectors.toSet());
//                //forbidden roles
//                for (String userRole : userRoles) {
//                    if (blacklistedRoles.contains(userRole)){
//                        log.debug("Method: " + method + " does not apply for curr user, bc user has blacklisted role: " + userRole);
//                        return false;
//                    }
//                }
//                //required roles
//                for (String requiredRole : requiredRoles) {
//                    if (!userRoles.contains(requiredRole)){
//                        log.debug("Method: " + method + " does not apply for curr user, bc user does not have required role : " + requiredRole);
//                        return false;
//                    }
//                }
//            } else {
//                if (requiredRoles.isEmpty())
//                    return allowAnon;
//                else
//                    return false;
////                return requiredRoles.isEmpty() && blacklistedRoles.isEmpty();
//            }
//        }
//        return true;
//    }
//
//    protected NullableOptional<Object> invokeAndAppendEntityClassArgIfNeeded(MethodHandle method, Object[] args) throws Throwable {
//        List<Object> finalArgs = Lists.newArrayList(args);
//        if (isEntityClassWanted(method, args.length)) {
//            finalArgs.add(getProxied().getEntityClass());
//        }
//        if (needsToBeInvoked(method)) {
//            return method.execute(finalArgs.toArray());
//        }else {
//            return NullableOptional.empty();
//        }
//    }
//
//
//    private boolean isEntityClassWanted(MethodHandle methodHandle, int amountDefaultArgs) {
//        int amountArgs = methodHandle.getMethod().getParameterTypes().length;
//        if (amountArgs == 0) {
//            return false;
//        }
//        Class<?> lastArgType = methodHandle.getMethod().getParameterTypes()[amountArgs - 1];
//        if (amountArgs == amountDefaultArgs + 1 && lastArgType.equals(Class.class)) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    protected abstract Object proxy(Object o, Method method, Object[] args) throws Throwable;

//    @NoArgsConstructor
//    @Getter
//    public static class MethodHandle {
//        private boolean voidMethod;
//        private String name;
//        private Method method;
//        private Object target;
//
//        public static MethodHandle create(Method method, Object target) {
//            Assert.isTrue(method.getName().length() > 3, "Method names are expected to be at least 2 characters long");
//            Assert.notNull(target, "Target Object must not be null");
//            MethodHandle methodHandle = new MethodHandle();
//            methodHandle.target = target;
//            methodHandle.voidMethod = method.getReturnType().equals(Void.TYPE);
//            methodHandle.name = method.getName();
//            methodHandle.method = method;
//            return methodHandle;
//        }
//
//        public boolean hasAnnotation(Class<? extends Annotation> type) {
//            return method.isAnnotationPresent(type);
//        }
//
//        public NullableOptional<Object> execute(Object[] args) throws Throwable {
//            try {
//                Object result = method.invoke(target, args);
//                if (voidMethod) {
//                    return NullableOptional.empty();
//                } else {
//                    return NullableOptional.of(result);
//                }
//            } catch (IllegalAccessException e) {
//                throw new RuntimeException(e);
//            } catch (InvocationTargetException e) {
//                throw e.getCause();
//            }
//        }
//    }
}
