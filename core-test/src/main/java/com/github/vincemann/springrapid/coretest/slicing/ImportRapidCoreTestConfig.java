package com.github.vincemann.springrapid.coretest.slicing;

import com.github.vincemann.springrapid.coretest.config.RapidServiceTestAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ImportAutoConfiguration({
        RapidServiceTestAutoConfiguration.class
})
public @interface ImportRapidCoreTestConfig {
}
