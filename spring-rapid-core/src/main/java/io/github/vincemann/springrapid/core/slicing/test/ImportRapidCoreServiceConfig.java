package io.github.vincemann.springrapid.core.slicing.test;

import io.github.vincemann.springrapid.core.config.BootstrapAutoConfiguration;
import io.github.vincemann.springrapid.core.config.CrudServiceLocatorAutoConfiguration;
import io.github.vincemann.springrapid.core.config.RapidAdviceAutoConfiguration;
import io.github.vincemann.springrapid.core.config.ReflectionCacheAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ImportAutoConfiguration({
//import rapid core config that is relevant for service tests
        BootstrapAutoConfiguration.class, CrudServiceLocatorAutoConfiguration.class, RapidAdviceAutoConfiguration.class, ReflectionCacheAutoConfiguration.class
})
public @interface ImportRapidCoreServiceConfig {
}
