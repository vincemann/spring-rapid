package com.github.vincemann.springrapid.core.proxy;

import com.github.vincemann.springrapid.core.service.SecurityChecker;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Create your own Service Extension by extending from this class and implementing the interface(s) of the service, that you want
 * to write an Extension for.
 * {@link ExtensionServiceProxy} will call the extension, when given to it, and integrate it in the extension chain.
 * Use {@link this#getChain()} to influence proxy behavior.
 *
 *
 * @param <T> Type of proxied Service, you are writing the Extension for.
 */
@Getter
public abstract class ServiceExtension<T> extends AbstractServiceExtension<T, ExtensionServiceProxy<? extends T>> {
    private SecurityChecker securityChecker;

    @Autowired
    public void injectSecurityChecker(SecurityChecker securityChecker) {
        this.securityChecker = securityChecker;
    }
}
