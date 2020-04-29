package io.github.vincemann.springrapid.demo;

import org.springframework.context.annotation.ComponentScan;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@ComponentScan(value = "io.github.vincemann.springrapid.demo")
public @interface EnableProjectComponentScan {
}
