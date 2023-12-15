package com.github.vincemann.springrapid.core.proxy;

import com.github.vincemann.aoplog.MethodUtils;
import com.github.vincemann.springrapid.core.util.Lists;
import com.github.vincemann.springrapid.core.util.ProxyUtils;
import com.github.vincemann.springrapid.core.service.CrudService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanNameAware;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread safe
 *
 * @param <S>
 * @param <St>
 */
@Getter
@Setter
@Slf4j
public abstract class AbstractExtensionServiceProxy
        <
                S extends CrudService<?, ?>,
                St extends AbstractExtensionServiceProxy.State,
                P extends ProxyController
                >

        implements ChainController, InvocationHandler, ProxyController, BeanNameAware {

    private final Map<MethodIdentifier, Method> methods = new HashMap<>();
    private List<String> ignoredMethodNames = Lists.newArrayList("getEntityClass", "getRepository", "toString", "equals", "hashCode", "getClass", "clone", "notify", "notifyAll", "wait", "finalize","setBeanName","getBeanName","getTargetClass");
    private List<MethodIdentifier> learnedIgnoredMethods = new ArrayList<>();
    private S proxied;
    private List<AbstractServiceExtension<?, ? super P>> extensions = new ArrayList<>();
    private ConcurrentHashMap<Thread, St> thead_state_map = new ConcurrentHashMap<>();
    //caches
    private ConcurrentHashMap<ExtensionState, Object> next_cache = new ConcurrentHashMap<>();
    private ConcurrentHashMap<MethodIdentifier, List<ExtensionHandle>> extensionChainCache = new ConcurrentHashMap<>();
    private String beanName;





    public AbstractExtensionServiceProxy(S proxied, AbstractServiceExtension<?, ? super P>... extensions) {
        for (Method method : proxied.getClass().getMethods()) {
            this.methods.put(new MethodIdentifier(method), method);
        }
        this.proxied = proxied;
        for (AbstractServiceExtension<?, ? super P> extension : extensions) {
            addExtension(extension);
        }
    }

    private void resetLearnedIgnoredMethods() {
        this.learnedIgnoredMethods.clear();
    }

    public void addExtension(AbstractServiceExtension<?, ? super P> extension) {
        this.extensions.add(extension);
        //extension expects chainController<T>, gets ChainController<S>, T is always superclass of S -> so this is safe
        extension.setChain(this);
        //docs state that this must be castable to P
        extension.setProxyController(provideProxyController());
        resetLearnedIgnoredMethods();
    }

    public void removeExtension(AbstractServiceExtension<?, ? super P> extension){
        this.extensions.remove(extension);
        extension.setChain(null);
        extension.setProxyController(null);
    }

    public void addExtension(AbstractServiceExtension<?, ? super P> extension, int index) {
        this.extensions.add(index,extension);
        //extension expects chainController<T>, gets ChainController<S>, T is always superclass of S -> so this is safe
        extension.setChain(this);
        //docs state that this must be castable to P
        extension.setProxyController(provideProxyController());
        resetLearnedIgnoredMethods();
    }


    protected St getState() {
        return getThead_state_map().get(Thread.currentThread());
    }

//    @Override
//    public void dontCallTargetMethod() {
//        getState().setCallTargetMethod(false);
//    }

    protected void setState(St state) {
        getThead_state_map().put(Thread.currentThread(), state);
    }

    protected P provideProxyController() {
        return (P) this;
    }

    @Override
    public CrudService<?, ?> getLast() {
        return proxied;
    }

    @Override
    public final Object invoke(Object o, Method method, Object[] args) throws Throwable {
        if (isIgnored(method)) {
            return invokeProxied(method, args);
        } else {
            //trying to figure out target class -> dont even ask extensions, go straight to final proxied
//            if (isGetTargetClassMethod(method)){
//                return invokeProxied(method, args);
//            }
            try {
                if (args == null) {
                    args = new Object[]{};
                }

                List<ExtensionHandle> extensionChain = createExtensionChain(method);
                if (!extensionChain.isEmpty()) {
                    setState(createState(o, method, args));
                    ExtensionHandle chainHandle = extensionChain.get(0);
                    return chainHandle.invoke(args);
                } else {
                    learnedIgnoredMethods.add(new MethodIdentifier(method));
                    return invokeProxied(method, args);
                }
            } finally {
                resetState(o, method, args);
            }
        }
    }

//    protected boolean isGetTargetClassMethod(Method method){
//        if (method.getName().equals("getTargetClass") && method.getParameterTypes().length==0){
//            return true;
//        }else {
//            return false;
//        }
//    }

    protected void resetState(Object o, Method method, Object[] args) {
        setState(createState(o, method, args));
    }

    protected abstract St createState(Object o, Method method, Object[] args);

    protected Object invokeProxied(Method method, Object... args) throws Throwable {
        try {
            return getMethods().get(new MethodIdentifier(method))
                    .invoke(getLast(), args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    @Override
    public Object getNext(AbstractServiceExtension extension) {
        State state = getState();
        //is next object cached?
        ExtensionState extensionState = new ExtensionState(state, extension);
        Object cached = next_cache.get(extensionState);
        if (cached != null) {
            return cached;
        }
        if (state == null){
            // MOST LIKELY: make sure your extensions have the Prototype scope, and for each proxy a new instance of extension is added!
            // make sure extensions method is not intercepted by aop proxy maybe that triggers method call directly
            throw new IllegalArgumentException("method of extension: " + extension + " is called directly. Make sure to only call methods of proxies. \n Also make sure your extensions have the Prototype scope!");
        }
        //get extension chain by method
        List<ExtensionHandle> extensionChain = extensionChainCache.get(state.getMethodIdentifier());
        Optional<ExtensionHandle> link = extensionChain.stream()
                .filter(e -> ProxyUtils.isEqual(e.getExtension(), (extension)))
                .findFirst();
        if (link.isEmpty()) {
            throw new IllegalArgumentException("Already called Extension: " + extension + " not part of extension chain: " + extensionChain);
        }

        int extensionIndex = extensionChain.indexOf(link.get());
        int nextIndex = extensionIndex + 1;
        Object result;
        if (nextIndex >= extensionChain.size()) {
            //no further extension available, return proxied
            //this cast is safe
//            if (state.callTargetMethod) {

            result = proxied;
//            }else {
//
//            }
        } else {
            //this cast is also safe
            result = createProxiedExtension(extensionChain.get(nextIndex).getExtension());
        }
        next_cache.put(extensionState, result);

        return result;
    }

    // wraps extension with proxy that has proxied class (i.E. UserService)
    // the proxy simply delegates all calls to extension -> used so casting to ServiceType in Extensions work
    // it is made sure the extension always has the method in question, so the cast is safe as long as only the callee method is called
    private Object createProxiedExtension(Object extension) {
        Class<?> proxiedClass = AopUtils.getTargetClass(proxied);
        return Proxy.newProxyInstance(
                proxiedClass.getClassLoader(),
                ClassUtils.getAllInterfaces(proxiedClass).toArray(new Class[0]),
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
//                        System.err.println("Calling method: " + method + " on extension: " + extension);
                        //this should always work, there should never get a method called, that does not exist in extension
                        try {
                            return MethodUtils.findMethod(extension.getClass(), method.getName(), method.getParameterTypes())
                                    .invoke(extension, objects);
                        }catch (InvocationTargetException e){
                            throw e.getTargetException();
                        }

                    }
                });
    }

    /**
     * Each method of Proxy has Extension Chain that consists of all Proxy's Extensions, that also define this method.
     * @param method
     * @return
     */
    protected List<ExtensionHandle> createExtensionChain(Method method) {
        MethodIdentifier methodIdentifier = new MethodIdentifier(method);
        //first look in cache
        List<ExtensionHandle> extensionChain = extensionChainCache.get(methodIdentifier);
        if (extensionChain == null) {
            // start from end of extensions for start and identify all extensions having the requested method
            // all matching methods together form a chain
            // each link of the chain also saves its declared method
            Map.Entry<MethodIdentifier, List<ExtensionHandle>> method_chain_entry = new HashMap.SimpleEntry<>(methodIdentifier, new ArrayList<>());
            for (int i = 0; i < extensions.size(); i++) {
                AbstractServiceExtension<?, ? super P> extension = extensions.get(i);
                try {
                    Method extensionsMethod = MethodUtils.findMethod(extension.getClass(), method.getName(), method.getParameterTypes());
                    method_chain_entry.getValue().add(new ExtensionHandle(extension, extensionsMethod));
                } catch (NoSuchMethodException e) {
                    // happens all the time, when extension does not define the method in question, that may be defined in a extension
                    // further downstream tho
                    if (log.isTraceEnabled())
                        log.trace("No such method found: ", e);
                }
            }
            extensionChainCache.entrySet().add(method_chain_entry);
            extensionChain = extensionChainCache.get(methodIdentifier);
        }
        return extensionChain;
    }

    protected boolean isIgnored(Method method) {
        return getIgnoredMethodNames().contains(method.getName())
                || getLearnedIgnoredMethods().contains(new MethodIdentifier(method));
    }


    /**
     * Saved state persisting during method call of proxy.
     * Is reset after method call on proxy ( = all Extensions and proxied) are called.
     */
    @EqualsAndHashCode
    protected static class State {
        @Getter
        private MethodIdentifier methodIdentifier;
//        private boolean callTargetMethod;

        public State(Method method) {
            this.methodIdentifier = new MethodIdentifier(method);
        }

//        public boolean isCallTargetMethod() {
//            return callTargetMethod;
//        }
//
//        public void setCallTargetMethod(boolean callTargetMethod) {
//            this.callTargetMethod = callTargetMethod;
//        }
    }


    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode
    private static class MethodIdentifier {
        String methodName;
        Class<?>[] argTypes;

        public MethodIdentifier(Method method) {
            this.methodName = method.getName();
            this.argTypes = method.getParameterTypes();
        }


    }

    // contains extension + method
    @AllArgsConstructor
    @Getter
    @ToString
    protected class ExtensionHandle {
        AbstractServiceExtension<?, ? super P> extension;
        Method method;

        Object invoke(Object... args) throws Throwable {
            try {
                return method.invoke(extension, args);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }

        }
    }

    // only used for caching
    @AllArgsConstructor
    @EqualsAndHashCode
    private static class ExtensionState {
        private State state;
        private AbstractServiceExtension extension;
    }
}
