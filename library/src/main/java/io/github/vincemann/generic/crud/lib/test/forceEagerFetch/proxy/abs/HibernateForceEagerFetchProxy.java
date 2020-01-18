package io.github.vincemann.generic.crud.lib.test.forceEagerFetch.proxy.abs;

import io.github.vincemann.generic.crud.lib.test.forceEagerFetch.HibernateForceEagerFetchUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Qualifier;

@Getter
@Qualifier(HibernateForceEagerFetchProxy.EAGER_FETCH_PROXY)
public abstract class HibernateForceEagerFetchProxy {

    private HibernateForceEagerFetchUtil eagerFetchUtil;

    public HibernateForceEagerFetchProxy(HibernateForceEagerFetchUtil eagerFetchUtil) {
        this.eagerFetchUtil = eagerFetchUtil;
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
