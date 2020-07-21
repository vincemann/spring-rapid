package com.github.vincemann.springrapid.acl.slicing.test;

import com.github.vincemann.springrapid.acl.config.*;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ImportAutoConfiguration({AclAutoConfiguration.class, AclMethodSecurityAutoConfiguration.class, AclExtensionAutoConfiguration.class, AclSchemaAutoConfiguration.class, SecurityProxyAutoConfiguration.class})
public @interface ImportRapidAclConfig {
}
