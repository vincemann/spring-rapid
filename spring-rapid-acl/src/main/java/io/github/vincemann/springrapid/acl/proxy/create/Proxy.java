package io.github.vincemann.springrapid.acl.proxy.create;

import io.github.vincemann.springrapid.acl.proxy.create.ConfigureProxies;
import io.github.vincemann.springrapid.core.service.ServiceBeanType;
import io.github.vincemann.springrapid.core.service.plugin.CrudServicePlugin;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Repeatable(ConfigureProxies.class)
public @interface Proxy {

    String name() default "";

    Class<? extends Annotation>[] qualifiers() default {};

    Class<? extends CrudServicePlugin>[] plugins() default {};

    boolean primary() default false;


}
