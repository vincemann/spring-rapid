package io.github.vincemann.generic.crud.lib.test.forceEagerFetch.proxy.abs;

import io.github.vincemann.generic.crud.lib.test.forceEagerFetch.Hibernate_ForceEagerFetch_Helper;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Qualifier;

@Getter
@Qualifier(Hibernate_ForceEagerFetch_Proxy.EAGER_FETCH_PROXY)
public abstract class Hibernate_ForceEagerFetch_Proxy {

    private Hibernate_ForceEagerFetch_Helper helper;

    public Hibernate_ForceEagerFetch_Proxy(Hibernate_ForceEagerFetch_Helper helper) {
        this.helper = helper;
    }

    /**
     * use this in your service/repository Beans as Qualifier String
     * example:
     *
     * @Qualifier(PROXY_QUALIFIER)
     * public interface MyServiceI {
     *     ...
     * }
     *
     * @Autowire @Qualifier(PROXY_QUALIFIER) MyServiceI myService
     *
     *
     */
    public static final String EAGER_FETCH_PROXY = "forceEagerFetchProxyBean";

}
