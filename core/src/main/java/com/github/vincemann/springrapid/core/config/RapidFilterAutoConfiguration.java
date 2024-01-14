package com.github.vincemann.springrapid.core.config;

import com.github.vincemann.springrapid.core.service.filter.jpa.ParentFilter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class RapidFilterAutoConfiguration {

    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Bean
    public ParentFilter parentFilter(){
        return new ParentFilter();
    }
}
