package com.github.vincemann.springrapid.syncdemo.config;

import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import com.github.vincemann.springrapid.syncdemo.service.filter.OwnerTelNumberFilter;
import org.springframework.context.annotation.Bean;

@ServiceConfig
public class MyFilterConfig {

    @Bean
    public OwnerTelNumberFilter telprefix(){
        return new OwnerTelNumberFilter();
    }
}
