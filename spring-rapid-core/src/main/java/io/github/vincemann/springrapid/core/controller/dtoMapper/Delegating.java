package io.github.vincemann.springrapid.core.controller.dtoMapper;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.*;

/**
 * The {@link Delegating} {@link DtoMapper} in {@link io.github.vincemann.springrapid.core.controller.rapid.RapidController}
 * will delegate to a DtoMapper, if it supports the dtoClass of the current mapping process.
 * If none ist found, the {@link Default} DtoMapper will delegated to.
 * @see {@link DtoMapper#isDtoClassSupported(Class)}
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier("delegatingDtoMapper")
@Inherited
public @interface Delegating {

}
