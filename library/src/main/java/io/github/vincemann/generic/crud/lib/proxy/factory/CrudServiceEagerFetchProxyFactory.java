package io.github.vincemann.generic.crud.lib.proxy.factory;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.proxy.invocationHandler.ForceEagerFetchCrudServiceDynamicInvocationHandler;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.test.forceEagerFetch.HibernateForceEagerFetchUtil;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.test.util.AopTestUtils;

import java.io.Serializable;
import java.lang.reflect.Proxy;

@Component
public class CrudServiceEagerFetchProxyFactory {
    //we need the class explicitly here to avoid issues with other proxies. HibernateProxys for example, are not interfaces, so service.getClass returns no interface
    //-> this would make this crash
    public <Id extends Serializable, E extends IdentifiableEntity<Id>, S extends CrudService<E, Id, ? extends CrudRepository<E, Id>>> S
    create(S crudService, HibernateForceEagerFetchUtil eagerFetchUtil, String... omittedMethods) {
        S unproxied = AopTestUtils.getTargetObject(crudService);
        S proxyInstance = (S) Proxy.newProxyInstance(
                unproxied.getClass().getClassLoader(), unproxied.getClass().getInterfaces(),
                new ForceEagerFetchCrudServiceDynamicInvocationHandler(unproxied, eagerFetchUtil,omittedMethods));

        return proxyInstance;
    }



//    //todo maybe put this logic in other proxy factories as well
//    //autodetect proxies and unproxy them
//    public <Id extends Serializable, E extends IdentifiableEntity<Id>, S extends CrudService<E, Id, ? extends CrudRepository<E, Id>>> S
//    create(S crudService, HibernateForceEagerFetchUtil eagerFetchUtil,String... omittedMethods) {
//        //figure out service interface class by itself
//        //resolve proxy if necessary
//        Class<? extends CrudService> serviceClass;
//        if(crudService instanceof HibernateProxy){
//            serviceClass = (Class<? extends CrudService>) HibernateUtils.unproxy(crudService).getClass();
//        }else {
//            serviceClass = crudService.getClass();
//        }
//        S proxyInstance = (S) Proxy.newProxyInstance(
//                serviceClass.getClassLoader(), new Class[]{serviceClass},
//                new ForceEagerFetchCrudServiceDynamicInvocationHandler(crudService, eagerFetchUtil,omittedMethods));
//
//        return proxyInstance;
//    }


}
