package io.github.vincemann.generic.crud.lib.service.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.security.access.expression.ExpressionUtils;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.util.SimpleMethodInvocation;
import org.springframework.web.context.ContextLoader;

import java.lang.reflect.Method;

@Slf4j
/**
 * https://gist.github.com/matteocedroni/b0e5a935127316603dfb
 */
public class SecurityChecker {


        private static class SecurityObject{
            public void triggerCheck(){ /*NOP*/ }
        }

        private static Method triggerCheckMethod;
        private static SpelExpressionParser parser;

        static{
            try{ triggerCheckMethod =  SecurityChecker.SecurityObject.class.getMethod("triggerCheck"); }
            catch (NoSuchMethodException e) { log.error(e.getMessage(),e); }
            parser = new SpelExpressionParser();
        }

        public static boolean preAuthorize(String securityExpression){
            logExpression(securityExpression);

            SecurityChecker.SecurityObject securityObject = new SecurityChecker.SecurityObject();
            MethodSecurityExpressionHandler expressionHandler = ContextLoader.getCurrentWebApplicationContext().getBean(DefaultMethodSecurityExpressionHandler.class);
            EvaluationContext evaluationContext = expressionHandler.createEvaluationContext(SecurityContextHolder.getContext().getAuthentication(), new SimpleMethodInvocation(securityObject, triggerCheckMethod));
            boolean checkResult = ExpressionUtils.evaluateAsBoolean(parser.parseExpression(securityExpression), evaluationContext);

            if (log.isDebugEnabled()){ log.debug("Check result: "+checkResult); }

            return checkResult;
        }

        private static void logExpression(String securityExpression){
            if (log.isDebugEnabled()) { log.debug("Checking security expression ["+securityExpression+"]..."); }
        }

    }