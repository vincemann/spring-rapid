package com.github.vincemann.springrapid.core.proxy;

import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.util.ProxyUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Extend this class to further configure PreConfigured {@link CrudService} Proxies.
 * Used primarily to add more plugins.
 *
 */
public abstract class ExtensionProxyConfigurer<S extends CrudService> implements InitializingBean {

    S service;

    @Override
    public void afterPropertiesSet() throws Exception {
        configureProxy((ProxyUtils.getExtensionProxy(service)));
    }

    public abstract void configureProxy(ExtensionProxy proxy);

    /** you can overwrite this method to autowire version/proxy of service that should be configured
       i.E.:

    @Overwrite
    @Autowired
    @Secured
    public void setService(S service) {
        this.service = service;
    }
     **/
    @Autowired
    public void setService(S service) {
        this.service = service;
    }
}
