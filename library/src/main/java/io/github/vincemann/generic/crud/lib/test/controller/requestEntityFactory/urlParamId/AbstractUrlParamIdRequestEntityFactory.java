package io.github.vincemann.generic.crud.lib.test.controller.requestEntityFactory.urlParamId;

import io.github.vincemann.generic.crud.lib.test.InitializingTest;
import io.github.vincemann.generic.crud.lib.test.TestContextAware;
import io.github.vincemann.generic.crud.lib.test.controller.ControllerIntegrationTest;
import io.github.vincemann.generic.crud.lib.test.controller.crudTests.config.abs.ControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.requestEntityFactory.RequestEntityFactory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.Serializable;
import java.net.URI;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
public abstract class AbstractUrlParamIdRequestEntityFactory<Id extends Serializable, C extends ControllerTestConfiguration<Id>>
        implements RequestEntityFactory<Id,C>, TestContextAware<ControllerIntegrationTest<?,Id>> {


    private ControllerIntegrationTest<?,Id> testContext;

    //url param id specific -> leave here dont put in context
    @Value("${controller.idFetchingStrategy.idUrlParamKey}")
    private String entityIdParamKey;

    protected Id determineId(C config, Id id){
        Id finalId;
        if(config.getId()!=null){
            log.warn("user changed id in test method. Using: " + config.getId() + " instead of id set by framework: " + id);
            finalId=config.getId();
        }else {
            finalId=id;
        }
        return finalId;
    }

    @Override
    public boolean supports(Class<? extends InitializingTest> contextClass) {
        return ControllerIntegrationTest.class.isAssignableFrom(contextClass);
    }

    public RequestEntity<Object> createRequestEntity(C config, Object body, URI uri){
        return new RequestEntity<>(body, config.getHeaders(), HttpMethod.resolve(config.getMethod().name()), uri);
    }


    protected UriComponentsBuilder buildUri(String methodName,C config, Id id) {
        Id finalId = determineId(config,id);
        String baseAddress = testContext.provideAddress();
        UriComponentsBuilder builder;
        if(finalId!=null) {
            builder= UriComponentsBuilder.fromHttpUrl(baseAddress + testContext.getController().getBaseUrl() + methodName)
                    .queryParam(entityIdParamKey, finalId);
        }else {
            builder = UriComponentsBuilder.fromHttpUrl(baseAddress + testContext.getController().getBaseUrl() + methodName);
        }
        if(config.getQueryParams()!=null){
            config.getQueryParams().forEach(builder::queryParam);
        }
        return builder;
    }
}
