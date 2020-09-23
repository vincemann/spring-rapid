package com.github.vincemann.springrapid.core.proxy;

/**
 * Write extensions for normal Services, managed by {@link ServiceExtensionProxy}.
 *
 * Create your own Service Extension by extending from this class and implementing the interface(s) of the service, that you want
 * to write an Extension for.
 * {@link ServiceExtensionProxy} will call the extension, when given to it, and integrate it in the extension chain.
 * Use {@link this#getProxyController()} ()} to get a handle to influence proxy behavior.
 *
 *
 * @param <T> Type of proxied Service, you are writing the Extension for.
 */
public abstract class BasicServiceExtension<T>
        extends AbstractServiceExtension<T, ProxyController> {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BasicServiceExtension)) return false;
        if (!super.equals(o)) return false;
        return true;
    }
}
