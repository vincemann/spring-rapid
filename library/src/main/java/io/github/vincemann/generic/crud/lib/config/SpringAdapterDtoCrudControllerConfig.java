package io.github.vincemann.generic.crud.lib.config;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMapper;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.IdResolvingDtoMapper;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.EntityIdResolver;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.idFetchingStrategy.IdFetchingStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.idFetchingStrategy.LongUrlParamIdFetchingStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.mediaTypeStrategy.JSONMediaTypeStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.mediaTypeStrategy.MediaTypeStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.validationStrategy.JavaXValidationStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.validationStrategy.ValidationStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SpringAdapterDtoCrudControllerConfig {

    @Value("${controller.idFetchingStrategy.idUrlParamKey}")
    private String idUrlParamKey;


    @Bean
    public IdFetchingStrategy<Long> getLongIdFetchingStrategy(){
        return new LongUrlParamIdFetchingStrategy(idUrlParamKey);
    }

    @Bean
    public MediaTypeStrategy getMediaTypeStrategy(){
        return new JSONMediaTypeStrategy();
    }

    @Bean
    public DtoMapper getDtoMapper(List<EntityIdResolver> entityIdResolvers){
        return new IdResolvingDtoMapper(entityIdResolvers);
    }

    @Bean
    public ValidationStrategy getValidationStrategy(){
        return new JavaXValidationStrategy();
    }



}
