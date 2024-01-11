package com.github.vincemann.springrapid.core.config;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.repo.CustomFilterRepository;
import com.github.vincemann.springrapid.core.repo.RapidCustomFilterRepository;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@ServiceConfig
public class RapidRepoAutoConfiguration {

//    @ConditionalOnMissingBean(CustomFilterRepository.class)
//    @Bean
//    public CustomFilterRepository customFilterRepositoryImpl(EntityManager entityManager) {
//        return new RapidCustomFilterRepository(entityManager);
//    }
}
