package com.github.vincemann.logutil.config;

import com.github.vincemann.springrapid.core.util.LazyLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;

@Configuration
public class DemoConfig {

    public static Boolean USE_LAZY_LOGGER = Boolean.FALSE;

    @Autowired
    public void setLoggingEntityManager(EntityManager entityManager){
        Assert.notNull(entityManager);
        LazyLogger.setEntityManager(entityManager);
    }
}
