package vincemann.github.generic.crud.lib.demo.config;

import vincemann.github.generic.crud.lib.controller.springAdapter.idFetchingStrategy.IdFetchingStrategy;
import vincemann.github.generic.crud.lib.controller.springAdapter.idFetchingStrategy.LongUrlParamIdFetchingStrategy;
import vincemann.github.generic.crud.lib.controller.springAdapter.mediaTypeStrategy.JSONMediaTypeStrategy;
import vincemann.github.generic.crud.lib.controller.springAdapter.mediaTypeStrategy.MediaTypeStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringAdapterCrudDtoControllerConfig {

    @Value("${controller.idFetchingStrategy.idUrlParamKey}")
    private String idUrlParamKey;

    @Bean
    public IdFetchingStrategy<Long> getLongIdFetchingStrategy(){
        return new LongUrlParamIdFetchingStrategy(idUrlParamKey);
    }

    @Bean
    public MediaTypeStrategy mediaTypeStrategy(){
        return new JSONMediaTypeStrategy();
    }




}
