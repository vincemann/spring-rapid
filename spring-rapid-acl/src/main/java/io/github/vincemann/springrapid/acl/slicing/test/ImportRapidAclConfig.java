package io.github.vincemann.springrapid.acl.slicing.test;

import io.github.vincemann.springrapid.acl.config.*;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ImportAutoConfiguration({AclAutoConfiguration.class, AclMethodSecurityAutoConfiguration.class, AclPluginAutoConfiguration.class, AclSchemaAutoConfiguration.class, SecurityProxyAutoConfiguration.class})
public @interface ImportRapidAclConfig {
}
