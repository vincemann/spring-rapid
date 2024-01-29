package com.github.vincemann.springrapid.coretest.boot;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier("beforeEachMethodInit")
@Inherited
public @interface BeforeEachMethodInitializable {
}
