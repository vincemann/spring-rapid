package io.github.vincemann.generic.crud.lib.proxy.invocationHandler.abs;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Slf4j
public abstract class MethodBlacklistingCrudServiceProxy<E extends IdentifiableEntity<Id>,Id extends Serializable>
        implements InvocationHandler
{

    private List<String> ignoredMethods = Arrays.asList("getEntityClass","getRepository","toString","equals","hashCode","getClass","clone","notify","notifyAll","wait","finalize");
    private CrudService<E,Id, CrudRepository<E,Id>> service;
    private final Map<String, Method> methods = new HashMap<>();

    public MethodBlacklistingCrudServiceProxy(CrudService<E, Id, CrudRepository<E, Id>> service, String... ignoredMethods) {
        this.service = service;
        for(Method method: service.getClass().getMethods()) {
            this.methods.put(method.getName(), method);
        }
        this.ignoredMethods.addAll(Arrays.asList(ignoredMethods));

    }

    @Override
    public final Object invoke(Object o, Method method, Object[] args) throws Throwable {
        if(isMethodIgnored(method)){
            log.debug("proxy call ignored, because method: " + method.getName() + " is not handled by this proxy: " + this.getClass().getSimpleName());
            return getMethods().get(method.getName()).invoke(getService(),args);
        }
        else{
            return handleProxyCall(o,method,args);
        }
    }

    protected abstract Object handleProxyCall(Object o, Method method, Object[] args) throws Throwable;

    public boolean isMethodIgnored(Method method){
        return getIgnoredMethods().contains(method.getName());
    }
}
