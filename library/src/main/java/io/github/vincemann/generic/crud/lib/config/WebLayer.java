package io.github.vincemann.generic.crud.lib.config;


import org.springframework.context.annotation.Profile;

import java.lang.annotation.*;

@Inherited
@Profile("web")
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebLayer {
}
