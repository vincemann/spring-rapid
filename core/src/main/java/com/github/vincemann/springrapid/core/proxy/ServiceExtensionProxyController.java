package com.github.vincemann.springrapid.core.proxy;

import com.github.vincemann.springrapid.core.service.CrudService;

public interface ServiceExtensionProxyController {
    void dontCallTargetMethod();

    /**
     * If {@link ServiceExtension} calls this, then result can safely be casted to <T>.
     * @return
     */
    CrudService getProxied();
    <T> T getNext(ServiceExtension<T> extension);

}
