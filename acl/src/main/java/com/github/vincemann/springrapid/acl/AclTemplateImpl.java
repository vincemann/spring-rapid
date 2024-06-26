package com.github.vincemann.springrapid.acl;

import com.github.vincemann.springrapid.acl.service.PermissionStringConverter;
import com.github.vincemann.springrapid.acl.util.AuthorizationUtils;
import com.github.vincemann.springrapid.auth.AuthPrincipal;
import com.github.vincemann.springrapid.auth.IdAware;
import com.github.vincemann.springrapid.auth.RapidSecurityContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.expression.ExpressionUtils;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.util.SimpleMethodInvocation;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;

/**
 *
 *
 * DefaultImpl of {@link AclTemplate}.
 * Evaluates {@link MethodSecurityExpressionHandler} in a programmatic fashion.
 *
 * Copied an modified from:
 * https://gist.github.com/matteocedroni/b0e5a935127316603dfb
 *
 */
public class AclTemplateImpl implements AclTemplate, ApplicationContextAware {

    private Method triggerCheckMethod;
    private SpelExpressionParser parser;
    private ApplicationContext applicationContext;
    private PermissionStringConverter permissionStringConverter;

    public AclTemplateImpl() {
        try {
            this.triggerCheckMethod = AclTemplateImpl.SecurityObject.class.getMethod("triggerCheck");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        parser = new SpelExpressionParser();
    }

    @Override
    public <E extends IdAware<? extends Serializable>, C extends Collection<E>> C filter(C toFilter, Permission permission) {
        AuthorizationUtils.assertAuthenticated();
        Collection<E> filtered = new HashSet<>();
        for (E entity : toFilter) {
            boolean permitted = _checkPermission(entity.getId(), entity.getClass(), permission);
            if (permitted) {
                filtered.add(entity);
            }
        }
        return (C) filtered;
    }

    @Override
    public void checkPermission(Serializable id, Class<?> clazz, Permission permission) {

        boolean permitted = _checkPermission(id, clazz, permission);
        if (!permitted) {
            AuthPrincipal principal = RapidSecurityContext.currentPrincipal();
            String permissionString = permissionStringConverter.convert(permission);
            throw new AccessDeniedException("Permission not Granted! Principal: " + principal.shortToString() +
                    " does not have Permission: " + permissionString + " for entity: {" + clazz.getSimpleName() + ", id: " + id + "}");
        }


    }

    private boolean _checkPermission(Serializable id, Class<?> clazz, Permission permission) {
        Assert.notNull(permission,"checked permission must not be null");
        Assert.notNull(clazz,"entity class must not be null");
        Assert.notNull(id,"id must not be null");

        AuthorizationUtils.assertAuthenticated();

        String permissionString = permissionStringConverter.convert(permission);
        return checkExpression("hasPermission(" + id + ",'" + clazz.getName() + "','" + permissionString + "')");
    }

    public void checkPermission(IdAware<?> entity, Permission permission) throws AccessDeniedException {
        Assert.notNull(entity,"checked permission must not be null");
        Assert.notNull(entity,"entity must not be null");
        Assert.notNull(entity.getId(),"id must not be null");

        boolean permitted = _checkPermission(entity.getId(), entity.getClass(), permission);
        if (!permitted) {
            AuthPrincipal principal = RapidSecurityContext.currentPrincipal();
            String permissionString = permissionStringConverter.convert(permission);
            throw new AccessDeniedException("Permission not Granted! Principal: " + principal.shortToString() +
                    " does not have Permission: " + permissionString + " for entity: " + entity);
        }
    }


    @Override
    public boolean checkExpression(String securityExpression) {

        AclTemplateImpl.SecurityObject securityObject = new AclTemplateImpl.SecurityObject();
        MethodSecurityExpressionHandler expressionHandler = applicationContext.getBean(MethodSecurityExpressionHandler.class);
        //gibt dem einfach nen gemockten Methodenaufruf und nen gemocktes securityObject rein
        EvaluationContext evaluationContext = expressionHandler.createEvaluationContext(
                SecurityContextHolder.getContext().getAuthentication(),
                new SimpleMethodInvocation(securityObject, triggerCheckMethod)
        );
        return ExpressionUtils.evaluateAsBoolean(parser.parseExpression(securityExpression), evaluationContext);
    }

    private static class SecurityObject {
        public void triggerCheck() { /*NOP*/ }
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Autowired
    public void setPermissionStringConverter(PermissionStringConverter permissionStringConverter) {
        this.permissionStringConverter = permissionStringConverter;
    }
}