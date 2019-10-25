package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.defaultUriFactory;

import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.defaultUriFactory.testBaseUrlProvider.BaseUrlProvider;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

public class UrlParamDefaultUriFactory implements DefaultUriFactory {

    private BaseUrlProvider baseUrlProvider;
    private String entityIdParamKey;

    public UrlParamDefaultUriFactory(String entityIdParamKey) {
        this.entityIdParamKey = entityIdParamKey;
    }

    @Override
    public void setBaseUrlProvider(BaseUrlProvider baseUrlProvider) {
        this.baseUrlProvider=baseUrlProvider;
    }



    @Override
    public URI createDefaultUri(String methodName, Object id, MultiValueMap<String, String> additionalQueryParams) {
        String baseUrl = getBaseUrlProvider().provideUrl();
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

    public BaseUrlProvider getBaseUrlProvider() {
        if(baseUrlProvider==null){
            throw new IllegalStateException("No Base Url Provider set yet");
        }
        return baseUrlProvider;
    }

}
