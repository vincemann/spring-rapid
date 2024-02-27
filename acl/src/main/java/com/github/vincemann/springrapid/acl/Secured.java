package com.github.vincemann.springrapid.acl;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.*;

/**
 * This is used as a qualifier for dependency injection.
 * Indicates, that service bean annotated with this annotation is secured.
 * Usually used in combination with {@link com.github.vincemann.springrapid.core.service.CrudServiceDecorator}.
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
 * class SecuredFooService implements FooService{
 *
 *     private FooService decorated;
 *     private AclTemplate aclTemplate;
 *
 *     @Overwrite
 *     public Object foo(){
 *         aclTemplate.checkWritePermission(...)
 *         return decorated.foo()
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
 * @see com.github.vincemann.springrapid.core.service.CrudServiceDecorator
 * @see com.github.vincemann.springrapid.acl.service.SecuredCrudServiceDecorator
 *
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier("secured")
@Inherited
public @interface Secured {
}
