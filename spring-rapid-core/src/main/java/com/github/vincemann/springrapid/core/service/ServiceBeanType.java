package com.github.vincemann.springrapid.core.service;

import java.lang.annotation.*;

/**
 * Create your own Annotations with this parent Annotation to qualify your ServiceBeans.
 * This way you can search and access your Services from everywhere with {@link com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator}.
 * Examples from the Framework are @Secured and @AclManaging from the Acl module.
 * @see CrudService
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ServiceBeanType {
}
