package com.github.vincemann.springlemon.auth.slicing.test;

import com.github.vincemann.springlemon.auth.config.LemonGeneralAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ImportAutoConfiguration({
        LemonGeneralAutoConfiguration.class
})
public @interface ImportLemonAuthGeneralConfig {
}
