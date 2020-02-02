package io.github.vincemann.generic.crud.lib.proxy.invocationHandler;


import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.proxy.invocationHandler.abs.MethodBlacklistingCrudServiceDynamicInvocationHandler;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.plugin.CrudServicePlugin;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

public class PluginCrudServiceDynamicInvocationHandler<E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends MethodBlacklistingCrudServiceDynamicInvocationHandler<E,Id> {

    private static final String BEFORE_METHOD_PREFIX = "onBefore";
    private static final String AFTER_METHOD_PREFIX = "onAfter";


    private List<CrudServicePlugin<E,Id>> plugins;

    public PluginCrudServiceDynamicInvocationHandler(CrudService<E, Id, CrudRepository<E, Id>> service, List<CrudServicePlugin<E, Id>> plugins, String... ignoredMethods) {
        super(service, ignoredMethods);
        this.plugins = plugins;
        plugins.forEach(plugin -> plugin.setService(service));
    }

    @Override
    protected Object handleProxyCall(Object o, Method method, Object[] args) throws Throwable {
        if(method.getName().length()<3){
            throw new IllegalArgumentException("Method names are expected to be at least 2 characters long");
        }
        String capitalFirstLetterMethodName = method.getName().substring(0, 1).toUpperCase() + method.getName().substring(1).toLowerCase();

        Object result;
        //call before method of plugins
        for (Object plugin : plugins) {
            Method beforeMethod = findPluginMethodByName(plugin, BEFORE_METHOD_PREFIX+capitalFirstLetterMethodName);
            if (beforeMethod!=null){
                beforeMethod.invoke(plugin,args);
            }
        }
        //actual call
        result = getMethods().get(method.getName()).invoke(getService(), args);

        //call after method of plugins
        for (Object plugin : plugins) {
            Method afterMethod = findPluginMethodByName(plugin, AFTER_METHOD_PREFIX+capitalFirstLetterMethodName);
            if(afterMethod!=null){
                //append result of proxy call to args for plugins onAfter method
                int extendedArgsLength = args.length+1;
                Object[] extendedArgs = new Object[extendedArgsLength];
                for (int i = 0; i < args.length; i++) {
                    extendedArgs[i]=args[i];
                }
                extendedArgs[extendedArgsLength-1]=result;
                afterMethod.invoke(plugin,extendedArgs);
            }
        }

        return result;
    }


    public Method findPluginMethodByName(Object plugin, String methodName){
        for (Method method : plugin.getClass().getMethods()) {
            if(method.getName().equals(methodName)){
                return method;
            }
        }
        return null;
    }
}