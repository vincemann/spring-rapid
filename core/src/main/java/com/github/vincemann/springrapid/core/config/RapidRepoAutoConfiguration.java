package com.github.vincemann.springrapid.core.config;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.repo.FilterRepository;
import com.github.vincemann.springrapid.core.repo.RapidFilterRepository;
import com.github.vincemann.springrapid.core.service.AbstractCrudService;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import com.github.vincemann.springrapid.core.util.RepositoryUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.EntityManager;
import java.io.Serializable;

@ServiceConfig
public class RapidRepoAutoConfiguration {

//    @ConditionalOnMissingBean(FilterRepository.class)
//    @Bean
//    public <E extends IdentifiableEntity<?>> FilterRepository<E,?> filterRepository(EntityManager entityManager, AbstractCrudService<E,?,?> service) {
//        return new RapidFilterRepository(service.getEntityClass(), entityManager);
//    }
}
