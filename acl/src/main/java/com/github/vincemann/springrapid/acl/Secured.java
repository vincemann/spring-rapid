package com.github.vincemann.springrapid.acl;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.*;

/**
 * This is used as a qualifier for dependency injection.
 * Indicates, that service bean annotated with this annotation is secured.
 * Usually used in combination with {@link com.github.vincemann.springrapid.acl.service.SecuredServiceDecorator}.
 * Usually it exists one "normal" version of the bean and one "secured" version of the bean. The secured service decorates
 * the root service with security checks.
 *
 * Example:
 *
 * interface FooService{
 *     void foo();
 * }
 *
 * class FooServiceImpl implements FooService{
 *     ...
 * }
 *
 * class SecuredFooService extends SecuredServiceDecorator<FooService> implements FooService{
 *
 *      public SecuredFooService(FooService decorated){
 *          super(decorated);
 *      }
 *
 *     @Overwrite
 *     public Object foo(){
 *         getAclTemplate().checkWritePermission(...)
 *         return getDecorated().foo()
 *     }
 *
 * }
 *
 * @Bean
 * public FooService fooService(){
 *     return new FooServiceImpl();
 * }
 *
 * @Bean
 * @Secured
 * public FooService securedFooService(FooService service){
 *     return new SecuredFooService(service);
 * }
 *
 *
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier("secured")
@Inherited
public @interface Secured {
}
