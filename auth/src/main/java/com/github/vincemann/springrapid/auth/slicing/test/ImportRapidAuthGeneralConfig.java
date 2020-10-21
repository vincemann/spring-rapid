package com.github.vincemann.springrapid.auth.slicing.test;

import com.github.vincemann.springrapid.auth.config.RapidAuthGeneralAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ImportAutoConfiguration({
        RapidAuthGeneralAutoConfiguration.class
})
public @interface ImportRapidAuthGeneralConfig {
}
