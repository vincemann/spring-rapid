package io.github.vincemann.springrapid.core.proxy.invocationHandler;


import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.proxy.invocationHandler.abs.CrudServiceExtensionProxy;
import io.github.vincemann.springrapid.core.service.CrudService;
import io.github.vincemann.springrapid.core.service.plugin.CrudServicePlugin;
import io.github.vincemann.springrapid.core.util.NullableOptional;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
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
    protected Object proxy(Object target, Method method, Object[] args){
        MethodHandle targetMethod = MethodHandle.create(method, target);
        NullableOptional<Object> result;
        //call before method of plugins
        for (Object plugin : plugins) {
            MethodHandle beforeMethod = findMethod(createPrefixedMethodName(BEFORE_METHOD_PREFIX, method.getName()), plugin);
            if(beforeMethod!=null){
                log.debug("Found before method:"+beforeMethod.getName()+" of plugin: " + plugin.getClass().getSimpleName());
                invokeAndAppendEntityClassArgIfNeeded(beforeMethod,Lists.newArrayList(args));
            }
        }

        //actual call
        result = targetMethod.execute(args);

        for (Object plugin : plugins) {
            MethodHandle afterMethod = findMethod(createPrefixedMethodName(AFTER_METHOD_PREFIX, method.getName()), plugin);
            if(afterMethod!=null){
                log.debug("Found after method: "+afterMethod.getName()+" of plugin: " + plugin.getClass().getSimpleName());
                //append service method return value if present
                List<Object> finalArgs = Lists.newArrayList(args);
                if(result.isPresent()){
                    finalArgs.add(result.get());
                }
                NullableOptional<Object> pluginResult = invokeAndAppendEntityClassArgIfNeeded(afterMethod, finalArgs);
                if(!afterMethod.isVoidMethod()){
                    log.debug("Plugin method updated old ret value: " + result + " to: " + pluginResult.get());
                    result=pluginResult;
                }
            }
        }

        return result.get();
    }
}