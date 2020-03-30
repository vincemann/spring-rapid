package io.github.vincemann.generic.crud.lib.proxy.invocationHandler.abs;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Getter
@Setter
@Slf4j
public abstract class CrudServiceExtensionProxy<E extends IdentifiableEntity<Id>,Id extends Serializable>
        implements InvocationHandler
{

    private List<String> ignoredMethods = Arrays.asList("getEntityClass","getRepository","toString","equals","hashCode","getClass","clone","notify","notifyAll","wait","finalize");
    private CrudService<E,Id, CrudRepository<E,Id>> service;
    private final Map<String, Method> methods = new HashMap<>();

    public CrudServiceExtensionProxy(CrudService<E, Id, CrudRepository<E, Id>> service, String... ignoredMethods) {
        this.service = service;
        for(Method method: service.getClass().getMethods()) {
            this.methods.put(method.getName(), method);
        }
        this.ignoredMethods.addAll(Arrays.asList(ignoredMethods));

    }

    @Override
    public final Object invoke(Object o, Method method, Object[] args) throws Throwable {
        if(isMethodIgnored(method)){
            //log.debug("proxy call ignored, because method: " + method.getName() + " is not handled by this proxy: " + this.getClass().getSimpleName());
            return getMethods().get(method.getName()).invoke(getService(),args);
        }
        else{
            return handleProxyCall(o,method,args);
        }
    }

    /**
     * Invokes the given method.
     * Before, it checks if the method that shall be invoked with given arguments, has one more argument, then in @param args.
     * If so, it checks, if it is of Type class. If so, it appends the {@link CrudService#getEntityClass()} as last argument.
     * @param target
     * @param method
     * @param args
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    protected Object invokeAndAppendEntityClassArgIfNeeded(Object target, Method method, List<Object> args) throws InvocationTargetException, IllegalAccessException {
        if(method.getParameterCount()==args.size()+1){
            //we have exactly one more argument in callback method
            if(method.getParameterTypes()[method.getParameterCount()-1].equals(Class.class)){
                //last parameter is of type class -> user wants type of entity in cb method
                List<Object> copiedArgs = new ArrayList<>(args);
                copiedArgs.add(getService().getEntityClass());
                return method.invoke(target,copiedArgs.toArray());
            }else {
                //last arg is of diff type, just call method
                return method.invoke(target,args.toArray());
            }
        }else {
            //user did not choose to include entity class as last arg in his callback method
            return method.invoke(target,args.toArray());
        }
    }

    protected abstract Object handleProxyCall(Object o, Method method, Object[] args) throws Throwable;

    public boolean isMethodIgnored(Method method){
        return getIgnoredMethods().contains(method.getName());
    }
}
