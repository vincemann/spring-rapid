package com.github.vincemann.springrapid.core.config;

import com.github.vincemann.springrapid.core.model.audit.LongIdSecurityAuditorAware;
import com.github.vincemann.springrapid.core.proxy.CustomAutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.util.Assert;

import java.util.List;

@EnableJpaAuditing
public class RapidAuditAutoConfiguration {

    // you can overwrite existing autorAware, is done in auth modules autoconfig
    // if you need an other type than long, define a bean with name rapidSecurityAuditorAware with other generic parameter
    @ConditionalOnMissingBean(name = "rapidSecurityAuditorAware")
    @Bean
    public AuditorAware<Long> auditorAware(){
        return new LongIdSecurityAuditorAware();
    }

}
