package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.core.proxy.ServiceExtensionProxy;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.util.ProxyUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Extend this class to further configure PreConfigured {@link CrudService} Proxies.
 * Used primarily to add more plugins.
 *
 */
public class ServiceProxyConfigurer<S extends CrudService> implements InitializingBean {

    S service;

    @Override
    public void afterPropertiesSet() throws Exception {
        configureProxy((ProxyUtils.getExtensionProxy(service)));
    }

    public void configureProxy(ServiceExtensionProxy proxy){

    }

    /** you can overwrite this method to autowire diff version of service
       with i.E.:

    @Overwrite
    @Autowired
    @Secured
    public void injectService(S service) {
        this.service = service;
    }
     **/
    @Autowired
    public void injectService(S service) {
        this.service = service;
    }
}
