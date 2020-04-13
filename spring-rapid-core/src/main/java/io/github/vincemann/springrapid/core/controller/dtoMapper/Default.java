package io.github.vincemann.springrapid.core.controller.dtoMapper;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.*;

/**
 * Qualifies the default, (fallback) DtoMapper in {@link io.github.vincemann.springrapid.core.controller.rapid.RapidController}s.
 * @see Delegating
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier("defaultDtoMapper")
@Inherited
public @interface Default {
}
