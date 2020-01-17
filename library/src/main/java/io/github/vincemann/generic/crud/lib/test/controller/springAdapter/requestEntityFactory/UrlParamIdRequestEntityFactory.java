package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.requestEntityFactory;

import io.github.vincemann.generic.crud.lib.controller.springAdapter.SpringAdapterDtoCrudController;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.ControllerTestMethod;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.UpdateControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.abs.ControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.BaseAddressProvider;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.Serializable;
import java.net.URI;
import java.util.AbstractMap;
import java.util.Map;

@Slf4j
public class UrlParamIdRequestEntityFactory<Id extends Serializable>
        extends RequestEntityFactory<Id> {

    public static final Map.Entry<String,String> FULL_UPDATE_QUERY_PARAM_ENTRY = new AbstractMap.SimpleEntry<>("full","true");
    private SpringAdapterDtoCrudController<?,Id> controller;
    private String entityIdParamKey;

    @Builder
    public UrlParamIdRequestEntityFactory(BaseAddressProvider baseAddressProvider, SpringAdapterDtoCrudController<?, Id> controller, String entityIdParamKey) {
        super(baseAddressProvider);
        this.controller = controller;
        this.entityIdParamKey = entityIdParamKey;
    }

    @Override
    public RequestEntity<?> create(ControllerTestConfiguration<Id> config, Object body,Id id, ControllerTestMethod controllerTestMethod) {
        //build uri
        Boolean fullUpdate = null;
        Id finalId;
        if(config.getId()!=null){
            log.warn("user changed id in test method. Using: " + config.getId() + " instead of id set by framework: " + id);
            finalId=config.getId();
        }else {
            finalId=id;
        }
        if(config instanceof UpdateControllerTestConfiguration){
             fullUpdate = ((UpdateControllerTestConfiguration) config).getFullUpdate();
        }
        URI uri = buildUri(mapTestMethodToMethodName(controllerTestMethod), controller.getBaseUrl(), finalId, config.getQueryParams(),fullUpdate);
        log.debug("uri for test request: " + uri.toString());
        RequestEntity<Object> requestEntity = new RequestEntity<>(body, config.getHeaders(), HttpMethod.resolve(config.getMethod().name()), uri);
        log.debug("test requestEntity: " + requestEntity);
        return requestEntity;
    }

    private String mapTestMethodToMethodName(ControllerTestMethod controllerTestMethod){

        switch (controllerTestMethod){
            case UPDATE:
                return controller.getUpdateMethodName();
            case CREATE:
                return controller.getCreateMethodName();
            case FIND:
                return controller.getFindMethodName();
            case DELETE:
                return controller.getDeleteMethodName();
            case FIND_ALL:
                return controller.getFindAllMethodName();
        }
        throw new IllegalArgumentException("Unknown controllerTestMethod: " + controllerTestMethod);
    }

    private URI buildUri(String methodName, String baseUrl, Object id, Map<String, String> additionalQueryParams, Boolean fullUpdate) {
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
        if(fullUpdate!=null){
            if(fullUpdate){
                //...?full=true
                builder.queryParam(FULL_UPDATE_QUERY_PARAM_ENTRY.getKey(),FULL_UPDATE_QUERY_PARAM_ENTRY.getValue());
            }
        }
        return builder.build().toUri();
    }
}
