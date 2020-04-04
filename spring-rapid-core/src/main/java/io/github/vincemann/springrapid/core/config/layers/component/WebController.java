package io.github.vincemann.springrapid.core.config.layers.component;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;

import java.lang.annotation.*;

@Inherited
@Profile("web")
@Controller
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebController {
}