package com.github.vincemann.springrapid.core.proxy;

import com.github.vincemann.springrapid.core.security.SecurityChecker;
import com.google.common.base.Objects;
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
        extends AbstractServiceExtension<T, ProxyController> {

    private SecurityChecker securityChecker;

    @Autowired
    public void injectSecurityChecker(SecurityChecker securityChecker) {
        this.securityChecker = securityChecker;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServiceExtension)) return false;
        if (!super.equals(o)) return false;
        ServiceExtension<?> that = (ServiceExtension<?>) o;
        return Objects.equal(getSecurityChecker(), that.getSecurityChecker());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), getSecurityChecker());
    }
}
