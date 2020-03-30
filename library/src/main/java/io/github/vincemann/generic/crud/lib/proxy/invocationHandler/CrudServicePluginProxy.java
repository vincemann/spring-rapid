package io.github.vincemann.generic.crud.lib.proxy.invocationHandler;


import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.proxy.invocationHandler.abs.CrudServiceExtensionProxy;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.plugin.CrudServicePlugin;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class CrudServicePluginProxy<E extends IdentifiableEntity<Id>, Id extends Serializable>
        extends CrudServiceExtensionProxy<E, Id> {

    private static final String BEFORE_METHOD_PREFIX = "onBefore";
    private static final String AFTER_METHOD_PREFIX = "onAfter";


    private List<CrudServicePlugin<E, Id>> plugins;

    public CrudServicePluginProxy(CrudService<E, Id, CrudRepository<E, Id>> service, List<CrudServicePlugin<E, Id>> plugins, String... ignoredMethods) {
        super(service, ignoredMethods);
        this.plugins = plugins;
    }

    @Override
    protected Object handleProxyCall(Object o, Method method, Object[] args) throws Throwable {
        if (method.getName().length() < 3) {
            throw new IllegalArgumentException("Method names are expected to be at least 2 characters long");
        }
        try {
            String capitalFirstLetterMethodName = method.getName().substring(0, 1).toUpperCase() + method.getName().substring(1);

            Object result;
            //call before method of plugins
            for (Object plugin : plugins) {
                Method beforeMethod = findPluginMethodByName(plugin, BEFORE_METHOD_PREFIX + capitalFirstLetterMethodName);
                if (beforeMethod != null) {
                    invokeAndAppendEntityClassArgIfNeeded(plugin,beforeMethod, Arrays.asList(args));
                    //beforeMethod.invoke(plugin, args);
                }
            }
            //actual call
            result = getMethods().get(method.getName()).invoke(getService(), args);

            //call after method of plugins
            for (Object plugin : plugins) {
                Method afterMethod = findPluginMethodByName(plugin, AFTER_METHOD_PREFIX + capitalFirstLetterMethodName);
                if (afterMethod != null) {
                    if (args != null) {
                        if (result != null) {
                            //append result of proxy call to args for plugins onAfter method
                            int extendedArgsLength = args.length + 1;
                            Object[] extendedArgs = new Object[extendedArgsLength];
                            for (int i = 0; i < args.length; i++) {
                                extendedArgs[i] = args[i];
                            }
                            extendedArgs[extendedArgsLength - 1] = result;
                            invokeAndAppendEntityClassArgIfNeeded(plugin,afterMethod,Arrays.asList(extendedArgs));
                            //afterMethod.invoke(plugin, extendedArgs);
                        } else {
                            //void -> only call with args
                            invokeAndAppendEntityClassArgIfNeeded(plugin,afterMethod,Arrays.asList(args));
                            //afterMethod.invoke(plugin, args);
                        }
                    } else {
                        if (result != null) {
                            invokeAndAppendEntityClassArgIfNeeded(plugin,afterMethod,Arrays.asList(result));
                            //afterMethod.invoke(plugin, result);
                        } else {
                            //void method without result
                            invokeAndAppendEntityClassArgIfNeeded(plugin,afterMethod,new ArrayList<>());
                            //afterMethod.invoke(plugin);
                        }
                    }


                }
            }

            return result;
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }


    public Method findPluginMethodByName(Object plugin, String methodName) {
        for (Method method : plugin.getClass().getMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }
}