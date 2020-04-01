package io.github.vincemann.generic.crud.lib.advice.log;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface LogInteraction {
}
