package com.github.vincemann.springrapid.core.slicing;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;

import java.lang.annotation.*;

@Inherited
@Profile(RapidProfiles.WEB)
@Controller
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebController {
}
