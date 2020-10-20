package com.github.vincemann.springrapid.acl.slicing.test;

import com.github.vincemann.springrapid.acl.config.*;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ImportAutoConfiguration({RapidAclAutoConfiguration.class, RapidAclMethodSecurityAutoConfiguration.class, RapidAclExtensionsAutoConfiguration.class, RapidAclSchemaAutoConfiguration.class, RapidSecurityProxyAutoConfiguration.class})
public @interface ImportRapidAclConfig {
}
