package io.github.vincemann.springrapid.core.controller.dtoMapper;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.*;

/**
 * The {@link Delegating} {@link DtoMapper} in {@link io.github.vincemann.springrapid.core.controller.rapid.RapidController}
 * will delegate to a DtoMapper, if it supports the dtoClass of the current mapping process.
 * The order in which the Mappers are registered is the order in which the delegates are asked to support the dto Class.
 * @see {@link DtoMapper#supports(Class)}
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier("delegatingDtoMapper")
@Inherited
public @interface Delegating {

}
