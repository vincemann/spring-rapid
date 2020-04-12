package io.github.vincemann.springrapid.acl.securityChecker;

import io.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.expression.ExpressionUtils;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.util.SimpleMethodInvocation;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;

@Slf4j
/**
 * Copied an modified from:
 * https://gist.github.com/matteocedroni/b0e5a935127316603dfb
 *
 * DefaultImpl of {@link SecurityChecker}.
 * Uses {@link MethodSecurityExpressionHandler} for expression evaluation and {@link org.springframework.security.core.context.SecurityContext}
 * to get information about authenticated user.
 */
@ServiceComponent
public class DefaultSecurityChecker implements SecurityChecker,ApplicationContextAware {


    private Method triggerCheckMethod;
    private SpelExpressionParser parser;
    private ApplicationContext applicationContext;

    public DefaultSecurityChecker() {
        try {
            this.triggerCheckMethod = DefaultSecurityChecker.SecurityObject.class.getMethod("triggerCheck");
        } catch (NoSuchMethodException e) {
            log.error(e.getMessage(), e);
        }
        parser = new SpelExpressionParser();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public <E extends IdentifiableEntity<? extends Serializable>, C extends Collection<E>> C filter(C toFilter, String permission){
        Collection<E> filtered =  new HashSet<>();
        for (E entity : toFilter) {
            boolean permitted = checkExpression("hasPermission(" + entity.getId() + ",'" + entity.getClass().getName() + "','" + permission + "')");
            if(permitted){
                filtered.add(entity);
            }
        }
        //todo legit?
        return (C) filtered;
    }

    @Override
    public void checkIfAuthenticated(){
        boolean authenticated = checkExpression("isAuthenticated()");
        if(!authenticated){
            throw new AccessDeniedException("User must be authenticated");
        }
    }


    @Override
    public void checkPermission(Serializable id,Class<?> clazz,String permission){
        if(id==null){
            throw new NullPointerException("Id must not be null");
        }
        boolean permitted = checkExpression("hasPermission(" + id + ",'" + clazz.getName() + "','" + permission + "')");
        if(!permitted){
            String principal = SecurityContextHolder.getContext().getAuthentication().getName();
            throw new AccessDeniedException("Permission not Granted! Principal: "+principal+" does not have Permission " + permission + " for entity: {"+clazz.getSimpleName() + ", id: " + id+"}");
        }
    }

    @Override
    public void checkRole(String role){
        String principal = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean permitted = checkExpression("hasRole('" + role + "')");
        if(!permitted){
            throw new AccessDeniedException("Permission not Granted! Principal : " + principal + " does not have role: " + role);
        }
    }

    @Override
    public boolean checkExpression(String securityExpression) {
        logExpression(securityExpression);

        DefaultSecurityChecker.SecurityObject securityObject = new DefaultSecurityChecker.SecurityObject();
        MethodSecurityExpressionHandler expressionHandler = applicationContext.getBean(MethodSecurityExpressionHandler.class);
        //gibt dem einfach nen gemockten Methodenaufruf und nen gemocktes securityObject rein
        EvaluationContext evaluationContext = expressionHandler.createEvaluationContext(
                SecurityContextHolder.getContext().getAuthentication(),
                new SimpleMethodInvocation(securityObject, triggerCheckMethod)
        );
        boolean checkResult = ExpressionUtils.evaluateAsBoolean(parser.parseExpression(securityExpression), evaluationContext);
        if (log.isDebugEnabled()) {
            log.debug("Check result: " + checkResult);
        }
        return checkResult;
    }

    private static class SecurityObject {
        public void triggerCheck() { /*NOP*/ }
    }

    private static void logExpression(String securityExpression) {
        if (log.isDebugEnabled()) {
            log.debug("Checking security expression [" + securityExpression + "]...");
        }
    }

}