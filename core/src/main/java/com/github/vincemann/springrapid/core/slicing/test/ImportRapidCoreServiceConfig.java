package com.github.vincemann.springrapid.core.slicing.test;

import com.github.vincemann.springrapid.core.config.RapidDatabaseInitAutoConfiguration;
import com.github.vincemann.springrapid.core.config.RapidCrudServiceLocatorAutoConfiguration;
import com.github.vincemann.springrapid.core.config.RapidAopLogAutoConfiguration;
import com.github.vincemann.springrapid.core.config.RapidGeneralAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ImportAutoConfiguration({
//import rapid core config that is relevant for service tests
        RapidDatabaseInitAutoConfiguration.class,
        RapidCrudServiceLocatorAutoConfiguration.class,
        RapidAopLogAutoConfiguration.class,
        RapidAopLogAutoConfiguration.class,
        RapidGeneralAutoConfiguration.class
})
public @interface ImportRapidCoreServiceConfig {
}
