package io.github.vincemann.generic.crud.lib.advice;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.model.biDir.child.BiDirChild;
import io.github.vincemann.generic.crud.lib.model.biDir.parent.BiDirParent;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.ServiceBeanType;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.service.locator.CrudServiceLocator;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.util.ProxyUtils;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.test.util.AopTestUtils;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

@Aspect
//@Transactional
@Component
@Slf4j
//Transactional Method interceptor is called before this advice -> methods will be executed in repoTransaction
//@Order(100)
public class BiDirRepositoryAdvice /*implements MethodInterceptor*/ {

    private CrudServiceLocator crudServiceLocator;


    @Autowired
    public BiDirRepositoryAdvice(CrudServiceLocator crudServiceLocator) {
        this.crudServiceLocator = crudServiceLocator;
    }


//    @Override
//    public Object invoke(MethodInvocation invocation) throws Throwable {
//        Method method = invocation.getMethod();
//        if (method.getName().startsWith("save")) {
//            prePersist(invocation);
//        } else if (method.getName().startsWith("delete")) {
//            preRemove(invocation);
//        }
//        // add update here
//        return invocation.proceed();
//    }
//
//    private void preRemove(MethodInvocation invocation) throws IllegalAccessException, NoIdException {
//        if (invocation.getMethod().getParameterTypes().length >= 1) {
//            Class<?> firstArgClass = invocation.getMethod().getParameterTypes()[0];
//            Object firstArg = invocation.getArguments()[0];
//            if (IdentifiableEntity.class.isAssignableFrom(firstArgClass)) {
//                if (firstArg != null) {
//                    preRemoveEntity(firstArg);
//                }
//            } else {
//                if (firstArg instanceof Serializable) {
//                    //delete by id
//                    Optional<Object> entity = resolveId(((Serializable) firstArg), invocation.getMethod().getDeclaringClass());
//                    if (entity.isPresent()) {
//                        preRemoveEntity(entity.get());
//                    } else {
//                        log.warn("preDelete BiDirEntity could not be done, because for id: " + firstArg + " was not entity found");
//                    }
//                }
//            }
//        }
//    }
//
//
//
//
//
//    private void prePersist(MethodInvocation invocation) throws IllegalAccessException {
//        if (invocation.getMethod().getParameterTypes().length >= 1) {
//            Class<?> firstArgClass = invocation.getMethod().getParameterTypes()[0];
//            Object firstArg = invocation.getArguments()[0];
//            if (BiDirParent.class.isAssignableFrom(firstArgClass)) {
//                log.debug("applying pre persist BiDirParent logic for: " + firstArg);
//                //prePersistBiDirParent(((BiDirParent) firstArg));
//            } else if (BiDirChild.class.isAssignableFrom(firstArgClass)) {
//                log.debug("applying pre persist BiDirChild logic for: " + firstArg);
//                prePersistBiDiChild(((BiDirChild) firstArg));
//            }
//        }
//    }

    @Before("io.github.vincemann.generic.crud.lib.advice.SystemArchitecture.saveOperation() && " +
            "io.github.vincemann.generic.crud.lib.advice.SystemArchitecture.repoOperation() && " +
            "args(biDirParent)")
    public void prePersistBiDirParent(BiDirParent biDirParent) throws IllegalAccessException {
        log.debug("pre persist biDirParent hook reached for: " + biDirParent);
        setChildrensParentRef(biDirParent);
    }

    @Before("io.github.vincemann.generic.crud.lib.advice.SystemArchitecture.saveOperation() && " +
            "io.github.vincemann.generic.crud.lib.advice.SystemArchitecture.repoOperation() && " +
            "args(biDirChild)")
    public void prePersistBiDiChild(BiDirChild biDirChild) throws IllegalAccessException {
        log.debug("pre persist biDirChild hook reached for: " + biDirChild);
        setParentsChildRef(biDirChild);
    }


    @Around("io.github.vincemann.generic.crud.lib.advice.SystemArchitecture.deleteOperation() && " +
            "io.github.vincemann.generic.crud.lib.advice.SystemArchitecture.repoOperation() && " +
            "args(serializable)")
    public Object preRemoveBiDirEntity(ProceedingJoinPoint joinPoint, Serializable serializable) throws Throwable {
        Optional<Object> parent = resolveId(serializable, joinPoint);
        if (parent.isPresent()){
            preRemoveEntity(parent.get());
        }else {
            log.warn("preDelete BiDirEntity could not be done, because for id: " + serializable + " was not entity found");
        }
        return joinPoint.proceed();
    }


    private void preRemoveEntity(Object entity) throws IllegalAccessException {
        if (BiDirParent.class.isAssignableFrom(entity.getClass())) {
            log.debug("applying pre remove BiDirParent logic for: " + entity.getClass());
            ((BiDirParent) entity).dismissChildrensParent();
        }
        if (BiDirChild.class.isAssignableFrom(entity.getClass())) {
            log.debug("applying pre remove BiDirChild logic for: " + entity);
            BiDirChild biDirChild = (BiDirChild) entity;
            for (BiDirParent parent : biDirChild.findParents()) {
                parent.dismissChild(biDirChild);
            }
            biDirChild.dismissParents();
        }
    }

    private Optional<Object> resolveId(Serializable id, ProceedingJoinPoint joinPoint) throws NoIdException, IllegalAccessException {
        Class entityClass = resolveEntityClass(joinPoint);
        log.debug("pre remove hook reached for entity " + entityClass+":"+id);
        CrudService service = crudServiceLocator.find(entityClass);
        Assert.notNull(service,"Did not find service for entityClass: " + entityClass);
        return service.findById((id));
    }

    private Class resolveEntityClass(ProceedingJoinPoint joinPoint) throws IllegalAccessException {
        SimpleJpaRepository jpaRepository = AopTestUtils.getUltimateTargetObject(joinPoint.getTarget());
        Field entityInformationField = ReflectionUtils.findField(SimpleJpaRepository.class, field -> field.getName().equals("entityInformation"));
        entityInformationField.setAccessible(true);
        JpaEntityInformation entityInformation = ((JpaEntityInformation) entityInformationField.get(jpaRepository));
        return entityInformation.getJavaType();
    }

    private void setChildrensParentRef(BiDirParent biDirParent) throws IllegalAccessException {
        Set<? extends BiDirChild> children = biDirParent.getChildren();
        for (BiDirChild child : children) {
            child.setParentRef(biDirParent);
        }
        Set<Collection<? extends BiDirChild>> childCollections = biDirParent.getChildrenCollections().keySet();
        for (Collection<? extends BiDirChild> childCollection : childCollections) {
            for (BiDirChild biDirChild : childCollection) {
                biDirChild.setParentRef(biDirParent);
            }
        }
    }


    private void setParentsChildRef(BiDirChild biDirChild) throws IllegalAccessException {
        //set backreferences
        for (BiDirParent parent : biDirChild.findParents()) {
            parent.addChild(biDirChild);
        }
    }

}
