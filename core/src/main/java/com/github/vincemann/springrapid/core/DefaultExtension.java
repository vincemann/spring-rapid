package com.github.vincemann.springrapid.core;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.*;

/**
 * Annotate extensions that should be automatically added to the {@link com.github.vincemann.springrapid.core.proxy.ExtensionProxy} by the framework.
 * 
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier("default-extension")
@Inherited
public @interface DefaultExtension {
    Class<? extends Annotation> qualifier();
    Class<?> service() default Object.class;
}
