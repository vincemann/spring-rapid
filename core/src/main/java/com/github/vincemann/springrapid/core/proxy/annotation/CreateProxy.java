package com.github.vincemann.springrapid.core.proxy.annotation;

import org.springframework.lang.Nullable;

import java.lang.annotation.*;

/**
 * Is used to create a proxy bean with qualifiers.
 * The proxy bean created will be an ordered combination of sub-proxies.
 * Proxies may be created with {@link DefineProxy} or as normal spring beans.
 * Use annotations that define a {@link org.springframework.beans.factory.annotation.Qualifier} meta annotation for {@link this#qualifiers()}.
 *
 * Example:
 * @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
 * @Retention(RetentionPolicy.RUNTIME)
 * @Qualifier("secured")
 * @Inherited
 * public @interface Secured {
 * }
 *
 * @DefineProxy(name = "acl",extensions = ...)
 * @DefineProxy(name = "secured",extensions = ...)
 * @CreateProxy(proxies = {"acl","secured"}, qualifiers = Secured.class)
 * public class MyService{
 *     ...
 * }
 *
 * public class SomeOtherClass{
 *
 *     @Autowired
 *     @Secured
 *     private MyService securedService;
 * }
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(CreateProxies.class)
public @interface CreateProxy {

    /**
     * Names of sub proxies in order of execution
     * May be bean names or {@link DefineProxy#name()}
     */
    String[] proxies() default {};
    Class<? extends Annotation>[] qualifiers();

    /**
     * Bean name or created proxy bean
     * If left empty bean name is generated based on qualifiers and root bean name
     * @return
     */
    @Nullable
    String name() default "";
    boolean primary() default false;
}
