package io.github.vincemann.generic.crud.lib.test.controller.requestEntityFactory.urlParamId;

import io.github.vincemann.generic.crud.lib.controller.springAdapter.SpringAdapterDtoCrudController;
import io.github.vincemann.generic.crud.lib.test.controller.BaseAddressProvider;
import io.github.vincemann.generic.crud.lib.test.controller.crudTests.config.abs.ControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.requestEntityFactory.RequestEntityFactory;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.Serializable;
import java.net.URI;

@Slf4j
@Getter
public abstract class AbstractUrlParamIdRequestEntityFactory<Id extends Serializable, C extends ControllerTestConfiguration<Id>,Controller extends SpringAdapterDtoCrudController<?,Id>>
        implements RequestEntityFactory<Id,C> {


    private Controller controller;
    private String entityIdParamKey;
    private BaseAddressProvider baseAddressProvider;

    @Builder
    public AbstractUrlParamIdRequestEntityFactory(Controller controller, String entityIdParamKey, BaseAddressProvider baseAddressProvider) {
        this.controller = controller;
        this.entityIdParamKey = entityIdParamKey;
        this.baseAddressProvider = baseAddressProvider;
    }

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


    public RequestEntity<Object> createRequestEntity(C config, Object body, URI uri){
        return new RequestEntity<>(body, config.getHeaders(), HttpMethod.resolve(config.getMethod().name()), uri);
    }


    protected UriComponentsBuilder buildUri(String methodName,C config, Id id) {
        Id finalId = determineId(config,id);
        String baseAddress = getBaseAddressProvider().provideAddress();
        UriComponentsBuilder builder;
        if(finalId!=null) {
            builder= UriComponentsBuilder.fromHttpUrl(baseAddress + getController().getBaseUrl() + methodName)
                    .queryParam(entityIdParamKey, finalId);
        }else {
            builder = UriComponentsBuilder.fromHttpUrl(baseAddress + getController().getBaseUrl() + methodName);
        }
        if(config.getQueryParams()!=null){
            config.getQueryParams().forEach(builder::queryParam);
        }
        return builder;
    }
}
