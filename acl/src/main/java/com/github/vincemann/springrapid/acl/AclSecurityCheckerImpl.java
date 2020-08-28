package com.github.vincemann.springrapid.acl;

import com.github.vincemann.aoplog.Severity;
import com.github.vincemann.aoplog.api.LogInteraction;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import com.github.vincemann.springrapid.core.security.RapidSecurityChecker;
import com.github.vincemann.springrapid.core.util.Authenticated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.expression.ExpressionUtils;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.util.SimpleMethodInvocation;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;

@Slf4j
/**
 * Copied an modified from:
 * https://gist.github.com/matteocedroni/b0e5a935127316603dfb
 *
 * DefaultImpl of {@link AclSecurityChecker}.
 * Uses {@link MethodSecurityExpressionHandler} for expression evaluation and {@link org.springframework.security.core.context.SecurityContext}
 * to get information about authenticated user.
 */
public class AclSecurityCheckerImpl extends RapidSecurityChecker implements AclSecurityChecker,ApplicationContextAware {


    private Method triggerCheckMethod;
    private SpelExpressionParser parser;
    private ApplicationContext applicationContext;
    private RapidSecurityContext<?> rapidSecurityContext;

    public AclSecurityCheckerImpl() {
        try {
            this.triggerCheckMethod = AclSecurityCheckerImpl.SecurityObject.class.getMethod("triggerCheck");
        } catch (NoSuchMethodException e) {
            log.error(e.getMessage(), e);
        }
        parser = new SpelExpressionParser();
    }

    @Autowired
    public void injectRapidSecurityContext(RapidSecurityContext<?> rapidSecurityContext) {
        this.rapidSecurityContext = rapidSecurityContext;
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

    @LogInteraction(Severity.TRACE)
    @Override
    public void checkAuthenticated(){
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
            RapidAuthenticatedPrincipal principal = rapidSecurityContext.currentPrincipal();
            throw new AccessDeniedException("Permission not Granted! Principal: "+principal+
                    " does not have Permission: " + permission + " for entity: {"+clazz.getSimpleName() + ", id: " + id+"}");
        }
    }

    @Override
    public void checkHasRole(String role){
        boolean permitted = checkExpression("hasRole('" + role + "')");
        if(!permitted){
            throw new AccessDeniedException("Permission not Granted! Principal : " + rapidSecurityContext.currentPrincipal()
                    + " does not have requested role: " + role);
        }
    }

    @Override
    public boolean checkExpression(String securityExpression) {
//        if (log.isDebugEnabled()) {
//            log.debug("EVALUATING SECURITY EXPRESSION: [" + securityExpression + "]...");
//        }

        AclSecurityCheckerImpl.SecurityObject securityObject = new AclSecurityCheckerImpl.SecurityObject();
        MethodSecurityExpressionHandler expressionHandler = applicationContext.getBean(MethodSecurityExpressionHandler.class);
        //gibt dem einfach nen gemockten Methodenaufruf und nen gemocktes securityObject rein
        EvaluationContext evaluationContext = expressionHandler.createEvaluationContext(
                SecurityContextHolder.getContext().getAuthentication(),
                new SimpleMethodInvocation(securityObject, triggerCheckMethod)
        );
        boolean checkResult = ExpressionUtils.evaluateAsBoolean(parser.parseExpression(securityExpression), evaluationContext);
//        if (log.isDebugEnabled()) {
//            log.debug("SECURITY EXPRESSION EVALUATED AS: " + checkResult);
//        }
        return checkResult;
    }

    private static class SecurityObject {
        public void triggerCheck() { /*NOP*/ }
    }


}