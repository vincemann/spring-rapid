package com.github.vincemann.springrapid.authdemo.adapter;

import org.springframework.context.annotation.ComponentScan;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@ComponentScan("com.github.vincemann.springrapid.authdemo")
public @interface EnableProjectComponentScan {
}
