package com.github.vincemann.springrapid.acl.proxy.annotation;

import com.github.vincemann.springrapid.core.proxy.AbstractServiceExtension;

public @interface CreateSecurityProxy {
    String name() default "";
    Class<? extends AbstractServiceExtension>[] extensions() default {};
}
