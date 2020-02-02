package io.github.vincemann.demo.config;

import io.github.vincemann.generic.crud.lib.proxy.factory.CrudServiceEagerFetchProxyFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class ForceEagerFetchConfig {

    @Bean
    public CrudServiceEagerFetchProxyFactory eagerFetchProxyFactory(){
        return new CrudServiceEagerFetchProxyFactory();
    }
}
