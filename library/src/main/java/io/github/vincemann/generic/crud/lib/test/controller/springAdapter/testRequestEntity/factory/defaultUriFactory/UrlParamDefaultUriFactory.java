package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.defaultUriFactory;

import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

public class UrlParamDefaultUriFactory implements DefaultUriFactory {
    private String baseUrl;
    private String entityIdParamKey;

    public UrlParamDefaultUriFactory(String baseUrl, String entityIdParamKey) {
        this.baseUrl = baseUrl;
        this.entityIdParamKey = entityIdParamKey;
    }

    @Override
    public URI createDefaultUri(String methodName, Object id, MultiValueMap<String, String> additionalQueryParams) {
        UriComponentsBuilder builder;
        if(id!=null) {
            builder= UriComponentsBuilder.fromHttpUrl(baseUrl + methodName)
                    .queryParam(entityIdParamKey, id);
        }else {
            builder = UriComponentsBuilder.fromHttpUrl(baseUrl + methodName);
        }
        if(additionalQueryParams!=null){
            additionalQueryParams.forEach(builder::queryParam);
        }
        return builder.build().toUri();
    }
}
