package io.github.vincemann.springrapid.acl.proxy;

import com.google.common.collect.Lists;
import io.github.vincemann.springrapid.acl.proxy.create.CrudServiceSecurityProxyFactory;
import io.github.vincemann.springrapid.acl.proxy.rules.DontCallRealMethod;
import io.github.vincemann.springrapid.acl.proxy.rules.OverrideDefaultSecurityRule;
import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.proxy.invocationHandler.abs.CrudServiceExtensionProxy;
import io.github.vincemann.springrapid.core.service.CrudService;
import io.github.vincemann.springrapid.acl.proxy.noRuleStrategy.HandleNoSecurityRuleStrategy;
import io.github.vincemann.springrapid.acl.proxy.rules.ServiceSecurityRule;
import io.github.vincemann.springrapid.acl.securityChecker.SecurityChecker;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Getter
/**
 * Proxy for {@link CrudService}, that applys {@link ServiceSecurityRule}s before calling service method.
 * After all Rules have been applied in the order they were givin for construction, the {@link io.github.vincemann.springrapid.acl.proxy.rules.DefaultServiceSecurityRule}
 * is applied if not prohibited for a specific method by a rule, by annotating the rule (intercept) methodwith {@link OverrideDefaultSecurityRule}.
 *
 * Create instances of this class with {@link CrudServiceSecurityProxyFactory}.
 */
public class CrudServiceSecurityProxy
        extends CrudServiceExtensionProxy<IdentifiableEntity<Serializable>, Serializable> {

    private static final String PRE_AUTHORIZE_METHOD_PREFIX = "preAuthorize";
    private static final String POST_AUTHORIZE_METHOD_PREFIX = "postAuthorize";

    private List<ServiceSecurityRule> rules =  new ArrayList<>();
    private HandleNoSecurityRuleStrategy noRuleHandlingStrategy;
    private ServiceSecurityRule defaultSecurityRule;


    public CrudServiceSecurityProxy(CrudService service,
                                    SecurityChecker securityChecker,
                                    HandleNoSecurityRuleStrategy noRuleHandlingStrategy,
                                    ServiceSecurityRule defaultSecurityRule,
                                    ServiceSecurityRule... rules) {
        super(service);
        this.noRuleHandlingStrategy = noRuleHandlingStrategy;
        this.defaultSecurityRule = defaultSecurityRule;
        this.rules.addAll(Lists.newArrayList(rules));
        this.rules.forEach(rule -> {
                rule.setSecurityChecker(securityChecker);
        });
        defaultSecurityRule.setSecurityChecker(securityChecker);

    }


    @Override
    protected Object handleProxyCall(Object o, Method method, Object[] args) throws Throwable {
        log.debug("SecurityProxy intercepting method: " + method.getName() + " of Class: " + method.getDeclaringClass().getSimpleName());
        if(method.getName().length()<3){
            throw new IllegalArgumentException("Method names are expected to be at least 2 characters long");
        }
        List<Object> argsList;
        if(args==null){
            argsList = new ArrayList<>();
        }else {
            argsList  = Lists.newArrayList(args);
        }

        try {
            AtomicBoolean invokeRealMethod = new AtomicBoolean(true);
            String capitalFirstLetterMethodName = method.getName().substring(0, 1).toUpperCase() + method.getName().substring(1);
            boolean defaultRuleReplaced = invokePreAuthorizeMethods(capitalFirstLetterMethodName,argsList,invokeRealMethod);
            Object result;
            if(!defaultRuleReplaced){
                log.debug("Applying preAuthorize method of default SecurityRule: " + defaultSecurityRule.getClass().getSimpleName());
                applyDefaultPreAuthSecurityRule(method,capitalFirstLetterMethodName,argsList);
            }else {
                log.debug("Default SecurityRule: " + defaultSecurityRule.getClass().getSimpleName() +" is skipped.");
            }
            if(invokeRealMethod.get()) {
                result = invokeRealMethod(method, args);
            }else {
                log.debug("Proxy rule decided to not call real method");
                result = null;
            }

            AtomicBoolean defaultPostAuthRuleOverridden = new AtomicBoolean(false);
            result = invokePostAuthorizeMethods(result,capitalFirstLetterMethodName,argsList,defaultPostAuthRuleOverridden);
            if(!defaultPostAuthRuleOverridden.get()){
                result = applyDefaultPostAuthSecurityRule(result,capitalFirstLetterMethodName,argsList);
            }
            return result;
        }catch (InvocationTargetException e){
            throw e.getCause();
        }
    }

    private Object invokeRealMethod(Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
        return getMethods().get(method.getName()).invoke(getService(),args);
    }


    private boolean invokePreAuthorizeMethods(String capitalFirstLetterMethodName, List<Object> argsList, AtomicBoolean invokeRealMethod) throws InvocationTargetException, IllegalAccessException {
        boolean defaultMethodOverritten = false;
        for (Object rule : rules) {
            Method preAuthMethod = findRuleMethodByName(rule, PRE_AUTHORIZE_METHOD_PREFIX + capitalFirstLetterMethodName);
            if (preAuthMethod != null) {
                log.debug("Found preAuthorize method: " + preAuthMethod.getName() + " in Rule: " + rule.getClass().getSimpleName());
//                if(!preAuthMethod.isAnnotationPresent(CalledByProxy.class)){
//                    log.warn("Found pre authorize method with suitable name:"+capitalFirstLetterMethodName+", but not annotated with "+CalledByProxy.class.getSimpleName() + " -> ignored.");
//                    continue;
//                }
                if(!preAuthMethod.getReturnType().equals(Void.TYPE)){
                    throw new RuntimeException("pre Authorize methods return type must be void");
                }
                if(preAuthMethod.isAnnotationPresent(DontCallRealMethod.class)){
                    invokeRealMethod.set(false);
                }
                if(preAuthMethod.isAnnotationPresent(OverrideDefaultSecurityRule.class)) {
                    log.debug("default pre authorize security method " + capitalFirstLetterMethodName + " was replaced by security rule " + rule.getClass().getSimpleName());
                    defaultMethodOverritten=true;
                }
                invokeAndAppendEntityClassArgIfNeeded(rule,preAuthMethod,argsList);
            }
        }
        return defaultMethodOverritten;
    }

    /**
     *
     * @param result
     * @param capitalFirstLetterMethodName
     * @param argsList
     * @param defaultPostAuthRuleOverridden
     * @return                      null if result did not change
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private Object invokePostAuthorizeMethods(Object result, String capitalFirstLetterMethodName, List<Object> argsList, AtomicBoolean defaultPostAuthRuleOverridden) throws InvocationTargetException, IllegalAccessException {
        //call after method of plugins
        Object returnValue = result;
        for (Object rule : rules) {

            Method postAuthMethod = findRuleMethodByName(rule, POST_AUTHORIZE_METHOD_PREFIX + capitalFirstLetterMethodName);
            if (postAuthMethod != null) {
                log.debug("Found post authorize method: " + postAuthMethod.getName() + " of rule: " + rule.getClass().getSimpleName());
//                if(!postAuthMethod.isAnnotationPresent(CalledByProxy.class)){
//                    log.warn("Found post authorize method with suitable name:"+capitalFirstLetterMethodName+", but not annotated with "+CalledByProxy.class.getSimpleName() + " -> ignored.");
//                    continue;
//                }
                if(postAuthMethod.isAnnotationPresent(OverrideDefaultSecurityRule.class)){
                    log.debug("post authorize method: " + postAuthMethod.getName() + " overrides default post auth method");
                    defaultPostAuthRuleOverridden.set(true);
                }

                List<Object> args = new ArrayList<>();
                if (!argsList.isEmpty()) {
                    if (returnValue != null) {
                        //append result of proxy call to args for plugins onAfter method
                        List<Object> copiedArgs = new ArrayList<>(argsList);
                        copiedArgs.add(returnValue);
                        args = copiedArgs;

                    } else {
                        args = argsList;
                    }
                } else {
                    if (returnValue != null) {
                        args.add(returnValue);
                    } else {
                        //void method without result
                        args = new ArrayList<>();
                    }
                }
                if(!postAuthMethod.getReturnType().equals(Void.TYPE)){
                    //wants to return value -> update return value
                    returnValue = invokeAndAppendEntityClassArgIfNeeded(rule,postAuthMethod,args);
                    log.debug("post auth method: " + postAuthMethod.getName() +" has return type -> this will be the updated return value: " + returnValue);
                }else {
                    //post auth method is void
                    invokeAndAppendEntityClassArgIfNeeded(rule,postAuthMethod,args);
                }

            }
        }
        return returnValue;
    }



    protected void applyDefaultPreAuthSecurityRule(Method method, String capitalFirstLetterMethodName, List<Object> args) throws InvocationTargetException, IllegalAccessException {
        if(!Lists.newArrayList(CrudService.class.getMethods()).contains(method)){
            log.debug("No Default pre auth Rule for method: " + method.getName());
            noRuleHandlingStrategy.react(method);
            return;
        }else {
            Method defaultPreAuthMethod = findRuleMethodByName(defaultSecurityRule, PRE_AUTHORIZE_METHOD_PREFIX + capitalFirstLetterMethodName);
            if(defaultPreAuthMethod!=null){
                log.debug("invoking default pre auth method: " + defaultPreAuthMethod.getName());
                invokeAndAppendEntityClassArgIfNeeded(defaultSecurityRule,defaultPreAuthMethod,args);
            }
        }
    }

    protected Object applyDefaultPostAuthSecurityRule(Object result , String capitalFirstLetterMethodName, List<Object> args) throws InvocationTargetException, IllegalAccessException {
        Method postAuthMethod = findRuleMethodByName(defaultSecurityRule, POST_AUTHORIZE_METHOD_PREFIX + capitalFirstLetterMethodName);
        if(postAuthMethod!=null){
            log.debug("applying default post auth method: " + postAuthMethod.getName());
            List<Object> copiedArgs = new ArrayList<>(args);
            copiedArgs.add(result);
            Object postAuthResult = invokeAndAppendEntityClassArgIfNeeded(defaultSecurityRule,postAuthMethod,copiedArgs);
            if(postAuthResult!=null){
                return postAuthResult;
            }else {
                return result;
            }
        }
        return result;
    }


    public Method findRuleMethodByName(Object target, String methodName){
        for (Method method : target.getClass().getMethods()) {
            if(method.getName().equals(methodName)){
                return method;
            }
        }
        return null;
    }
}
