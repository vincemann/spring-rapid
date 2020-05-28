package io.github.vincemann.springrapid.coretest.slicing.test;

import io.github.vincemann.springrapid.coretest.config.RapidCoreTestAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ImportAutoConfiguration({
        RapidCoreTestAutoConfiguration.class
})
public @interface ImportRapidCoreTestConfig {
}
