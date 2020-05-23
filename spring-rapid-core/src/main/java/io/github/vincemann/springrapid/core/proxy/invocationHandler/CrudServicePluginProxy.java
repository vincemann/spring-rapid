package io.github.vincemann.springrapid.core.proxy.invocationHandler;


import io.github.vincemann.springrapid.commons.NullableOptional;
import io.github.vincemann.springrapid.core.proxy.invocationHandler.abs.CrudServiceExtensionProxy;
import io.github.vincemann.springrapid.core.service.CrudService;
import io.github.vincemann.springrapid.core.service.plugin.CrudServicePlugin;
import io.github.vincemann.springrapid.commons.Lists;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Proxy that calls hook methods {@link CrudServicePlugin}s.
 * @see CrudServicePlugin
 */
@Slf4j
public class CrudServicePluginProxy
        extends CrudServiceExtensionProxy {

    private static final String BEFORE_METHOD_PREFIX = "onBefore";
    private static final String AFTER_METHOD_PREFIX = "onAfter";


    @Getter
    private List<CrudServicePlugin> plugins;

    public CrudServicePluginProxy(CrudService service, List<CrudServicePlugin> plugins, String... ignoredMethods) {
        super(service, ignoredMethods);
        this.plugins = plugins;
    }

    @Override
    protected Object proxy(Object target, Method method, Object[] args) throws Throwable {
//        List<Method> methods = Arrays.stream(target.getClass().getMethods())
//                .filter(m -> m.getName().equals(method.getName()))
//                .collect(Collectors.toList());
//        Assert.isTrue(methods.size()==1);

        MethodHandle targetMethod = MethodHandle.create(getMethods().get(method.getName()),getService());
        NullableOptional<Object> result;
        //call before method of plugins
        for (Object plugin : plugins) {
            MethodHandle beforeMethod = findMethod(createPrefixedMethodName(BEFORE_METHOD_PREFIX, method.getName()), plugin);
            if(beforeMethod!=null){
                log.debug("Found before method:"+beforeMethod.getName()+" of plugin: " + plugin.getClass().getSimpleName());
                invokeAndAppendEntityClassArgIfNeeded(beforeMethod,args);
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
                NullableOptional<Object> pluginResult = invokeAndAppendEntityClassArgIfNeeded(afterMethod, finalArgs.toArray());
                if(!afterMethod.isVoidMethod()){
                    log.debug("Plugin method updated old ret value: " + result + " to: " + pluginResult.get());
                    result=pluginResult;
                }
            }
        }

        return result.get();
    }
}