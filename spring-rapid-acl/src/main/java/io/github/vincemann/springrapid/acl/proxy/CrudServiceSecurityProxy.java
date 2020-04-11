package io.github.vincemann.springrapid.acl.proxy;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.collect.Lists;
import io.github.vincemann.springrapid.acl.proxy.create.CrudServiceSecurityProxyFactory;
import io.github.vincemann.springrapid.acl.proxy.rules.DontCallTargetMethod;
import io.github.vincemann.springrapid.acl.proxy.rules.OverrideDefaultSecurityRule;
import io.github.vincemann.springrapid.core.util.NullableOptional;
import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.proxy.invocationHandler.abs.CrudServiceExtensionProxy;
import io.github.vincemann.springrapid.core.service.CrudService;
import io.github.vincemann.springrapid.acl.proxy.rules.ServiceSecurityRule;
import io.github.vincemann.springrapid.acl.securityChecker.SecurityChecker;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

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

    @Getter
    @Setter
    public static class State {
        boolean invokeTargetMethod = true;
        boolean overrideDefaultPreAuthMethod = false;
        boolean overrideDefaultPostAuthMethod = false;
        MethodHandle targetMethod;
        String targetMethodName;
        Object target;
        Object[] targetMethodArgs;
        NullableOptional<Object> result = NullableOptional.empty();

        public static State create(Method targetMethod, Object target, Object[] targetMethodArgs) {
            State state = new State();
            state.targetMethodArgs = targetMethodArgs;
            state.targetMethod = MethodHandle.create(targetMethod, target);
            state.overrideDefaultPostAuthMethod = false;
            state.overrideDefaultPreAuthMethod = false;
            state.invokeTargetMethod = true;
            state.targetMethodName = targetMethod.getName();
            state.target = target;
            return state;
        }
    }

    private static final String PRE_AUTHORIZE_METHOD_PREFIX = "preAuthorize";
    private static final String POST_AUTHORIZE_METHOD_PREFIX = "postAuthorize";

    private List<ServiceSecurityRule> rules = new ArrayList<>();
    private ServiceSecurityRule defaultSecurityRule;
    private State state;

    public CrudServiceSecurityProxy(CrudService service,
                                    SecurityChecker securityChecker,
                                    ServiceSecurityRule defaultSecurityRule,
                                    ServiceSecurityRule... rules) {
        super(service);
        this.defaultSecurityRule = defaultSecurityRule;
        this.rules.addAll(Lists.newArrayList(rules));
        this.rules.forEach(rule -> {
            rule.setSecurityChecker(securityChecker);
        });
        defaultSecurityRule.setSecurityChecker(securityChecker);

    }



    @Override
    protected Object proxy(Object target, Method method, Object[] args) throws Throwable {
        log.debug("SecurityProxy intercepting method: " + method.getName() + " of Class: " + method.getDeclaringClass().getSimpleName());
        state = State.create(method, target,args);

        invokePreAuthorizeMethods();
        if (!state.overrideDefaultPreAuthMethod) {
            log.debug("Applying preAuthorize method of default SecurityRule: " + defaultSecurityRule.getClass().getSimpleName());
            invokePreAuthorizeMethod(defaultSecurityRule);
        } else {
            log.debug("Default SecurityRule: " + defaultSecurityRule.getClass().getSimpleName() + " is skipped.");
        }

        if (state.invokeTargetMethod) {
            state.result = state.getTargetMethod().execute(state.targetMethodArgs);
        } else {
            log.debug("Real Method call is skipped");
        }

        invokePostAuthorizeMethods();

        if (!state.overrideDefaultPostAuthMethod) {
            log.debug("Applying postAuthorize method of default SecurityRule: " + defaultSecurityRule.getClass().getSimpleName());

            invokePostAuthorizeMethod(defaultSecurityRule);
        }
        return state.result.get();
    }


    private void invokePreAuthorizeMethods() throws InvocationTargetException, IllegalAccessException {
        for (Object rule : rules) {
            invokePreAuthorizeMethod(rule);
        }
    }

    private void invokePreAuthorizeMethod(Object rule) {
        MethodHandle preAuthMethod = findMethod(createPrefixedMethodName(PRE_AUTHORIZE_METHOD_PREFIX,state.targetMethodName),rule);
        if (preAuthMethod != null) {
            log.debug("Found preAuthorize method: " + preAuthMethod.getName() + " in Rule: " + rule.getClass().getSimpleName());
            Assert.isTrue(preAuthMethod.isVoidMethod(), "Pre Authorize methods return type must be void");
            applyPreAuthMethodConfig(preAuthMethod);
            invokeAndAppendEntityClassArgIfNeeded(preAuthMethod, state.targetMethodArgs);
        }
    }

    private void invokePostAuthorizeMethod(Object rule) {
        MethodHandle postAuthMethod = findMethod(createPrefixedMethodName(POST_AUTHORIZE_METHOD_PREFIX,state.targetMethodName),rule);
        if (postAuthMethod != null) {
            log.debug("Found postAuthorize method: " + postAuthMethod.getName() + " in Rule: " + rule.getClass().getSimpleName());
            applyPostAuthMethodConfig(postAuthMethod);
            List<Object> args = Lists.newArrayList(state.targetMethodArgs);
            //append result of service method if there is one
            if (!state.targetMethod.isVoidMethod()) {
                args.add(state.getResult().get());
            }
            NullableOptional<Object> ruleResult = invokeAndAppendEntityClassArgIfNeeded(postAuthMethod, args.toArray());
            if (!postAuthMethod.isVoidMethod()) {
                state.result = ruleResult;
            }
        }
    }

    private void applyPostAuthMethodConfig(MethodHandle method) {
        if (method.hasAnnotation(OverrideDefaultSecurityRule.class)) {
            state.overrideDefaultPostAuthMethod = true;
        }
    }

    private void applyPreAuthMethodConfig(MethodHandle method) {
        if (method.hasAnnotation(DontCallTargetMethod.class)) {
            state.invokeTargetMethod = false;
        }
        if (method.hasAnnotation(OverrideDefaultSecurityRule.class)) {
            state.overrideDefaultPreAuthMethod = true;
        }
    }

    private void invokePostAuthorizeMethods() {
        //call after method of plugins
        for (Object rule : rules) {
            invokePostAuthorizeMethod(rule);
        }
    }

}
