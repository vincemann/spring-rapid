package com.github.vincemann.springrapid.acl.proxy;

import com.github.vincemann.springrapid.commons.Lists;
import com.github.vincemann.springrapid.acl.proxy.rules.DontCallTargetMethod;
import com.github.vincemann.springrapid.acl.proxy.rules.OverrideDefaultSecurityRule;
import com.github.vincemann.springrapid.commons.NullableOptional;
import com.github.vincemann.springrapid.core.proxy.CrudServiceExtensionProxy;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.acl.proxy.rules.ServiceSecurityRule;
import com.github.vincemann.springrapid.acl.SecurityChecker;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.test.util.AopTestUtils;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Slf4j
/**
 * Proxy for {@link CrudService}, that applys {@link ServiceSecurityRule}s before calling service method.
 * After all Rules have been applied in the order they were given in for construction, the {@link com.github.vincemann.springrapid.acl.proxy.rules.DefaultServiceSecurityRule}
 * is applied if not prohibited (@see {@link OverrideDefaultSecurityRule})
 *
 * Is created by {@link CrudServiceSecurityProxyFactory} or by {@link ConfigureProxies}.
 */
public class CrudServiceSecurityProxy
        extends CrudServiceExtensionProxy {

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

        private State(){}

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

    @Getter
    private List<ServiceSecurityRule> rules = new ArrayList<>();
    @Getter
    @Setter
    private ServiceSecurityRule defaultSecurityRule;
    private State state;

    protected CrudServiceSecurityProxy(CrudService service,
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
        log.debug("SecurityProxy intercepting method: " + method.getName() + " of Class: " + AopTestUtils.getUltimateTargetObject(target).getClass());
        state = State.create(getMethods().get(method.getName()), getService(),args);

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


    private void invokePreAuthorizeMethods() throws Throwable {
        for (Object rule : rules) {
            invokePreAuthorizeMethod(rule);
        }
    }

    private void invokePreAuthorizeMethod(Object rule) throws Throwable {
        MethodHandle preAuthMethod = findMethod(createPrefixedMethodName(PRE_AUTHORIZE_METHOD_PREFIX,state.targetMethodName),rule);
        if (preAuthMethod != null) {
            log.debug("Found preAuthorize method: " + preAuthMethod.getName() + " in Rule: " + rule.getClass().getSimpleName());
            Assert.isTrue(preAuthMethod.isVoidMethod(), "Pre Authorize methods return type must be void");
            applyPreAuthMethodConfig(preAuthMethod);
            invokeAndAppendEntityClassArgIfNeeded(preAuthMethod, state.targetMethodArgs);
        }
    }

    private void invokePostAuthorizeMethod(Object rule) throws Throwable {
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

    private void invokePostAuthorizeMethods() throws Throwable {
        //call after method of plugins
        for (Object rule : rules) {
            invokePostAuthorizeMethod(rule);
        }
    }



}
