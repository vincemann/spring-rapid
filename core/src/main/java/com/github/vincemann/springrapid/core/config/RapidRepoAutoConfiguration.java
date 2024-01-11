package com.github.vincemann.springrapid.core.config;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.repo.CustomFilterRepositoryImpl;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import org.springframework.context.annotation.Bean;

import javax.persistence.EntityManager;

@ServiceConfig
public class RapidRepoAutoConfiguration {

    @Bean
    public <E extends IdentifiableEntity<?>> CustomFilterRepositoryImpl<E> customFilterRepositoryImpl(EntityManager entityManager) {
        return new CustomFilterRepositoryImpl<>(entityManager);
    }
}
