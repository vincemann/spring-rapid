package com.github.vincemann.springrapid.core.proxy;

import com.github.vincemann.springrapid.core.service.SecurityChecker;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Create your own Service Extension by extending from this class and implementing the interface(s) of the service, that you want
 * to write an Extension for.
 * {@link ServiceExtensionProxy} will call the extension, when given to it, and integrate it in the extension chain.
 * Use {@link this#getProxyController()} ()} to influence proxy behavior.
 *
 *
 * @param <T> Type of proxied Service, you are writing the Extension for.
 */
@Getter
public abstract class ServiceExtension<T>
        extends AbstractServiceExtension<T, ServiceExtensionProxy> {

    private SecurityChecker securityChecker;

    @Autowired
    public void injectSecurityChecker(SecurityChecker securityChecker) {
        this.securityChecker = securityChecker;
    }
}
