package com.github.vincemann.springrapid.core.slicing.test;

import com.github.vincemann.springrapid.core.config.DatabaseInitAutoConfiguration;
import com.github.vincemann.springrapid.core.config.CrudServiceLocatorAutoConfiguration;
import com.github.vincemann.springrapid.core.config.AopLogAutoConfiguration;
import com.github.vincemann.springrapid.core.config.RapidUtilAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ImportAutoConfiguration({
//import rapid core config that is relevant for service tests
        DatabaseInitAutoConfiguration.class, CrudServiceLocatorAutoConfiguration.class, AopLogAutoConfiguration.class, AopLogAutoConfiguration.class, RapidUtilAutoConfiguration.class
})
public @interface ImportRapidCoreServiceConfig {
}
