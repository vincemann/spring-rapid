package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.requestEntityFactory;

import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.configuration.abs.AbstractControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.BaseAddressProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.Serializable;
import java.net.URI;
import java.util.Map;

@Slf4j
public class UrlParamIdRequestEntityFactory<Id extends Serializable>
        extends RequestEntityFactory<Id> {

    private String entityIdParamKey;

    public UrlParamIdRequestEntityFactory(BaseAddressProvider baseAddressProvider, String entityIdParamKey) {
        super(baseAddressProvider);
        this.entityIdParamKey = entityIdParamKey;
    }

    @Override
    public RequestEntity<?> create(AbstractControllerTestConfiguration<Id> config, Object body) {
        //build uri
        String baseAddress = getBaseAddressProvider().provideAddress();
        URI uri = buildUri(config.getMethod().name(), baseAddress, config.getId(), config.getQueryParams());
        log.debug("uri for test request: " + uri.toString());
        RequestEntity<Object> requestEntity = new RequestEntity<>(body, config.getHeaders(), HttpMethod.resolve(config.getMethod().name()), uri);
        log.debug("test requestEntity: " + requestEntity);
        return requestEntity;
    }

    private URI buildUri(String methodName, String baseUrl, Object id, Map<String, String> additionalQueryParams) {
        String baseAddress = getBaseAddressProvider().provideAddress();
        UriComponentsBuilder builder;
        if(id!=null) {
            builder= UriComponentsBuilder.fromHttpUrl(baseAddress + baseUrl + methodName)
                    .queryParam(entityIdParamKey, id);
        }else {
            builder = UriComponentsBuilder.fromHttpUrl(baseAddress + baseUrl + methodName);
        }
        if(additionalQueryParams!=null){
            additionalQueryParams.forEach(builder::queryParam);
        }
        return builder.build().toUri();
    }
}
