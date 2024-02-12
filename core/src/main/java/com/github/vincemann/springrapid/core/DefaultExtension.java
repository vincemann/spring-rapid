package com.github.vincemann.springrapid.core;

import com.github.vincemann.springrapid.core.proxy.annotation.CreateProxy;
import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface DefaultExtension {
    Class<? extends Annotation> qualifier();
}
