package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.defaultUriFactory;

import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.defaultUriFactory.testBaseUrlProvider.BaseAddressProvider;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

public class UrlParamUrlParamIdDefaultUriFactory implements UrlParamIdDefaultUriFactory {

    private BaseAddressProvider baseAddressProvider;
    private String entityIdParamKey;

    public UrlParamUrlParamIdDefaultUriFactory(String entityIdParamKey) {
        this.entityIdParamKey = entityIdParamKey;
    }

    @Override
    public void setBaseAddressProvider(BaseAddressProvider baseAddressProvider) {
        this.baseAddressProvider = baseAddressProvider;
    }



    @Override
    public URI createDefaultUri(String methodName, String baseUrl, Object id,MultiValueMap<String, String> additionalQueryParams) {
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

    public BaseAddressProvider getBaseAddressProvider() {
        if(baseAddressProvider ==null){
            throw new IllegalStateException("No Base Url Provider set yet");
        }
        return baseAddressProvider;
    }

}
