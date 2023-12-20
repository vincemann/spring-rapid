package com.github.vincemann.springrapid.core.service.context;

import com.github.vincemann.springrapid.core.IdConverter;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * Sets {@link ServiceCallContext} in {@link ServiceCallContextHolder} for each service call.
 * hooks onto every service call for services implementing {@link CrudService}.
 * Checks first argument of function. If it is of type {@link IdentifiableEntity}, this will be interpreted as {@link ServiceCallContext}s target Entity.
 * If first arg is of Type {@link IdConverter#getIdType()}, then it will be interpreted as id of target entity.
 *
 * target entity is the entity targeted bc service call.
 */
@Aspect
public class ServiceCallContextAdvice {

    IdConverter<?> idConverter;

    public ServiceCallContextAdvice(IdConverter<?> idConverter) {
        this.idConverter = idConverter;
    }

    @Around(value = "com.github.vincemann.springrapid.core.advice.SystemArchitecture.serviceOperation()")
    public Object aroundServiceOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        ServiceCallContext context = ServiceCallContextHolder.createEmptyContext();

        Class entityClass = ((CrudService) joinPoint.getTarget()).getEntityClass();
        context.setEntityClass(entityClass);

        Object[] args = joinPoint.getArgs();
        if (args.length > 0){
            Object firstArg = args[0];
            if (firstArg != null){

                if (firstArg instanceof IdentifiableEntity){
                    context.setId(((IdentifiableEntity<?>) firstArg).getId());
                }else if (idConverter.getIdType().equals(firstArg.getClass())){
                    context.setId(idConverter.toId(String.valueOf(firstArg)));
                }
            }
        }

        ServiceCallContextHolder.setContext(context);

        Object ret;
        try {
           ret = joinPoint.proceed();
        }finally {
            ServiceCallContextHolder.clearContext();
        }

        return ret;
    }
}
