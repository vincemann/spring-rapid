package com.github.vincemann.springrapid.core.proxy;


import org.apache.commons.lang3.ClassUtils;
import org.springframework.test.util.AopTestUtils;

import java.lang.reflect.Proxy;

/**
 * Use to programmatically create {@link ExtensionProxy} proxy chains in a typesafe manner.
 *
 * For proxies that have {@link com.github.vincemann.springrapid.core.service.CrudService} as proxied object, use
 * {@link CrudServiceExtensionProxyBuilder} for additional typesafe methods.
 *
 * Example:
 *
 * @Service
 * @Root
 * public class MyService{
 *     ...
 * }
 *
 * @Configuration
 * public class MyServiceConfig {
 *
 *     @Acl
 *     @Bean
 *     public MyService myAclService(@Root MyService myRootService,
 *                                         AclExtension1 extension1,
 *                                         AclExtension2 extension2
 *     ) {
 *         return new ExtensionProxyBuilder(myRootService)
 *                 .addExtension(extension1)
 *                 .addExtension(extension2)
 *                 .build();
 *     }
 *
 *     @Secured
 *     @Bean
 *     public MyService mySecuredService(@Acl MyService myAclService,
 *                                            OnlyAdminCanCreate adminCreateExtension){
 *         // using shorter builder creation here -> {@link ExtensionProxies}
 *         return crudProxy(myAclService)
 *              .addExtension(adminCreateExtension)
 *              .build();
 *     }
 * }
 *
 *
 *
 * Note: {@link com.github.vincemann.springrapid.core.proxy.annotation.CreateProxy} and {@link com.github.vincemann.springrapid.core.proxy.annotation.DefineProxy}
 * provide an annotation-based alternative approach.
 *
 * @param <T> type of proxied service
 */
public class ExtensionProxyBuilder<T> extends AbstractExtensionProxyBuilder<T,ExtensionProxyBuilder<T>>{
    public ExtensionProxyBuilder(T proxied) {
        super(proxied);
    }
}
