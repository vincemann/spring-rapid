package com.github.vincemann.springrapid.core.proxy;

import com.github.vincemann.aoplog.MethodUtils;
import com.github.vincemann.springrapid.commons.Lists;
import com.github.vincemann.springrapid.commons.ProxyUtils;
import com.github.vincemann.springrapid.core.service.SimpleCrudService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread safe
 * @param <S>
 * @param <E>
 * @param <St>
 * @param <P> By default this must be instance of P. You change that behavior by overriding {@link this#provideProxyController()}
 */
@Getter
@Setter
@Slf4j
public abstract class AbstractExtensionServiceProxy
        <
                S extends SimpleCrudService<?,?>,
                E  extends AbstractServiceExtension<?,P>,
                St extends AbstractExtensionServiceProxy.State,
                P extends ProxyController>

        implements ChainController, InvocationHandler, ProxyController {

    private List<String> ignoredMethodNames = Lists.newArrayList("getEntityClass", "getRepository", "toString", "equals", "hashCode", "getClass", "clone", "notify", "notifyAll", "wait", "finalize");
    private List<MethodIdentifier> learnedIgnoredMethods = new ArrayList<>();
    private final Map<MethodIdentifier, Method> methods = new HashMap<>();
    private S proxied;
    private List<E> extensions = new ArrayList<>();
    private ConcurrentHashMap<MethodIdentifier,List<ExtensionChainLink>> method_extensionChain_map = new ConcurrentHashMap<>();
    private ConcurrentHashMap<StateExtension,Object> state_next_map = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Thread, St> thead_state_map = new ConcurrentHashMap<>();

    public  AbstractExtensionServiceProxy(S proxied, E... extensions) {
        for (Method method : proxied.getClass().getMethods()) {
            this.methods.put(new MethodIdentifier(method), method);
        }
        this.proxied = proxied;
        for (E extension : extensions) {
            addExtension(extension);
        }
    }

    private void resetLearnedIgnoredMethods(){
        this.learnedIgnoredMethods.clear();
    }

    public void addExtension(E extension){
        this.extensions.add(extension);
        //extension expects chainController<T>, gets ChainController<S>, T is always superclass of S -> so this is safe
        extension.setChain(this);
        //docs state that this must be castable to P
        extension.setProxyController(provideProxyController());
        resetLearnedIgnoredMethods();
    }




    protected St getState(){
        return thead_state_map.get(Thread.currentThread());
    }

//    @Override
//    public void dontCallTargetMethod() {
//        getState().setCallTargetMethod(false);
//    }


    protected P provideProxyController(){
        return (P) this;
    }

    @EqualsAndHashCode
    protected static class State{
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
    @EqualsAndHashCode
    private static class StateExtension{
        private State state;
        private AbstractServiceExtension extension;
    }

    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode
    private static class MethodIdentifier{
        String methodName;
        Class<?>[] argTypes;

        public MethodIdentifier(Method method){
            this.methodName=method.getName();
            this.argTypes=method.getParameterTypes();
        }


    }

    @Override
    public SimpleCrudService<?, ?> getLast() {
        return proxied;
    }

    //link of a chain
    @AllArgsConstructor
    @Getter
    @ToString
    protected class ExtensionChainLink {
        E extension;
        Method method;

        Object invoke(Object... args) throws InvocationTargetException, IllegalAccessException {
            return method.invoke(extension,args);
        }


    }

    @Override
    public final Object invoke(Object o, Method method, Object[] args) throws Throwable {
        if (isIgnored(method)) {
            return invokeProxied(method,args);
        } else {
            try {
                if (args == null) {
                    args = new Object[]{};
                }

                List<ExtensionChainLink> extensionChain = createExtensionChain(method);
                if (!extensionChain.isEmpty()){
                    thead_state_map.put(Thread.currentThread(),createState(o,method,args));
                    return extensionChain.get(0).invoke(args);
                }else {
                    learnedIgnoredMethods.add(new MethodIdentifier(method));
                    return invokeProxied(method,args);
                }
            }finally {
                resetState(o,method,args);
            }
        }
    }

    protected void resetState(Object o, Method method, Object[] args){
        setState(createState(o,method,args));
    }

    protected void setState(St state){
        thead_state_map.put(Thread.currentThread(),state);
    }

    protected abstract St createState(Object o, Method method, Object[] args);


    protected Object invokeProxied(Method method,Object... args) throws InvocationTargetException, IllegalAccessException {
        return getMethods().get(new MethodIdentifier(method))
                .invoke(getLast(), args);
    }

    @Override
    public Object getNext(AbstractServiceExtension extension) {
        State state = thead_state_map.get(Thread.currentThread());
        StateExtension stateExtension = new StateExtension(state,extension);
        Object cached = state_next_map.get(stateExtension);
        if (cached!=null){
            return cached;
        }
        List<ExtensionChainLink> extensionChain = method_extensionChain_map.get(state.getMethodIdentifier());
        Optional<ExtensionChainLink> link = extensionChain.stream()
                .filter(e -> ProxyUtils.isEqual(e.getExtension(),(extension)))
                .findFirst();
        if (link.isEmpty()){
            throw new IllegalArgumentException("Already called Extension: " + extension + " not part of extension chain: " + extensionChain);
        }

        int extensionIndex = extensionChain.indexOf(link.get());
        int nextIndex = extensionIndex+1;
        Object result;
        if (nextIndex>=extensionChain.size()){
            //no further extension available, return proxied
            //this cast is safe
//            if (state.callTargetMethod) {

            result =  proxied;
//            }else {
//
//            }
        }else {
            //this cast is also safe
            result = createProxiedExtension(extensionChain.get(nextIndex).getExtension());
        }
        state_next_map.put(stateExtension,result);

        return result;
    }

    //simply delegates all calls to extension -> used so casting to ServiceType in Extensions work
    //it is made sure the extension always has the method in question, so the cast is safe
    private Object createProxiedExtension(Object extension){
        Class<?> proxiedClass = AopUtils.getTargetClass(proxied);
        return Proxy.newProxyInstance(
                proxiedClass.getClassLoader(),
                ClassUtils.getAllInterfaces(proxiedClass).toArray(new Class[0]),
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                        //this should always work, there should never get a method called, that does not exist in extension
                        return method.invoke(extension,objects);
                    }
                });
    }


//    private <T> T createNoopProxy(){
//        return (T) Proxy.newProxyInstance(
//                proxied.getClass().getClassLoader(),
//                ClassUtils.getAllInterfaces(proxied.getClass()).toArray(new Class[0]),
//                new InvocationHandler() {
//                    @Override
//                    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
//                        return null;
//                    }
//                }))
//    }

    protected List<ExtensionChainLink> createExtensionChain(Method method){
        MethodIdentifier methodIdentifier = new MethodIdentifier(method);
        //first look in cache
        List<ExtensionChainLink> extensionChain = method_extensionChain_map.get(methodIdentifier);
        if (extensionChain==null){
            //start from end of extensions for start and identify all extensions having the requested method
            //all matching methods together form a chain
            //each link of the chain also saves its declared method
            Map.Entry<MethodIdentifier,List<ExtensionChainLink>> method_chain_entry = new HashMap.SimpleEntry<>(methodIdentifier,new ArrayList<>());
            for (int i = 0; i < extensions.size() ; i++) {
                E extension = extensions.get(i);
                try {
                    Method extensionsMethod = MethodUtils.findMethod(extension.getClass(), method.getName(), method.getParameterTypes());
                    method_chain_entry.getValue().add(new ExtensionChainLink(extension,extensionsMethod));
                } catch (NoSuchMethodException e) {
                }
            }
            method_extensionChain_map.entrySet().add(method_chain_entry);
            extensionChain = method_extensionChain_map.get(methodIdentifier);
        }
        return extensionChain;
    }

    protected boolean isIgnored(Method method) {
        return getIgnoredMethodNames().contains(method.getName())
                || getLearnedIgnoredMethods().contains(new MethodIdentifier(method));
    }
}
