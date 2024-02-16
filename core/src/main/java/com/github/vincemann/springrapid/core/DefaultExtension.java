package com.github.vincemann.springrapid.core;

import com.github.vincemann.springrapid.core.proxy.ExtensionProxyBuilder;
import com.github.vincemann.springrapid.core.proxy.annotation.DefineProxy;
import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.*;

/**
 * Annotate extensions that should be automatically added to the {@link com.github.vincemann.springrapid.core.proxy.ExtensionProxy} by the framework.
 * Each default extension is mapped to one qualifier.
 * You are supposed to supply a class of an annotation that defines a meta annotation of type {@link Qualifier}.
 * Example of adding default extension for all secured {@link com.github.vincemann.springrapid.core.service.CrudService}s :
 *
 * @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
 * @Retention(RetentionPolicy.RUNTIME)
 * @Qualifier("secured")
 * @Inherited
 * public @interface Secured {
 * }
 *
 * @DefaultExtension(qualfier = Secured.class, service=CrudService.class)
 * @Component
 * @Scope(Prototype)
 * public MyDefaultSecurityExtension extends ServiceExtension implements CrudService{
 *      // add default security checks, that should be performed for all secured services
 * }
 *
 * You can ignore default extensions on a per proxy basis using {@link DefineProxy#defaultExtensionsEnabled()}
 * or {@link DefineProxy#ignoredExtensions()}.
 * Or when using programmatic proxy creation:
 * {@link ExtensionProxyBuilder#disableDefaultExtensions()}
 *
 * Note:
 * When exposing the default extension as a bean within a @Configuration class using @Bean,
 * you still have to put this annotation on the class, not only on the bean providing method.
 *
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier("default-extension")
@Inherited
public @interface DefaultExtension {
    Class<? extends Annotation> qualifier();

    /**
     * provide the base class/interface that has be extended/implemented, in order for this extension should be added
     */
    Class<?> service() default Object.class;
}
